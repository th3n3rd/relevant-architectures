package com.example.architectures.auth;

import com.example.architectures.postings.ClientId;
import com.example.architectures.postings.ConsultantId;

public interface Authorisations {
    boolean existsByConsultantIdAndClientId(ConsultantId consultantId, ClientId clientId);
}
