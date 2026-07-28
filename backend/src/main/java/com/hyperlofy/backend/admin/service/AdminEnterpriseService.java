package com.hyperlofy.backend.admin.service;

import com.hyperlofy.backend.admin.entity.AdminAgentWorkload;
import com.hyperlofy.backend.admin.entity.AdminSessionAudit;
import com.hyperlofy.backend.admin.entity.AdminTask;
import com.hyperlofy.backend.admin.entity.AdminWorkflow;
import com.hyperlofy.backend.admin.repository.AdminAgentWorkloadRepository;
import com.hyperlofy.backend.admin.repository.AdminSessionAuditRepository;
import com.hyperlofy.backend.admin.repository.AdminTaskRepository;
import com.hyperlofy.backend.admin.repository.AdminWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(AdminEnterpriseService.class);

    private final AdminWorkflowRepository workflowRepository;
    private final AdminTaskRepository taskRepository;
    private final AdminAgentWorkloadRepository workloadRepository;
    private final AdminSessionAuditRepository sessionRepository;

    @Transactional
    public AdminWorkflow startWorkflow(String workflowName, String triggerEvent, String initialStep) {
        log.info("[ADMIN ENTERPRISE] Registering automated operational workflow Name={}, Trigger={}, InitialStep={}",
                workflowName, triggerEvent, initialStep);

        AdminWorkflow workflow = AdminWorkflow.builder()
                .workflowName(workflowName)
                .triggerEvent(triggerEvent)
                .currentStep(initialStep)
                .status("ACTIVE")
                .slaHours(24)
                .build();

        return workflowRepository.save(workflow);
    }

    @Transactional
    public AdminTask assignSupportTask(UUID workflowId, String title, String agentUser, String priority) {
        log.info("[ADMIN ENTERPRISE] Skill-based task assignment: Title={}, Agent={}, Priority={}", title, agentUser, priority);

        String taskNo = "TSK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        AdminTask task = AdminTask.builder()
                .taskNumber(taskNo)
                .workflowId(workflowId)
                .title(title)
                .assignedAgent(agentUser)
                .priority(priority != null ? priority : "MEDIUM")
                .status("IN_PROGRESS")
                .dueAt(ZonedDateTime.now().plusHours(24))
                .build();

        // Increment active workload count for assigned agent
        AdminAgentWorkload workload = workloadRepository.findByAgentUser(agentUser).orElseGet(() ->
                AdminAgentWorkload.builder().agentUser(agentUser).build()
        );
        workload.setActiveCasesCount(workload.getActiveCasesCount() + 1);
        workloadRepository.save(workload);

        return taskRepository.save(task);
    }

    @Transactional
    public AdminSessionAudit recordSessionAudit(String adminUser, String ipAddress, String privilegeLevel) {
        log.info("[ADMIN ENTERPRISE] Auditing administrative session: User={}, IP={}, Privilege={}", adminUser, ipAddress, privilegeLevel);

        AdminSessionAudit session = AdminSessionAudit.builder()
                .adminUser(adminUser)
                .ipAddress(ipAddress)
                .sessionStatus("ACTIVE")
                .privilegeLevel(privilegeLevel != null ? privilegeLevel : "SUPER_ADMIN")
                .build();

        return sessionRepository.save(session);
    }
}
