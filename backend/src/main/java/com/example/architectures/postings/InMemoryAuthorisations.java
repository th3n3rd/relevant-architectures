package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAuthorisations implements Authorisations {
    private final List<Authorisation> authorisations = new ArrayList<>();

    @Override
    public boolean existsByConsultantIdAndClientId(int consultantId, int clientId) {
        return authorisations
            .stream()
            .anyMatch(it -> it.consultantId() == consultantId && it.clientId() == clientId);
    }

    public void authorise(int consultantId, int clientID) {
        authorisations.add(new Authorisation(consultantId, clientID));
    }

    public void deleteAll() {
        authorisations.clear();
    }
}
