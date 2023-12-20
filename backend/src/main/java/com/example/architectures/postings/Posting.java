package com.example.architectures.postings;

import java.math.BigDecimal;

record Posting(
    ClientId clientId,
    AccountId accountId,
    BigDecimal amount,
    String currency
) {}
