package com.example.architectures.postings

import spock.lang.Specification

class ImportTransactionsTests extends Specification {

    def transactions = new InMemoryTransactions()
    def transactionsGateway = Mock(TransactionsGateway)
    def generatePostings = new ImportTransactions(transactionsGateway, transactions)

    def "import transactions for the given client and account"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789
        transactionsGateway.fetchAll(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, new BigDecimal("5.00"), "EUR"),
            new Transaction(anyClientId, new BigDecimal("75.00"), "GBP"),
            new Transaction(anyClientId, new BigDecimal("150.00"), "GBP"),
        ]

        when:
        generatePostings.handle(anyClientId, anyAccountId)

        then:
        transactions.findAll() == [
            new Transaction(anyClientId, new BigDecimal("5.00"), "EUR"),
            new Transaction(anyClientId, new BigDecimal("75.00"), "GBP"),
            new Transaction(anyClientId, new BigDecimal("150.00"), "GBP"),
        ]
    }
}
