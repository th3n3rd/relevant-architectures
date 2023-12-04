package com.example.architectures.postings

import com.example.architectures.common.EventPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest
class ImportTransactionsTaskTests extends Specification {

    @Autowired
    private EventPublisher eventPublisher

    @Autowired
    private Transactions transactions

    def "import transactions when a new account is setup"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789

        when:
        eventPublisher.publish(new NewAccountSetup(anyClientId, anyAccountId))

        then:
        new PollingConditions().within(5) {
            !transactions.findAll().isEmpty()
        }
    }
}
