package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Account.
 * Extiende JpaRepository para tener métodos CRUD básicos
 * y define queries personalizadas usando la convención de nombres de Spring Data JPA.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    // --- Búsqueda por propietarios ---
    List<Account> findByPrimaryOwnerIdOrSecondaryOwnerId(Long primaryId, Long secondaryId);
    List<Account> findByPrimaryOwnerId(Long ownerId);
    List<Account> findBySecondaryOwnerId(Long ownerId);

    // --- Búsqueda por estado y tipo ---
    List<Account> findByStatus(AccountStatus status);
    List<Account> findByAccountType(String accountType);

    // --- Búsqueda por balance ---
    @Query("SELECT a FROM Account a WHERE a.balance.amount < a.minimumBalance.amount")
    List<Account> findByBalanceAmountLessThanMinimumBalance();

    @Query("SELECT a FROM Account a WHERE a.accountType = 'STUDENT' AND a.balance.amount < :amount")
    List<Account> findStudentAccountsByBalanceLessThan(@Param("amount") BigDecimal amount);

    // --- Búsqueda por fecha ---
    @Query("SELECT a FROM Account a WHERE a.lastMaintenanceFeeDate < :date")
    List<Account> findByLastMaintenanceFeeDateBefore(@Param("date") LocalDate date);

    // Busca cuentas de tipo c
}
