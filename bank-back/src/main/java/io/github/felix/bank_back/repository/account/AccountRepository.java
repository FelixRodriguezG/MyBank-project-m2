package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Account.
 * Extiende JpaRepository para tener métodos CRUD básicos
 * y define queries personalizadas usando la convención de nombres de Spring Data JPA.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {


    // * Encuentra todas las cuentas donde el owner coincida ya sea como primary o secondary.
    List<Account> findByPrimaryOwnerIdOrSecondaryOwnerId(Long primaryId, Long secondaryId);

    // * Encuentra todas las cuentas asociadas a un owner específico.
    List<Account> findByPrimaryOwnerId(Long ownerId);

    // * Encuentra todas las cuentas asociadas a un secondary owner específico.
    List<Account> findBySecondaryOwnerId(Long ownerId);

    // * Encuentra cuentas filtrando por estado (ACTIVE o FROZEN)
    List<Account> findByStatus(String status);

    // * Encuentra una cuenta por su secretKey única. -> Util para autenticación.
    Account findBySecretKey(String secretKey);

    /**
     * Encuentra todas las cuentas cuyo balance sea menor a su Balance minimo.
     * Útil para aplicar PenaltyFee.
     */
    @Query("SELECT a FROM Account a WHERE a.balance.amount < :amount")
    List<Account> findByBalanceAmountLessThan(@Param("amount") BigDecimal amount);;

    /**
     * Encuentra todas las cuentas cuyo balance sea mayor que la cantidad especificada.
     * Útil para validaciones o reportes.
     */
    @Query("SELECT a FROM Account a WHERE a.balance.amount > :amount")

    List<Account> findByBalanceGreaterThan(@Param("amount") BigDecimal amount);

}
