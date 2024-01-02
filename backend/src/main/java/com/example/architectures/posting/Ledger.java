package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import java.math.BigDecimal;
import java.util.List;

record Ledger(ClientId clientId, List<LedgerAccount> accounts) {
    public void updateAccount(FinancialAccount account, BigDecimal amount) {
        var ledgerAccount = accounts
            .stream()
            .filter(it -> it.account().equals(account))
            .findFirst()
            .orElseThrow();
        ledgerAccount.updateBalance(amount);
    }
}
