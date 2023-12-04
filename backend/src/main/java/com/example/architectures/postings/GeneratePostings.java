package com.example.architectures.postings;

import org.springframework.stereotype.Component;

@Component
class GeneratePostings {

    private final TransactionsGateway transactionsGateway;
    private final Postings postings;

    GeneratePostings(TransactionsGateway transactionsGateway, Postings postings) {
        this.transactionsGateway = transactionsGateway;
        this.postings = postings;
    }

    void handle(int clientId, int accountId) {
        var newPostings = transactionsGateway.fetchAll(clientId, accountId)
            .stream()
            .map(it -> new Posting(it.clientId(), it.accountId(), it.amount(), it.currency()))
            .toList();

        postings.saveAll(newPostings);
    }
}
