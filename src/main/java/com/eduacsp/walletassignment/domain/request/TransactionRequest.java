package com.eduacsp.walletassignment.domain.request;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        String description
) {}
