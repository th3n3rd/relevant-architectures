package com.example.architectures.posting;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
class EditJournal {

    private final Journal journal;

    EditJournal(Journal journal) {
        this.journal = journal;
    }

    void handle(JournalEntryId entryId, List<JournalEntry.Line> entryLines) {
        var entry = journal.findById(entryId).orElseThrow();
        var updatedEntry = entry.withLines(entryLines);
        journal.save(updatedEntry);
    }
}
