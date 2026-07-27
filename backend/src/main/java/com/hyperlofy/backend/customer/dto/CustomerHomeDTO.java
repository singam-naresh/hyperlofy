package com.hyperlofy.backend.customer.dto;

import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import com.hyperlofy.backend.platform.entity.Banner;
import com.hyperlofy.backend.platform.entity.Coupon;
import com.hyperlofy.backend.platform.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer Home Experience Aggregated DTO")
public class CustomerHomeDTO {

    @Schema(description = "Active Marketing Banners")
    private List<Banner> banners;

    @Schema(description = "Product Categories Taxonomy")
    private List<ProductCategory> categories;

    @Schema(description = "Nearby Active Merchants")
    private List<MerchantProfile> nearbyMerchants;

    @Schema(description = "Featured Merchants")
    private List<MerchantProfile> featuredMerchants;

    @Schema(description = "Active Discount Coupons")
    private List<Coupon> activeCoupons;

    @Schema(description = "Estimated Average Delivery Time Minutes", example = "25")
    private Integer estimatedDeliveryTimeMinutes;
}
