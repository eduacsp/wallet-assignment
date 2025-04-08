package com.eduacsp.walletassignment.strategy.impl;

import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.TransactionRepository;
import com.eduacsp.walletassignment.strategy.TransactionStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositStrategy implements TransactionStrategy {

    private final TransactionRepository transactionRepository;

    public DepositStrategy(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BigDecimal execute(WalletEntity wallet, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        TransactionEntity deposit = new TransactionEntity(
                wallet,
                amount,
                TransactionType.DEPOSIT,
                null,
                description != null ? description : "Deposit"
        );

        transactionRepository.save(deposit);

        return transactionRepository.findByWallet(wallet).stream()
                .map(t -> switch (t.getType()) {
                    case DEPOSIT, TRANSFER_IN -> t.getAmount();
                    case WITHDRAWAL, TRANSFER_OUT -> t.getAmount().negate();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
