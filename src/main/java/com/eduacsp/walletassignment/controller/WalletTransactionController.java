package com.eduacsp.walletassignment.controller;

import com.eduacsp.walletassignment.domain.request.TransactionRequest;
import com.eduacsp.walletassignment.domain.response.ErrorResponse;
import com.eduacsp.walletassignment.domain.response.SuccessResponse;
import com.eduacsp.walletassignment.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{cpfCnpj}/wallet")
public class WalletTransactionController {

    private final WalletService walletService;

    public WalletTransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable String cpfCnpj,
            @RequestBody TransactionRequest request
    ) {
        try {
            var message = walletService.deposit(cpfCnpj, request.amount(), request.description());
            return ResponseEntity.ok(new SuccessResponse(200, message));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String cpfCnpj,
            @RequestBody TransactionRequest request
    ) {
        try {
            var message = walletService.withdraw(cpfCnpj, request.amount(), request.description());
            return ResponseEntity.ok(new SuccessResponse(200, message));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        }
    }

}
