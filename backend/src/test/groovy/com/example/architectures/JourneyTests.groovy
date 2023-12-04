package com.example.architectures

import com.example.architectures.postings.Transaction
import com.example.architectures.postings.TransactionsGateway
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

}
