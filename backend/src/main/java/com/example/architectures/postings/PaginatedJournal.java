package com.example.architectures.postings;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface PaginatedJournal {
    Page<JournalEntry> findAllByClientId(ClientId clientId, Pageable page);
}
