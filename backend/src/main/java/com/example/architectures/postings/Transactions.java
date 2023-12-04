package com.example.architectures.postings;

import java.util.List;

interface Transactions {
    void saveAll(List<Transaction> entities);
    List<Transaction> findAll();
}
