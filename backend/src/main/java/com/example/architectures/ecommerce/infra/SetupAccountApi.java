package com.example.architectures.ecommerce.infra;

import com.example.architectures.auth.ConsultantAuthorised;
import com.example.architectures.common.ClientId;
import com.example.architectures.ecommerce.AccountId;
import com.example.architectures.ecommerce.SetupAccount;
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
    void handle(@PathVariable ClientId clientId, @RequestBody Request.Account account) {
        setupAccount.handle(clientId, account.accountId());
    }

    static class Request {
        record Account(AccountId accountId) {}
    }
}
