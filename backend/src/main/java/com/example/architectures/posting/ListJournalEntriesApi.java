package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.auth.ConsultantAuthorised;
import com.example.architectures.ecommerce.AccountId;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ListJournalEntriesApi {

    private final PaginatedJournal journal;

    ListJournalEntriesApi(PaginatedJournal journal) {
        this.journal = journal;
    }

    @ConsultantAuthorised
    @GetMapping("/clients/{clientId}/journal")
    Response.Entries handle(
        @PathVariable ClientId clientId,
        Pageable page
    ) {
        var entries = journal.findAllByClientId(clientId, page);
        return new Response.Entries(
            entries
                .stream()
                .map(it -> new Response.Entry(
                    it.id(),
                    it.clientId(),
                    it.accountId(),
                    it.amount().toString(),
                    it.currency())
                )
                .toList(),
            new Response.Metadata(
                entries.getNumber(),
                entries.getSize(),
                entries.getTotalPages(),
                entries.getTotalElements()
            )
        );
    }

    static class Response {
        record Entries(List<Entry> entries, Metadata metadata) {}
        record Entry(JournalEntryId id, ClientId clientId, AccountId accountId, String amount, String currency) {}
        record Metadata(int pageNumber, int pageSize, int totalPages, long totalElements) {}
    }
}
