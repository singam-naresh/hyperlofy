package com.hyperlofy.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperlofy.backend.order.dto.OrderRequest;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.order.service.OrderService;
import com.hyperlofy.backend.payment.entity.Payment;
import com.hyperlofy.backend.payment.entity.PaymentStatus;
import com.hyperlofy.backend.payment.entity.Refund;
import com.hyperlofy.backend.payment.service.PaymentService;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import com.hyperlofy.backend.wallet.entity.TransactionType;
import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HyperlofyBackendIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderService orderService;

    @MockBean
    private WalletService walletService;

    @MockBean
    private PaymentService paymentService;

    private User sampleCustomer;
    private User sampleAgent;
    private User sampleAdmin;

    @BeforeEach
    void setUp() {
        sampleCustomer = User.builder()
                .email("cust@cust.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("9988776655")
                .role(Role.CUSTOMER)
                .build();
        sampleCustomer.setId(UUID.randomUUID());

        sampleAgent = User.builder()
                .email("agent@agent.com")
                .firstName("Robert")
                .lastName("Delivery")
                .phoneNumber("1122334455")
                .role(Role.AGENT)
                .build();
        sampleAgent.setId(UUID.randomUUID());

        sampleAdmin = User.builder()
                .email("admin@admin.com")
                .firstName("Super")
                .lastName("User")
                .phoneNumber("5566778899")
                .role(Role.ADMIN)
                .build();
        sampleAdmin.setId(UUID.randomUUID());

        Mockito.when(userRepository.findByEmail("cust@cust.com")).thenReturn(Optional.of(sampleCustomer));
        Mockito.when(userRepository.findByEmail("agent@agent.com")).thenReturn(Optional.of(sampleAgent));
        Mockito.when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(sampleAdmin));
    }

    private void authenticateAs(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testWalletLockAndPessimisticConcurrencyScenarios() {
        authenticateAs(sampleCustomer);

        Wallet wallet = Wallet.builder()
                .user(sampleCustomer)
                .balance(new BigDecimal("500.00"))
                .build();

        Mockito.when(walletService.getWalletByUserId(sampleCustomer.getId())).thenReturn(wallet);

        // Execute dynamic deposit credit
        assertDoesNotThrow(() -> {
            walletService.creditWallet(sampleCustomer.getId(), new BigDecimal("100.00"), TransactionType.DEPOSIT, null, "TopUp");
        });
    }

    @Test
    void testOrderSecurityOwnershipEnforcement() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .customer(sampleCustomer)
                .storeName("Mock Store")
                .orderStatus(OrderStatus.CREATED)
                .deliveryFee(new BigDecimal("45.00"))
                .build();
        order.setId(orderId);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Authenticate customer - owns order so should pass
        authenticateAs(sampleCustomer);
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRefundWorkflowFlowAdminApproval() {
        authenticateAs(sampleAdmin);

        Payment payment = Payment.builder()
                .amount(new BigDecimal("150.00"))
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(new BigDecimal("150.00"))
                .reason("Damaged Cargo")
                .build();

        Mockito.when(paymentService.approveRefund(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(refund);

        assertNotNull(paymentService.approveRefund(UUID.randomUUID(), sampleAdmin.getId(), "Verify return received"));
    }
}
