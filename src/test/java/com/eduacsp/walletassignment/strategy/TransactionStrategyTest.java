package com.eduacsp.walletassignment.strategy;

import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.TransactionRepository;
import com.eduacsp.walletassignment.service.BalanceService;
import com.eduacsp.walletassignment.strategy.impl.DepositStrategy;
import com.eduacsp.walletassignment.strategy.impl.WithdrawStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransactionStrategyTest {

    private TransactionRepository transactionRepository;
    private BalanceService balanceService;
    private WalletEntity wallet;

    @BeforeEach
    void setup() {
        transactionRepository = mock(TransactionRepository.class);
        balanceService = mock(BalanceService.class);
        wallet = new WalletEntity();
        wallet.setId(UUID.randomUUID());
    }

    @Test
    void depositStrategy_shouldSaveDepositTransactionAndReturnCorrectBalance() {
        DepositStrategy depositStrategy = new DepositStrategy(transactionRepository);

        when(transactionRepository.findByWallet(wallet)).thenReturn(List.of(
                new TransactionEntity(wallet, new BigDecimal("50.00"), TransactionType.DEPOSIT, null, "Initial")
        ));

        BigDecimal result = depositStrategy.execute(wallet, new BigDecimal("50.00"), "Test deposit");

        assertThat(result).isEqualTo(new BigDecimal("50.00"));
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void depositStrategy_shouldThrowException_whenAmountIsZero() {
        DepositStrategy depositStrategy = new DepositStrategy(transactionRepository);

        assertThatThrownBy(() ->
                depositStrategy.execute(wallet, BigDecimal.ZERO, "Invalid deposit")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Deposit amount must be greater than zero");
    }

    @Test
    void withdrawStrategy_shouldSaveWithdrawTransactionAndReturnUpdatedBalance() {
        WithdrawStrategy withdrawStrategy = new WithdrawStrategy(transactionRepository, balanceService);

        when(balanceService.calculate(wallet)).thenReturn(new BigDecimal("100.00"));

        BigDecimal result = withdrawStrategy.execute(wallet, new BigDecimal("50.00"), "Test withdrawal");

        assertThat(result).isEqualTo(new BigDecimal("100.00"));
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void withdrawStrategy_shouldThrowException_whenAmountIsGreaterThanBalance() {
        WithdrawStrategy withdrawStrategy = new WithdrawStrategy(transactionRepository, balanceService);

        when(balanceService.calculate(wallet)).thenReturn(new BigDecimal("10.00"));

        assertThatThrownBy(() ->
                withdrawStrategy.execute(wallet, new BigDecimal("50.00"), "Too much")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient funds");
    }

    @Test
    void transferStrategy_shouldTransferAmountAndSaveTwoTransactions() {
        WalletEntity toWallet = new WalletEntity();
        toWallet.setId(UUID.randomUUID());

        TransferTransactionStrategy transferStrategy = new TransferTransactionStrategy(transactionRepository, balanceService);

        when(balanceService.calculate(wallet)).thenReturn(new BigDecimal("100.00"));

        BigDecimal result = transferStrategy.executeTransfer(wallet, toWallet, new BigDecimal("50.00"), "Transfer test");

        assertThat(result).isEqualTo(new BigDecimal("100.00"));
        verify(transactionRepository, times(2)).save(any(TransactionEntity.class));
    }

    @Test
    void transferStrategy_shouldThrowException_whenBalanceInsufficient() {
        WalletEntity toWallet = new WalletEntity();
        toWallet.setId(UUID.randomUUID());

        TransferTransactionStrategy transferStrategy = new TransferTransactionStrategy(transactionRepository, balanceService);

        when(balanceService.calculate(wallet)).thenReturn(new BigDecimal("10.00"));

        assertThatThrownBy(() ->
                transferStrategy.executeTransfer(wallet, toWallet, new BigDecimal("50.00"), null)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient balance for transfer");
    }

    @Test
    void transferStrategy_shouldThrowException_whenAmountNonPositive() {
        WalletEntity toWallet = new WalletEntity();
        toWallet.setId(UUID.randomUUID());

        TransferTransactionStrategy transferStrategy = new TransferTransactionStrategy(transactionRepository, balanceService);

        assertThatThrownBy(() ->
                transferStrategy.executeTransfer(wallet, toWallet, BigDecimal.ZERO, null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transfer amount must be greater than zero");
    }
}
