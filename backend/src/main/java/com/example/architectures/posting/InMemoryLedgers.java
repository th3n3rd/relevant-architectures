package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryLedgers implements Ledgers {
    private final List<Ledger> ledgers = new ArrayList<>();

    @Override
    public void save(Ledger ledger) {
        ledgers.add(ledger);
    }

    @Override
    public Optional<Ledger> findByClientId(ClientId clientId) {
        return ledgers
            .stream()
            .filter(it -> it.clientId().equals(clientId))
            .findAny();
    }
}
