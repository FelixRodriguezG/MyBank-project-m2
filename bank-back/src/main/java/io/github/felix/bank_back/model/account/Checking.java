package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
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

    /* Metodos privados */


    // Comprueba si el balance está por debajo del mínimo
    public boolean isBelowMinimumBalance() {
        return getBalance().getAmount().compareTo(minimumBalance.getAmount()) < 0;
    }

    // Verifica si ha pasado un mes desde la última cuota de mantenimiento
    public boolean shouldApplyMonthlyMaintenanceFee() {
        if(lastMaintenanceFeeDate == null) return true; // El primer mes siempre se cobra
        return lastMaintenanceFeeDate.plusMonths(1).isBefore(LocalDate.now());
    }

    // Aplica la cuota de mantenimiento mensual si corresponde
    // (actualiza el balance y la fecha, pero no guarda los cambios en la base de datos)
    public void applyMonthlyMaintenanceFee() {
        if (shouldApplyMonthlyMaintenanceFee()) {
            // Restar la cuota de mantenimiento del balance
            getBalance().decreaseAmount(monthlyMaintenanceFee);
            // Actualizar la fecha de la última cuota aplicada
            lastMaintenanceFeeDate = LocalDate.now();
        }
    }

    // Verifica si hay suficiente balance para realizar una operación sin caer por debajo del mínimo
    public boolean hasEnoughBalance(Money amount) {
        Money balanceAfterOperation = new Money(
                getBalance().getAmount().subtract(amount.getAmount()),
                getBalance().getCurrencyCode()
        );
        return balanceAfterOperation.getAmount().compareTo(minimumBalance.getAmount()) >= 0;
    }

    public boolean hasEnoughBalanceWithFees(Money amount) {
        Money balanceAfterOperation = new Money(
                getBalance().getAmount().subtract(amount.getAmount()).subtract(monthlyMaintenanceFee.getAmount()),
                getBalance().getCurrencyCode()
        );
        return balanceAfterOperation.getAmount().compareTo(minimumBalance.getAmount()) >= 0;
    }


    // Constructor con propietario principal solamente
    public Checking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        initializeDefaults(balance.getCurrencyCode());
    }

    // Constructor con propietario principal y secundario
    public Checking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        initializeDefaults(balance.getCurrencyCode());
    }

    // Constructor con currency personalizado
    public Checking(Money balance, String secretKey, AccountHolder primaryOwner, String currencyCode) {
        super(balance, secretKey, primaryOwner);
        initializeDefaults(Currency.getInstance(currencyCode));
    }

    // Constructor con propietario principal y secundario y currency personalizado
    public Checking(Money balance, String secretKey, AccountHolder primaryOwner,
                    AccountHolder secondaryOwner, String currencyCode) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        initializeDefaults(Currency.getInstance(currencyCode));
    }

    // Metodo para inicializar los valores por defecto en los constructores
    private void initializeDefaults(Currency currency) {
        this.minimumBalance = new Money(BigDecimal.valueOf(250), currency);
        this.monthlyMaintenanceFee = new Money(BigDecimal.valueOf(12), currency);
        this.lastMaintenanceFeeDate = getCreationDate(); // Inicializar con fecha de creación
    }

}