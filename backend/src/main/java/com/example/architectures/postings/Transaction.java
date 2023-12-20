package com.example.architectures.postings;

import java.math.BigDecimal;

public record Transaction(
    ClientId clientId,
    int accountId,
    BigDecimal amount,
    String currency
) {}
