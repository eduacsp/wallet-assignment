package com.eduacsp.walletassignment.integration;

import com.eduacsp.walletassignment.controller.WalletTransactionController;
import com.eduacsp.walletassignment.domain.Wallet;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletTransactionController.class)
@Import(WalletTransactionControllerIntegrationTest.TestConfig.class)
public class WalletTransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WalletService walletService() {
            return new WalletService() {
                @Override
                public String deposit(String cpfCnpj, BigDecimal amount, String description) {
                    if (cpfCnpj.equals("12345678900") && amount.compareTo(BigDecimal.ZERO) > 0) {
                        return "Deposited " + amount;
                    }
                    throw new IllegalArgumentException("Invalid deposit request");
                }

                @Override
                public String withdraw(String cpfCnpj, BigDecimal amount, String description) {
                    if (cpfCnpj.equals("12345678900") && amount.compareTo(new BigDecimal("50.00")) <= 0) {
                        return "Withdrawn " + amount;
                    }
                    throw new IllegalStateException("Insufficient funds");
                }

                // Métodos não utilizados neste teste
                @Override
                public String createWallet(Wallet wallet, BigDecimal value) { return null; }
                @Override
                public String transfer(String fromCpfCnpj, String toCpfCnpj, BigDecimal amount, String description) { return null; }
                @Override
                public String getBalanceByCpfCnpj(String cpfCnpj) { return null; }
                @Override
                public String getHistoricalBalanceByCpfCnpj(String cpfCnpj, java.time.LocalDateTime datetime) { return null; }
            };
        }
    }

    @Test
    void testDepositSuccess() throws Exception {
        mockMvc.perform(post("/users/12345678900/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.00, \"description\": \"Salary\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Deposited 100.00"));
    }

    @Test
    void testDepositInvalidAmount() throws Exception {
        mockMvc.perform(post("/users/12345678900/wallet/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 0, \"description\": \"Invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").value("Invalid deposit request"));
    }

    @Test
    void testWithdrawSuccess() throws Exception {
        mockMvc.perform(post("/users/12345678900/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00, \"description\": \"Groceries\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Withdrawn 50.00"));
    }

    @Test
    void testWithdrawInsufficientFunds() throws Exception {
        mockMvc.perform(post("/users/12345678900/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.00, \"description\": \"Big purchase\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }
}
