package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("authorisations")
public class InMemoryAuthorisations implements Authorisations {
    private final List<Authorisation> authorisations = new ArrayList<>();

    @Override
    public boolean existsByConsultantIdAndClientId(int consultantId, ClientId clientId) {
        return authorisations.contains(new Authorisation(consultantId, clientId));
    }

    public void authorise(int consultantId, ClientId clientId) {
        authorisations.add(new Authorisation(consultantId, clientId));
    }

    public void deleteAll() {
        authorisations.clear();
    }
}
