package com.example.architectures.postings;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
record NewAccountSetup(int clientId, int accountId) {}
