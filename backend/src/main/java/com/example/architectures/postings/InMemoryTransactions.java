package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryTransactions implements Transactions {
    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void saveAll(List<Transaction> entities) {
        transactions.addAll(entities);
    }

    @Override
    public List<Transaction> findAll() {
        return transactions;
    }
}
