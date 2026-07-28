package com.hyperlofy.backend.finance.service;

import com.hyperlofy.backend.finance.entity.FinanceAccountingPeriod;
import com.hyperlofy.backend.finance.entity.FinanceCreditNote;
import com.hyperlofy.backend.finance.entity.FinanceInvoice;
import com.hyperlofy.backend.finance.entity.FinanceInvoiceItem;
import com.hyperlofy.backend.finance.repository.FinanceAccountingPeriodRepository;
import com.hyperlofy.backend.finance.repository.FinanceCreditNoteRepository;
import com.hyperlofy.backend.finance.repository.FinanceInvoiceItemRepository;
import com.hyperlofy.backend.finance.repository.FinanceInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceBillingService {

    private static final Logger log = LoggerFactory.getLogger(FinanceBillingService.class);

    private final FinanceInvoiceRepository invoiceRepository;
    private final FinanceInvoiceItemRepository itemRepository;
    private final FinanceCreditNoteRepository creditNoteRepository;
    private final FinanceAccountingPeriodRepository periodRepository;

    @Transactional
    public FinanceInvoice generateCustomerInvoice(UUID orderId, UUID customerId, UUID merchantId, BigDecimal grossAmount, BigDecimal discountAmount) {
        log.info("[FINANCE ENGINE] Generating GST Tax Invoice for OrderId={}, CustomerId={}", orderId, customerId);

        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal taxableAmount = grossAmount.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        // Calculate GST Breakdown: 9% CGST + 9% SGST (Intra-state default)
        BigDecimal cgst = taxableAmount.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sgst = taxableAmount.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalTax = cgst.add(sgst);
        BigDecimal netPayable = taxableAmount.add(totalTax).setScale(2, RoundingMode.HALF_UP);

        String invoiceNo = "INV-" + ZonedDateTime.now().getYear() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        FinanceInvoice invoice = FinanceInvoice.builder()
                .invoiceNumber(invoiceNo)
                .orderId(orderId)
                .customerId(customerId)
                .merchantId(merchantId)
                .invoiceType("CUSTOMER_TAX_INVOICE")
                .grossAmount(grossAmount)
                .discountAmount(discount)
                .cgstAmount(cgst)
                .sgstAmount(sgst)
                .igstAmount(BigDecimal.ZERO)
                .totalTaxAmount(totalTax)
                .netPayableAmount(netPayable)
                .status("ISSUED")
                .build();

        FinanceInvoice saved = invoiceRepository.save(invoice);

        FinanceInvoiceItem item = FinanceInvoiceItem.builder()
                .invoiceId(saved.getId())
                .itemDescription("Hyperlocal Express Delivery & Order Fulfillment")
                .hsnSacCode("998313")
                .unitPrice(taxableAmount)
                .quantity(1)
                .subtotal(taxableAmount)
                .taxRate(new BigDecimal("18.00"))
                .taxAmount(totalTax)
                .build();

        itemRepository.save(item);
        return saved;
    }

    @Transactional
    public FinanceCreditNote issueCreditNote(UUID invoiceId, BigDecimal refundAmount, String reason) {
        log.info("[FINANCE ENGINE] Issuing Tax Credit Note for InvoiceId={}, Amount={}", invoiceId, refundAmount);

        FinanceInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        invoice.setStatus("PARTIALLY_REFUNDED");
        invoiceRepository.save(invoice);

        String cnNo = "CN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        FinanceCreditNote creditNote = FinanceCreditNote.builder()
                .creditNoteNumber(cnNo)
                .invoiceId(invoiceId)
                .reason(reason)
                .refundAmount(refundAmount)
                .status("ISSUED")
                .build();

        return creditNoteRepository.save(creditNote);
    }

    @Transactional
    public FinanceAccountingPeriod closeAccountingPeriod(String periodCode, String closedBy) {
        log.info("[FINANCE ENGINE] Closing accounting period: {}", periodCode);

        FinanceAccountingPeriod period = periodRepository.findByPeriodCode(periodCode).orElseGet(() ->
                FinanceAccountingPeriod.builder()
                        .periodCode(periodCode)
                        .startDate(LocalDate.now().withDayOfMonth(1))
                        .endDate(LocalDate.now())
                        .build()
        );

        period.setIsClosed(true);
        period.setClosedAt(ZonedDateTime.now());
        period.setClosedBy(closedBy);

        return periodRepository.save(period);
    }

    @Transactional(readOnly = true)
    public List<FinanceInvoice> getCustomerInvoices(UUID customerId) {
        return invoiceRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional(readOnly = true)
    public List<FinanceInvoice> getMerchantInvoices(UUID merchantId) {
        return invoiceRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);
    }
}
