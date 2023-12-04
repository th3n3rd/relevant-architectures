package com.example.architectures.postings;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FakeTransactionsGateway implements TransactionsGateway {
    public List<Transaction> fetchAll(int clientId, int accountId) {
        return List.of(
            new Transaction(clientId, accountId, new BigDecimal("10.0"), "EUR"),
            new Transaction(clientId, accountId, new BigDecimal("15.0"), "EUR")
        );
    }
}
