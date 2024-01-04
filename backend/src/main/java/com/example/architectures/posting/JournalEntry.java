package com.example.architectures.posting;

import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = "id")
@ToString
public final class JournalEntry {
    private final JournalEntryId id;
    private final ClientId clientId;
    private final BigDecimal amount;
    private final String currency;
    private final Status status;
    private final List<Line> lines;
    private final LocalDateTime postedAt;
    private final Metadata metadata;
    private final transient Balance runningBalance;

    @Builder(toBuilder = true)
    private JournalEntry(
        JournalEntryId id,
        ClientId clientId,
        BigDecimal amount,
        String currency,
        List<Line> lines,
        LocalDateTime postedAt,
        Metadata metadata
    ) {
        this.id = id == null ? new JournalEntryId() : id;
        this.clientId = clientId;
        this.amount = amount;
        this.currency = currency;
        this.lines = lines == null ? List.of() : lines;
        this.metadata = metadata;
        this.postedAt = postedAt;
        this.runningBalance = Balance.of(this.lines);
        this.status = computeStatus();
        if (markedAsPosted() && !isPosted()) {
            throw new JournalEntryNotReadyForPosting();
        }
    }

    JournalEntry withLines(List<Line> lines) {
        if (isPosted()) {
            throw new JournalEntryAlreadyPosted();
        }
        return toBuilder().lines(lines).build();
    }

    boolean isIncomplete() {
        return Status.Incomplete.equals(status);
    }

    boolean isComplete() {
        return Status.Complete.equals(status);
    }

    boolean isPosted() {
        return Status.Posted.equals(status);
    }

    JournalEntry postToLedger(Ledger ledger) {
        if (isPosted()) {
            throw new JournalEntryAlreadyPosted();
        }
        lines.forEach(it -> {
            if (it.isDebit()) {
                ledger.debit(it.account, it.amount);
            } else {
                ledger.credit(it.account, it.amount);
            }
        });
        return toBuilder()
            .postedAt(LocalDateTime.now())
            .build();
    }

    private boolean markedAsPosted() {
        return !Objects.isNull(postedAt);
    }

    private Status computeStatus() {
        if (!isFullyBalanced()) {
            return Status.Incomplete;
        }

        if (!markedAsPosted()) {
            return Status.Complete;
        }

        return Status.Posted;
    }

    private boolean isFullyBalanced() {
        return runningBalance.equals(new Balance(amount));
    }

    static JournalEntry.JournalEntryBuilder fromEcommerce(AccountId accountId) {
        return JournalEntry.builder()
            .metadata(new Metadata("e-commerce", accountId));
    }

    public enum Status {
        Incomplete,
        Complete,
        Posted
    }

    public record Line(FinancialAccount account, BigDecimal amount, String currency, Type type) {
        public enum Type { Debit, Credit;}

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

    public record Metadata(String origin, AccountId accountId) {}

    record Balance(BigDecimal value) {
        static Balance of(List<Line> lines) {
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

            return new Balance(totalCredit);
        }
    }
}
