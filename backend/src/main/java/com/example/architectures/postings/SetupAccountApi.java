package com.example.architectures.postings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SetupAccountApi {

    private final SetupAccount setupAccount;

    SetupAccountApi(SetupAccount setupAccount) {
        this.setupAccount = setupAccount;
    }

    @ConsultantAuthorised
    @PostMapping("/clients/{clientId}/accounts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void handle(@PathVariable ClientId clientId, @RequestBody Account account) {
        setupAccount.handle(clientId, account.accountId());
    }

    record Account(AccountId accountId) {}
}
