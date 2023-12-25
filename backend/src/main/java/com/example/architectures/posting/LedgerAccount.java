package com.example.architectures.posting;

import java.math.BigDecimal;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record LedgerAccount(FinancialAccount account, BigDecimal balance) {
    public LedgerAccount(FinancialAccount account) {
        this(account, new BigDecimal("0.0"));
    }
}
