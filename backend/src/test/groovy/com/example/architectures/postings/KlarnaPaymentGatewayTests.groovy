package com.example.architectures.postings


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

@SpringBootTest
class KlarnaPaymentGatewayTests extends Specification {

    @Autowired
    private PaymentGateway transactionsGateway

    static private server = new KlarnaServer()

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("payment-gateway.klarna.uri", { server.baseUrl() })
    }

    def "fetches all transactions for a given account"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789
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

}
