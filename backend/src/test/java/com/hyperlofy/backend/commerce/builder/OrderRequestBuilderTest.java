package com.hyperlofy.backend.commerce.builder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantCandidate;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionPlan;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionResponse;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraftItem;
import com.hyperlofy.backend.ai.planner.PlanningResponse;
import com.hyperlofy.backend.catalog.dto.ProductDto;
import com.hyperlofy.backend.catalog.service.ProductService;
import com.hyperlofy.backend.inventory.service.InventoryService;
import com.hyperlofy.backend.order.dto.OrderItemDto;
import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationResponse;
import com.hyperlofy.backend.zone.service.ZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRequestBuilderTest {

    @Mock
    private com.hyperlofy.backend.ai.merchantselection.MerchantSelectionService merchantSelectionService;

    @Mock
    private ProductService productService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ZoneService zoneService;

    @InjectMocks
    private OrderRequestBuilderService builder;

    private UUID productId;
    private UUID merchantZoneId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        merchantZoneId = UUID.randomUUID();
    }

    // --- helpers ---

    private OrderDraft draftWithItems(OrderDraftItem... items) {
        return OrderDraft.builder()
                .draftId(UUID.randomUUID())
                .conversationId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .items(List.of(items))
                .build();
    }

    private PlanningResponse planningWithMerchant(UUID merchantId, String merchantName, double lat, double lon) {
        MerchantCandidate candidate = MerchantCandidate.builder()
                .merchantId(merchantId)
                .merchantName(merchantName)
                .latitude(lat)
                .longitude(lon)
                .build();
        MerchantSelectionPlan plan = MerchantSelectionPlan.builder()
                .planId(UUID.randomUUID())
                .draftId(UUID.randomUUID())
                .selectedMerchants(List.of(candidate))
                .build();
        MerchantSelectionResponse sel = MerchantSelectionResponse.builder()
                .success(true)
                .plan(plan)
                .build();
        return PlanningResponse.builder()
                .success(true)
                .merchantSelection(sel)
                .build();
    }

    private void stubCatalog(UUID merchantId, ProductDto... products) {
        when(productService.findByMerchantId(merchantId)).thenReturn(List.of(products));
    }

    private void stubDeliveryFee(BigDecimal fee) {
        DeliveryFeeCalculationResponse feeResp = DeliveryFeeCalculationResponse.builder()
                .deliveryFee(fee)
                .distanceKm(1.0)
                .zoneId(merchantZoneId)
                .zoneName("Test Zone")
                .withinZoneBounds(true)
                .build();
        when(zoneService.calculateDeliveryFee(any())).thenReturn(feeResp);
    }

    // --- tests ---

    @Test
    void productFoundInventoryAvailable() {
        OrderDraftItem item = OrderDraftItem.builder().itemName("SKU-1").quantity(2).estimatedPrice(BigDecimal.valueOf(10)).build();
        OrderDraft draft = draftWithItems(item);
        PlanningResponse planning = planningWithMerchant(merchantZoneId, "Test Store", 12.34, 56.78);

        ProductDto pd = ProductDto.builder().id(productId).merchantId(merchantZoneId).sku("SKU-1").name("Test Product").price(BigDecimal.valueOf(12.50)).build();
        stubCatalog(merchantZoneId, pd);
        when(inventoryService.getAvailableQuantity(merchantZoneId, productId)).thenReturn(10);
        when(inventoryService.isAvailable(merchantZoneId, productId, 2)).thenReturn(true);
        stubDeliveryFee(BigDecimal.valueOf(5.00));

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        assertNotNull(res.getOrderRequest());
        assertEquals(1, res.getAvailableItems().size());
        assertEquals(0, res.getUnavailableItems().size());
        assertEquals(0, res.getMissingProducts().size());

        OrderRequestBuilderResult.Item built = res.getAvailableItems().get(0);
        assertEquals(productId, built.getProductId());
        assertEquals("SKU-1", built.getSku());
        assertEquals(BigDecimal.valueOf(12.50), built.getUnitPrice());
        assertEquals(BigDecimal.valueOf(25.00).setScale(2), built.getEstimatedPrice().setScale(2));

        OrderItemDto itemDto = res.getOrderRequest().getItems().get(0);
        assertEquals(productId, itemDto.getProductId());
        assertEquals("SKU-1", itemDto.getSku());
        assertEquals("Test Product", itemDto.getProductName());
        assertEquals(merchantZoneId, itemDto.getMerchantId());
        assertEquals(2, itemDto.getQuantity());
        assertEquals(10, itemDto.getAvailableQuantity());
        assertTrue(itemDto.getAvailable());
        assertEquals(BigDecimal.valueOf(25.00).setScale(2), itemDto.getEstimatedPrice().setScale(2));
        assertEquals(BigDecimal.valueOf(25.00).setScale(2), itemDto.getSubtotal().setScale(2));
        assertTrue(itemDto.getWarnings().isEmpty());

        assertEquals(BigDecimal.valueOf(25.00).setScale(2), res.getSubtotal().setScale(2));
        assertEquals(BigDecimal.valueOf(5.00).setScale(2), res.getDeliveryFee().setScale(2));
        assertEquals(BigDecimal.valueOf(30.00).setScale(2), res.getEstimatedTotal().setScale(2));
    }

    @Test
    void productMissing() {
        OrderDraftItem item = OrderDraftItem.builder().itemName("UnknownProduct").quantity(1).estimatedPrice(BigDecimal.valueOf(5)).build();
        OrderDraft draft = draftWithItems(item);
        PlanningResponse planning = planningWithMerchant(merchantZoneId, "Test Store", 12.34, 56.78);

        stubCatalog(merchantZoneId); // empty catalog
        stubDeliveryFee(BigDecimal.valueOf(5.00));

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        assertEquals(0, res.getAvailableItems().size());
        assertEquals(1, res.getUnavailableItems().size());
        assertEquals(1, res.getMissingProducts().size());
        assertTrue(res.getMissingProducts().contains("UnknownProduct"));
        assertTrue(res.getWarnings().stream().anyMatch(w -> w.contains("Product not found")));
    }

    @Test
    void inventoryUnavailable() {
        OrderDraftItem item = OrderDraftItem.builder().itemName("SKU-1").quantity(5).estimatedPrice(BigDecimal.valueOf(10)).build();
        OrderDraft draft = draftWithItems(item);
        PlanningResponse planning = planningWithMerchant(merchantZoneId, "Test Store", 12.34, 56.78);

        ProductDto pd = ProductDto.builder().id(productId).merchantId(merchantZoneId).sku("SKU-1").name("Test Product").price(BigDecimal.valueOf(12.50)).build();
        stubCatalog(merchantZoneId, pd);
        when(inventoryService.getAvailableQuantity(merchantZoneId, productId)).thenReturn(2);
        when(inventoryService.isAvailable(merchantZoneId, productId, 5)).thenReturn(false);
        stubDeliveryFee(BigDecimal.valueOf(5.00));

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        assertEquals(0, res.getAvailableItems().size());
        assertEquals(1, res.getUnavailableItems().size());
        assertTrue(res.getWarnings().stream().anyMatch(w -> w.contains("Insufficient inventory")));
        assertEquals(BigDecimal.ZERO.setScale(2), res.getSubtotal().setScale(2)); // unavailable items excluded from subtotal
    }

    @Test
    void multipleItemsMixedAvailability() {
        OrderDraftItem item1 = OrderDraftItem.builder().itemName("SKU-1").quantity(2).estimatedPrice(BigDecimal.valueOf(10)).build();
        OrderDraftItem item2 = OrderDraftItem.builder().itemName("SKU-2").quantity(1).estimatedPrice(BigDecimal.valueOf(8)).build();
        OrderDraft draft = draftWithItems(item1, item2);
        PlanningResponse planning = planningWithMerchant(merchantZoneId, "Test Store", 12.34, 56.78);

        UUID productId2 = UUID.randomUUID();
        ProductDto pd1 = ProductDto.builder().id(productId).merchantId(merchantZoneId).sku("SKU-1").name("Product One").price(BigDecimal.valueOf(12.50)).build();
        ProductDto pd2 = ProductDto.builder().id(productId2).merchantId(merchantZoneId).sku("SKU-2").name("Product Two").price(BigDecimal.valueOf(8.00)).build();
        stubCatalog(merchantZoneId, pd1, pd2);

        when(inventoryService.getAvailableQuantity(merchantZoneId, productId)).thenReturn(10);
        when(inventoryService.isAvailable(merchantZoneId, productId, 2)).thenReturn(true);
        when(inventoryService.getAvailableQuantity(merchantZoneId, productId2)).thenReturn(0);
        when(inventoryService.isAvailable(merchantZoneId, productId2, 1)).thenReturn(false);
        stubDeliveryFee(BigDecimal.valueOf(5.00));

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        assertEquals(1, res.getAvailableItems().size());
        assertEquals(1, res.getUnavailableItems().size());
        assertEquals(0, res.getMissingProducts().size());

        // subtotal only from available items
        assertEquals(BigDecimal.valueOf(25.00).setScale(2), res.getSubtotal().setScale(2));
        assertEquals(BigDecimal.valueOf(5.00).setScale(2), res.getDeliveryFee().setScale(2));
        assertEquals(BigDecimal.valueOf(30.00).setScale(2), res.getEstimatedTotal().setScale(2));
    }

    @Test
    void subtotalCalculation() {
        OrderDraftItem item1 = OrderDraftItem.builder().itemName("SKU-1").quantity(3).estimatedPrice(BigDecimal.valueOf(10)).build();
        OrderDraftItem item2 = OrderDraftItem.builder().itemName("SKU-2").quantity(2).estimatedPrice(BigDecimal.valueOf(5)).build();
        OrderDraft draft = draftWithItems(item1, item2);
        PlanningResponse planning = planningWithMerchant(merchantZoneId, "Test Store", 12.34, 56.78);

        UUID productId2 = UUID.randomUUID();
        ProductDto pd1 = ProductDto.builder().id(productId).merchantId(merchantZoneId).sku("SKU-1").name("P1").price(BigDecimal.valueOf(10.00)).build();
        ProductDto pd2 = ProductDto.builder().id(productId2).merchantId(merchantZoneId).sku("SKU-2").name("P2").price(BigDecimal.valueOf(5.00)).build();
        stubCatalog(merchantZoneId, pd1, pd2);

        when(inventoryService.getAvailableQuantity(merchantZoneId, productId)).thenReturn(10);
        when(inventoryService.isAvailable(merchantZoneId, productId, 3)).thenReturn(true);
        when(inventoryService.getAvailableQuantity(merchantZoneId, productId2)).thenReturn(10);
        when(inventoryService.isAvailable(merchantZoneId, productId2, 2)).thenReturn(true);
        stubDeliveryFee(BigDecimal.valueOf(7.50));

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        // 3*10 + 2*5 = 40
        assertEquals(BigDecimal.valueOf(40.00).setScale(2), res.getSubtotal().setScale(2));
        assertEquals(BigDecimal.valueOf(7.50).setScale(2), res.getDeliveryFee().setScale(2));
        assertEquals(BigDecimal.valueOf(47.50).setScale(2), res.getEstimatedTotal().setScale(2));
    }

    @Test
    void deliveryFeeCalculation() {
        OrderDraftItem item = OrderDraftItem.builder().itemName("SKU-1").quantity(1).estimatedPrice(BigDecimal.valueOf(10)).build();
        OrderDraft draft = draftWithItems(item);
        PlanningResponse planning = planningWithMerchant(merchantZoneId, "Test Store", 12.34, 56.78);

        ProductDto pd = ProductDto.builder().id(productId).merchantId(merchantZoneId).sku("SKU-1").name("P1").price(BigDecimal.valueOf(10.00)).build();
        stubCatalog(merchantZoneId, pd);
        when(inventoryService.getAvailableQuantity(merchantZoneId, productId)).thenReturn(5);
        when(inventoryService.isAvailable(merchantZoneId, productId, 1)).thenReturn(true);
        stubDeliveryFee(BigDecimal.valueOf(12.34));

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        assertEquals(BigDecimal.valueOf(12.34).setScale(2), res.getDeliveryFee().setScale(2));
        assertEquals(BigDecimal.valueOf(22.34).setScale(2), res.getEstimatedTotal().setScale(2));
    }

    @Test
    void warningsCollected() {
        OrderDraftItem item = OrderDraftItem.builder().itemName("Unknown").quantity(1).estimatedPrice(BigDecimal.valueOf(5)).build();
        OrderDraft draft = draftWithItems(item);
        // no merchant selection
        PlanningResponse planning = PlanningResponse.builder().success(true).build();

        // no merchant -> falls through to productService.findAll()
        when(productService.findAll()).thenReturn(List.of());

        OrderRequestBuilderResult res = builder.build(draft, ConversationResponse.builder().build(), planning);

        assertTrue(res.getWarnings().stream().anyMatch(w -> w.contains("Merchant selection not available")));
        assertTrue(res.getWarnings().stream().anyMatch(w -> w.contains("Product not found")));
        assertTrue(res.getWarnings().stream().anyMatch(w -> w.contains("skipping delivery fee")));
    }
}
