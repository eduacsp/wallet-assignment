package com.eduacsp.walletassignment.controller;

import com.eduacsp.walletassignment.domain.Wallet;
import com.eduacsp.walletassignment.domain.request.TransferRequest;
import com.eduacsp.walletassignment.domain.request.WalletRequest;
import com.eduacsp.walletassignment.domain.response.BalanceResponse;
import com.eduacsp.walletassignment.domain.response.SuccessResponse;
import com.eduacsp.walletassignment.domain.response.WalletResponse;
import com.eduacsp.walletassignment.domain.response.ErrorResponse;
import com.eduacsp.walletassignment.mapper.WalletMapper;
import com.eduacsp.walletassignment.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @PostMapping
    public ResponseEntity<?> createWallet(@RequestBody WalletRequest request) {
        try {
            Wallet wallet = walletMapper.toEntity(request);
            String message = walletService.createWallet(wallet, request.value() != null ? request.value() : BigDecimal.ZERO);
            return ResponseEntity.ok(new SuccessResponse(201, message));
        } catch (IllegalStateException e) {
            ErrorResponse error = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            var message = walletService.transfer(
                    request.fromCpfCnpj(),
                    request.toCpfCnpj(),
                    request.amount(),
                    request.description()
            );
            return ResponseEntity.ok(new SuccessResponse(200, message));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        }
    }

}
