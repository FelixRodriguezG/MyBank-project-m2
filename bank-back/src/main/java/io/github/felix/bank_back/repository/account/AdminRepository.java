package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
