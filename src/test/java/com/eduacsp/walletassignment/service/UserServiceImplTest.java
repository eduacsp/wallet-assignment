package com.eduacsp.walletassignment.service;

import com.eduacsp.walletassignment.model.UserEntity;
import com.eduacsp.walletassignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testFindOrCreateUser_whenUserExists_shouldReturnExistingUser() {
        String cpfCnpj = "12345678900";
        String name = "John Doe";

        UserEntity existingUser = new UserEntity();
        existingUser.setId(UUID.randomUUID());
        existingUser.setName(name);
        existingUser.setCpfCnpj(cpfCnpj);

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(existingUser));

        UserEntity result = userService.findOrCreateUser(name, cpfCnpj);

        assertNotNull(result);
        assertEquals(existingUser.getId(), result.getId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateUser_whenUserDoesNotExist_shouldCreateNewUser() {
        String cpfCnpj = "98765432100";
        String name = "Jane Smith";

        when(userRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.empty());

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(UUID.randomUUID()); // simulate DB ID generation
            return user;
        });

        UserEntity result = userService.findOrCreateUser(name, cpfCnpj);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(cpfCnpj, result.getCpfCnpj());
        assertNotNull(result.getId());

        verify(userRepository).save(any(UserEntity.class));
    }
}
