package com.example.architectures.postings;

import java.math.BigDecimal;

record Posting(int clientId, int accountId, BigDecimal amount, String currency) {}
