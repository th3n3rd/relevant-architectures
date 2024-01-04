package com.example.architectures.auth.infra;

import com.example.architectures.auth.Authorisation;
import com.example.architectures.auth.Authorisations;
import com.example.architectures.common.ClientId;
import com.example.architectures.common.ConsultantId;
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
