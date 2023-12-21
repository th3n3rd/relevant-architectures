package com.example.architectures.ecommerce;

import com.example.architectures.postings.ClientId;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@EnableConfigurationProperties(KlarnaPaymentGateway.Properties.class)
class KlarnaPaymentGateway implements PaymentGateway {

    private final RestTemplate client;

    KlarnaPaymentGateway(Properties properties, RestTemplateBuilder builder) {
        this.client = builder
            .rootUri(properties.uri())
            .build();
    }

    @Override
    public List<Transaction> fetchTransactions(ClientId clientId, AccountId accountId) {
        var consent = requestConsent(accountId);
        if (consent.isRejected()) {
            throw new FetchTransactionsFailed.Unauthorised();
        }

        var request = RequestEntity
            .get("/v2/accounts/{accountId}/transactions", accountId.value())
            .header("consent-id", consent.consentId)
            .build();

        try {
            var receivedTransactions = client.exchange(
                request,
                Klarna.Transactions.class
            );
            return receivedTransactions.getBody().transactions
                .stream()
                .map(it -> new Transaction(
                    clientId,
                    accountId,
                    new BigDecimal(it.amount.amount),
                    it.amount.currency
                ))
                .toList();
        } catch (HttpClientErrorException.Forbidden e) {
            throw new FetchTransactionsFailed.Unauthorised();
        }
    }

    private Klarna.Consent requestConsent(AccountId accountId) {
        return client.postForObject(
            "/v2/consent-sessions",
            new Klarna.RequestConsent(accountId.value()),
            Klarna.Consent.class
        );
    }

    @ConfigurationProperties(prefix = "payment-gateway.klarna")
    record Properties(String uri) {}

    private static class Klarna {
        record RequestConsent(String clientId) {}
        record Consent(String status, @JsonProperty("consent_id") String consentId) {
            boolean isRejected() {
                return "REJECTED".equals(status);
            }
        }
        record Transactions(List<Transaction> transactions) {}
        record Transaction(Amount amount) {}
        record Amount(String amount, String currency) {}
    }
}
