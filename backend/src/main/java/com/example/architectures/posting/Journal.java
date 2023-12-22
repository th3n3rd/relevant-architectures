package com.example.architectures.posting;

import java.util.List;

interface Journal {
    void saveAll(List<JournalEntry> entries);
}
