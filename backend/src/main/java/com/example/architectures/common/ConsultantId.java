package com.example.architectures.common;

import org.jmolecules.ddd.types.Identifier;

public record ConsultantId(int value) implements Identifier {
    public static ConsultantId of(String value) {
        return new ConsultantId(Integer.parseInt(value));
    }
}
