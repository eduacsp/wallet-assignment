package com.eduacsp.walletassignment.strategy;

import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.TransactionRepository;
import com.eduacsp.walletassignment.service.BalanceService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("TRANSFER")
public class TransferTransactionStrategy implements TransferStrategy {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;

    public TransferTransactionStrategy(TransactionRepository transactionRepository, BalanceService balanceService) {
        this.transactionRepository = transactionRepository;
        this.balanceService = balanceService;
    }

    @Override
    public BigDecimal executeTransfer(WalletEntity fromWallet, WalletEntity toWallet, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        BigDecimal fromBalance = balanceService.calculate(fromWallet);
        if (fromBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance for transfer");
        }

        TransactionEntity transferOut = new TransactionEntity(
                fromWallet,
                amount,
                TransactionType.TRANSFER_OUT,
                toWallet,
                description != null ? description : "Transfer to wallet " + toWallet.getId()
        );

        TransactionEntity transferIn = new TransactionEntity(
                toWallet,
                amount,
                TransactionType.TRANSFER_IN,
                fromWallet,
                description != null ? description : "Transfer from wallet " + fromWallet.getId()
        );

        transactionRepository.save(transferOut);
        transactionRepository.save(transferIn);

        return balanceService.calculate(fromWallet);
    }

    @Override
    public BigDecimal execute(WalletEntity wallet, BigDecimal amount, String description) {
        throw new UnsupportedOperationException("Use executeTransfer for transfer operations");
    }
}
