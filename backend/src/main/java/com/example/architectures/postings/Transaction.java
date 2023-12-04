package com.example.architectures.postings;

import java.math.BigDecimal;

public record Transaction(
    int clientId,
    BigDecimal amount,
    String currency
) {}
