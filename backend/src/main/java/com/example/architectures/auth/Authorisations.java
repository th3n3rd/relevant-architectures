package com.example.architectures.auth;

import com.example.architectures.common.ClientId;
import com.example.architectures.common.ConsultantId;

public interface Authorisations {
    boolean existsByConsultantIdAndClientId(ConsultantId consultantId, ClientId clientId);
}
