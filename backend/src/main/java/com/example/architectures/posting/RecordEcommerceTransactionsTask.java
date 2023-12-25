package com.example.architectures.posting;

import com.example.architectures.ecommerce.NewAccountSetup;
import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class RecordEcommerceTransactionsTask {

    private final RecordEcommerceTransactions recordEcommerceTransactions;

    RecordEcommerceTransactionsTask(RecordEcommerceTransactions recordEcommerceTransactions) {
        this.recordEcommerceTransactions = recordEcommerceTransactions;
    }

    @DomainEventHandler
    @EventListener
    void on(NewAccountSetup event) {
        recordEcommerceTransactions.handle(event.clientId(), event.accountId());
    }
}
