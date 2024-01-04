package com.example.architectures.ecommerce;

import com.example.architectures.common.ClientId;
import com.example.architectures.common.EventPublisher;
import org.jmolecules.event.annotation.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SetupAccount {

    private final EventPublisher eventPublisher;

    SetupAccount(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @DomainEventPublisher
    public void handle(ClientId clientId, AccountId accountId) {
        eventPublisher.publish(new NewAccountSetup(clientId, accountId));
    }
}
