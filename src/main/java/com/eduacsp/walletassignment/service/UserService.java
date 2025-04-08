package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.model.UserEntity;

public interface UserService {
    UserEntity findOrCreateUser(String name, String cpfCnpj);
}
