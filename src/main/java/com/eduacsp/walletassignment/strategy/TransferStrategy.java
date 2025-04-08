package com.eduacsp.walletassignment.strategy;

import com.eduacsp.walletassignment.model.WalletEntity;

import java.math.BigDecimal;

public interface TransferStrategy extends TransactionStrategy {
    BigDecimal executeTransfer(WalletEntity fromWallet, WalletEntity toWallet, BigDecimal amount, String description);
}
