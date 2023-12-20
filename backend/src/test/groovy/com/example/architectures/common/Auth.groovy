package com.example.architectures.common

import com.example.architectures.postings.ConsultantId
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.oauth2.jwt.Jwt

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication

class Auth {

    static validToken(ConsultantId consultantId, String issuerUri, RSAKey signingKey = generateSigningKey()) {
        def signer = new RSASSASigner(signingKey);

        def token = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(signingKey.getKeyID())
                .build(),
            new JWTClaimsSet.Builder()
                .subject("alice")
                .claim("consultantId", consultantId.value())
                .issuer(issuerUri)
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build()
        )

        token.sign(signer)

        return token
    }

    static authenticatedConsultant(ConsultantId consultantId, RSAKey signingKey = generateSigningKey()) {
        def token = validToken(consultantId, "https://dont-care.example.com", signingKey)
        def claims = token.getJWTClaimsSet()
        return authentication(new ConsultantAuthToken(
            Jwt.withTokenValue(token.serialize())
                .claims {
                    it.putAll(claims.getClaims())
                }
                .headers {
                    it.put("alg", token.getHeader().getAlgorithm().getName())
                    it.put("kid", token.getHeader().getKeyID())
                }
                .issuedAt(claims.getIssueTime().toInstant())
                .expiresAt(claims.getExpirationTime().toInstant())
                .build()
        ))
    }

    static RSAKey generateSigningKey() {
        return new RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .generate()
    }
}
