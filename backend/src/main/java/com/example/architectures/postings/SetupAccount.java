package com.example.architectures.postings;

import org.springframework.stereotype.Component;

@Component
class SetupAccount {

    private final EventPublisher eventPublisher;

    SetupAccount(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    void handle(int clientId, int accountId) {
        eventPublisher.publish(new NewAccountSetup(clientId, accountId));
    }
}
