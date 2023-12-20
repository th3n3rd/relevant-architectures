package com.example.architectures.postings;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
record NewAccountSetup(ClientId clientId, AccountId accountId) {}
