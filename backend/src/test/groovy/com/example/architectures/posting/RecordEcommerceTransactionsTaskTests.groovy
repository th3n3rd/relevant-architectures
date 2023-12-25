package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.common.EventPublisher
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.ecommerce.NewAccountSetup
import com.example.architectures.ecommerce.PaymentGateway
import com.example.architectures.ecommerce.Transaction
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest
class RecordEcommerceTransactionsTaskTests extends Specification {

    private static anyClientId = new ClientId(123)
    private static anyAccountId = new AccountId("789")

    @Autowired
    private EventPublisher eventPublisher

    @Autowired
    private InMemoryJournal journal

    @SpringBean
    private PaymentGateway transactionsGateway = Mock()

    def "generate journal entries when a new account is setup"() {
        given:
        transactionsGateway.fetchTransactions(anyClientId, anyAccountId) >> [
            new Transaction(anyClientId, anyAccountId, new BigDecimal("10.0"), "EUR")
        ]

        when:
        eventPublisher.publish(new NewAccountSetup(anyClientId, anyAccountId))

        then:
        new PollingConditions().within(5) {
            !journal.findAll().isEmpty()
        }
    }
}
