package com.example.architectures.postings;

import org.springframework.stereotype.Component;

@Component
class GeneratePostings {

    private final PaymentGateway paymentGateway;
    private final Postings postings;

    GeneratePostings(PaymentGateway paymentGateway, Postings postings) {
        this.paymentGateway = paymentGateway;
        this.postings = postings;
    }

    void handle(ClientId clientId, AccountId accountId) {
        var newPostings = paymentGateway.fetchTransactions(clientId, accountId)
            .stream()
            .map(it -> new Posting(it.clientId(), it.accountId(), it.amount(), it.currency()))
            .toList();

        postings.saveAll(newPostings);
    }
}
