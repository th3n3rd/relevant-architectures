package com.example.architectures.postings

import spock.lang.Specification

class GenerateJournalEntriesTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId(789)

    def transactionsGateway = Mock(PaymentGateway)
    def journal = new InMemoryJournal()
    def generatePostings = new GenerateJournalEntries(transactionsGateway, journal)

    def "generate one journal entry foreach transaction received"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new Transaction(anyClientId, anyAccountId, new BigDecimal("45.0"), "GPB"),
        ]

        when:
        generatePostings.handle(anyClientId, anyAccountId)

        then:
        journal.findAll() == [
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("45.0"), "GPB"),
        ]
    }

}
