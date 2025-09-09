package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.account.enums.AccountStatus;
import io.github.felix.bank_back.model.account.enums.AccountType;
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

    List<Account> findByAccountType(AccountType accountType);

    // --- Búsqueda por balance ---
    // * Búsqueda de cuentas tipo Checking y Savings con un balance menor al balance mínimo(para aplicar penalización)
    @Query(value = "SELECT * FROM account WHERE (account_type = 'CHECKING' OR account_type = 'SAVINGS') AND balance_amount < minimum_balance_amount", nativeQuery = true)
    List<Account> findByBalanceAmountLessThanMinimumBalance();


    // * Búsqueda para aplicar penalización a las cuentas StudentChecking
    // * Cuentas de ahorro de estudiante con balance menor a 0
    @Query("SELECT a FROM Account a WHERE a.accountType = 'STUDENT' AND a.balance.amount < 0")
    List<Account> findStudentAccountsByBalanceLessThanZero();

    // --- Búsqueda por fecha ---
    // * Cuentas de ahorro con fecha de la última cuota de mantenimiento anterior a hoy
    @Query(value = "SELECT * FROM account WHERE account_type = 'CHECKING' AND DATE_ADD(last_maintenance_fee_date, INTERVAL 1 MONTH) <= CURDATE()", nativeQuery = true)
    List<Account> findCheckingAccountsByLastMaintenanceFeeDateBeforeToday();

    // * Búsqueda para aplicar intereses a las cuentas de ahorro donde la fecha del último interés aplicado sea
    // * anterior a un año a de la fecha del Último interés aplicado
    @Query(value = "SELECT * FROM account a WHERE a.account_type = 'SAVINGS' AND DATE_ADD(a.last_interest_applied_date, INTERVAL 1 YEAR) <= CURDATE()", nativeQuery = true)
    List<Account> findSavingsAccountsByLastInterestAppliedDateBeforeToday();

    // * Búsqueda para aplicar intereses a las cuentas de crédito donde la fecha del último interés aplicado sea
    // * anterior a un mes a de la fecha del Último interés aplicado
    @Query(value = "SELECT * FROM account a WHERE a.account_type = 'CREDIT_CARD' AND DATE_ADD(a.last_interest_applied_date, INTERVAL 1 MONTH) <= CURDATE()", nativeQuery = true)
    List<Account> findCreditCardAccountsByLastInterestAppliedDateBeforeToday();

}
