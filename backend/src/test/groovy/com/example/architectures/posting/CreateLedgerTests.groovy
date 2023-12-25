package com.example.architectures.posting

import com.example.architectures.common.ClientId
import spock.lang.Specification

class CreateLedgerTests extends Specification {

    private static final anyClientId = new ClientId(123)

    def ledgers = new InMemoryLedgers()
    def createLedger = new CreateLedger(ledgers, new ChartOfAccounts())

    def "create a new ledger for a given client"() {
        when:
        createLedger.handle(anyClientId)

        then:
        ledgers.findByClientId(anyClientId).isPresent()
    }

    def "new ledgers are created starting from a standard chart of accounts"() {
        when:
        createLedger.handle(anyClientId)

        then:
        def ledger = ledgers.findByClientId(anyClientId).orElseThrow()
        matchChartOfAccounts(ledger)
    }

    void matchChartOfAccounts(Ledger ledger) {
        assert ledger.accounts().collect {
            [
                name: it.account().name(),
                type: it.account().type().name(),
                balance: it.balance().toString()
            ]
        } == [
            [name: "cash", type: "Asset", balance: "0.0"],
            [name: "account-receivable", type: "Asset", balance: "0.0"],
            [name: "inventory", type: "Asset", balance: "0.0"],
            [name: "account-payable", type: "Liability", balance: "0.0"],
            [name: "loans-payable", type: "Liability", balance: "0.0"],
            [name: "owner-capital", type: "Equity", balance: "0.0"],
            [name: "retained-earnings", type: "Equity", balance: "0.0"],
            [name: "sales-revenue", type: "Revenue", balance: "0.0"],
            [name: "service-revenue", type: "Revenue", balance: "0.0"],
            [name: "rent-expense", type: "Expense", balance: "0.0"],
            [name: "salary-expense", type: "Expense", balance: "0.0"],
            [name: "utilities-expense", type: "Expense", balance: "0.0"],
        ]
    }
}
