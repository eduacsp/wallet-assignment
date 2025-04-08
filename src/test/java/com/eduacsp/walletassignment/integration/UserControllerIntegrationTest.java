package com.eduacsp.walletassignment.integration;

import com.eduacsp.walletassignment.controller.UserController;
import com.eduacsp.walletassignment.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerIntegrationTest.TestConfig.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WalletService walletService() {
            return new WalletService() {
                @Override
                public String getBalanceByCpfCnpj(String cpfCnpj) {
                    if (cpfCnpj.equals("12345678900")) {
                        return "The balance of user 12345678900 is 100.00";
                    }
                    throw new IllegalArgumentException("User not found");
                }

                @Override
                public String getHistoricalBalanceByCpfCnpj(String cpfCnpj, LocalDateTime datetime) {
                    if (cpfCnpj.equals("12345678900") && datetime.equals(LocalDateTime.of(2024, 1, 1, 12, 0))) {
                        return "The balance of user 12345678900 is 50.00 until 2024-01-01T12:00";
                    }
                    throw new IllegalArgumentException("User not found");
                }

                @Override
                public String deposit(String cpfCnpj, BigDecimal amount, String description) {
                    return "";
                }

                @Override
                public String withdraw(String cpfCnpj, BigDecimal amount, String description) {
                    return "";
                }

                @Override
                public String transfer(String fromCpfCnpj, String toCpfCnpj, BigDecimal amount, String description) {
                    return "Transfer simulated";
                }

                @Override
                public String createWallet(com.eduacsp.walletassignment.domain.Wallet wallet, BigDecimal value) {
                    return "Wallet created";
                }
            };
        }
    }

    @Test
    void testGetBalance_Success() throws Exception {
        mockMvc.perform(get("/users/12345678900/wallet/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("The balance of user 12345678900 is 100.00"));
    }

    @Test
    void testGetBalance_UserNotFound() throws Exception {
        mockMvc.perform(get("/users/00000000000/wallet/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testGetHistoricalBalance_Success() throws Exception {
        mockMvc.perform(get("/users/12345678900/wallet/balance/at")
                        .param("datetime", "2024-01-01T12:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("The balance of user 12345678900 is 50.00 until 2024-01-01T12:00"));
    }

    @Test
    void testGetHistoricalBalance_UserNotFound() throws Exception {
        mockMvc.perform(get("/users/00000000000/wallet/balance/at")
                        .param("datetime", "2024-01-01T12:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
