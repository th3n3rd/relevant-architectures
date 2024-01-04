package com.example.architectures.posting;

import static com.example.architectures.posting.FinancialAccount.asset;
import static com.example.architectures.posting.FinancialAccount.equity;
import static com.example.architectures.posting.FinancialAccount.expense;
import static com.example.architectures.posting.FinancialAccount.liability;
import static com.example.architectures.posting.FinancialAccount.revenue;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ChartOfAccounts {
    public static final FinancialAccount Cash = asset("cash");
    public static final FinancialAccount AccountsReceivable = asset("accounts-receivable");
    public static final FinancialAccount Inventory = asset("inventory");
    public static final FinancialAccount AccountsPayable = liability("accounts-payable");
    public static final FinancialAccount LoansPayable = liability("loans-payable");
    public static final FinancialAccount OwnerCapital = equity("owner-capital");
    public static final FinancialAccount RetainedEarnings = equity("retained-earnings");
    public static final FinancialAccount SalesRevenue = revenue("sales-revenue");
    public static final FinancialAccount ServiceRevenue = revenue("service-revenue");
    public static final FinancialAccount RentExpense = expense("rent-expense");
    public static final FinancialAccount SalaryExpense = expense("salary-expense");
    public static final FinancialAccount UtilitiesExpense = expense("utilities-expense");

    private static final List<FinancialAccount> Standard = List.of(
        // Assets
        Cash, AccountsReceivable, Inventory,
        // Liabilities
        AccountsPayable, LoansPayable,
        // Equities
        OwnerCapital, RetainedEarnings,
        // Revenues
        SalesRevenue, ServiceRevenue,
        // Expenses
        RentExpense, SalaryExpense, UtilitiesExpense
    );

    List<FinancialAccount> findAll() {
        return Standard;
    }

    public Optional<FinancialAccount> findByName(String accountName) {
        return Standard
            .stream()
            .filter(it -> it.name().equals(accountName))
            .findFirst();
    }
}
