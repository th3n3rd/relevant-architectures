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
}
