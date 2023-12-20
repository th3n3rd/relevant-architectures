package com.example.architectures.postings;

public interface Authorisations {
    boolean existsByConsultantIdAndClientId(ConsultantId consultantId, ClientId clientId);
}
