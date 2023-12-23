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
@Builder
@With
final class JournalEntry {
    private final JournalEntryId id;
    private final ClientId clientId;
    private final AccountId accountId;
    private final BigDecimal amount;
    private final String currency;

    public JournalEntry(ClientId clientId, AccountId accountId, BigDecimal amount, String currency) {
        this.id = new JournalEntryId();
        this.clientId = clientId;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
    }
}
