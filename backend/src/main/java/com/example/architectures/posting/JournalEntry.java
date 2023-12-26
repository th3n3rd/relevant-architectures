package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@With
final class JournalEntry {
    private final JournalEntryId id;
    private final ClientId clientId;
    private final BigDecimal amount;
    private final String currency;
    private final Status status;
    private final Metadata metadata;

    @Builder
    public JournalEntry(ClientId clientId, BigDecimal amount, String currency, Metadata metadata) {
        this.id = new JournalEntryId();
        this.clientId = clientId;
        this.amount = amount;
        this.currency = currency;
        this.status = Status.Incomplete;
        this.metadata = metadata;
    }

    boolean isIncomplete() {
        return Status.Incomplete.equals(status);
    }

    static JournalEntry.JournalEntryBuilder fromEcommerce(AccountId accountId) {
        return JournalEntry.builder()
            .metadata(new Metadata("e-commerce", accountId));
    }

    enum Status {
        Incomplete
    }

    record Metadata(String origin, AccountId accountId) {}
}
