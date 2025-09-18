package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Long> {
    List<StudentChecking> findByPrimaryOwner_Id(Long ownerId);
    List<StudentChecking> findBySecondaryOwner_Id(Long ownerId);
}
