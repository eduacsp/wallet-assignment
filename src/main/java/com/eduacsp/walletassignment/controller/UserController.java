package com.eduacsp.walletassignment.controller;

import com.eduacsp.walletassignment.domain.response.ErrorResponse;
import com.eduacsp.walletassignment.domain.response.SuccessResponse;
import com.eduacsp.walletassignment.service.WalletService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {

    private final WalletService walletService;

    public UserController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{cpfCnpj}/wallet/balance")
    public ResponseEntity<?> getBalance(@PathVariable String cpfCnpj) {
        try {
            String message = walletService.getBalanceByCpfCnpj(cpfCnpj);
            return ResponseEntity.ok(new SuccessResponse(200, message));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        }
    }

    @GetMapping("/{cpfCnpj}/wallet/balance/at")
    public ResponseEntity<?> getHistoricalBalance(
            @PathVariable String cpfCnpj,
            @RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime) {
        try {
            String message = walletService.getHistoricalBalanceByCpfCnpj(cpfCnpj, datetime);
            return ResponseEntity.ok(new SuccessResponse(200, message));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        }
    }
}
