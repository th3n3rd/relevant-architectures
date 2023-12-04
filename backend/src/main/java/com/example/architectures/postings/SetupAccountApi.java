package com.example.architectures.postings;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SetupAccountApi {

    @PostMapping("/clients/{clientId}/accounts")
    void handle(@PathVariable String clientId) {

    }

}
