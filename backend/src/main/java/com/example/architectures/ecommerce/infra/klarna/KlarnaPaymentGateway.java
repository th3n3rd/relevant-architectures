package com.example.architectures.ecommerce.infra.klarna;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import com.example.architectures.ecommerce.FetchTransactionsFailed;
import com.example.architectures.ecommerce.PaymentGateway;
import com.example.architectures.ecommerce.Transaction;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@EnableConfigurationProperties(KlarnaSettings.class)
class KlarnaPaymentGateway implements PaymentGateway {

    private final RestTemplate client;

    KlarnaPaymentGateway(KlarnaSettings settings, RestTemplateBuilder builder) {
        this.client = builder
            .rootUri(settings.uri())
            .build();
    }

    @Override
    public List<Transaction> fetchTransactions(ClientId clientId, AccountId accountId) {
        var consent = requestConsent(accountId);
        if (consent.isRejected()) {
            throw new FetchTransactionsFailed.Unauthorised();
        }

        var request = RequestEntity
            .get(Klarna.Endpoints.Transactions, accountId.value())
            .header(Klarna.Headers.ConsentId, consent.consentId())
            .build();

        try {
            var receivedTransactions = client.exchange(
                request,
                Klarna.Transactions.class
            );
            return receivedTransactions.getBody().transactions()
                .stream()
                .map(it -> new Transaction(
                    clientId,
                    accountId,
                    new BigDecimal(it.amount().amount()),
                    it.amount().currency()
                ))
                .toList();
        } catch (HttpClientErrorException.Forbidden e) {
            throw new FetchTransactionsFailed.Unauthorised();
        }
    }

    private Klarna.Consent requestConsent(AccountId accountId) {
        return client.postForObject(
            Klarna.Endpoints.ConsentSessions,
            new Klarna.RequestConsent(accountId.value()),
            Klarna.Consent.class
        );
    }

}
