package com.monie.xpress.transaction.repository;

import com.monie.xpress.transaction.data.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
