package com.eduacsp.walletassignment.domain.request;

import java.math.BigDecimal;

public record WalletRequest(UserRequest user,BigDecimal value){}