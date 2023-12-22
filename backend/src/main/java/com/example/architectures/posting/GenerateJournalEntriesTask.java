package com.example.architectures.posting;

import com.example.architectures.ecommerce.NewAccountSetup;
import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class GenerateJournalEntriesTask {

    private final GenerateJournalEntries generateJournalEntries;

    GenerateJournalEntriesTask(GenerateJournalEntries generateJournalEntries) {
        this.generateJournalEntries = generateJournalEntries;
    }

    @DomainEventHandler
    @EventListener
    void on(NewAccountSetup event) {
        generateJournalEntries.handle(event.clientId(), event.accountId());
    }
}
