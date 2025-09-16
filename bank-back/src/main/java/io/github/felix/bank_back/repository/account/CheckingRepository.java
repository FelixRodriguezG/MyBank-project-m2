package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.Checking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckingRepository extends JpaRepository<Checking, Long> {
}
