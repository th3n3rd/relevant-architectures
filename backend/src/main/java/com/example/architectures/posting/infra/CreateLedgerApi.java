package com.example.architectures.posting.infra;

import com.example.architectures.auth.ConsultantAuthorised;
import com.example.architectures.common.ClientId;
import com.example.architectures.posting.CreateLedger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CreateLedgerApi {

    private final CreateLedger createLedger;

    CreateLedgerApi(CreateLedger createLedger) {
        this.createLedger = createLedger;
    }

    @ConsultantAuthorised
    @PostMapping("/clients/{clientId}/ledger")
    @ResponseStatus(HttpStatus.CREATED)
    void handle(@PathVariable ClientId clientId) {
        createLedger.handle(clientId);
    }

}
