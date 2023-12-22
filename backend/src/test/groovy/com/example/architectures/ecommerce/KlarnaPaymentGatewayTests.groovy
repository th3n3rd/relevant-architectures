package com.example.architectures.ecommerce


import com.example.architectures.common.ClientId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

@SpringBootTest
class KlarnaPaymentGatewayTests extends Specification {

    static private server = new KlarnaServer()
    static final anyClientId = new ClientId(123)
    static final anyAccountId = new AccountId("789")

    @Autowired
    private PaymentGateway transactionsGateway

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("payment-gateway.klarna.uri", { server.baseUrl() })
    }

    void cleanup() {
        server.reset()
    }

    def "fetches all transactions for a given account"() {
        given:
        server.givenConsentIsApproved()
        server.givenExistingTransactions(anyAccountId, [
            [id: UUID.randomUUID(), amount: [ amount: "90.0", currency: "EUR" ]]
        ])

        when:
        def transactions = transactionsGateway.fetchTransactions(anyClientId, anyAccountId)

        then:
        transactions == [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("90.0"), "EUR")
        ]
    }

    def "fails to fetch transactions if does not have a consent"() {
        given:
        server.givenConsentIsRejected()

        when:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId)

        then:
        thrown(FetchTransactionsFailed.Unauthorised)
    }

    def "fails to fetch transactions if the consent is expired"() {
        given:
        server.givenConsentWillExpire()

        when:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId)

        then:
        thrown(FetchTransactionsFailed.Unauthorised)
    }
}
