package io.github.felix.bank_back.repository.transaction;

import io.github.felix.bank_back.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import io.github.felix.bank_back.model.transaction.enums.TransactionStatus;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_Id(Long accountId);
    List<Transaction> findByAccount_PrimaryOwner_Id(Long ownerId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByThirdParty_Id(Long thirdPartyId);
    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    long countByAccount_IdAndTransactionDateBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}
