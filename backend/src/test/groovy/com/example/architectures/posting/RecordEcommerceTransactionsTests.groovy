package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.ecommerce.PaymentGateway
import com.example.architectures.ecommerce.Transaction
import spock.lang.Specification

class RecordEcommerceTransactionsTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("789")

    def transactionsGateway = Mock(PaymentGateway)
    def journal = new InMemoryJournal()
    def recordEcommerceTransactions = new RecordEcommerceTransactions(transactionsGateway, journal)

    def "generate one journal entry foreach transaction received"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("100.0"), "EUR"),
            new Transaction(anyClientId, anyAccountId, new BigDecimal("45.0"), "GBP"),
        ]

        when:
        recordEcommerceTransactions.handle(anyClientId, anyAccountId)

        then:
        match(journal.findAll(), [
            JournalEntry.fromEcommerce(anyAccountId)
                .clientId(anyClientId)
                .amount(new BigDecimal("100.0"))
                .currency("EUR")
                .build(),
            JournalEntry.fromEcommerce(anyAccountId)
                .clientId(anyClientId)
                .amount(new BigDecimal("45.0"))
                .currency("GBP")
                .build(),
        ])
    }

    def "generates journal entries in an incomplete status"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("95.0"), "GBP"),
        ]

        when:
        recordEcommerceTransactions.handle(anyClientId, anyAccountId)

        then:
        def entries = journal.findAll()
        entries.first().isIncomplete()
    }

    def "generates unique journal entries"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("10.0"), "GBP"),
            new Transaction(anyClientId, anyAccountId, new BigDecimal("60.0"), "EUR"),
        ]

        when:
        recordEcommerceTransactions.handle(anyClientId, anyAccountId)

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
        assert expected.amount() == actual.amount()
        assert expected.currency() == actual.currency()
        assert expected.status() == actual.status()
        assert expected.metadata() == actual.metadata()
        assert expected.lines() == actual.lines()
    }
}
