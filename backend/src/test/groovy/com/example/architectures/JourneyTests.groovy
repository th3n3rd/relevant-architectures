package com.example.architectures

import com.example.architectures.common.AuthServer
import com.example.architectures.postings.ClientId
import com.example.architectures.postings.InMemoryAuthorisations
import com.example.architectures.postings.KlarnaServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(
    webEnvironment = RANDOM_PORT
)
class JourneyTests extends Specification {

    static private klarnaServer = new KlarnaServer()
    static private authServer = new AuthServer()
    static final consultantId = 456
    static final clientId = new ClientId(123)
    static final klarnaAccount = 789

    @Autowired
    private TestRestTemplate httpClient

    @Autowired
    private InMemoryAuthorisations authorisations

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("payment-gateway.klarna.uri", { klarnaServer.baseUrl() })
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", { authServer.baseUrl() })
    }

    void setup() {
        authorisations.authorise(consultantId, clientId)
        klarnaServer.givenConsentIsApproved()
        klarnaServer.givenExistingTransactions(klarnaAccount, [
            [id: UUID.randomUUID(), amount: [ amount: "10.0", currency: "EUR"]],
            [id: UUID.randomUUID(), amount: [ amount: "15.0", currency: "EUR"]],
        ])
    }

    def "tax consultant receives postings proposal"() {
        def consultant = new TaxConsultant(httpClient, consultantId)

        consultant.authenticateOn(authServer)
        consultant.setupAccount(clientId, klarnaAccount)

        expect:
        consultant.receivedPostings(clientId, klarnaAccount, [
            [clientId: clientId.value(), accountId: klarnaAccount, amount: "10.0", currency: "EUR"],
            [clientId: clientId.value(), accountId: klarnaAccount, amount: "15.0", currency: "EUR"],
        ])
    }

}
