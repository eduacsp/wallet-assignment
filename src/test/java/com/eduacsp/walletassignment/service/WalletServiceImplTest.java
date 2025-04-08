package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.domain.User;
import com.eduacsp.walletassignment.domain.Wallet;
import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.UserEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.UserRepository;
import com.eduacsp.walletassignment.repository.WalletRepository;
import com.eduacsp.walletassignment.strategy.TransactionStrategy;
import com.eduacsp.walletassignment.strategy.TransactionStrategyFactory;
import com.eduacsp.walletassignment.strategy.TransferStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionStrategyFactory strategyFactory;

    @Mock
    private BalanceService balanceService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionStrategy depositStrategy;

    @Mock
    private TransactionStrategy withdrawStrategy;

    @Mock
    private TransferStrategy transferStrategy;

    @InjectMocks
    private WalletServiceImpl walletService;

    private final String cpfCnpj = "12345678901";
    private final String recipientCpfCnpj = "98765432100";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWallet_Success() {
        Wallet wallet = new Wallet(new User("Test User", cpfCnpj), new BigDecimal("100.0"));
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "Test User", cpfCnpj);
        WalletEntity walletEntity = new WalletEntity(userEntity);

        when(userService.findOrCreateUser(any(), any())).thenReturn(userEntity);
        when(walletRepository.existsByUser(userEntity)).thenReturn(false);
        when(walletRepository.save(any())).thenReturn(walletEntity);
        when(strategyFactory.getStrategy(TransactionType.DEPOSIT)).thenReturn(depositStrategy);
        when(depositStrategy.execute(any(), any(), any())).thenReturn(BigDecimal.TEN);

        String result = walletService.createWallet(wallet, BigDecimal.TEN);
        assertTrue(result.contains("Wallet successfully created for user"));
    }

    @Test
    void testCreateWallet_InvalidCpfCnpjFails() {
        Wallet wallet = new Wallet(new User("Test User", ""), new BigDecimal("100.0"));
        assertThrows(IllegalStateException.class, () ->
                walletService.createWallet(wallet, BigDecimal.TEN));
    }

    @Test
    void testCreateWallet_InvalidInitialBalanceFails() {
        Wallet wallet = new Wallet(new User("Test User", cpfCnpj), BigDecimal.ZERO);
        assertThrows(IllegalStateException.class, () ->
                walletService.createWallet(wallet, BigDecimal.ZERO));
    }

    @Test
    void testCreateWallet_FailsIfAlreadyExists() {
        Wallet wallet = new Wallet(new User("Test User", cpfCnpj), new BigDecimal("100.0"));
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "Test User", cpfCnpj);

        when(userService.findOrCreateUser(any(), any())).thenReturn(userEntity);
        when(walletRepository.existsByUser(userEntity)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                walletService.createWallet(wallet, BigDecimal.TEN));
    }

    @Test
    void testGetBalanceByCpfCnpj_Success() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "Test", cpfCnpj);
        WalletEntity wallet = new WalletEntity(user);

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(balanceService.calculate(wallet)).thenReturn(BigDecimal.TEN);

        String result = walletService.getBalanceByCpfCnpj(cpfCnpj);
        assertTrue(result.contains("The balance of user"));
    }

    @Test
    void testGetHistoricalBalanceByCpfCnpj_Success() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "Test", cpfCnpj);
        WalletEntity wallet = new WalletEntity(user);
        LocalDateTime datetime = LocalDateTime.of(2024, 1, 1, 12, 0);

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(balanceService.calculateAt(wallet, datetime)).thenReturn(BigDecimal.valueOf(50.00));

        String result = walletService.getHistoricalBalanceByCpfCnpj(cpfCnpj, datetime);
        assertTrue(result.contains("The balance of user"));
    }

    @Test
    void testDeposit_Success() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "Test", cpfCnpj);
        WalletEntity wallet = new WalletEntity(user);

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(strategyFactory.getStrategy(TransactionType.DEPOSIT)).thenReturn(depositStrategy);
        when(depositStrategy.execute(wallet, BigDecimal.TEN, "desc")).thenReturn(BigDecimal.valueOf(100));

        String result = walletService.deposit(cpfCnpj, BigDecimal.TEN, "desc");
        assertTrue(result.contains("Successfully deposited"));
    }

    @Test
    void testWithdraw_Success() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "Test", cpfCnpj);
        WalletEntity wallet = new WalletEntity(user);

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(strategyFactory.getStrategy(TransactionType.WITHDRAWAL)).thenReturn(withdrawStrategy);
        when(withdrawStrategy.execute(wallet, BigDecimal.TEN, "desc")).thenReturn(BigDecimal.valueOf(90));

        String result = walletService.withdraw(cpfCnpj, BigDecimal.TEN, "desc");
        assertTrue(result.contains("Successfully withdrew"));
    }

    @Test
    void testTransfer_Success() {
        UserEntity fromUser = new UserEntity(UUID.randomUUID(), "Sender", cpfCnpj);
        UserEntity toUser = new UserEntity(UUID.randomUUID(), "Recipient", recipientCpfCnpj);
        WalletEntity fromWallet = new WalletEntity(fromUser);
        WalletEntity toWallet = new WalletEntity(toUser);

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(fromUser));
        when(userRepository.findByCpfCnpj(recipientCpfCnpj)).thenReturn(Optional.of(toUser));
        when(walletRepository.findByUser(fromUser)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByUser(toUser)).thenReturn(Optional.of(toWallet));
        when(strategyFactory.getStrategy(TransactionType.TRANSFER_OUT)).thenReturn(transferStrategy);

        String result = walletService.transfer(cpfCnpj, recipientCpfCnpj, BigDecimal.TEN, "transfer");
        assertTrue(result.contains("Transfer of"));
    }

    @Test
    void testTransfer_SameUserFails() {
        assertThrows(IllegalArgumentException.class, () ->
                walletService.transfer(cpfCnpj, cpfCnpj, BigDecimal.TEN, "desc"));
    }

    @Test
    void testTransfer_NullParamFails() {
        assertThrows(IllegalArgumentException.class, () ->
                walletService.transfer(null, recipientCpfCnpj, BigDecimal.TEN, "desc"));
    }

    @Test
    void testTransfer_ZeroAmountFails() {
        assertThrows(IllegalArgumentException.class, () ->
                walletService.transfer(cpfCnpj, recipientCpfCnpj, BigDecimal.ZERO, "desc"));
    }
}
