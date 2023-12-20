package com.example.architectures.postings;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ListPostingsApi {

    private final PaginatedPostings postings;

    ListPostingsApi(PaginatedPostings postings) {
        this.postings = postings;
    }

    @ConsultantAuthorised
    @GetMapping("/clients/{clientId}/accounts/{accountId}/postings")
    PaginatedPostingList handle(
        @PathVariable ClientId clientId,
        @PathVariable AccountId accountId,
        Pageable page
    ) {
        var paginatedPostings = postings.findAllByClientIdAndAccountId(clientId, accountId, page);
        return new PaginatedPostingList(
            paginatedPostings
                .stream()
                .map(it -> new Posting(
                    it.clientId(),
                    it.accountId(),
                    it.amount().toString(),
                    it.currency())
                )
                .toList(),
            new Metadata(
                paginatedPostings.getNumber(),
                paginatedPostings.getSize(),
                paginatedPostings.getTotalPages(),
                paginatedPostings.getTotalElements()
            )
        );
    }

    record PaginatedPostingList(List<Posting> postings, Metadata metadata) {}
    record Metadata(int pageNumber, int pageSize, int totalPages, long totalElements) {}
    record Posting(ClientId clientId, AccountId accountId, String amount, String currency) {}
}
