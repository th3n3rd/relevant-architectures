package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryJournal implements Journal, PaginatedJournal {
    private final List<JournalEntry> entries = new ArrayList<>();

    @Override
    public void saveAll(List<JournalEntry> entries) {
        this.entries.addAll(entries);
    }

    public List<JournalEntry> findAll() {
        return entries;
    }

    @Override
    public Page<JournalEntry> findAllByClientId(ClientId clientId, Pageable page) {
        var nonPaginatedEntries = entries
            .stream()
            .filter(it -> it.clientId().equals(clientId))
            .toList();

        var paginatedEntries = nonPaginatedEntries
            .stream()
            .skip(page.getOffset())
            .limit(page.getPageSize())
            .toList();

        return new PageImpl<>(
            paginatedEntries,
            page,
            nonPaginatedEntries.size()
        );
    }

    public void deleteAll() {
        entries.clear();
    }
}
