package com.example.architectures.postings

import com.example.architectures.common.EventPublisher
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest
class GeneratePostingsTaskTests extends Specification {

    @Autowired
    private EventPublisher eventPublisher

    @Autowired
    private InMemoryPostings postings

    @SpringBean
    private PaymentGateway transactionsGateway = Mock()

    def "generate postings when a new account is setup"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("10.0"), "EUR")
        ]

        when:
        eventPublisher.publish(new NewAccountSetup(anyClientId, anyAccountId))

        then:
        new PollingConditions().within(5) {
            !postings.findAll().isEmpty()
        }
    }
}
