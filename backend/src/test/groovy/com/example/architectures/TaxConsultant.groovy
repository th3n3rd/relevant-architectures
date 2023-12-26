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
        def journal = journalDetails(clientId)
        def actual = journal.entries.collect {
            [
                amount: it.amount,
                currency: it.currency,
                status: it.status,
                lines: it.lines.collect {
                    [
                        accountName: it.accountName,
                        amount: it.amount,
                        currency: it.currency,
                        type: it.type
                    ]
                },
                metadata: [
                    origin: it.metadata.origin,
                    accountId: it.metadata.accountId
                ]
            ]
        }
        assert actual.containsAll(expected)
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

    void editJournal(ClientId clientId, int nthEntry, entryLines) {
        def journal = journalDetails(clientId)
        def entry = journal.entries.get(nthEntry)
        def request = RequestEntity
            .patch("/clients/{clientId}/journal/{entryId}", clientId.value(), entry.id)
            .header("Authorization", "Bearer $authenticationToken")
            .body([
                lines: entryLines
            ])
        def response = httpClient.exchange(request, Void)
        assert response.statusCode.is2xxSuccessful()
    }

    private Journal journalDetails(ClientId clientId) {
        def request = RequestEntity
            .get("/clients/{clientId}/journal", clientId.value())
            .header("Authorization", "Bearer $authenticationToken")
            .build()
        def response = httpClient.exchange(request, Journal)
        assert response.statusCode.is2xxSuccessful()
        return response.body
    }

    static class Journal {
        List<Entry> entries

        static class Entry {
            String id
            String amount
            String currency
            String status
            List<Line> lines
            Metadata metadata

            static class Line {
                String accountName
                String amount
                String currency
                String type
            }

            static class Metadata {
                String origin
                String accountId
            }
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
