package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BalanceServiceImpl implements BalanceService{

    private final TransactionRepository transactionRepository;

    public BalanceServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public BigDecimal calculate(WalletEntity wallet) {
        List<TransactionEntity> transactions = transactionRepository.findByWallet(wallet);
        return calculateBalance(transactions);
    }

    public BigDecimal calculateAt(WalletEntity wallet, LocalDateTime datetime) {
        List<TransactionEntity> transactions = transactionRepository.findByWalletAndCreatedAtBefore(wallet, datetime);
        if (transactions.isEmpty()) {
            throw new IllegalStateException("No transactions found for the specified period");
        }
        return calculateBalance(transactions);
    }

    private BigDecimal calculateBalance(List<TransactionEntity> transactions) {
        return transactions.stream()
                .map(tx -> switch (tx.getType()) {
                    case DEPOSIT, TRANSFER_IN -> tx.getAmount();
                    case WITHDRAWAL, TRANSFER_OUT -> tx.getAmount().negate();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
