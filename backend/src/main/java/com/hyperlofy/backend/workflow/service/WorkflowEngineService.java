package com.hyperlofy.backend.workflow.service;

import com.hyperlofy.backend.workflow.entity.*;
import com.hyperlofy.backend.workflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowEngineService.class);

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowTaskRepository taskRepository;
    private final WorkflowHistoryRepository historyRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1: Workflow Definition Management
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowDefinition registerWorkflowDefinition(String workflowKey, String workflowName,
            String workflowType, String description, Integer timeoutHours, Integer retryLimit) {

        log.info("[WORKFLOW ENGINE] Registering workflow definition Key={}, Type={}, Timeout={}h",
                workflowKey, workflowType, timeoutHours);

        return definitionRepository.findByWorkflowKey(workflowKey).orElseGet(() -> {
            WorkflowDefinition def = WorkflowDefinition.builder()
                    .workflowKey(workflowKey)
                    .workflowName(workflowName)
                    .workflowType(workflowType)
                    .description(description)
                    .timeoutHours(timeoutHours != null ? timeoutHours : 72)
                    .retryLimit(retryLimit != null ? retryLimit : 3)
                    .isActive(true)
                    .version(1)
                    .build();
            return definitionRepository.save(def);
        });
    }

    @Transactional(readOnly = true)
    public List<WorkflowDefinition> getAllDefinitions() {
        return definitionRepository.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2: Workflow Instance Execution (Process State Machine)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowInstance startWorkflow(UUID definitionId, String instanceRef,
            UUID initiatorUserId, UUID tenantId, String priority,
            String correlationKey, String businessContext, Integer timeoutHours) {

        WorkflowDefinition definition = definitionRepository.findById(definitionId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found: " + definitionId));

        log.info("[WORKFLOW ENGINE] Starting workflow instance DefinitionId={}, Ref={}, Initiator={}, Priority={}",
                definitionId, instanceRef, initiatorUserId, priority);

        WorkflowInstance instance = WorkflowInstance.builder()
                .definition(definition)
                .instanceRef(instanceRef)
                .initiatorUserId(initiatorUserId)
                .tenantId(tenantId)
                .currentState("PENDING")
                .priority(priority != null ? priority : "NORMAL")
                .correlationKey(correlationKey)
                .businessContext(businessContext)
                .dueAt(OffsetDateTime.now().plusHours(timeoutHours != null ? timeoutHours : definition.getTimeoutHours()))
                .retryCount(0)
                .build();

        instance = instanceRepository.save(instance);

        // Emit STARTED audit history entry
        recordHistory(instance, null, "STARTED", null, "PENDING", initiatorUserId, "Workflow instance started");

        // Automatically create first human approval task
        createHumanTask(instance, definition.getWorkflowName() + " — Initial Review",
                "HUMAN_APPROVAL", "APPROVAL_TEAM", priority);

        return instance;
    }

    @Transactional
    public WorkflowInstance approveWorkflow(UUID instanceId, UUID actorUserId, String comment) {
        WorkflowInstance instance = requireInstance(instanceId);
        String prevState = instance.getCurrentState();

        log.info("[WORKFLOW ENGINE] Approving workflow InstanceId={}, Actor={}", instanceId, actorUserId);

        instance.setCurrentState("APPROVED");
        instance.setCompletedAt(OffsetDateTime.now());
        instanceRepository.save(instance);

        completePendingTasks(instance, actorUserId, "APPROVED — " + comment);
        recordHistory(instance, null, "APPROVED", prevState, "APPROVED", actorUserId, comment);

        return instance;
    }

    @Transactional
    public WorkflowInstance rejectWorkflow(UUID instanceId, UUID actorUserId, String comment) {
        WorkflowInstance instance = requireInstance(instanceId);
        String prevState = instance.getCurrentState();

        log.info("[WORKFLOW ENGINE] Rejecting workflow InstanceId={}, Actor={}", instanceId, actorUserId);

        instance.setCurrentState("REJECTED");
        instance.setCompletedAt(OffsetDateTime.now());
        instanceRepository.save(instance);

        completePendingTasks(instance, actorUserId, "REJECTED — " + comment);
        recordHistory(instance, null, "REJECTED", prevState, "REJECTED", actorUserId, comment);

        return instance;
    }

    @Transactional
    public WorkflowInstance cancelWorkflow(UUID instanceId, UUID actorUserId, String comment) {
        WorkflowInstance instance = requireInstance(instanceId);
        String prevState = instance.getCurrentState();

        log.info("[WORKFLOW ENGINE] Cancelling workflow InstanceId={}, Actor={}", instanceId, actorUserId);

        instance.setCurrentState("CANCELLED");
        instance.setCompletedAt(OffsetDateTime.now());
        instanceRepository.save(instance);

        completePendingTasks(instance, actorUserId, "CANCELLED — " + comment);
        recordHistory(instance, null, "CANCELLED", prevState, "CANCELLED", actorUserId, comment);

        return instance;
    }

    @Transactional
    public WorkflowInstance executeCompensation(UUID instanceId, UUID actorUserId) {
        WorkflowInstance instance = requireInstance(instanceId);
        String prevState = instance.getCurrentState();

        log.info("[WORKFLOW ENGINE] Executing Saga compensation rollback InstanceId={}", instanceId);

        instance.setCurrentState("COMPENSATED");
        instance.setCompletedAt(OffsetDateTime.now());
        instanceRepository.save(instance);

        // Create compensation task as SAGA_STEP
        createCompensationTask(instance);
        recordHistory(instance, null, "COMPENSATED", prevState, "COMPENSATED", actorUserId,
                "Saga compensation executed — distributed transaction rolled back");

        return instance;
    }

    @Transactional(readOnly = true)
    public WorkflowInstance getWorkflowInstance(UUID instanceId) {
        return requireInstance(instanceId);
    }

    @Transactional(readOnly = true)
    public List<WorkflowInstance> getInstancesByState(String state) {
        return instanceRepository.findByCurrentState(state);
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistory> getAuditTrail(UUID instanceId) {
        return historyRepository.findByInstance_IdOrderByCreatedAtAsc(instanceId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3: Human Task Management
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowTask claimTask(UUID taskId, UUID claimantUserId) {
        WorkflowTask task = requireTask(taskId);

        log.info("[WORKFLOW ENGINE] Claiming human task TaskId={}, Claimant={}", taskId, claimantUserId);

        task.setAssigneeUserId(claimantUserId);
        task.setStatus("CLAIMED");
        task.setClaimedAt(OffsetDateTime.now());
        taskRepository.save(task);

        recordHistory(task.getInstance(), task, "TASK_CLAIMED", "PENDING", "CLAIMED", claimantUserId,
                "Task claimed by user " + claimantUserId);

        return task;
    }

    @Transactional
    public WorkflowTask completeTask(UUID taskId, UUID actorUserId, String completionReason) {
        WorkflowTask task = requireTask(taskId);

        log.info("[WORKFLOW ENGINE] Completing task TaskId={}, Actor={}", taskId, actorUserId);

        task.setStatus("COMPLETED");
        task.setCompletedAt(OffsetDateTime.now());
        task.setCompletionReason(completionReason);
        taskRepository.save(task);

        recordHistory(task.getInstance(), task, "TASK_COMPLETED", "CLAIMED", "COMPLETED", actorUserId, completionReason);

        // Advance workflow state
        WorkflowInstance instance = task.getInstance();
        if ("PENDING".equals(instance.getCurrentState()) || "WAITING_APPROVAL".equals(instance.getCurrentState())) {
            instance.setCurrentState("IN_PROGRESS");
            instanceRepository.save(instance);
            recordHistory(instance, task, "STATE_CHANGED", "PENDING", "IN_PROGRESS", actorUserId, "Task completed — advancing state");
        }

        return task;
    }

    @Transactional
    public WorkflowTask delegateTask(UUID taskId, UUID fromUserId, UUID toUserId, String comment) {
        WorkflowTask task = requireTask(taskId);

        log.info("[WORKFLOW ENGINE] Delegating task TaskId={}, From={}, To={}", taskId, fromUserId, toUserId);

        task.setAssigneeUserId(toUserId);
        task.setStatus("DELEGATED");
        taskRepository.save(task);

        recordHistory(task.getInstance(), task, "TASK_DELEGATED", null, null, fromUserId,
                "Delegated to " + toUserId + ": " + comment);

        return task;
    }

    @Transactional(readOnly = true)
    public List<WorkflowTask> getMyTasks(UUID userId) {
        return taskRepository.findByAssigneeUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<WorkflowTask> getAllPendingTasks() {
        return taskRepository.findByStatus("PENDING");
    }

    @Transactional(readOnly = true)
    public List<WorkflowTask> getTasksByInstance(UUID instanceId) {
        return taskRepository.findByInstance_Id(instanceId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private WorkflowInstance requireInstance(UUID id) {
        return instanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found: " + id));
    }

    private WorkflowTask requireTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow task not found: " + id));
    }

    private WorkflowTask createHumanTask(WorkflowInstance instance, String taskName,
            String taskType, String candidateGroup, String priority) {
        WorkflowTask task = WorkflowTask.builder()
                .instance(instance)
                .taskName(taskName)
                .taskType(taskType)
                .candidateGroup(candidateGroup)
                .priority(priority != null ? priority : "NORMAL")
                .status("PENDING")
                .dueAt(instance.getDueAt())
                .isCompensation(false)
                .build();
        return taskRepository.save(task);
    }

    private WorkflowTask createCompensationTask(WorkflowInstance instance) {
        WorkflowTask task = WorkflowTask.builder()
                .instance(instance)
                .taskName("SAGA_COMPENSATION — " + instance.getDefinition().getWorkflowName())
                .taskType("SAGA_STEP")
                .status("COMPLETED")
                .priority("CRITICAL")
                .isCompensation(true)
                .completedAt(OffsetDateTime.now())
                .completionReason("Distributed transaction compensation executed")
                .build();
        return taskRepository.save(task);
    }

    private void completePendingTasks(WorkflowInstance instance, UUID actorUserId, String reason) {
        taskRepository.findByInstance_Id(instance.getId()).stream()
                .filter(t -> "PENDING".equals(t.getStatus()) || "CLAIMED".equals(t.getStatus()))
                .forEach(t -> {
                    t.setStatus("COMPLETED");
                    t.setCompletedAt(OffsetDateTime.now());
                    t.setCompletionReason(reason);
                    taskRepository.save(t);
                });
    }

    private void recordHistory(WorkflowInstance instance, WorkflowTask task,
            String action, String fromState, String toState,
            UUID actorUserId, String comment) {
        WorkflowHistory history = WorkflowHistory.builder()
                .instance(instance)
                .task(task)
                .action(action)
                .fromState(fromState)
                .toState(toState)
                .actorUserId(actorUserId)
                .comment(comment)
                .build();
        historyRepository.save(history);
    }
}
