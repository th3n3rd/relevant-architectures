package com.example.architectures.common

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.mockserver.configuration.Configuration
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.slf4j.event.Level

import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt

class AuthServer {
    private jwk = new RSAKeyGenerator(2048)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .generate()

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

    def validToken(int consultantId) {
        def signer = new RSASSASigner(jwk);

        def token = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(jwk.getKeyID())
                .build(),
            new JWTClaimsSet.Builder()
                .subject("alice")
                .claim("consultantId", consultantId)
                .issuer(url())
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build()
        )

        token.sign(signer)

        return token.serialize()
    }

    static validTokenForSpring(int consultantId) {
        return jwt().jwt({ it.claim("consultantId", consultantId)})
    }
}
