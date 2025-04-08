package com.eduacsp.walletassignment.domain.request;

import java.math.BigDecimal;

public record TransferRequest(
        String fromCpfCnpj,
        String toCpfCnpj,
        BigDecimal amount,
        String description
) {}
