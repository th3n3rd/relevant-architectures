package com.example.architectures.postings;

public interface Authorisations {
    boolean existsByConsultantIdAndClientId(int consultantId, ClientId clientId);
}
