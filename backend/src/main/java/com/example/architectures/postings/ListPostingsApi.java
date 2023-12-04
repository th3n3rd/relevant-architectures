package com.example.architectures.postings;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ListPostingsApi {

    private final TransactionsGateway transactionsGateway;

    ListPostingsApi(TransactionsGateway transactionsGateway) {
        this.transactionsGateway = transactionsGateway;
    }

    @GetMapping("/clients/{clientId}/accounts/{accountId}/postings")
    PostingList handle(@PathVariable int clientId, @PathVariable int accountId) {
        return new PostingList(
            transactionsGateway.fetchAll(clientId, accountId)
                .stream()
                .map(it -> new Posting(
                    it.clientId(),
                    it.amount().toString(),
                    it.currency())
                )
                .toList()
        );
    }

    record PostingList(List<Posting> postings) {}
    record Posting(int clientId, String amount, String currency) {}
}
