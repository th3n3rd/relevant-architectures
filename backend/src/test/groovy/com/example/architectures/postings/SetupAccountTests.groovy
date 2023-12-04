package com.example.architectures.postings

import com.example.architectures.common.InMemoryEventPublisher
import spock.lang.Specification

class SetupAccountTests extends Specification {

    def amyClientId = 123
    def anyAccountId = 789
    def eventPublisher = new InMemoryEventPublisher()
    def setupAccount = new SetupAccount(eventPublisher)

    def "publish a new event"() {
        when:
        setupAccount.handle(
            amyClientId,
            anyAccountId
        )

        then:
        eventPublisher.publishedEvents() == [new NewAccountSetup(amyClientId, anyAccountId)]
    }
}
