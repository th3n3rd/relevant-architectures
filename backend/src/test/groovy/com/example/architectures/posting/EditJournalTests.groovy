package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.ecommerce.AccountId
import spock.lang.Specification

import static com.example.architectures.posting.FinancialAccount.asset
import static com.example.architectures.posting.FinancialAccount.revenue
import static com.example.architectures.posting.JournalEntry.Line.credit
import static com.example.architectures.posting.JournalEntry.Line.debit

class EditJournalTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("729")

    def journal = new InMemoryJournal()
    def editJournal = new EditJournal(journal)

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
            debit(asset("cost"), new BigDecimal("100.0"), "EUR"),
            credit(revenue("sales-revenue"), new BigDecimal("100.0"), "EUR"),
        ))

        then:
        def updatedEntry = journal.findById(entry.id()).orElseThrow()
        updatedEntry.lines() == [
            debit(asset("cost"), new BigDecimal("100.0"), "EUR"),
            credit(revenue("sales-revenue"), new BigDecimal("100.0"), "EUR"),
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
            debit(asset("cost"), new BigDecimal("25.0"), "EUR"),
            credit(revenue("sales-revenue"), new BigDecimal("20.0"), "EUR"),
        ))

        then:
        thrown(JournalEntryUnbalanced)
    }
}
