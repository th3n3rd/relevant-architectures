package com.example.architectures.postings;

import java.math.BigDecimal;

record JournalEntry(
    ClientId clientId,
    AccountId accountId,
    BigDecimal amount,
    String currency
) {}
