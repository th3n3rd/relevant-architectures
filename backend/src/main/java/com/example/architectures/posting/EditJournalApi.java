package com.example.architectures.posting;

import com.example.architectures.auth.ConsultantAuthorised;
import com.example.architectures.common.ClientId;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EditJournalApi {

    private final ChartOfAccounts chartOfAccounts;
    private final EditJournal editJournal;

    EditJournalApi(ChartOfAccounts chartOfAccounts, EditJournal editJournal) {
        this.chartOfAccounts = chartOfAccounts;
        this.editJournal = editJournal;
    }

    @ConsultantAuthorised
    @PatchMapping("/clients/{clientId}/journal/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void handle(@PathVariable ClientId clientId, @PathVariable JournalEntryId entryId, @RequestBody Request.Entry entry) {
        editJournal.handle(
            entryId,
            entry.lines().stream()
                .map(it -> new JournalEntry.Line(
                    chartOfAccounts.findByName(it.accountName).orElseThrow(),
                    it.amount,
                    it.currency,
                    it.type
                ))
                .toList()
        );
    }

    @ExceptionHandler(JournalEntryUnbalanced.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handleJournalEntryUnbalanced() {}

    static class Request {
        record Entry(List<Line> lines) {
            record Line(
                String accountName,
                BigDecimal amount,
                String currency,
                JournalEntry.Line.Type type
            ) {}
        }
    }
}
