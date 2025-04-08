package com.eduacsp.walletassignment.domain.request;

import java.math.BigDecimal;

public record DepositRequest(String cpfCnpj, BigDecimal amount, String description) {}
