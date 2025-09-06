package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Account.
 * Extiende JpaRepository para tener métodos CRUD básicos
 * y define queries personalizadas usando la convención de nombres de Spring Data JPA.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Encuentra todas las cuentas donde el ID del primaryOwner coincida.
     */
    List<Account> findByPrimaryOwnerId(Long ownerId);

    /**
     * Encuentra todas las cuentas donde el ID del secondaryOwner coincida.
     */
    List<Account> findBySecondaryOwnerId(Long ownerId);

    /**
     * Encuentra todas las cuentas donde el owner coincida ya sea como primary o secondary.
     * Útil para traer todas las cuentas de un usuario.
     */
    List<Account> findByPrimaryOwnerIdOrSecondaryOwnerId(Long primaryId, Long secondaryId);

    /**
     * Encuentra cuentas filtrando por estado (ACTIVE o FROZEN).
     */
    List<Account> findByStatus(String status);

    /**
     * Encuentra una cuenta a partir de su secretKey.
     * Útil para operaciones de terceros (Third-Party).
     */
    Account findBySecretKey(String secretKey);

    /**
     * Encuentra cuentas según el tipo (dtype).
     * Solo es útil si usas herencia con @DiscriminatorColumn en Account
     * y cada subtipo tiene un valor distinto (CHECKING, SAVINGS, etc).
     */
    List<Account> findByDtype(String dtype);

    /**
     * Encuentra todas las cuentas cuyo balance sea menor que la cantidad especificada.
     * Útil para aplicar PenaltyFee.
     */
    List<Account> findByBalanceLessThan(BigDecimal amount);

    /**
     * Encuentra todas las cuentas cuyo balance sea mayor que la cantidad especificada.
     * Útil para validaciones o reportes.
     */
    List<Account> findByBalanceGreaterThan(BigDecimal amount);

}
