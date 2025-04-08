package com.eduacsp.walletassignment.repository;

import com.eduacsp.walletassignment.model.TransactionEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByWallet(WalletEntity wallet);

    List<TransactionEntity> findByWalletAndCreatedAtBefore(WalletEntity wallet, LocalDateTime before);

}
