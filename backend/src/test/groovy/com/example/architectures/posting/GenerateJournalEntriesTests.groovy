package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.ecommerce.PaymentGateway
import com.example.architectures.ecommerce.Transaction
import spock.lang.Specification

class GenerateJournalEntriesTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("789")

    def transactionsGateway = Mock(PaymentGateway)
    def journal = new InMemoryJournal()
    def generatePostings = new GenerateJournalEntries(transactionsGateway, journal)

    def "generate one journal entry foreach transaction received"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new Transaction(anyClientId, anyAccountId, new BigDecimal("45.0"), "GBP"),
        ]

        when:
        generatePostings.handle(anyClientId, anyAccountId)

        then:
        match(journal.findAll(), [
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("45.0"), "GBP"),
        ])
    }

    def "generates unique journal entries"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("10.0"), "GBP"),
            new Transaction(anyClientId, anyAccountId, new BigDecimal("60.0"), "EUR"),
        ]

        when:
        generatePostings.handle(anyClientId, anyAccountId)

        then:
        def entries = journal.findAll()
        entries.first().id() != entries.last().id()
    }

    void match(List<JournalEntry> expected, List<JournalEntry> actual) {
        assert expected.size() == actual.size()
        expected.eachWithIndex { exp, int i -> match(exp, actual.get(i)) }
    }

    void match(JournalEntry expected, JournalEntry actual) {
        assert expected.clientId() == actual.clientId()
        assert expected.accountId() == actual.accountId()
        assert expected.amount() == actual.amount()
        assert expected.currency() == actual.currency()
    }
}
