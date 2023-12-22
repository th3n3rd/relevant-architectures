package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface PaginatedJournal {
    Page<JournalEntry> findAllByClientId(ClientId clientId, Pageable page);
}
