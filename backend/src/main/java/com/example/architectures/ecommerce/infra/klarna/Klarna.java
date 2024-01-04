package com.example.architectures.ecommerce.infra.klarna;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

class Klarna {

    static class Headers {
        static final String ConsentId = "consent-id";
    }

    static class Endpoints {
        static final String Transactions = "/v2/accounts/{accountId}/transactions";
        static final String ConsentSessions = "/v2/consent-sessions";
    }

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
