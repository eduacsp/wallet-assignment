package com.eduacsp.walletassignment.strategy;

import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.strategy.impl.DepositStrategy;
import com.eduacsp.walletassignment.strategy.impl.WithdrawStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class TransactionStrategyFactory {

    private final Map<TransactionType, TransactionStrategy> strategies = new EnumMap<>(TransactionType.class);

    public TransactionStrategyFactory(List<TransactionStrategy> strategyList) {
        for (TransactionStrategy strategy : strategyList) {
            if (strategy instanceof DepositStrategy) {
                strategies.put(TransactionType.DEPOSIT, strategy);
            } else if (strategy instanceof WithdrawStrategy) {
                strategies.put(TransactionType.WITHDRAWAL, strategy);
            } else if (strategy instanceof TransferTransactionStrategy) {
                strategies.put(TransactionType.TRANSFER_OUT, strategy);
            } else {
                throw new IllegalArgumentException("Unknown strategy type: " + strategy.getClass().getSimpleName());
            }
        }
    }

    public TransactionStrategy getStrategy(TransactionType type) {
        TransactionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Transaction type not supported: " + type);
        }
        return strategy;
    }
}
