package com.example.architectures.postings;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryPostings implements Postings, PaginatedPostings {
    private final List<Posting> postings = new ArrayList<>();

    @Override
    public void saveAll(List<Posting> postings) {
        this.postings.addAll(postings);
    }

    public List<Posting> findAll() {
        return postings;
    }

    @Override
    public Page<Posting> findAllByClientIdAndAccountId(ClientId clientId, AccountId accountId, Pageable page) {
        var nonPaginatedPostings = postings
            .stream()
            .filter(it -> it.clientId().equals(clientId) && it.accountId().equals(accountId))
            .toList();

        var paginatedPostings = nonPaginatedPostings
            .stream()
            .skip(page.getOffset())
            .limit(page.getPageSize())
            .toList();

        return new PageImpl<>(
            paginatedPostings,
            page,
            nonPaginatedPostings.size()
        );
    }

    public void deleteAll() {
        postings.clear();
    }
}
