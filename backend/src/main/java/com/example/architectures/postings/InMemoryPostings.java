package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryPostings implements Postings {
    private final List<Posting> postings = new ArrayList<>();

    @Override
    public void saveAll(List<Posting> postings) {
        this.postings.addAll(postings);
    }

    @Override
    public List<Posting> findAll() {
        return postings;
    }

    @Override
    public List<Posting> findAllByClientIdAndAccountId(int clientId, int accountId) {
        return postings
            .stream()
            .filter(it -> it.clientId() == clientId && it.accountId() == accountId)
            .toList();
    }
}
