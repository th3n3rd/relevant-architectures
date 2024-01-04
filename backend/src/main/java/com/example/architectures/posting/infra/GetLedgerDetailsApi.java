package com.example.architectures.posting.infra;

import com.example.architectures.auth.CheckConsultantAuthorisation;
import com.example.architectures.common.ClientId;
import com.example.architectures.posting.Ledgers;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetLedgerDetailsApi {

    private final Ledgers ledgers;

    GetLedgerDetailsApi(Ledgers ledgers) {
        this.ledgers = ledgers;
    }

    @CheckConsultantAuthorisation
    @GetMapping("/clients/{clientId}/ledger")
    Response.Ledger handle(@PathVariable ClientId clientId) {
        return ledgers.findByClientId(clientId)
            .map(it -> new Response.Ledger(
                it.accounts()
                    .stream()
                    .map(account -> new Response.Ledger.Account(
                        account.account().name(),
                        account.balance()
                    ))
                    .toList()
            ))
            .orElseThrow();
    }

    static class Response {
        record Ledger(List<Account> accounts) {
            record Account(String name, BigDecimal balance) {}
        }
    }
}
