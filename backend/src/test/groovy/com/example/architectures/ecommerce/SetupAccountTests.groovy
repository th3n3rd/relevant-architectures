package com.example.architectures.ecommerce

import com.example.architectures.common.ClientId
import com.example.architectures.common.infra.InMemoryEventPublisher
import spock.lang.Specification

class SetupAccountTests extends Specification {

    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("789")

    def eventPublisher = new InMemoryEventPublisher()
    def setupAccount = new SetupAccount(eventPublisher)

    def "publish a new event"() {
        when:
        setupAccount.handle(anyClientId, anyAccountId)

        then:
        eventPublisher.publishedEvents() == [
            new NewAccountSetup(anyClientId, anyAccountId)
        ]
    }
}
