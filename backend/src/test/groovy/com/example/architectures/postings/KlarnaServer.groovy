package com.example.architectures.postings

import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody

import static org.mockserver.integration.ClientAndServer.startClientAndServer

class KlarnaServer {
    private server = startClientAndServer()

    def baseUrl() {
        return "http://localhost:$server.localPort"
    }

    def givenExistingTransactions(accountId, transactions) {
        server
            .when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/v2/accounts/$accountId/transactions"))
            .respond(HttpResponse.response()
                .withStatusCode(200)
                .withBody(JsonBody.json([
                    account_id: accountId,
                    transactions: transactions
                ])))
    }

}
