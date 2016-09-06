package com.tmasuda.fc.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.tmasuda.fc.model.Account;
import com.tmasuda.fc.model.Transaction;

public interface TransactionRepo extends PagingAndSortingRepository<Transaction, Long> {

	Page<Transaction> findByAccount(Pageable aPageable, Account anAccount);

}
