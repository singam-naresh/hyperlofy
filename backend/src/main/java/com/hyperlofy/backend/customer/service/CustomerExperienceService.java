package com.hyperlofy.backend.customer.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.customer.dto.*;
import com.hyperlofy.backend.customer.entity.*;
import com.hyperlofy.backend.customer.repository.*;
import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import com.hyperlofy.backend.merchant.repository.MerchantProfileRepository;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.platform.entity.Banner;
import com.hyperlofy.backend.platform.entity.Coupon;
import com.hyperlofy.backend.platform.entity.ProductCategory;
import com.hyperlofy.backend.platform.repository.BannerRepository;
import com.hyperlofy.backend.platform.repository.CouponRepository;
import com.hyperlofy.backend.platform.repository.ProductCategoryRepository;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerExperienceService {

    private static final Logger log = LoggerFactory.getLogger(CustomerExperienceService.class);

    private final UserRepository userRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final OrderRepository orderRepository;
    private final BannerRepository bannerRepository;
    private final ProductCategoryRepository categoryRepository;
    private final CouponRepository couponRepository;
    private final PlatformAdministrationService platformAdministrationService;

    private final CustomerAddressRepository addressRepository;
    private final CustomerWishlistRepository wishlistRepository;
    private final CustomerCartRepository cartRepository;
    private final CustomerCartItemRepository cartItemRepository;
    private final CustomerReviewRepository reviewRepository;
    private final CustomerWalletRepository walletRepository;
    private final CustomerWalletTransactionRepository walletTransactionRepository;

    // --- MODULE 1: HOME EXPERIENCE ---
    @Transactional(readOnly = true)
    public CustomerHomeDTO getHomeExperience(UUID userId) {
        List<Banner> banners = bannerRepository.findByIsActiveOrderByPriorityOrderAsc(true);
        List<ProductCategory> categories = categoryRepository.findAll();
        List<MerchantProfile> merchants = merchantProfileRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                .collect(Collectors.toList());

        List<Coupon> coupons = couponRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .collect(Collectors.toList());

        CustomerHomeDTO dto = new CustomerHomeDTO();
        dto.setBanners(banners);
        dto.setCategories(categories);
        dto.setNearbyMerchants(merchants);
        dto.setFeaturedMerchants(merchants.stream().limit(5).collect(Collectors.toList()));
        dto.setActiveCoupons(coupons);
        dto.setEstimatedDeliveryTimeMinutes(25);

        return dto;
    }

    // --- MODULE 2: SEARCH PLATFORM ---
    @Transactional(readOnly = true)
    public Map<String, Object> search(String query, String category, Double maxDistance, int page, int size) {
        String term = (query != null) ? query.toLowerCase() : "";

        List<MerchantProfile> matchingStores = merchantProfileRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                .filter(m -> {
                    if (term.isEmpty()) return true;
                    return (m.getBusinessName() != null && m.getBusinessName().toLowerCase().contains(term));
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("matchingStores", matchingStores);
        result.put("totalStoresCount", matchingStores.size());
        return result;
    }

    // --- MODULE 3: WISHLIST ---
    @Transactional(readOnly = true)
    public List<CustomerWishlist> getWishlist(UUID userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public CustomerWishlist addToWishlist(UUID userId, UUID productId, UUID merchantId) {
        Optional<CustomerWishlist> existing = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) return existing.get();

        CustomerWishlist item = new CustomerWishlist();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setMerchantId(merchantId);
        item.setFolderName("FAVORITES");

        return wishlistRepository.save(item);
    }

    @Transactional
    public void removeFromWishlist(UUID userId, UUID wishlistId) {
        CustomerWishlist item = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new BusinessException("Wishlist item not found: " + wishlistId, HttpStatus.NOT_FOUND));

        if (!item.getUserId().equals(userId)) {
            throw new BusinessException("Unauthorized access to wishlist item", HttpStatus.FORBIDDEN);
        }
        wishlistRepository.delete(item);
    }

    // --- MODULE 4: SHOPPING CART ---
    @Transactional(readOnly = true)
    public CartSummaryDTO getCartSummary(UUID userId) {
        CustomerCart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CustomerCart newCart = new CustomerCart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        List<CustomerCartItem> items = cartItemRepository.findByCartId(cart.getId());
        BigDecimal subtotal = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = cart.getDiscountAmount() != null ? cart.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxes = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("30.00") : BigDecimal.ZERO;
        BigDecimal finalTotal = subtotal.subtract(discount).add(taxes).add(deliveryFee);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;

        CartSummaryDTO dto = new CartSummaryDTO();
        dto.setCartId(cart.getId());
        dto.setMerchantId(cart.getMerchantId());
        dto.setItems(items);
        dto.setItemSubtotal(subtotal);
        dto.setCouponCode(cart.getAppliedCouponCode());
        dto.setDiscountAmount(discount);
        dto.setEstimatedTaxes(taxes);
        dto.setDeliveryFee(deliveryFee);
        dto.setFinalTotal(finalTotal);

        return dto;
    }

    @Transactional
    public CartSummaryDTO addItemToCart(UUID userId, UUID merchantId, UUID productId, String productName, BigDecimal unitPrice, Integer quantity) {
        CustomerCart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CustomerCart newCart = new CustomerCart();
                    newCart.setUserId(userId);
                    newCart.setMerchantId(merchantId);
                    return cartRepository.save(newCart);
                });

        if (cart.getMerchantId() != null && !cart.getMerchantId().equals(merchantId)) {
            cartItemRepository.deleteByCartId(cart.getId());
            cart.setMerchantId(merchantId);
            cart.setAppliedCouponCode(null);
            cart.setDiscountAmount(BigDecimal.ZERO);
            cartRepository.save(cart);
        } else if (cart.getMerchantId() == null) {
            cart.setMerchantId(merchantId);
            cartRepository.save(cart);
        }

        Optional<CustomerCartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingItem.isPresent()) {
            CustomerCartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CustomerCartItem newItem = new CustomerCartItem();
            newItem.setCartId(cart.getId());
            newItem.setProductId(productId);
            newItem.setProductName(productName);
            newItem.setUnitPrice(unitPrice);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        return getCartSummary(userId);
    }

    // --- MODULE 5: CHECKOUT EXPERIENCE ---
    @Transactional(readOnly = true)
    public CheckoutPreviewDTO getCheckoutPreview(UUID userId, UUID addressId, boolean useWallet) {
        CartSummaryDTO cart = getCartSummary(userId);

        CustomerAddress address = null;
        if (addressId != null) {
            address = addressRepository.findById(addressId).orElse(null);
        } else {
            address = addressRepository.findByUserIdAndIsDefaultTrue(userId).orElse(null);
        }

        BigDecimal walletBalance = BigDecimal.ZERO;
        if (useWallet) {
            Optional<CustomerWallet> w = walletRepository.findByUserId(userId);
            if (w.isPresent()) walletBalance = w.get().getBalance();
        }

        BigDecimal walletDeduction = walletBalance.min(cart.getFinalTotal());
        BigDecimal payable = cart.getFinalTotal().subtract(walletDeduction);

        CheckoutPreviewDTO dto = new CheckoutPreviewDTO();
        dto.setDeliveryAddress(address);
        dto.setMerchantId(cart.getMerchantId());
        dto.setItemSubtotal(cart.getItemSubtotal());
        dto.setDiscountAmount(cart.getDiscountAmount());
        dto.setDeliveryFee(cart.getDeliveryFee());
        dto.setTaxesAndFees(cart.getEstimatedTaxes());
        dto.setWalletDeduction(walletDeduction);
        dto.setAmountPayable(payable);
        dto.setEscrowPlacementAmount(payable);
        dto.setEstimatedArrivalMinutes(30);

        return dto;
    }

    // --- MODULE 6: ADDRESS MANAGEMENT ---
    @Transactional(readOnly = true)
    public List<CustomerAddress> getAddresses(UUID userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public CustomerAddress saveAddress(UUID userId, CustomerAddress address) {
        address.setUserId(userId);
        if (address.isDefault()) {
            List<CustomerAddress> existing = addressRepository.findByUserId(userId);
            existing.forEach(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
        }
        return addressRepository.save(address);
    }

    // --- MODULE 7: CUSTOMER WALLET ---
    @Transactional(readOnly = true)
    public CustomerWallet getWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CustomerWallet w = new CustomerWallet();
                    w.setUserId(userId);
                    return walletRepository.save(w);
                });
    }

    @Transactional(readOnly = true)
    public List<CustomerWalletTransaction> getWalletTransactions(UUID userId) {
        CustomerWallet wallet = getWallet(userId);
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    // --- MODULE 8: ORDERS ---
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(UUID userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId);
    }

    // --- MODULE 9: REVIEWS ---
    @Transactional
    public CustomerReview submitReview(UUID userId, CustomerReview review) {
        review.setUserId(userId);
        return reviewRepository.save(review);
    }

    // --- MODULE 11: LOYALTY ---
    @Transactional(readOnly = true)
    public LoyaltyRewardsDTO getLoyaltyRewards(UUID userId) {
        CustomerWallet wallet = getWallet(userId);

        LoyaltyRewardsDTO dto = new LoyaltyRewardsDTO();
        dto.setRewardPointsBalance(wallet.getRewardPoints());
        dto.setMembershipTier("GOLD");
        dto.setLifetimeCashbackEarned(new BigDecimal("250.00"));
        dto.setSuccessfulReferralsCount(3);
        dto.setReferralCode("HYPER" + userId.toString().substring(0, 5).toUpperCase());

        return dto;
    }

    // --- MODULE 13: PROFILE ---
    @Transactional(readOnly = true)
    public CustomerProfileDTO getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found: " + userId, HttpStatus.NOT_FOUND));

        CustomerProfileDTO dto = new CustomerProfileDTO();
        dto.setUserId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPreferredLanguage("EN");
        dto.setPushNotificationsEnabled(true);

        return dto;
    }
}
