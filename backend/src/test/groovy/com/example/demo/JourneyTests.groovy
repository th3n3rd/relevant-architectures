package com.example.demo

import org.spockframework.spring.SpringSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(
    webEnvironment = RANDOM_PORT
)
class JourneyTests extends Specification {

    public static final clientId = 123
    public static final consultantId = 456
    public static final klarna = 789

    @Autowired
    private TestRestTemplate httpClient

    @SpringSpy
    private TransactionsGateway transactionsGateway

    void setup() {
        transactionsGateway.fetchAll(clientId, klarna) >> [
            new Transaction(clientId, new BigDecimal("10.0"), "EUR"),
            new Transaction(clientId, new BigDecimal("15.0"), "EUR")
        ]
    }

    def "tax consultant receives postings proposal"() {
        def consultant = new TaxConsultant(httpClient, consultantId)

        consultant.setupAccount(clientId, klarna)

        expect:
        consultant.receivedPostings(clientId, klarna, [
            [clientId: clientId, amount: "10.0", currency: "EUR"],
            [clientId: clientId, amount: "15.0", currency: "EUR"],
        ])
    }

    class TaxConsultant {
        private final TestRestTemplate httpClient
        private final int consultantId

        TaxConsultant(TestRestTemplate httpClient, int consultantId) {
            this.consultantId = consultantId
            this.httpClient = httpClient
        }

        def setupAccount(int clientId, int accountId) {
            def response = httpClient.postForEntity(
                "/clients/{clientId}/accounts",
                [ accountId: accountId ],
                Void,
                clientId
            )
            assert response.statusCode.is2xxSuccessful()
        }

        void receivedPostings(clientId, accountId, expected) {
            def response = httpClient.getForEntity(
                "/clients/{clientId}/accounts/{accountId}/postings",
                PostingsList,
                clientId,
                accountId
            )

            def actual = response.body.postings.collect {
                [
                    clientId: it.clientId,
                    amount: it.amount,
                    currency: it.currency
                ]
            }
            assert expected == actual
        }

        static class PostingsList {
            List<Posting> postings
        }

        static class Posting {
            int clientId
            String amount
            String currency
        }
    }
}
