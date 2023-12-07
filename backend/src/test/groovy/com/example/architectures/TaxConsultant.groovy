package com.example.architectures

import com.example.architectures.common.AuthServer
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.RequestEntity

class TaxConsultant {
    private final TestRestTemplate httpClient
    private final int consultantId
    private String authenticationToken

    TaxConsultant(TestRestTemplate httpClient, int consultantId) {
        this.httpClient = httpClient
        this.consultantId = consultantId
    }

    def authenticateOn(AuthServer server) {
        this.authenticationToken = server.validToken();
    }

    def setupAccount(int clientId, int accountId) {
        def request = RequestEntity
            .post("/clients/{clientId}/accounts", clientId)
            .header("Authorization", "Bearer $authenticationToken")
            .body([ accountId: accountId ])
        def response = httpClient.exchange(request, Void)
        assert response.statusCode.is2xxSuccessful()
    }

    void receivedPostings(clientId, accountId, expected) {
        def request = RequestEntity
            .get("/clients/{clientId}/accounts/{accountId}/postings", clientId, accountId)
            .header("Authorization", "Bearer $authenticationToken")
            .build()
        def response = httpClient.exchange(request, PostingsList)
        assert response.statusCode.is2xxSuccessful()
        def actual = response.body.postings.collect {
            [
                clientId: it.clientId,
                accountId: it.accountId,
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
        int accountId
        String amount
        String currency
    }
}
