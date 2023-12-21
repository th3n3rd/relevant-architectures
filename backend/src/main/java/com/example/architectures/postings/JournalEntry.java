package com.example.architectures.postings;

import com.example.architectures.ecommerce.AccountId;
import java.math.BigDecimal;

record JournalEntry(
    ClientId clientId,
    AccountId accountId,
    BigDecimal amount,
    String currency
) {}
