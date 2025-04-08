package com.eduacsp.walletassignment.repository;

import com.eduacsp.walletassignment.model.UserEntity;
import com.eduacsp.walletassignment.model.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

    boolean existsByUser(UserEntity user);

    Optional<WalletEntity> findByUser(UserEntity user);

}
