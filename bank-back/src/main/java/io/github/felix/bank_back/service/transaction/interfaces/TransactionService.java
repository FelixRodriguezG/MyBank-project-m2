package io.github.felix.bank_back.service.transaction.interfaces;

import io.github.felix.bank_back.model.transaction.Transaction;
import java.time.LocalDateTime;
import java.util.List;


public interface TransactionService {
    List<Transaction> getTransactionsBetweenDates(LocalDateTime start, LocalDateTime end);
    long countTransactionsByAccountAndDateRange(Long accountId, LocalDateTime start, LocalDateTime end);
}
