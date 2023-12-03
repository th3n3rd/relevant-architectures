package com.example.demo;

import java.math.BigDecimal;

record Transaction(
    int clientId,
    BigDecimal amount,
    String currency
) {}
