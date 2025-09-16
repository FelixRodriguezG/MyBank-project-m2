package io.github.felix.bank_back.repository.user;
import io.github.felix.bank_back.dto.user.Admin.AdminResponseDTO;
import io.github.felix.bank_back.model.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(Long id);
    Optional<Admin> findByName(String name);
}
