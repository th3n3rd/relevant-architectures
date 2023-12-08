package com.example.architectures.postings;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface PaginatedPostings {
    Page<Posting> findAllByClientIdAndAccountId(int clientId, int accountId, Pageable page);
}
