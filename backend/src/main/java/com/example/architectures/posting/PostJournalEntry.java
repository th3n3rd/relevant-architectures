package com.example.architectures.posting;

import org.springframework.stereotype.Component;

@Component
class PostJournalEntry {

    private final Journal journal;
    private final Ledgers ledgers;

    PostJournalEntry(Journal journal, Ledgers ledgers) {
        this.journal = journal;
        this.ledgers = ledgers;
    }

    void handle(JournalEntryId entryId) {
        var entry = journal.findById(entryId).orElseThrow();
        var ledger = ledgers.findByClientId(entry.clientId()).orElseThrow();
        var postedEntry = entry.postToLedger(ledger);
        journal.save(postedEntry);
        ledgers.save(ledger);
    }

}
