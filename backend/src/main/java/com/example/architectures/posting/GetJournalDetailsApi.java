package com.example.architectures.posting;

import com.example.architectures.auth.ConsultantAuthorised;
import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetJournalDetailsApi {

    private final PaginatedJournal journal;

    GetJournalDetailsApi(PaginatedJournal journal) {
        this.journal = journal;
    }

    @ConsultantAuthorised
    @GetMapping("/clients/{clientId}/journal")
    Response.Journal handle(
        @PathVariable ClientId clientId,
        Pageable page
    ) {
        var entries = journal.findAllByClientId(clientId, page);
        return new Response.Journal(
            entries
                .stream()
                .map(it -> new Response.Entry(
                    it.id(),
                    it.clientId(),
                    it.amount().toString(),
                    it.currency(),
                    it.status(),
                    it.lines()
                        .stream()
                        .map(line -> new Response.Entry.Line(
                            line.account().name(),
                            line.amount().toString(),
                            line.currency(),
                            line.type()
                        ))
                        .toList(),
                    new Response.Entry.Metadata(
                        it.metadata().origin(),
                        it.metadata().accountId()
                    )
                ))
                .toList(),
            new Response.Journal.Metadata(
                entries.getNumber(),
                entries.getSize(),
                entries.getTotalPages(),
                entries.getTotalElements()
            )
        );
    }

    static class Response {
        record Journal(List<Entry> entries, Metadata metadata) {
            record Metadata(int pageNumber, int pageSize, int totalPages, long totalElements) {}
        }
        record Entry(
            JournalEntryId id,
            ClientId clientId,
            String amount,
            String currency,
            JournalEntry.Status status,
            List<Line> lines,
            Metadata metadata
        ) {
            record Line(String accountName, String amount, String currency, JournalEntry.Line.Type type) {}
            record Metadata(String origin, AccountId accountId) {}
        }
    }
}
