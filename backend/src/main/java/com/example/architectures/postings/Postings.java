package com.example.architectures.postings;

import java.util.List;

interface Postings {
    void saveAll(List<Posting> postings);
    List<Posting> findAll();
    List<Posting> findAllByClientIdAndAccountId(int clientId, int accountId);
}
