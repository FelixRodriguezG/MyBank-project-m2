package io.github.felix.bank_back.repository.user;

import io.github.felix.bank_back.model.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
}
