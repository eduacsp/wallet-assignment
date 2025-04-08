package com.eduacsp.walletassignment.integration;

import com.eduacsp.walletassignment.config.WalletControllerTestConfig;
import com.eduacsp.walletassignment.controller.WalletController;
import com.eduacsp.walletassignment.domain.User;
import com.eduacsp.walletassignment.domain.Wallet;
import com.eduacsp.walletassignment.domain.request.TransferRequest;
import com.eduacsp.walletassignment.domain.request.UserRequest;
import com.eduacsp.walletassignment.domain.request.WalletRequest;
import com.eduacsp.walletassignment.mapper.WalletMapper;
import com.eduacsp.walletassignment.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(WalletControllerTestConfig.class)
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletMapper walletMapper;

    @Test
    void testCreateWallet_Success() throws Exception {
        WalletRequest request = new WalletRequest(new UserRequest("John Doe","12345678900"),  new BigDecimal("100.00"));
        Wallet wallet = new Wallet(new User(request.user().name(), request.user().cpfCnpj()), new BigDecimal("100.00"));

        Mockito.when(walletMapper.toEntity(Mockito.any())).thenReturn(wallet);
        Mockito.when(walletService.createWallet(eq(wallet), eq(request.value())))
                .thenReturn("Wallet created successfully");

        mockMvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Wallet created successfully"));
    }

    @Test
    void testCreateWallet_AlreadyExists() throws Exception {
        WalletRequest request = new WalletRequest(new UserRequest("John Doe","12345678900"),  new BigDecimal("100.00"));
        Wallet wallet = new Wallet(new User(request.user().name(), request.user().cpfCnpj()), new BigDecimal("100.00"));

        when(walletMapper.toEntity(request)).thenReturn(wallet);
        when(walletService.createWallet(wallet, request.value())).thenThrow(new IllegalStateException("Wallet already exists"));

        mockMvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "John Doe",
                              "cpfCnpj": "12345678900",
                              "value": 100.00
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").value("Wallet already exists"));
    }

    @Test
    void testTransfer_Success() throws Exception {
        TransferRequest request = new TransferRequest("12345678900", "98765432100", new BigDecimal("50.00"), "Gift");

        when(walletService.transfer(request.fromCpfCnpj(), request.toCpfCnpj(), request.amount(), request.description()))
                .thenReturn("Transfer of 50.00 from 12345678900 to 98765432100 was done successfully");

        mockMvc.perform(post("/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "fromCpfCnpj": "12345678900",
                              "toCpfCnpj": "98765432100",
                              "amount": 50.00,
                              "description": "Gift"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Transfer of 50.00 from 12345678900 to 98765432100 was done successfully"));
    }

    @Test
    void testTransfer_Failure() throws Exception {
        TransferRequest request = new TransferRequest("12345678900", "98765432100", new BigDecimal("50.00"), "Gift");

        when(walletService.transfer(request.fromCpfCnpj(), request.toCpfCnpj(), request.amount(), request.description()))
                .thenThrow(new IllegalStateException("Insufficient funds"));

        mockMvc.perform(post("/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "fromCpfCnpj": "12345678900",
                              "toCpfCnpj": "98765432100",
                              "amount": 50.00,
                              "description": "Gift"
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(400))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }
}