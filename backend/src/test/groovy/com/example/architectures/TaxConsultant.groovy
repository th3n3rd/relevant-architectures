package com.example.architectures

import com.example.architectures.auth.AuthServer
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.ecommerce.AccountId
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

    def setupEcommerceAccount(ClientId clientId, AccountId accountId) {
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
        def response = httpClient.exchange(request, Journal)
        assert response.statusCode.is2xxSuccessful()
        def actual = response.body.entries.collect {
            [
                clientId: it.clientId,
                accountId: it.accountId,
                amount: it.amount,
                currency: it.currency,
                status: it.status
            ]
        }
        assert expected == actual
    }

    void createLedger(ClientId clientId) {
        def request = RequestEntity
            .post("/clients/{clientId}/ledger", clientId.value())
            .header("Authorization", "Bearer $authenticationToken")
            .build()
        def response = httpClient.exchange(request, Void)
        assert response.statusCode.is2xxSuccessful()
    }

    void ledgerContains(ClientId clientId, expected) {
        def request = RequestEntity
            .get("/clients/{clientId}/ledger", clientId.value())
            .header("Authorization", "Bearer $authenticationToken")
            .build()
        def response = httpClient.exchange(request, Ledger)
        assert response.statusCode.is2xxSuccessful()
        def actual = response.body.accounts.collect {
            [
                name: it.name,
                balance: it.balance
            ]
        }
        assert actual.containsAll(expected)
    }

    static class Journal {
        List<Entry> entries

        static class Entry {
            int clientId
            String accountId
            String amount
            String currency
            String status
        }
    }

    static class Ledger {
        List<Account> accounts

        static class Account {
            String name
            String balance
        }
    }
}
