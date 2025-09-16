package io.github.felix.bank_back.service.transaction.impl;

import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
import io.github.felix.bank_back.service.transaction.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactionsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByTransactionDateBetween(start, end);
    }

    @Override
    public long countTransactionsByAccountAndDateRange(Long accountId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.countByAccount_IdAndTransactionDateBetween(accountId, start, end);
    }
}
