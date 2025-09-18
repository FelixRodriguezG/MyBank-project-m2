package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.Savings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsRepository extends JpaRepository<Savings, Long> {
    List<Savings> findByPrimaryOwner_Id(Long ownerId);
    List<Savings> findBySecondaryOwner_Id(Long ownerId);
}
