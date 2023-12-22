package com.example.architectures.ecommerce;

import com.example.architectures.common.ClientId;
import java.math.BigDecimal;

public record Transaction(
    ClientId clientId,
    AccountId accountId,
    BigDecimal amount,
    String currency
) {}
