package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import java.util.Optional;

public interface Ledgers {
    void save(Ledger ledger);
    Optional<Ledger> findByClientId(ClientId clientId);
}
