package com.example.architectures.ecommerce


import org.mockserver.configuration.Configuration
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.slf4j.event.Level

import static org.mockserver.integration.ClientAndServer.startClientAndServer

class KlarnaServer {
    private validConsentId = "valid-consent"
    private expiredConsentId = "expired-consent"

    private configuration = Configuration
        .configuration()
        .logLevel(Level.WARN)

    private server = startClientAndServer(configuration)

    def reset() {
        server.reset()
    }

    def baseUrl() {
        return "http://localhost:$server.localPort"
    }

    def anyTransactionId() {
        return UUID.randomUUID()
    }

    def givenExistingTransactions(AccountId accountId, transactions) {
        server
            .when(HttpRequest.request()
                .withMethod("GET")
                .withHeader("consent-id", validConsentId)
                .withPath("/v2/accounts/$accountId.value/transactions"))
            .withId("klarna server - list transactions")
            .respond(HttpResponse.response()
                .withStatusCode(200)
                .withBody(JsonBody.json([
                    account_id: accountId.value(),
                    transactions: transactions
                ])))
    }

    def givenConsentIsRejected() {
        givenConsentIs("REJECTED")
    }

    def givenConsentIsApproved() {
        givenConsentIs("APPROVED", validConsentId)
    }

    def givenConsentWillExpire() {
        givenConsentIs("APPROVED", expiredConsentId)
        server
            .when(HttpRequest.request()
                .withHeader("consent-id", expiredConsentId))
            .withId("klarna server - consent expired")
            .respond(HttpResponse.response()
                .withStatusCode(403))
    }

    private void givenConsentIs(String status, consentId = "") {
        server
            .when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/v2/consent-sessions"))
            .withId("klarna server - request for consent is $status")
            .respond(HttpResponse.response()
                .withStatusCode(200)
                .withBody(JsonBody.json([
                    status: status,
                    consent_id: consentId
                ])))
    }
}
