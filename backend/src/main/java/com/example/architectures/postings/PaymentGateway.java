package com.example.architectures.postings;

import java.util.List;

public interface PaymentGateway {
    List<Transaction> fetchTransactions(ClientId clientId, AccountId accountId);
}
