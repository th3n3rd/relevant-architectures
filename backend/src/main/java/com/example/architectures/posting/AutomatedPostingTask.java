package com.example.architectures.posting;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class AutomatedPostingTask {

    private final Journal journal;
    private final Ledgers ledgers;

    AutomatedPostingTask(Journal journal, Ledgers ledgers) {
        this.journal = journal;
        this.ledgers = ledgers;
    }

    @EventListener(JournalEntryCompleted.class)
    void on(JournalEntryCompleted event) {
        var entry = journal.findById(event.id()).orElseThrow();
        var ledger = ledgers.findByClientId(entry.clientId()).orElseThrow();
        var postedEntry = entry.postToLedger(ledger);
        journal.save(postedEntry);
        ledgers.save(ledger);
    }
}
