package com.example.architectures.posting;

import java.util.List;
import java.util.Optional;

public interface Journal {
    Optional<JournalEntry> findById(JournalEntryId id);
    void save(JournalEntry entry);
    void saveAll(List<JournalEntry> entries);
}
