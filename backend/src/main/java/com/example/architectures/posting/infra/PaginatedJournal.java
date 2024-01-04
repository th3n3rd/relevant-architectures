package com.example.architectures.posting.infra;

import com.example.architectures.common.ClientId;
import com.example.architectures.posting.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface PaginatedJournal {
    Page<JournalEntry> findAllByClientId(ClientId clientId, Pageable page);
}
