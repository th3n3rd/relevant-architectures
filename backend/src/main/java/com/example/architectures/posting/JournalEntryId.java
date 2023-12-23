package com.example.architectures.posting;

import java.util.UUID;
import org.jmolecules.ddd.types.Identifier;

record JournalEntryId(UUID value) implements Identifier {
    JournalEntryId() {
        this(UUID.randomUUID());
    }

    static JournalEntryId of(String value) {
        return new JournalEntryId(UUID.fromString(value));
    }
}
