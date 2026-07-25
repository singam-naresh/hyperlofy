package com.hyperlofy.backend.commerce.builder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantCandidate;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionService;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraftItem;
import com.hyperlofy.backend.ai.planner.PlanningResponse;
import com.hyperlofy.backend.catalog.dto.ProductDto;
import com.hyperlofy.backend.catalog.service.ProductService;
import com.hyperlofy.backend.inventory.service.InventoryService;
import com.hyperlofy.backend.order.dto.OrderItemDto;
import com.hyperlofy.backend.order.dto.OrderRequest;
import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationRequest;
import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationResponse;
import com.hyperlofy.backend.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderRequestBuilderService {

    private final MerchantSelectionService merchantSelectionService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final ZoneService zoneService;

    public OrderRequestBuilderResult build(OrderDraft draft, ConversationResponse conversation, PlanningResponse planning) {
        List<String> warnings = new ArrayList<>();

        if (draft == null) {
            warnings.add("Order draft is null");
            return OrderRequestBuilderResult.builder().warnings(warnings).build();
        }

        // --- 1. Resolve merchant ---
        MerchantSelectionResponse selection = planning != null ? planning.getMerchantSelection() : null;
        if (selection == null || !selection.isSuccess() || selection.getPlan() == null || selection.getPlan().getSelectedMerchants().isEmpty()) {
            warnings.add("Merchant selection not available in planning response");
        }

        UUID merchantId = null;
        String merchantName = null;
        Double storeLat = 0.0;
        Double storeLon = 0.0;

        if (selection != null && selection.isSuccess() && selection.getPlan() != null && !selection.getPlan().getSelectedMerchants().isEmpty()) {
            MerchantCandidate chosen = selection.getPlan().getSelectedMerchants().get(0);
            merchantId = chosen.getMerchantId();
            merchantName = chosen.getMerchantName();
            storeLat = chosen.getLatitude();
            storeLon = chosen.getLongitude();
        }

        // --- 2. Resolve products & check inventory ---
        List<OrderRequestBuilderResult.Item> availableItems = new ArrayList<>();
        List<OrderRequestBuilderResult.Item> unavailableItems = new ArrayList<>();
        List<OrderItemDto> itemDtos = new ArrayList<>();
        List<String> missingProducts = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        List<ProductDto> catalogCandidates = merchantId != null ? productService.findByMerchantId(merchantId) : productService.findAll();

        for (OrderDraftItem draftItem : draft.getItems()) {
            String itemName = draftItem.getItemName();
            int qty = draftItem.getQuantity();
            List<String> itemWarnings = new ArrayList<>();

            // --- 2a. Resolve product ---
            Optional<ProductDto> found = resolveProduct(catalogCandidates, itemName);

            UUID productId = null;
            String sku = null;
            String resolvedName = itemName;
            BigDecimal unitPrice = BigDecimal.ZERO;
            BigDecimal itemEstimatedPrice = BigDecimal.ZERO;
            boolean productResolved = false;

            if (found.isPresent()) {
                ProductDto pd = found.get();
                productId = pd.getId();
                sku = pd.getSku();
                resolvedName = pd.getName();
                unitPrice = pd.getPrice() != null ? pd.getPrice() : BigDecimal.ZERO;
                itemEstimatedPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
                productResolved = true;
            } else {
                missingProducts.add(itemName);
                itemWarnings.add("Product not found in catalog: " + itemName);
                // fallback to draft estimated price
                unitPrice = draftItem.getEstimatedPrice() != null ? draftItem.getEstimatedPrice() : BigDecimal.ZERO;
                itemEstimatedPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
            }

            // --- 2b. Check inventory availability ---
            int availableQty = 0;
            boolean itemAvailable = false;

            if (productResolved && merchantId != null) {
                availableQty = inventoryService.getAvailableQuantity(merchantId, productId);
                itemAvailable = inventoryService.isAvailable(merchantId, productId, qty);
            } else if (productResolved) {
                itemWarnings.add("Inventory check skipped: merchant not selected");
            } else {
                itemWarnings.add("Inventory check skipped: product not resolved");
            }

            if (productResolved && merchantId != null && !itemAvailable) {
                itemWarnings.add("Insufficient inventory for " + resolvedName + ": need " + qty + ", have " + availableQty);
            }

            // --- 2c. Build item DTO ---
            OrderItemDto itemDto = OrderItemDto.builder()
                    .id(productId)
                    .productId(productId)
                    .sku(sku)
                    .productName(resolvedName)
                    .itemName(resolvedName)
                    .merchantId(merchantId)
                    .quantity(qty)
                    .availableQuantity(availableQty)
                    .available(itemAvailable)
                    .estimatedPrice(itemEstimatedPrice)
                    .subtotal(itemEstimatedPrice)
                    .warnings(itemWarnings)
                    .itemStatus(itemAvailable ? "AVAILABLE" : "UNAVAILABLE")
                    .build();
            itemDtos.add(itemDto);

            // --- 2d. Build result item ---
            OrderRequestBuilderResult.Item resultItem = OrderRequestBuilderResult.Item.builder()
                    .productId(productId)
                    .sku(sku)
                    .productName(resolvedName)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .estimatedPrice(itemEstimatedPrice)
                    .build();

            if (itemAvailable) {
                availableItems.add(resultItem);
                subtotal = subtotal.add(itemEstimatedPrice);
            } else {
                unavailableItems.add(resultItem);
            }

            if (!itemWarnings.isEmpty()) {
                warnings.addAll(itemWarnings);
            }
        }

        // --- 3. Calculate delivery fee ---
        BigDecimal deliveryFee = BigDecimal.ZERO;

        if (merchantId != null) {
            try {
                DeliveryFeeCalculationRequest feeReq = DeliveryFeeCalculationRequest.builder()
                        .zoneId(merchantId)
                        .storeLatitude(storeLat == null ? 0.0 : storeLat)
                        .storeLongitude(storeLon == null ? 0.0 : storeLon)
                        .deliveryLatitude(storeLat == null ? 0.0 : storeLat)
                        .deliveryLongitude(storeLon == null ? 0.0 : storeLon)
                        .build();

                DeliveryFeeCalculationResponse feeResp = zoneService.calculateDeliveryFee(feeReq);
                if (feeResp != null && feeResp.getDeliveryFee() != null) {
                    deliveryFee = feeResp.getDeliveryFee();
                }
            } catch (Exception ex) {
                warnings.add("Failed to calculate delivery fee: " + ex.getMessage());
            }
        } else {
            warnings.add("Merchant/zone not selected; skipping delivery fee calculation");
        }

        // --- 4. Calculate estimated total ---
        BigDecimal estimatedTotal = subtotal.add(deliveryFee);

        // --- 5. Build OrderRequest ---
        OrderRequest orderRequest = OrderRequest.builder()
                .customerId(draft.getCustomerId())
                .zoneId(merchantId)
                .storeName(merchantName != null ? merchantName : "Hyperlofy Store")
                .storeLatitude(storeLat == null ? 0.0 : storeLat)
                .storeLongitude(storeLon == null ? 0.0 : storeLon)
                .deliveryAddress(draft.getDeliveryDetails() != null && draft.getDeliveryDetails().getDrop() != null ? draft.getDeliveryDetails().getDrop() : "Customer address")
                .deliveryLatitude(storeLat == null ? 0.0 : storeLat)
                .deliveryLongitude(storeLon == null ? 0.0 : storeLon)
                .itemsDesc(draft.getItems().stream().map(i -> i.getQuantity() + " x " + i.getItemName()).collect(Collectors.joining(", ")))
                .items(itemDtos)
                .build();

        return OrderRequestBuilderResult.builder()
                .orderRequest(orderRequest)
                .merchantSelection(selection)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .estimatedTotal(estimatedTotal)
                .warnings(warnings)
                .availableItems(availableItems)
                .unavailableItems(unavailableItems)
                .missingProducts(missingProducts)
                .items(availableItems) // legacy field: populated with available items
                .build();
    }

    private Optional<ProductDto> resolveProduct(List<ProductDto> candidates, String itemName) {
        // exact SKU match
        Optional<ProductDto> found = candidates.stream()
                .filter(p -> p.getSku() != null && p.getSku().equalsIgnoreCase(itemName))
                .findFirst();
        if (found.isPresent()) return found;

        // exact name match
        found = candidates.stream()
                .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(itemName))
                .findFirst();
        if (found.isPresent()) return found;

        // partial name match
        return candidates.stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(itemName.toLowerCase()))
                .findFirst();
    }
}
