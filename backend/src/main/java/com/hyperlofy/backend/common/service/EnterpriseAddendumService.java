package com.hyperlofy.backend.common.service;

import com.hyperlofy.backend.admin.entity.AdminNote;
import com.hyperlofy.backend.admin.repository.AdminNoteRepository;
import com.hyperlofy.backend.marketplace.entity.InventoryTransaction;
import com.hyperlofy.backend.marketplace.repository.InventoryTransactionRepository;
import com.hyperlofy.backend.merchant.entity.MerchantOnboardingChecklist;
import com.hyperlofy.backend.merchant.repository.MerchantOnboardingChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnterpriseAddendumService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseAddendumService.class);

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AdminNoteRepository adminNoteRepository;
    private final MerchantOnboardingChecklistRepository checklistRepository;

    @Transactional
    public InventoryTransaction recordInventoryTransaction(UUID variantId, String type, int qtyChanged, int before, int after, String reason, UUID adminId) {
        log.info("Recording inventory transaction: variantId={}, type={}, qty={}", variantId, type, qtyChanged);
        InventoryTransaction tx = InventoryTransaction.builder()
                .variantId(variantId)
                .transactionType(type)
                .quantityChanged(qtyChanged)
                .stockBefore(before)
                .stockAfter(after)
                .reason(reason)
                .performedBy(adminId)
                .build();
        return inventoryTransactionRepository.save(tx);
    }

    @Transactional
    public AdminNote addAdminNote(UUID targetId, String targetType, String content, UUID adminId) {
        log.info("Adding admin note for targetId={}, type={}", targetId, targetType);
        AdminNote note = AdminNote.builder()
                .targetId(targetId)
                .targetType(targetType)
                .noteContent(content)
                .adminUserId(adminId)
                .build();
        return adminNoteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public List<AdminNote> getAdminNotes(UUID targetId, String targetType) {
        return adminNoteRepository.findByTargetIdAndTargetTypeOrderByCreatedAtDesc(targetId, targetType);
    }

    @Transactional(readOnly = true)
    public MerchantOnboardingChecklist getMerchantOnboardingChecklist(UUID merchantId) {
        return checklistRepository.findByMerchantId(merchantId).orElseGet(() ->
                MerchantOnboardingChecklist.builder()
                        .merchantId(merchantId)
                        .completionPercentage(20.0)
                        .build()
        );
    }
}
