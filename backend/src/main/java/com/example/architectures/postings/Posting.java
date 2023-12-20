package com.example.architectures.postings;

import java.math.BigDecimal;

record Posting(
    ClientId clientId,
    int accountId,
    BigDecimal amount,
    String currency
) {}
