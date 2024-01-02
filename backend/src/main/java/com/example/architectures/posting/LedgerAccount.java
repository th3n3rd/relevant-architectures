package com.example.architectures.posting;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = {"account", "balance"})
public final class LedgerAccount {
    private final FinancialAccount account;
    private BigDecimal balance;

    public LedgerAccount(FinancialAccount account, BigDecimal balance) {
        this.account = account;
        this.balance = balance;
    }

    public LedgerAccount(FinancialAccount account) {
        this(account, new BigDecimal("0.0"));
    }

    void updateBalance(BigDecimal delta) {
        balance = balance.add(delta);
    }
}
