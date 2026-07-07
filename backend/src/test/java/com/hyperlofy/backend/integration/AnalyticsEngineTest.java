package com.hyperlofy.backend.integration;

import com.hyperlofy.backend.analytics.dto.KPIReport;
import com.hyperlofy.backend.analytics.dto.OperationalMetrics;
import com.hyperlofy.backend.analytics.dto.RevenueReport;
import com.hyperlofy.backend.analytics.entity.AnalyticsSnapshot;
import com.hyperlofy.backend.analytics.repository.AnalyticsSnapshotRepository;
import com.hyperlofy.backend.analytics.service.AnalyticsEngineService;
import com.hyperlofy.backend.ledger.entity.EscrowTransaction;
import com.hyperlofy.backend.ledger.repository.EscrowTransactionRepository;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.payment.entity.Payment;
import com.hyperlofy.backend.payment.entity.PaymentStatus;
import com.hyperlofy.backend.payment.repository.PaymentRepository;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnalyticsEngineTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalyticsEngineService service;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EscrowTransactionRepository escrowTransactionRepository;

    @MockBean
    private AnalyticsSnapshotRepository analyticsSnapshotRepository;

    @BeforeEach
    void setUp() {
        Order fakeOrder = Order.builder().build();
        Mockito.when(orderRepository.findAll()).thenReturn(Collections.singletonList(fakeOrder));

        Payment fakePayment = Payment.builder()
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("150.00"))
                .build();
        fakePayment.setCreatedAt(OffsetDateTime.now());
        Mockito.when(paymentRepository.findAll()).thenReturn(Collections.singletonList(fakePayment));

        EscrowTransaction fakeEscrow = EscrowTransaction.builder()
                .amount(new BigDecimal("150.00"))
                .status("HELD")
                .build();
        Mockito.when(escrowTransactionRepository.findAll()).thenReturn(Collections.singletonList(fakeEscrow));

        Mockito.when(analyticsSnapshotRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetKpisAndDashboardsAggregation() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(1))
                .andExpect(jsonPath("$.totalRevenue").value(150.00))
                .andExpect(jsonPath("$.platformRevenue").value(22.50))
                .andExpect(jsonPath("$.escrowBalance").value(150.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRevenueBreakdownAggregation() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/revenue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlatformEarnings").value(22.50))
                .andExpect(jsonPath("$.totalAgentPayouts").value(127.50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDailyAggregationSnapshotGeneration() throws Exception {
        mockMvc.perform(post("/api/v1/analytics/snapshot/trigger")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(1))
                .andExpect(jsonPath("$.totalRevenue").value(150.00));
    }
}
