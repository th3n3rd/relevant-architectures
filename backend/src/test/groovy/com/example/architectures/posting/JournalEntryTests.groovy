package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.ecommerce.AccountId
import spock.lang.Specification

import static com.example.architectures.posting.FinancialAccount.asset
import static com.example.architectures.posting.FinancialAccount.revenue
import static com.example.architectures.posting.JournalEntry.Line.credit
import static com.example.architectures.posting.JournalEntry.Line.debit

class JournalEntryTests extends Specification {

    def "two entries with the same identifier, but different values, are considered equal"() {
        given:
        def first = JournalEntry
            .builder()
            .clientId(new ClientId(456))
            .amount(new BigDecimal("10.0"))
            .currency("EUR")
            .build()

        when:
        def second = first.withAmount(new BigDecimal("15.0"))
        def third = first.withId(new JournalEntryId())

        then:
        first == second
        first != third
    }

    def "entries with fully balanced entries and "() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(new ClientId(456))
            .amount(new BigDecimal("10.0"))
            .currency("EUR")
            .build()

        when:
        def updatedEntry = entry.withLines(List.of(
            credit(asset("cash"), new BigDecimal("10.0"), "EUR"),
            debit(revenue("sales-revenue"), new BigDecimal("10.0"), "EUR"),
        ))

        then:
        updatedEntry.isComplete()
    }

    def "entries with no lines are incomplete"() {
        when:
        def entry = JournalEntry
            .builder()
            .clientId(new ClientId(456))
            .amount(new BigDecimal("50.0"))
            .currency("EUR")
            .build()

        then:
        entry.isIncomplete()
    }

    def "entries partially balanced are incomplete"() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(new ClientId(456))
            .amount(new BigDecimal("120.0"))
            .currency("GBP")
            .build()

        when:
        def updatedEntry = entry.withLines(List.of(
            credit(asset("cash"), new BigDecimal("100.0"), "GBP"),
            debit(revenue("sales-revenue"), new BigDecimal("100.0"), "GBP"),
        ))

        then:
        updatedEntry.isIncomplete()
    }
}
