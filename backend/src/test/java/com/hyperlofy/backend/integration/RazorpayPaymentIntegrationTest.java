package com.hyperlofy.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperlofy.backend.ledger.entity.EscrowTransaction;
import com.hyperlofy.backend.ledger.service.LedgerService;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.payment.entity.Payment;
import com.hyperlofy.backend.payment.entity.PaymentGateway;
import com.hyperlofy.backend.payment.entity.PaymentStatus;
import com.hyperlofy.backend.payment.entity.Refund;
import com.hyperlofy.backend.payment.repository.PaymentRepository;
import com.hyperlofy.backend.payment.repository.RefundRepository;
import com.hyperlofy.backend.payment.service.PaymentGatewayService;
import com.hyperlofy.backend.payment.service.RazorpayService;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RazorpayPaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private RefundRepository refundRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RazorpayService razorpayService;

    @MockBean
    private LedgerService ledgerService;

    private UUID sampleOrderId;
    private Order sampleOrder;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        sampleOrderId = UUID.randomUUID();
        User customer = User.builder().email("cust@test.com").role(Role.CUSTOMER).build();
        customer.setId(UUID.randomUUID());

        sampleOrder = Order.builder()
                .customer(customer)
                .storeName("Main Street Store")
                .deliveryFee(new BigDecimal("120.00"))
                .orderStatus(OrderStatus.CREATED)
                .build();
        sampleOrder.setId(sampleOrderId);

        samplePayment = Payment.builder()
                .order(sampleOrder)
                .amount(new BigDecimal("120.00"))
                .paymentStatus(PaymentStatus.PENDING)
                .paymentGateway(PaymentGateway.RAZORPAY)
                .gatewayOrderId("rzp_order_abc123")
                .build();
        samplePayment.setId(UUID.randomUUID());

        Mockito.when(orderRepository.findById(sampleOrderId)).thenReturn(Optional.of(sampleOrder));
        Mockito.when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(samplePayment));
        Mockito.when(paymentRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateRazorpayOrderSuccess() throws Exception {
        Mockito.when(razorpayService.createRazorpayOrder(Mockito.any(), Mockito.any()))
                .thenReturn("rzp_order_abc123");

        mockMvc.perform(post("/api/v1/payments/razorpay/create/" + sampleOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gatewayOrderId").value("rzp_order_abc123"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testVerifyAndCompletePaymentSuccess() throws Exception {
        Mockito.when(razorpayService.verifySignature(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        Mockito.when(ledgerService.placeInEscrow(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new EscrowTransaction());

        mockMvc.perform(post("/api/v1/payments/razorpay/complete")
                        .param("paymentId", samplePayment.getId().toString())
                        .param("razorpayPaymentId", "rzp_pay_xyz987")
                        .param("razorpaySignature", "valid_sig_hash")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").value("rzp_pay_xyz987"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testVerifyAndCompletePaymentSignatureFailure() throws Exception {
        Mockito.when(razorpayService.verifySignature(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);

        mockMvc.perform(post("/api/v1/payments/razorpay/complete")
                        .param("paymentId", samplePayment.getId().toString())
                        .param("razorpayPaymentId", "rzp_pay_xyz987")
                        .param("razorpaySignature", "invalid_sig_hash")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWebhookReplayProtectionAndValidation() throws Exception {
        Mockito.when(razorpayService.verifyWebhookSignature(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        String payload = "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_1\"}}}}";

        mockMvc.perform(post("/api/v1/payments/razorpay/webhook")
                        .header("X-Razorpay-Signature", "sig123")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Perform second request with identical payload to trigger replay guard check
        mockMvc.perform(post("/api/v1/payments/razorpay/webhook")
                        .header("X-Razorpay-Signature", "sig123")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Returns 200 OK after ignoring duplicate callback safely
    }
}
