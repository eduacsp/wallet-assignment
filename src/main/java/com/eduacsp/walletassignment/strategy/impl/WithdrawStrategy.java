package com.eduacsp.walletassignment.strategy.impl;

import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.TransactionRepository;
import com.eduacsp.walletassignment.service.BalanceService;
import com.eduacsp.walletassignment.strategy.TransactionStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawStrategy implements TransactionStrategy {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;

    public WithdrawStrategy(TransactionRepository transactionRepository, BalanceService balanceService) {
        this.transactionRepository = transactionRepository;
        this.balanceService = balanceService;
    }

    @Override
    public BigDecimal execute(WalletEntity wallet, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero");
        }

        BigDecimal currentBalance = balanceService.calculate(wallet);
        if (amount.compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        TransactionEntity withdrawal = new TransactionEntity(
                wallet,
                amount,
                TransactionType.WITHDRAWAL,
                null,
                description != null ? description : "Withdrawal"
        );

        transactionRepository.save(withdrawal);

        return balanceService.calculate(wallet);
    }
}
