package io.github.felix.bank_back.repository.user;


import io.github.felix.bank_back.model.user.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    Optional<SystemUser> findByName(String name);
    Optional<SystemUser> findByHashedKey(String hashedKey);
}
