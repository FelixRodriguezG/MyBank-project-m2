package io.github.felix.bank_back.repository.account;
import io.github.felix.bank_back.model.account.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Long> {
}
