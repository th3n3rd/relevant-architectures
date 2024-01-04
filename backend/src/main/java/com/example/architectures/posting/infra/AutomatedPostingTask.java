package com.example.architectures.posting.infra;

import com.example.architectures.posting.JournalEntryCompleted;
import com.example.architectures.posting.PostJournalEntry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class AutomatedPostingTask {

    private final PostJournalEntry postJournalEntry;

    AutomatedPostingTask(PostJournalEntry postJournalEntry) {
        this.postJournalEntry = postJournalEntry;
    }

    @EventListener(JournalEntryCompleted.class)
    void on(JournalEntryCompleted event) {
        postJournalEntry.handle(event.id());
    }
}
