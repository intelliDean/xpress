package com.monie.xpress.transaction.services;

import com.monie.xpress.transaction.data.model.Transaction;

public interface TransactionService {
    void saveTransaction(Transaction transaction);
}