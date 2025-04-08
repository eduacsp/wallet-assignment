package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.model.UserEntity;
import com.eduacsp.walletassignment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Retryable(
            retryFor = { DataAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public UserEntity findOrCreateUser(String name, String cpfCnpj) {
        Optional<UserEntity> optionalUser = userRepository.findByCpfCnpj(cpfCnpj);

        if (optionalUser.isPresent()) {
            log.info("Usuário encontrado: {}", optionalUser.get().getId());
            return optionalUser.get();
        }

        UserEntity newUser = new UserEntity();
        newUser.setId(UUID.randomUUID());
        newUser.setName(name);
        newUser.setCpfCnpj(cpfCnpj);
        UserEntity savedUser = userRepository.save(newUser);
        log.info("Novo usuário criado: {}", savedUser.getId());
        return savedUser;
    }
}
