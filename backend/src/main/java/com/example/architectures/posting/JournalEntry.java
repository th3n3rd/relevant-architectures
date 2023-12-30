package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = "id")
@With
final class JournalEntry {
    private final JournalEntryId id;
    private final ClientId clientId;
    private final BigDecimal amount;
    private final String currency;
    private final Status status;
    private final List<Line> lines;
    private final Metadata metadata;

    @Builder
    public JournalEntry(ClientId clientId, BigDecimal amount, String currency, Metadata metadata) {
        this(
            new JournalEntryId(),
            clientId,
            amount,
            currency,
            Status.Incomplete,
            List.of(),
            metadata
        );
    }

    private JournalEntry(
        JournalEntryId id,
        ClientId clientId,
        BigDecimal amount,
        String currency,
        Status status,
        List<Line> lines,
        Metadata metadata
    ) {
        this.id = id;
        this.clientId = clientId;
        this.amount = amount;
        this.currency = currency;
        this.lines = lines;
        this.metadata = metadata;
        this.status = computeStatus();
    }

    boolean isIncomplete() {
        return Status.Incomplete.equals(status);
    }

    boolean isComplete() {
        return Status.Complete.equals(status);
    }

    static JournalEntry.JournalEntryBuilder fromEcommerce(AccountId accountId) {
        return JournalEntry.builder()
            .metadata(new Metadata("e-commerce", accountId));
    }

    private Status computeStatus() {
        if (lines.isEmpty()) {
            return Status.Incomplete;
        }

        var totalDebit = BigDecimal.ZERO;
        var totalCredit = BigDecimal.ZERO;

        for (var line : lines) {
            if (line.isDebit()) {
                totalDebit = totalDebit.add(line.amount());
            } else {
                totalCredit = totalCredit.add(line.amount());
            }
        }

        if (!totalDebit.equals(totalCredit)) {
            throw new JournalEntryUnbalanced();
        }

        if (!totalCredit.equals(amount)) {
            return Status.Incomplete;
        }

        return Status.Complete;
    }

    enum Status {
        Incomplete,
        Complete
    }

    record Line(FinancialAccount account, BigDecimal amount, String currency, Type type) {
        enum Type { Debit, Credit }

        boolean isDebit() {
            return Type.Debit.equals(type);
        }

        static Line debit(FinancialAccount account, BigDecimal amount, String currency) {
            return new Line(account, amount, currency, Type.Debit);
        }

        static Line credit(FinancialAccount account, BigDecimal amount, String currency) {
            return new Line(account, amount, currency, Type.Credit);
        }
    }

    record Metadata(String origin, AccountId accountId) {}
}
