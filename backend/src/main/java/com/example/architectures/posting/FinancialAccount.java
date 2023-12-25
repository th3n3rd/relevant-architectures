package com.example.architectures.posting;

public record FinancialAccount(String name, Type type) {

    public enum Type {
        Asset,
        Liability,
        Equity,
        Revenue,
        Expense
    }

    static FinancialAccount asset(String name) {
        return new FinancialAccount(name, Type.Asset);
    }

    static FinancialAccount liability(String name) {
        return new FinancialAccount(name, Type.Liability);
    }

    static FinancialAccount equity(String name) {
        return new FinancialAccount(name, Type.Equity);
    }

    static FinancialAccount revenue(String name) {
        return new FinancialAccount(name, Type.Revenue);
    }

    static FinancialAccount expense(String name) {
        return new FinancialAccount(name, Type.Expense);
    }
}
