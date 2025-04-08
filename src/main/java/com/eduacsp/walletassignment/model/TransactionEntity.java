package com.eduacsp.walletassignment.model;

import com.eduacsp.walletassignment.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "related_wallet_id")
    private WalletEntity relatedWallet;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TransactionEntity() {
    }

    public TransactionEntity(WalletEntity wallet, BigDecimal amount, TransactionType type, WalletEntity relatedWallet, String description) {
        this.id = UUID.randomUUID();
        this.wallet = wallet;
        this.amount = amount;
        this.type = type;
        this.relatedWallet = relatedWallet;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public WalletEntity getWallet() {
        return wallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public WalletEntity getRelatedWallet() {
        return relatedWallet;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setWallet(WalletEntity wallet) {
        this.wallet = wallet;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setRelatedWallet(WalletEntity relatedWallet) {
        this.relatedWallet = relatedWallet;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
