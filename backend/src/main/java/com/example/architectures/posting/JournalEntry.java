package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import java.math.BigDecimal;

record JournalEntry(
    ClientId clientId,
    AccountId accountId,
    BigDecimal amount,
    String currency
) {}
