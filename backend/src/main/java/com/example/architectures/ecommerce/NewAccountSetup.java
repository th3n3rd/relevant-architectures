package com.example.architectures.ecommerce;

import com.example.architectures.postings.ClientId;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record NewAccountSetup(ClientId clientId, AccountId accountId) {}
