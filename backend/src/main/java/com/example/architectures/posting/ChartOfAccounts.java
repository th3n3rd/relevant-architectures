package com.example.architectures.posting;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
class ChartOfAccounts {
    private static final List<FinancialAccount> Standard = List.of(
        FinancialAccount.asset("cash"),
        FinancialAccount.asset("account-receivable"),
        FinancialAccount.asset("inventory"),
        FinancialAccount.liability("account-payable"),
        FinancialAccount.liability("loans-payable"),
        FinancialAccount.equity("owner-capital"),
        FinancialAccount.equity("retained-earnings"),
        FinancialAccount.revenue("sales-revenue"),
        FinancialAccount.revenue("service-revenue"),
        FinancialAccount.expense("rent-expense"),
        FinancialAccount.expense("salary-expense"),
        FinancialAccount.expense("utilities-expense")
    );

    List<FinancialAccount> findAll() {
        return Standard;
    }
}
