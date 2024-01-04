package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.common.EventPublisher
import com.example.architectures.ecommerce.AccountId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static com.example.architectures.posting.ChartOfAccounts.AccountsReceivable
import static com.example.architectures.posting.ChartOfAccounts.Cash
import static com.example.architectures.posting.ChartOfAccounts.SalesRevenue
import static com.example.architectures.posting.JournalEntry.Line.credit
import static com.example.architectures.posting.JournalEntry.Line.debit

@SpringBootTest
class AutomatedPostingTaskTests extends Specification {

    private static anyClientId = new ClientId(123)
    private static anyAccountId = new AccountId("789")

    @Autowired
    private EventPublisher eventPublisher

    @Autowired
    private InMemoryJournal journal

    @Autowired
    private InMemoryLedgers ledgers

    def "post journal entries that have been marked as completed"() {
        given:
        ledgers.save(new Ledger(anyClientId, List.of(
            new LedgerAccount(Cash, new BigDecimal("100.0")),
            new LedgerAccount(AccountsReceivable, new BigDecimal("50.0")),
            new LedgerAccount(SalesRevenue, new BigDecimal("150.0"))
        )))
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("75.0"))
            .currency("EUR")
            .lines(List.of(
                debit(Cash, new BigDecimal("75.0"), "EUR"),
                credit(SalesRevenue, new BigDecimal("75.0"), "EUR"),
            ))
            .build()
        journal.save(entry)

        when:
        eventPublisher.publish(new JournalEntryCompleted(entry.id()))

        then:
        def updatedEntry = journal.findById(entry.id()).orElseThrow()
        def updatedLedger = ledgers.findByClientId(entry.clientId()).orElseThrow();
        updatedEntry.isPosted()
        updatedLedger.accounts() == [
            new LedgerAccount(Cash, new BigDecimal("175.0")),
            new LedgerAccount(AccountsReceivable, new BigDecimal("50.0")),
            new LedgerAccount(SalesRevenue, new BigDecimal("225.0"))
        ]
    }
}
