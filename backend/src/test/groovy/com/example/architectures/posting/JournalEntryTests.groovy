package com.example.architectures.posting

import com.example.architectures.common.ClientId
import spock.lang.Specification

import java.time.LocalDateTime

import static com.example.architectures.posting.ChartOfAccounts.Cash
import static com.example.architectures.posting.ChartOfAccounts.SalesRevenue
import static com.example.architectures.posting.JournalEntry.Line.credit
import static com.example.architectures.posting.JournalEntry.Line.debit

class JournalEntryTests extends Specification {

    private static ClientId anyClientId = new ClientId(456)
    private static Ledger anyLedger = new Ledger(anyClientId, List.of(
        new LedgerAccount(Cash),
        new LedgerAccount(SalesRevenue),
    ))

    def "two entries with the same identifier, but different values, are considered equal"() {
        given:
        def first = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("10.0"))
            .currency("EUR")
            .build()

        when:
        def second = first.toBuilder().amount(new BigDecimal("15.0")).build()
        def third = first.toBuilder().id(new JournalEntryId()).build()

        then:
        first == second
        first != third
    }

    def "entries with no lines are considered incomplete"() {
        when:
        def entry = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("50.0"))
            .currency("EUR")
            .build()

        then:
        entry.isIncomplete()
    }

    def "entries partially balanced are considered incomplete"() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("120.0"))
            .currency("GBP")
            .build()

        when:
        def updatedEntry = entry.withLines(List.of(
            credit(Cash, new BigDecimal("100.0"), "GBP"),
            debit(SalesRevenue, new BigDecimal("100.0"), "GBP"),
        ))

        then:
        updatedEntry.isIncomplete()
    }

    def "entries with fully balanced lines are considered complete"() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("10.0"))
            .currency("EUR")
            .build()

        when:
        def updatedEntry = entry.withLines(List.of(
            credit(Cash, new BigDecimal("10.0"), "EUR"),
            debit(SalesRevenue, new BigDecimal("10.0"), "EUR"),
        ))

        then:
        updatedEntry.isComplete()
    }

    def "complete entries with can posted"() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("80.0"))
            .currency("GBP")
            .lines(List.of(
                credit(Cash, new BigDecimal("80.0"), "GBP"),
                debit(SalesRevenue, new BigDecimal("80.0"), "GBP"),
            ))
            .build()

        when:
        def updatedEntry = entry.postToLedger(anyLedger)

        then:
        updatedEntry.isPosted()
    }

    def "fails to post an incomplete entry"() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("80.0"))
            .currency("GBP")
            .build()

        when:
        entry.postToLedger(anyLedger)

        then:
        thrown(JournalEntryNotReadyForPosting)
    }

    def "fails to post an entry that has been already marked as posted"() {
        given:
        def entry = JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("80.0"))
            .currency("GBP")
            .lines(List.of(
                credit(Cash, new BigDecimal("80.0"), "GBP"),
                debit(SalesRevenue, new BigDecimal("80.0"), "GBP"),
            ))
            .postedAt(LocalDateTime.now())
            .build()

        when:
        entry.postToLedger(anyLedger)

        then:
        thrown(JournalEntryAlreadyPosted)
    }

    def "cannot create an invalid posted entry"() {
        when:
        JournalEntry
            .builder()
            .clientId(anyClientId)
            .amount(new BigDecimal("80.0"))
            .currency("GBP")
            .lines(List.of(
                credit(Cash, new BigDecimal("50.0"), "GBP"),
                debit(SalesRevenue, new BigDecimal("50.0"), "GBP"),
            ))
            .postedAt(LocalDateTime.now())
            .build()

        then:
        thrown(JournalEntryNotReadyForPosting)
    }
}
