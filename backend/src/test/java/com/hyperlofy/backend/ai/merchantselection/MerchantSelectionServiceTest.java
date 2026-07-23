package com.hyperlofy.backend.ai.merchantselection;

import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionMemoryEnhancer;
import com.hyperlofy.backend.zone.entity.Zone;
import com.hyperlofy.backend.zone.repository.ZoneRepository;
import com.hyperlofy.backend.zone.service.GeoLocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MerchantSelectionServiceTest {

    @Test
    void createsWeightedSelectionPlanForEligibleZones() {
        ZoneRepository zoneRepository = Mockito.mock(ZoneRepository.class);
        GeoLocationService geoLocationService = Mockito.mock(GeoLocationService.class);
        MerchantSelectionFactory factory = new MerchantSelectionFactory(List.of(new DistanceMerchantRankingStrategy()));
        MerchantSelectionMemoryEnhancer memoryEnhancer = Mockito.mock(MerchantSelectionMemoryEnhancer.class);
        Mockito.when(memoryEnhancer.boostMerchantScore(Mockito.any(), Mockito.any(), Mockito.anyList())).thenReturn(0.0);
        MerchantSelectionService service = new MerchantSelectionService(zoneRepository, geoLocationService, factory, memoryEnhancer);

        Zone nearZone = Zone.builder()
                .name("Near Zone")
                .centerLatitude(12.91)
                .centerLongitude(77.54)
                .radiusKm(10.0)
                .active(true)
                .build();

        Zone farZone = Zone.builder()
                .name("Far Zone")
                .centerLatitude(12.93)
                .centerLongitude(77.58)
                .radiusKm(7.0)
                .active(true)
                .build();

        Mockito.when(zoneRepository.findByActiveTrue()).thenReturn(List.of(nearZone, farZone));
        Mockito.when(geoLocationService.isWithinRadius(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble()))
                .thenReturn(true);
        Mockito.when(geoLocationService.calculateDistanceKm(12.98, 77.59, 12.91, 77.54)).thenReturn(2.0);
        Mockito.when(geoLocationService.calculateDistanceKm(12.98, 77.59, 12.93, 77.58)).thenReturn(5.5);

        OrderDraft draft = OrderDraft.builder()
                .draftId(UUID.randomUUID())
                .conversationId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .intent("GROCERY")
                .build();

        MerchantSelectionResponse response = service.select(MerchantSelectionRequest.builder()
                .draft(draft)
                .latitude(12.98)
                .longitude(77.59)
                .build());

        assertTrue(response.isSuccess());
        assertNotNull(response.getPlan());
        assertNotNull(response.getPlan().getSummary());
        assertEquals(2, response.getPlan().getSummary().getCandidateCount());
        assertEquals(1, response.getPlan().getSummary().getSelectedCount());
        assertTrue(response.getPlan().getSelectionScore() > 0.0);
        assertEquals("Near Zone", response.getPlan().getSelectedMerchants().get(0).getMerchantName());
    }
}
