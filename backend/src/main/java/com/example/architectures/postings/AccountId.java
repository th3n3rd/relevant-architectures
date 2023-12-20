package com.example.architectures.postings;

import org.jmolecules.ddd.types.Identifier;

public record AccountId(int value) implements Identifier {
    public static AccountId of(String value) {
        return new AccountId(Integer.parseInt(value));
    }
}
