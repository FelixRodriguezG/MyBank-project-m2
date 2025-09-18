package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.Checking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckingRepository extends JpaRepository<Checking, Long> {
    List<Checking> findByPrimaryOwner_Id(Long ownerId);
    List<Checking> findBySecondaryOwner_Id(Long ownerId);
}
