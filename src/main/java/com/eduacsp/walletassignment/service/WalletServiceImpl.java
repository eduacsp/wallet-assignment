package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.domain.Wallet;
import com.eduacsp.walletassignment.enums.TransactionType;
import com.eduacsp.walletassignment.model.UserEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import com.eduacsp.walletassignment.repository.UserRepository;
import com.eduacsp.walletassignment.repository.WalletRepository;
import com.eduacsp.walletassignment.strategy.TransactionStrategy;
import com.eduacsp.walletassignment.strategy.TransactionStrategyFactory;
import com.eduacsp.walletassignment.strategy.TransferStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionStrategyFactory strategyFactory;
    private final BalanceService balanceService;
    private final UserService userService;

    public WalletServiceImpl(WalletRepository walletRepository,
                             UserRepository userRepository,
                             TransactionStrategyFactory strategyFactory,
                             BalanceService balanceService,
                             UserService userService) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.strategyFactory = strategyFactory;
        this.balanceService = balanceService;
        this.userService = userService;
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String createWallet(Wallet wallet, BigDecimal initialBalance) {
        String cpfCnpj = wallet.user().cpfCnpj();
        if ("".equals(cpfCnpj)) {
            throw new IllegalStateException("Invalid CPF/CNPJ");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Initial value must be greater than zero");
        }

        UserEntity user = userService.findOrCreateUser(wallet.user().name(), cpfCnpj);
        boolean hasWallet = walletRepository.existsByUser(user);
        if (hasWallet) {
            log.warn("User {} already has a wallet", user.getId());
            throw new IllegalStateException("Wallet already exists for this user");
        }

        WalletEntity walletEntity = new WalletEntity(user);
        walletEntity = walletRepository.save(walletEntity);

        String message = "Wallet successfully created for user " + user.getCpfCnpj() + ". Wallet ID: " + walletEntity.getId();
        log.info(message);

        TransactionStrategy strategy = strategyFactory.getStrategy(TransactionType.DEPOSIT);
        BigDecimal finalBalance = strategy.execute(walletEntity, initialBalance, "Initial deposit");
        log.info("Initial deposit of {} registered. Current balance: {}", initialBalance, finalBalance);

        return message;
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String getBalanceByCpfCnpj(String cpfCnpj) {
        UserEntity user = userRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        WalletEntity wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        BigDecimal balance = balanceService.calculate(wallet);
        return "The balance of user "+cpfCnpj+" is "+balance;
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String getHistoricalBalanceByCpfCnpj(String cpfCnpj, LocalDateTime datetime) {
        UserEntity user = userRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        WalletEntity wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for this user"));
        BigDecimal balance = balanceService.calculateAt(wallet, datetime);
        return "The balance of user "+cpfCnpj+" is "+balance+ " until "+datetime;
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String deposit(String cpfCnpj, BigDecimal amount, String description) {
        UserEntity user = userRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        WalletEntity wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        TransactionStrategy strategy = strategyFactory.getStrategy(TransactionType.DEPOSIT);
        BigDecimal newBalance = strategy.execute(wallet, amount, description);
        return String.format("Successfully deposited %.2f. New balance is %.2f", amount, newBalance);
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String withdraw(String cpfCnpj, BigDecimal amount, String description) {
        UserEntity user = userRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        WalletEntity wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        TransactionStrategy strategy = strategyFactory.getStrategy(TransactionType.WITHDRAWAL);
        BigDecimal newBalance = strategy.execute(wallet, amount, description);
        return String.format("Successfully withdrew %.2f. New balance is %.2f", amount, newBalance);
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String transfer(String fromCpfCnpj, String toCpfCnpj, BigDecimal amount, String description) {
        if (fromCpfCnpj == null || toCpfCnpj == null || amount == null) {
            throw new IllegalArgumentException("All parameters must be provided");
        }

        if (fromCpfCnpj.equals(toCpfCnpj)) {
            throw new IllegalArgumentException("Cannot transfer to the same user");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        var fromUser = userRepository.findByCpfCnpj(fromCpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("Sender user not found"));

        var toUser = userRepository.findByCpfCnpj(toCpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("Recipient user not found"));

        var fromWallet = walletRepository.findByUser(fromUser)
                .orElseThrow(() -> new IllegalStateException("Sender wallet not found"));

        var toWallet = walletRepository.findByUser(toUser)
                .orElseThrow(() -> new IllegalStateException("Recipient wallet not found"));

        TransferStrategy strategy = (TransferStrategy) strategyFactory.getStrategy(TransactionType.TRANSFER_OUT);
        strategy.executeTransfer(fromWallet, toWallet, amount, description);

        return String.format("Transfer of %.2f from %s to %s was done successfully", amount, fromCpfCnpj, toCpfCnpj);
    }
}
