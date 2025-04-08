package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BalanceServiceImplTest {

    private TransactionRepository transactionRepository;
    private BalanceServiceImpl balanceService;

    private WalletEntity wallet;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        balanceService = new BalanceServiceImpl(transactionRepository);
        wallet = new WalletEntity();
        wallet.setId(UUID.randomUUID());
    }

    @Test
    void testCalculate_withMultipleTransactions_shouldReturnCorrectBalance() {
        var transactions = List.of(
                new TransactionEntity(wallet, new BigDecimal("100.00"), TransactionType.DEPOSIT, null, "dep"),
                new TransactionEntity(wallet, new BigDecimal("20.00"), TransactionType.WITHDRAWAL, null, "withdraw"),
                new TransactionEntity(wallet, new BigDecimal("30.00"), TransactionType.TRANSFER_IN, null, "in"),
                new TransactionEntity(wallet, new BigDecimal("10.00"), TransactionType.TRANSFER_OUT, null, "out")
        );

        when(transactionRepository.findByWallet(wallet)).thenReturn(transactions);

        BigDecimal result = balanceService.calculate(wallet);

        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void testCalculateAt_withTransactionsBeforeDate_shouldReturnCorrectBalance() {
        var datetime = LocalDateTime.now();
        var transactions = List.of(
                new TransactionEntity(wallet, new BigDecimal("100.00"), TransactionType.DEPOSIT, null, "dep"),
                new TransactionEntity(wallet, new BigDecimal("50.00"), TransactionType.WITHDRAWAL, null, "withdraw")
        );

        when(transactionRepository.findByWalletAndCreatedAtBefore(wallet, datetime)).thenReturn(transactions);

        BigDecimal result = balanceService.calculateAt(wallet, datetime);

        assertEquals(new BigDecimal("50.00"), result); // 100 - 50
    }

    @Test
    void testCalculateAt_withNoTransactions_shouldThrowException() {
        var datetime = LocalDateTime.now();
        when(transactionRepository.findByWalletAndCreatedAtBefore(wallet, datetime)).thenReturn(List.of());

        Exception exception = assertThrows(IllegalStateException.class, () ->
                balanceService.calculateAt(wallet, datetime)
        );

        assertEquals("No transactions found for the specified period", exception.getMessage());
    }
}
