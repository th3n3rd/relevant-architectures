package com.example.architectures.posting

import spock.lang.Specification

import static com.example.architectures.posting.ChartOfAccounts.AccountsPayable
import static com.example.architectures.posting.ChartOfAccounts.AccountsReceivable
import static com.example.architectures.posting.ChartOfAccounts.Cash
import static com.example.architectures.posting.ChartOfAccounts.Inventory
import static com.example.architectures.posting.ChartOfAccounts.LoansPayable
import static com.example.architectures.posting.ChartOfAccounts.OwnerCapital
import static com.example.architectures.posting.ChartOfAccounts.RentExpense
import static com.example.architectures.posting.ChartOfAccounts.RetainedEarnings
import static com.example.architectures.posting.ChartOfAccounts.SalaryExpense
import static com.example.architectures.posting.ChartOfAccounts.SalesRevenue

class LedgerAccountTests extends Specification {

    def "updates the balance by debiting the account"(FinancialAccount financialAccount, String expectedBalance) {
        given:
        def ledgerAccount = new LedgerAccount(financialAccount)

        when:
        ledgerAccount.debit(BigDecimal.ONE)

        then:
        ledgerAccount.balance() == new BigDecimal(expectedBalance)

        where:
        financialAccount   | expectedBalance
        Cash               | "1.0"
        Inventory          | "1.0"
        AccountsReceivable | "1.0"
        AccountsPayable    | "-1.0"
        LoansPayable       | "-1.0"
        OwnerCapital       | "-1.0"
        RetainedEarnings   | "-1.0"
        SalesRevenue       | "-1.0"
        RetainedEarnings   | "-1.0"
        RentExpense        | "1.0"
        SalaryExpense      | "1.0"
    }

    def "updates the balance by crediting the account"(FinancialAccount financialAccount, String expectedBalance) {
        given:
        def ledgerAccount = new LedgerAccount(financialAccount)

        when:
        ledgerAccount.credit(BigDecimal.ONE)

        then:
        ledgerAccount.balance() == new BigDecimal(expectedBalance)

        where:
        financialAccount   | expectedBalance
        Cash               | "-1.0"
        Inventory          | "-1.0"
        AccountsReceivable | "-1.0"
        AccountsPayable    | "1.0"
        LoansPayable       | "1.0"
        OwnerCapital       | "1.0"
        RetainedEarnings   | "1.0"
        SalesRevenue       | "1.0"
        RetainedEarnings   | "1.0"
        RentExpense        | "-1.0"
        SalaryExpense      | "-1.0"
    }
}
