package com.example.architectures.postings;

import java.util.List;

interface Journal {
    void saveAll(List<JournalEntry> entries);
}
