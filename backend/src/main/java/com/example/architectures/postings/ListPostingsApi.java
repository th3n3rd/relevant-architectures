package com.example.architectures.postings;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ListPostingsApi {

    private final Postings postings;

    ListPostingsApi(Postings postings) {
        this.postings = postings;
    }

    @PreAuthorize("@authorisations.existsByConsultantIdAndClientId(principal.claims['consultantId'], #clientId)")
    @GetMapping("/clients/{clientId}/accounts/{accountId}/postings")
    PostingList handle(
        @PathVariable int clientId,
        @PathVariable int accountId
    ) {
        return new PostingList(
            postings.findAllByClientIdAndAccountId(clientId, accountId)
                .stream()
                .map(it -> new Posting(
                    it.clientId(),
                    it.accountId(),
                    it.amount().toString(),
                    it.currency())
                )
                .toList()
        );
    }

    record PostingList(List<Posting> postings) {}
    record Posting(int clientId, int accountId, String amount, String currency) {}
}
