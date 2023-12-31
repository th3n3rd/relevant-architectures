package com.example.architectures

import com.example.architectures.auth.infra.AuthServer
import com.example.architectures.auth.infra.InMemoryAuthorisations
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.ecommerce.infra.klarna.KlarnaServer
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
    static final firstEntry = 0

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

        consultant.createLedger(clientId)
        consultant.ledgerContains(clientId, [
            [name: "cash", balance: "0.0"],
            [name: "sales-revenue", balance: "0.0"]
        ])

        consultant.journalContains(clientId, [
            [amount: "10.0", currency: "EUR", status: "Incomplete", lines: [], metadata: [ origin: "e-commerce", accountId: klarnaAccount.value() ]],
            [amount: "15.0", currency: "EUR", status: "Incomplete", lines: [], metadata: [ origin: "e-commerce", accountId: klarnaAccount.value() ]],
        ])

        consultant.editJournal(clientId, firstEntry, [
            [type: "Debit", accountName: "cash", amount: "10.0", currency: "EUR"],
            [type: "Credit", accountName: "sales-revenue", amount: "10.0", currency: "EUR"]
        ])
        consultant.journalContains(clientId, [
            [
                amount: "10.0",
                currency: "EUR",
                status: "Posted",
                lines: [
                    [type: "Debit", accountName: "cash", amount: "10.0", currency: "EUR"],
                    [type: "Credit", accountName: "sales-revenue", amount: "10.0", currency: "EUR"]
                ],
                metadata: [ origin: "e-commerce", accountId: klarnaAccount.value() ]
            ],
        ])
        consultant.ledgerContains(clientId, [
            [name: "cash", balance: "10.0"],
            [name: "sales-revenue", balance: "10.0"]
        ])

        expect: true
    }

}
