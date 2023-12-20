package com.example.architectures.postings;

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
    void handle(ClientId clientId, int accountId) {
        eventPublisher.publish(new NewAccountSetup(clientId, accountId));
    }
}
