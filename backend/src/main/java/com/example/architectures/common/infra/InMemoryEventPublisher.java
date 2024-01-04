package com.example.architectures.common.infra;

import com.example.architectures.common.EventPublisher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
