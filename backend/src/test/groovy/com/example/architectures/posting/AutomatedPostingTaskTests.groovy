package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.common.EventPublisher
import com.example.architectures.ecommerce.AccountId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static com.example.architectures.posting.FinancialAccount.asset
import static com.example.architectures.posting.FinancialAccount.revenue
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

    def "post a journal entry that has been marked as completed"() {
        given:
        ledgers.save(new Ledger(anyClientId, List.of(
            new LedgerAccount(asset("cash"), new BigDecimal("100.0")),
            new LedgerAccount(asset("account-receivable"), new BigDecimal("50.0")),
            new LedgerAccount(revenue("sales-revenue"), new BigDecimal("150.0"))
        )))
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("75.0"))
            .currency("EUR")
            .lines(List.of(
                debit(asset("cash"), new BigDecimal("75.0"), "EUR"),
                credit(revenue("sales-revenue"), new BigDecimal("75.0"), "EUR"),
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
            new LedgerAccount(asset("cash"), new BigDecimal("175.0")),
            new LedgerAccount(asset("account-receivable"), new BigDecimal("50.0")),
            new LedgerAccount(revenue("sales-revenue"), new BigDecimal("225.0"))
        ]
    }
}
