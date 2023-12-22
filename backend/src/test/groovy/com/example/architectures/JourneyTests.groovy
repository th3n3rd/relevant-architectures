package com.example.architectures

import com.example.architectures.auth.AuthServer
import com.example.architectures.auth.InMemoryAuthorisations
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.ecommerce.klarna.KlarnaServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
class JourneyTests extends Specification {

    static private klarnaServer = new KlarnaServer()
    static private authServer = new AuthServer()
    static final consultantId = new ConsultantId(456)
    static final clientId = new ClientId(123)
    static final klarnaAccount = new AccountId("789")

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
            [id: klarnaServer.anyTransactionId(), amount: [amount: "10.0", currency: "EUR"]],
            [id: klarnaServer.anyTransactionId(), amount: [amount: "15.0", currency: "EUR"]],
        ])
    }

    def "tax consultant can inspect entries automatically generated in the journal"() {
        def consultant = new TaxConsultant(httpClient, consultantId)

        consultant.authenticateOn(authServer)
        consultant.setupEcommerceAccount(clientId, klarnaAccount)

        expect:
        consultant.journalContains(clientId, [
            [clientId: clientId.value(), accountId: klarnaAccount.value(), amount: "10.0", currency: "EUR"],
            [clientId: clientId.value(), accountId: klarnaAccount.value(), amount: "15.0", currency: "EUR"],
        ])
    }

}
