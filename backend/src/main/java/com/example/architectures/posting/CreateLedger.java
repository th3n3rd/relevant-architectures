package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import org.springframework.stereotype.Component;

@Component
public class CreateLedger {
    private final Ledgers ledgers;
    private final ChartOfAccounts chartOfAccounts;

    CreateLedger(Ledgers ledgers, ChartOfAccounts chartOfAccounts) {
        this.ledgers = ledgers;
        this.chartOfAccounts = chartOfAccounts;
    }

    public void handle(ClientId clientId) {
        ledgers.save(new Ledger(
            clientId,
            chartOfAccounts.findAll()
                .stream()
                .map(LedgerAccount::new)
                .toList()
        ));
    }
}
