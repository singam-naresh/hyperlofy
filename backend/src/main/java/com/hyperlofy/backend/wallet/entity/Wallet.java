package com.hyperlofy.backend.wallet.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@SQLDelete(sql = "UPDATE wallets SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity {

    @Column(name = "owner_id", nullable = false, unique = true)
    private UUID ownerId;

    @Builder.Default
    @Column(name = "owner_type", nullable = false, length = 30)
    private String ownerType = "CUSTOMER"; // CUSTOMER, DRIVER, MERCHANT, TREASURY

    @Builder.Default
    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Builder.Default
    @Column(name = "spendable_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal spendableBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "reserved_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal reservedBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "promotional_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal promotionalBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, FROZEN, CLOSED

    @Builder.Default
    @Column(name = "kyc_status", nullable = false, length = 30)
    private String kycStatus = "VERIFIED";

    // --- Legacy compatibility methods ---

    public BigDecimal getBalance() {
        return getSpendableBalance();
    }

    public void setBalance(BigDecimal balance) {
        setSpendableBalance(balance);
    }

    public User getUser() {
        if (ownerId == null) return null;
        User u = new User();
        u.setId(ownerId);
        return u;
    }

    public void setUser(User user) {
        if (user != null) {
            this.ownerId = user.getId();
        }
    }

    public static class WalletBuilder {
        public WalletBuilder user(User user) {
            if (user != null) {
                this.ownerId(user.getId());
            }
            return this;
        }

        public WalletBuilder balance(BigDecimal balance) {
            this.spendableBalance(balance);
            return this;
        }
    }
}
