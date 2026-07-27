package com.hyperlofy.backend.ai.recommendation.service;

import com.hyperlofy.backend.ai.recommendation.entity.CustomerBehaviourEvent;
import com.hyperlofy.backend.ai.recommendation.entity.CustomerPersonalizationProfile;
import com.hyperlofy.backend.ai.recommendation.repository.CustomerBehaviourEventRepository;
import com.hyperlofy.backend.ai.recommendation.repository.CustomerPersonalizationProfileRepository;
import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import com.hyperlofy.backend.merchant.repository.MerchantProfileRepository;
import com.hyperlofy.backend.platform.entity.Coupon;
import com.hyperlofy.backend.platform.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final CustomerBehaviourEventRepository behaviourRepository;
    private final CustomerPersonalizationProfileRepository personalizationRepository;
    private final MerchantProfileRepository merchantRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public void trackEvent(UUID userId, String eventType, UUID productId, UUID merchantId, UUID categoryId, String searchQuery) {
        CustomerBehaviourEvent event = CustomerBehaviourEvent.builder()
                .userId(userId)
                .eventType(eventType)
                .productId(productId)
                .merchantId(merchantId)
                .categoryId(categoryId)
                .searchQuery(searchQuery)
                .build();

        behaviourRepository.save(event);
        log.info("Recorded customer behaviour event: userId={}, eventType={}", userId, eventType);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "recommendations", key = "'merchants_' + #userId")
    public List<MerchantProfile> getRecommendedMerchants(UUID userId) {
        List<MerchantProfile> allMerchants = merchantRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                .collect(Collectors.toList());

        CustomerPersonalizationProfile profile = personalizationRepository.findByUserId(userId).orElse(null);
        if (profile != null && profile.getFavoriteMerchantIds() != null) {
            String favs = profile.getFavoriteMerchantIds();
            allMerchants.sort((m1, m2) -> {
                boolean m1Fav = favs.contains(m1.getId().toString());
                boolean m2Fav = favs.contains(m2.getId().toString());
                return Boolean.compare(m2Fav, m1Fav);
            });
        }

        return allMerchants.stream().limit(10).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "recommendations", key = "'coupons_' + #userId")
    public List<Coupon> getPersonalizedCoupons(UUID userId) {
        return couponRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTrendingItems() {
        Map<String, Object> trending = new HashMap<>();
        List<MerchantProfile> trendingMerchants = merchantRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                .limit(5)
                .collect(Collectors.toList());

        trending.put("trendingMerchants", trendingMerchants);
        trending.put("trendingSearches", List.of("Biryani", "Groceries", "Milk", "Pizza", "Pharmacy"));
        return trending;
    }
}
