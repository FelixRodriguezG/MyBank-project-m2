package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Checking extends Account {

    // ==================== CHECKING_INFO ====================
    // Hereda de Account : id, balance, secretKey, primaryOwner, secondaryOwner, creationDate, status
    // Atributos específicos de Checking:
    // - minimumBalance (Balance mínimo): 250 USD por defecto, no puede ser menor
    // - monthlyMaintenanceFee (Cuota de mantenimiento mensual): 12 USD por defecto
    // - lastMaintenanceFeeDate (Última fecha en la que se aplicó la cuota de mantenimiento)
    // Comportamiento adicional:
    // - Si el balance cae por debajo del mínimo, se cobra la cuota una penalización de 40 USD
    // - Cada mes se cobra la cuota de mantenimiento automáticamente de 12 USD

    // ==================== CONSTANTES ====================
    public static final Money DEFAULT_MINIMUM_BALANCE = new Money(BigDecimal.valueOf(250), Currency.getInstance("USD"));
    public static final Money DEFAULT_MONTHLY_MAINTENANCE_FEE = new Money(BigDecimal.valueOf(12), Currency.getInstance("USD"));

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",       column = @Column(name = "minimum_balance_amount", precision =
                    19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "minimum_balance_currency", length = 3,
                    nullable = false))
    })
    @Min(250)
    private Money minimumBalance;// Balance mínimo: 250 USD

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",       column = @Column(name = "monthly_maintenance_fee_amount",
                    precision =
                    19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "monthly_maintenance_fee_currency",
                    length = 3,
                    nullable = false))
    })
    private Money monthlyMaintenanceFee; // Cuota de mantenimiento mensual: 12 USD

    private LocalDate lastMaintenanceFeeDate;// Última fecha en la que se aplicó la cuota de mantenimiento



    // ==================== CONSTRUCTORES ====================

    // Constructor con propietario principal solamente(currency por defecto USD)
    public Checking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner, AccountType.CHECKING);
        initializeDefaults(balance.getCurrencyCode());
    }

    // Constructor con propietario principal y secundario(currency por defecto USD)
    public Checking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner, AccountType.CHECKING);
        initializeDefaults(balance.getCurrencyCode());
    }

    // ==================== MÉTODOS DE INICIALIZACIÓN ====================

    // Metodo para inicializar los valores por defecto en los constructores
    private void initializeDefaults(Currency currency) {
        this.minimumBalance = new Money(BigDecimal.valueOf(250), currency);
        this.monthlyMaintenanceFee = new Money(BigDecimal.valueOf(12), currency);
        this.lastMaintenanceFeeDate = getCreationDate(); // Inicializar con fecha de creación
    }




    // =========================== MÉTODOS DEL SYSTEMA ==============================

    // * Comprueba si el balance está por debajo del mínimo
    public boolean isBelowMinimumBalance() {
        return getBalance().getAmount().compareTo(minimumBalance.getAmount()) < 0;
    }

    // * Verifica si ha pasado un mes desde la última cuota de mantenimiento
    public boolean shouldApplyMonthlyMaintenanceFee() {
        if(lastMaintenanceFeeDate == null) return true; // El primer mes siempre se cobra
        return lastMaintenanceFeeDate.plusMonths(1).isBefore(LocalDate.now());
    }

    // * Aplica la cuota de mantenimiento mensual si corresponde
    // (actualiza el balance y la fecha, pero no guarda los cambios en la base de datos)
    public void applyMonthlyMaintenanceFee() {
        if (shouldApplyMonthlyMaintenanceFee()) {
            // Restar la cuota de mantenimiento del balance
            getBalance().decreaseAmount(monthlyMaintenanceFee);
            // Actualizar la fecha de la última cuota aplicada
            lastMaintenanceFeeDate = LocalDate.now();
        }
    }

    // * Aplica la penalización si el balance está por debajo del mínimo
    public void applyPenaltyIfBelowMinimum() {
        if (isBelowMinimumBalance()) {
            Money penalty = new Money(BigDecimal.valueOf(40), getBalance().getCurrencyCode());
            getBalance().decreaseAmount(penalty);
        }
    }

    // * Verifica si hay suficiente balance para realizar una operación sin caer por debajo del mínimo
    public boolean hasEnoughBalance(Money amount) {
        Money balanceAfterOperation = new Money(
                getBalance().getAmount().subtract(amount.getAmount()),
                getBalance().getCurrencyCode()
        );
        return balanceAfterOperation.getAmount().compareTo(minimumBalance.getAmount()) >= 0;
    }

    // ==================== INFORMACIÓN ====================

    @Override
    public String getAccountTypeInfo() {
        return "Checking Account - Balance mínimo: 250 USD, Cuota de mantenimiento mensual: 12 USD, Penalización si el balance cae por debajo del mínimo.";
    }

   @Override
   public String toString() {
       return String.format(
               "Cuenta Corriente [ID: %d, Balance: %s, Clave Secreta: %s, Titular: %s%s, Fecha de Creación: %s, Estado: %s, Balance Mínimo: %s, Cuota de Mantenimiento Mensual: %s, Última Fecha de Mantenimiento: %s]",
               getId(),
               getBalance(),
               getSecretKey(),
               getPrimaryOwner().getName(),
               getSecondaryOwner() != null ? ", Titular Secundario: " + getSecondaryOwner().getName() : "",
               getCreationDate(),
               getStatus(),
               minimumBalance,
               monthlyMaintenanceFee,
               lastMaintenanceFeeDate
       );
   }
}