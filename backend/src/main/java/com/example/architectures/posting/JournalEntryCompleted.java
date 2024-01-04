package com.example.architectures.posting;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record JournalEntryCompleted(JournalEntryId id) {}
