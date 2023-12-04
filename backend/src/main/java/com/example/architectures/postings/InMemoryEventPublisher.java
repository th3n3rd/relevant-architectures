package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class InMemoryEventPublisher implements EventPublisher {

    private final List<Object> events = new ArrayList<>();

    @Override
    public void publish(Object event) {
        events.add(event);
    }

    List<Object> publishedEvents() {
        return events;
    }
}
