package com.hyperlofy.backend.order.service;

import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import com.hyperlofy.backend.agent.repository.AgentRepository;
import com.hyperlofy.backend.agent.service.AgentGeoService;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.order.entity.AssignmentAudit;
import com.hyperlofy.backend.order.entity.AssignmentHistory;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.AssignmentAuditRepository;
import com.hyperlofy.backend.order.repository.AssignmentHistoryRepository;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AgentGeoService agentGeoService;
    private final AgentRepository agentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final AssignmentHistoryRepository assignmentHistoryRepository;
    private final AssignmentAuditRepository assignmentAuditRepository;
    private final UserRepository userRepository;

    private static final double ASSIGNMENT_SEARCH_RADIUS_KM = 10.0;

    /**
     * Automatically finds and assigns the best available agent to an order after successful payment.
     *
     * Search priority:
     * 1. Nearby location (Redis GEO search within radius)
     * 2. Verification status = APPROVED
     * 3. Available flag = true
     * 4. Online status = true (Redis)
     * 5. Nearest distance first
     *
     * Creates audit trail via AssignmentHistory and AssignmentAudit.
     * Updates order status to ASSIGNED via OrderService state machine.
     *
     * @param orderId UUID of the order requiring agent assignment
     * @throws BusinessException if no qualified agents found or order not found
     */
    @Transactional
    public void assignAgentToOrder(UUID orderId) {
        log.info("Initiating automatic agent assignment for order: {}", orderId);

        // 1. Load order and validate exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + orderId, HttpStatus.NOT_FOUND));

        // 2. Verify order is in PAYMENT_SUCCESS status (required before assignment)
        if (order.getOrderStatus() != OrderStatus.PAYMENT_SUCCESS) {
            throw new BusinessException(
                    "Order must be in PAYMENT_SUCCESS status before agent assignment. Current status: " + order.getOrderStatus(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // 3. Get delivery location coordinates
        double deliveryLatitude = order.getDeliveryLatitude();
        double deliveryLongitude = order.getDeliveryLongitude();

        // 4. Query Redis GEO index for nearby online agents
        List<UUID> nearbyAgentIds = agentGeoService.findNearbyAgents(
                deliveryLatitude,
                deliveryLongitude,
                ASSIGNMENT_SEARCH_RADIUS_KM
        );

        if (nearbyAgentIds.isEmpty()) {
            log.warn("No nearby agents found for order {} within {}km radius", orderId, ASSIGNMENT_SEARCH_RADIUS_KM);
            throw new BusinessException(
                    "No available agents found in delivery area. Please try again later.",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        // 5. Iterate through nearby agents and select best qualified candidate
        UUID selectedAgentId = null;
        AgentProfile selectedAgentProfile = null;
        User selectedAgent = null;

        for (UUID agentId : nearbyAgentIds) {
            // Load agent profile with pessimistic write lock to prevent concurrent assignment race conditions
            AgentProfile profile = agentRepository.findByUserIdForUpdate(agentId)
                    .orElse(null);

            if (profile == null) {
                continue;
            }

            // Apply selection filters: APPROVED verification status + available flag
            if (profile.getVerificationStatus() != VerificationStatus.APPROVED) {
                continue;
            }

            if (!profile.isAvailable()) {
                continue;
            }

            // Load user entity for logging and auditing
            User agent = userRepository.findById(agentId)
                    .orElse(null);

            if (agent == null || agent.getRole() != Role.AGENT) {
                log.debug("Skipping agent {} - user not found or invalid role", agentId);
                continue;
            }

            // First qualified agent is the best (ordered by nearest distance from Redis)
            selectedAgentId = agentId;
            selectedAgentProfile = profile;
            selectedAgent = agent;
                break;
        }

        // 6. Verify at least one qualified agent was found
        if (selectedAgentId == null) {
            log.warn("No qualified agents found for order {} after filtering", orderId);
            // TODO: Automated background retry worker will pick up orders in UNASSIGNED audit state for periodic retry
            AssignmentAudit audit = AssignmentAudit.builder()
                    .orderId(orderId)
                    .actionType("UNASSIGNED")
                    .description("No available qualified agents in delivery area during payment success auto-assignment")
                    .triggeredBy("PAYMENT_SUCCESS_SYSTEM")
                    .build();
            assignmentAuditRepository.save(audit);
            return;
        }

        // Lock agent availability to prevent concurrent double-booking
        selectedAgentProfile.setAvailable(false);
        agentRepository.save(selectedAgentProfile);

        // 7. Create AssignmentHistory record for audit trail
        AssignmentHistory history = AssignmentHistory.builder()
                .orderId(orderId)
                .agent(selectedAgent)
                .assignmentTime(OffsetDateTime.now())
                .build();
        assignmentHistoryRepository.save(history);

        // 8. Create AssignmentAudit record for compliance tracking
        AssignmentAudit audit = AssignmentAudit.builder()
                .orderId(orderId)
                .actionType("ASSIGNED")
                .description(String.format("Agent auto-assigned via payment success: %s (%s)",
                        selectedAgent.getEmail(), selectedAgent.getId()))
                .triggeredBy("PAYMENT_SUCCESS_SYSTEM")
                .build();
        assignmentAuditRepository.save(audit);

        // 9. Update order status to ASSIGNED via OrderService state machine
        //    This ensures the strict state machine validation is enforced
        com.hyperlofy.backend.order.dto.OrderStatusUpdateRequest statusRequest =
                com.hyperlofy.backend.order.dto.OrderStatusUpdateRequest.builder()
                        .nextStatus(OrderStatus.ASSIGNED)
                        .agentId(selectedAgentId)
                        .build();

        orderService.updateOrderStatus(orderId, statusRequest);
        log.info("Order {} successfully assigned to agent {} after PAYMENT_SUCCESS", orderId, selectedAgent.getEmail());
    }
}
