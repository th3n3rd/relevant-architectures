package com.example.architectures.postings;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@EnableConfigurationProperties(KlarnaTransactionsGateway.Properties.class)
class KlarnaTransactionsGateway implements TransactionsGateway {

    private final RestTemplate client;

    KlarnaTransactionsGateway(Properties properties, RestTemplateBuilder builder) {
        this.client = builder
            .rootUri(properties.uri())
            .build();
    }

    @Override
    public List<Transaction> fetchAll(int clientId, int accountId) {
        var receivedTransactions = client.getForObject(
            "/v2/accounts/{accountId}/transactions",
            Klarna.Transactions.class,
            accountId
        );
        return receivedTransactions.transactions
            .stream()
            .map(it -> new Transaction(
                clientId,
                accountId,
                new BigDecimal(it.amount.amount),
                it.amount.currency
            ))
            .toList();
    }

    @ConfigurationProperties(prefix = "transactions-gateway.klarna")
    record Properties(String uri) {}

    private static class Klarna {
        record Transactions(List<Transaction> transactions) {}
        record Transaction(Amount amount) {}
        record Amount(String amount, String currency) {}
    }
}
