package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.domain.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface WalletService {
        String createWallet(Wallet wallet, BigDecimal initialBalance);
        String getBalanceByCpfCnpj(String cpfCnpj);
        String getHistoricalBalanceByCpfCnpj(String cpfCnpj, LocalDateTime datetime);
        String deposit(String cpfCnpj, BigDecimal amount, String description);
        String withdraw(String cpfCnpj, BigDecimal amount, String description);
        String transfer(String fromCpfCnpj, String toCpfCnpj, BigDecimal amount, String description);
}
