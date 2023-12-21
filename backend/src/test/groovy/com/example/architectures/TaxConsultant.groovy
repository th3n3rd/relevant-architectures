package com.example.architectures

import com.example.architectures.auth.AuthServer
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.postings.ClientId
import com.example.architectures.postings.ConsultantId
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.RequestEntity

class TaxConsultant {
    private final TestRestTemplate httpClient
    private final ConsultantId consultantId
    private String authenticationToken

    TaxConsultant(TestRestTemplate httpClient, ConsultantId consultantId) {
        this.httpClient = httpClient
        this.consultantId = consultantId
    }

    def authenticateOn(AuthServer server) {
        this.authenticationToken = server.validBearerToken(consultantId)
    }

    def setupAccount(ClientId clientId, AccountId accountId) {
        def request = RequestEntity
            .post("/clients/{clientId}/accounts", clientId.value())
            .header("Authorization", "Bearer $authenticationToken")
            .body([ accountId: accountId.value() ])
        def response = httpClient.exchange(request, Void)
        assert response.statusCode.is2xxSuccessful()
    }

    void journalContains(ClientId clientId, expected) {
        def request = RequestEntity
            .get("/clients/{clientId}/journal", clientId.value())
            .header("Authorization", "Bearer $authenticationToken")
            .build()
        def response = httpClient.exchange(request, JournalEntries)
        assert response.statusCode.is2xxSuccessful()
        def actual = response.body.entries.collect {
            [
                clientId: it.clientId,
                accountId: it.accountId,
                amount: it.amount,
                currency: it.currency
            ]
        }
        assert expected == actual
    }

    static class JournalEntries {
        List<JournalEntry> entries
    }

    static class JournalEntry {
        int clientId
        String accountId
        String amount
        String currency
    }
}
