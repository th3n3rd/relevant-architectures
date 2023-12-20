package com.example.architectures.postings

import spock.lang.Specification

class GeneratePostingsTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = 789

    def transactionsGateway = Mock(PaymentGateway)
    def postings = new InMemoryPostings()
    def generatePostings = new GeneratePostings(transactionsGateway, postings)

    def "generate one posting foreach transaction received"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new Transaction(anyClientId, anyAccountId, new BigDecimal("45.0"), "GPB"),
        ]

        when:
        generatePostings.handle(anyClientId, anyAccountId)

        then:
        postings.findAll() == [
            new Posting(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new Posting(anyClientId, anyAccountId, new BigDecimal("45.0"), "GPB"),
        ]
    }

}
