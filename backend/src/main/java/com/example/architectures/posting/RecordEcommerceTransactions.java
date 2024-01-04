package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import com.example.architectures.ecommerce.PaymentGateway;
import org.springframework.stereotype.Component;

@Component
public class RecordEcommerceTransactions {

    private final PaymentGateway paymentGateway;
    private final Journal journal;

    RecordEcommerceTransactions(PaymentGateway paymentGateway, Journal journal) {
        this.paymentGateway = paymentGateway;
        this.journal = journal;
    }

    public void handle(ClientId clientId, AccountId accountId) {
        var newEntries = paymentGateway.fetchTransactions(clientId, accountId)
            .stream()
            .map(it -> JournalEntry
                .fromEcommerce(it.accountId())
                .clientId(it.clientId())
                .amount(it.amount())
                .currency(it.currency())
                .build()
            )
            .toList();

        journal.saveAll(newEntries);
    }
}
