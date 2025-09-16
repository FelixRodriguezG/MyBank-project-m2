package io.github.felix.bank_back.repository.user;

import io.github.felix.bank_back.model.user.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.github.felix.bank_back.model.user.enums.UserStatus;

import java.util.List;
import java.util.Optional;

public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {
    Optional<ThirdParty> findByHashedKey(String hashedKey);

    List<ThirdParty> findByNameContainingIgnoreCase(String name);

    List<ThirdParty> findByStatus(UserStatus status);

    boolean existsByHashedKey(String hashedKey);

    List<ThirdParty> findByStatusAndName(UserStatus status, String name);

    @Query("SELECT COUNT(tp) FROM ThirdParty tp WHERE tp.status = UserStatus.ACTIVE")
    long countActiveThirdParties();
}