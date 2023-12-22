package com.example.architectures.ecommerce;

import com.example.architectures.common.ClientId;
import com.example.architectures.common.EventPublisher;
import org.jmolecules.event.annotation.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
class SetupAccount {

    private final EventPublisher eventPublisher;

    SetupAccount(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @DomainEventPublisher
    void handle(ClientId clientId, AccountId accountId) {
        eventPublisher.publish(new NewAccountSetup(clientId, accountId));
    }
}
