package com.example.architectures.postings;

import com.example.architectures.ecommerce.AccountId;
import com.example.architectures.ecommerce.PaymentGateway;
import org.springframework.stereotype.Component;

@Component
class GenerateJournalEntries {

    private final PaymentGateway paymentGateway;
    private final Journal journal;

    GenerateJournalEntries(PaymentGateway paymentGateway, Journal journal) {
        this.paymentGateway = paymentGateway;
        this.journal = journal;
    }

    void handle(ClientId clientId, AccountId accountId) {
        var newEntries = paymentGateway.fetchTransactions(clientId, accountId)
            .stream()
            .map(it -> new JournalEntry(it.clientId(), it.accountId(), it.amount(), it.currency()))
            .toList();

        journal.saveAll(newEntries);
    }
}
