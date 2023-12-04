package com.example.architectures.postings;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class ImportTransactionsTask {

    private final ImportTransactions importTransactions;

    ImportTransactionsTask(ImportTransactions importTransactions) {
        this.importTransactions = importTransactions;
    }

    @EventListener
    void on(NewAccountSetup event) {
        importTransactions.handle(event.clientId(), event.accountId());
    }
}
