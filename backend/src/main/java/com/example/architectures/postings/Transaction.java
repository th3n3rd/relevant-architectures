package com.example.architectures.postings;

import java.math.BigDecimal;

public record Transaction(
    int clientId,
    int accountId,
    BigDecimal amount,
    String currency
) {}
