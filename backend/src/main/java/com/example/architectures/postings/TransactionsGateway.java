package com.example.architectures.postings;

import java.util.List;

public interface TransactionsGateway {
    List<Transaction> fetchAll(int clientId, int accountId);
}
