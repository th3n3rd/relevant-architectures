package com.example.architectures.auth

import com.example.architectures.postings.ConsultantId
import org.mockserver.configuration.Configuration
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.slf4j.event.Level

import static org.mockserver.integration.ClientAndServer.startClientAndServer

class AuthServer {
    private jwk = Auth.generateSigningKey()

    private configuration = Configuration
        .configuration()
        .logLevel(Level.WARN)

    private server = startClientAndServer(configuration)

    AuthServer() {
        server
            .when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/.well-known/openid-configuration"))
            .withId("auth server - provide configuration")
            .respond(HttpResponse.response()
                .withStatusCode(200)
                .withBody(JsonBody.json([
                    issuer: url(),
                    jwks_uri: url("/protocol/openid-connect/certs")
                ])))

        server
            .when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/protocol/openid-connect/certs"))
            .withId("auth server - list public keys")
            .respond(HttpResponse.response()
                .withStatusCode(200)
                .withBody(JsonBody.json([
                    keys: [jwk.toPublicJWK().toJSONObject()]
                ])))
    }

    def baseUrl() {
        return url()
    }

    def url(String path = "") {
        return "http://localhost:$server.localPort$path" as String
    }

    def validBearerToken(ConsultantId consultantId) {
        return Auth.validToken(consultantId, url(), jwk).serialize()
    }
}
