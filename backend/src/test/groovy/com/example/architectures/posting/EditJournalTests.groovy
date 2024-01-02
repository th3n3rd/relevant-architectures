package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.common.InMemoryEventPublisher
import com.example.architectures.ecommerce.AccountId
import spock.lang.Specification

import static com.example.architectures.posting.ChartOfAccounts.Cash
import static com.example.architectures.posting.ChartOfAccounts.SalesRevenue
import static com.example.architectures.posting.JournalEntry.Line.credit
import static com.example.architectures.posting.JournalEntry.Line.debit

class EditJournalTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("729")

    def eventPublisher = new InMemoryEventPublisher();
    def journal = new InMemoryJournal()
    def editJournal = new EditJournal(journal, eventPublisher)

    def "append the given entry lines for an existing entry"() {
        given:
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("100.0"))
            .currency("EUR")
            .build()
        journal.save(entry)

        when:
        editJournal.handle(entry.id(), List.of(
            debit(Cash, new BigDecimal("100.0"), "EUR"),
            credit(SalesRevenue, new BigDecimal("100.0"), "EUR"),
        ))

        then:
        def updatedEntry = journal.findById(entry.id()).orElseThrow()
        updatedEntry.lines() == [
            debit(Cash, new BigDecimal("100.0"), "EUR"),
            credit(SalesRevenue, new BigDecimal("100.0"), "EUR"),
        ]
    }

    def "publish an event when an entry is completed"() {
        given:
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("35.0"))
            .currency("GBP")
            .build()
        journal.save(entry)

        when:
        editJournal.handle(entry.id(), List.of(
            debit(Cash, new BigDecimal("35.0"), "GBP"),
            credit(SalesRevenue, new BigDecimal("35.0"), "GBP"),
        ))

        then:
        eventPublisher.publishedEvents() == [
            new JournalEntryCompleted(entry.id())
        ]
    }

    def "fails to update an entry if the entry lines are not balanced"() {
        given:
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("50.0"))
            .currency("EUR")
            .build()
        journal.save(entry)

        when:
        editJournal.handle(entry.id(), List.of(
            debit(Cash, new BigDecimal("25.0"), "EUR"),
            credit(SalesRevenue, new BigDecimal("20.0"), "EUR"),
        ))

        then:
        thrown(JournalEntryUnbalanced)
    }
}
