package com.example.architectures.common.infra;

import com.example.architectures.common.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    SpringEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(Object event) {
        eventPublisher.publishEvent(event);
    }
}
