package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.model.WalletEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BalanceService {
    BigDecimal calculate(WalletEntity wallet);
    BigDecimal calculateAt(WalletEntity wallet, LocalDateTime datetime);
}
