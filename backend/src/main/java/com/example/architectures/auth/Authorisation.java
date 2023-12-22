package com.example.architectures.auth;

import com.example.architectures.common.ClientId;
import com.example.architectures.common.ConsultantId;

public record Authorisation(ConsultantId consultantId, ClientId clientId) {}
