package com.example.architectures.posting;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
record JournalEntryCompleted(JournalEntryId id) {}
