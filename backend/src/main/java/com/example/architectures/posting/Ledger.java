package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import java.math.BigDecimal;
import java.util.List;

record Ledger(ClientId clientId, List<LedgerAccount> accounts) {
    void debit(FinancialAccount account, BigDecimal amount) {
        var ledgerAccount = findBy(account);
        ledgerAccount.debit(amount);
    }

    void credit(FinancialAccount account, BigDecimal amount) {
        var ledgerAccount = findBy(account);
        ledgerAccount.credit(amount);
    }

    private LedgerAccount findBy(FinancialAccount account) {
        return accounts
            .stream()
            .filter(it -> it.account().equals(account))
            .findFirst()
            .orElseThrow();
    }
}
