package com.example.architectures.postings;

import org.springframework.stereotype.Component;

@Component
class ImportTransactions {

    private final TransactionsGateway transactionsGateway;
    private final Transactions transactions;

    public ImportTransactions(
        TransactionsGateway transactionsGateway,
        Transactions transactions
    ) {
        this.transactionsGateway = transactionsGateway;
        this.transactions = transactions;
    }

    void handle(int clientId, int accountId) {
        var availableTransactions = transactionsGateway.fetchAll(clientId, accountId);
        transactions.saveAll(availableTransactions);
    }
}
