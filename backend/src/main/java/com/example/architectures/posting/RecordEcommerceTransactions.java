package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import com.example.architectures.ecommerce.PaymentGateway;
import org.springframework.stereotype.Component;

@Component
class RecordEcommerceTransactions {

    private final PaymentGateway paymentGateway;
    private final Journal journal;

    RecordEcommerceTransactions(PaymentGateway paymentGateway, Journal journal) {
        this.paymentGateway = paymentGateway;
        this.journal = journal;
    }

    void handle(ClientId clientId, AccountId accountId) {
        var newEntries = paymentGateway.fetchTransactions(clientId, accountId)
            .stream()
            .map(it -> new JournalEntry(
                it.clientId(),
                it.accountId(),
                it.amount(),
                it.currency(),
                new JournalEntry.Metadata(
                    "e-commerce",
                    it.accountId()
                )
            ))
            .toList();

        journal.saveAll(newEntries);
    }
}
