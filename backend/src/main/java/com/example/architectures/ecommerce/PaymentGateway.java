package com.example.architectures.ecommerce;

import com.example.architectures.postings.ClientId;
import java.util.List;

public interface PaymentGateway {
    List<Transaction> fetchTransactions(ClientId clientId, AccountId accountId);
}
