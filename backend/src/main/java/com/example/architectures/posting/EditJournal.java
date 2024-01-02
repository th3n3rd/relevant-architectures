package com.example.architectures.posting;

import com.example.architectures.common.EventPublisher;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class EditJournal {

    private final Journal journal;
    private final EventPublisher eventPublisher;

    EditJournal(Journal journal, EventPublisher eventPublisher) {
        this.journal = journal;
        this.eventPublisher = eventPublisher;
    }

    void handle(JournalEntryId entryId, List<JournalEntry.Line> entryLines) {
        var entry = journal.findById(entryId).orElseThrow();
        var updatedEntry = entry.withLines(entryLines);
        journal.save(updatedEntry);
        eventPublisher.publish(new JournalEntryCompleted(entryId));
    }
}
