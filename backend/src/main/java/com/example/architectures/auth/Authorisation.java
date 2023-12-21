package com.example.architectures.auth;

import com.example.architectures.postings.ClientId;
import com.example.architectures.postings.ConsultantId;

public record Authorisation(ConsultantId consultantId, ClientId clientId) {}
