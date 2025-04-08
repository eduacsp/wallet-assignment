package com.eduacsp.walletassignment.strategy;

import com.eduacsp.walletassignment.model.WalletEntity;

import java.math.BigDecimal;

public interface TransactionStrategy {
    BigDecimal execute(WalletEntity wallet, BigDecimal amount, String description);
}
