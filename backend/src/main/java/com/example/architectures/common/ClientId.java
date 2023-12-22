package com.example.architectures.common;

import org.jmolecules.ddd.types.Identifier;

public record ClientId(int value) implements Identifier {
    public static ClientId of(String value) {
        return new ClientId(Integer.parseInt(value));
    }
}
