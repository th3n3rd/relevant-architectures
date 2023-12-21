package com.example.architectures.auth;

import com.example.architectures.postings.ClientId;
import com.example.architectures.postings.ConsultantId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("authorisations")
public class InMemoryAuthorisations implements Authorisations {
    private final List<Authorisation> authorisations = new ArrayList<>();

    @Override
    public boolean existsByConsultantIdAndClientId(ConsultantId consultantId, ClientId clientId) {
        return authorisations.contains(new Authorisation(consultantId, clientId));
    }

    public void authorise(ConsultantId consultantId, ClientId clientId) {
        authorisations.add(new Authorisation(consultantId, clientId));
    }

    public void deleteAll() {
        authorisations.clear();
    }
}
