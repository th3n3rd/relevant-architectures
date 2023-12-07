package com.example.architectures.postings;

import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class GeneratePostingsTask {

    private final GeneratePostings generatePostings;

    GeneratePostingsTask(GeneratePostings generatePostings) {
        this.generatePostings = generatePostings;
    }

    @DomainEventHandler
    @EventListener
    void on(NewAccountSetup event) {
        generatePostings.handle(event.clientId(), event.accountId());
    }
}
