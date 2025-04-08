package com.eduacsp.walletassignment.domain;

import java.math.BigDecimal;

public record Wallet(User user, BigDecimal value) {
}
