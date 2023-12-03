package com.example.demo

import org.springframework.boot.test.web.client.TestRestTemplate

class TaxConsultant {
    private final TestRestTemplate httpClient
    private final int consultantId

    TaxConsultant(TestRestTemplate httpClient, int consultantId) {
        this.consultantId = consultantId
        this.httpClient = httpClient
    }

    def setupAccount(int clientId, int accountId) {
        def response = httpClient.postForEntity(
            "/clients/{clientId}/accounts",
            [ accountId: accountId ],
            Void,
            clientId
        )
        assert response.statusCode.is2xxSuccessful()
    }

    void receivedPostings(clientId, accountId, expected) {
        def response = httpClient.getForEntity(
            "/clients/{clientId}/accounts/{accountId}/postings",
            PostingsList,
            clientId,
            accountId
        )

        def actual = response.body.postings.collect {
            [
                clientId: it.clientId,
                amount: it.amount,
                currency: it.currency
            ]
        }
        assert expected == actual
    }

    static class PostingsList {
        List<Posting> postings
    }

    static class Posting {
        int clientId
        String amount
        String currency
    }
}
