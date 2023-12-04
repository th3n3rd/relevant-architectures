package com.example.architectures.common;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InMemoryEventPublisher implements EventPublisher {

    private final List<Object> events = new ArrayList<>();

    @Override
    public void publish(Object event) {
        events.add(event);
    }

    public List<Object> publishedEvents() {
        return events;
    }
}
