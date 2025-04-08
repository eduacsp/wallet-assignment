package com.eduacsp.walletassignment.domain.response;

import java.math.BigDecimal;

public record DepositResponse(String message, BigDecimal newBalance) {}
