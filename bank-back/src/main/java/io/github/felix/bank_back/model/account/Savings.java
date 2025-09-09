package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.account.enums.AccountType;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Savings extends Account {

    // ==================== SAVINGS_INFO ====================
    // Hereda de Account
    // id, balance, secretKey, primaryOwner, secondaryOwner, creationDate, status
    // Atributos específicos de Savings
    // minimumBalance, interestRate, lastInterestDate
    // No tiene comisión mensual de mantenimiento pero si tiene penalización por debajo del balance mínimo
    // Tiene interés anual que se aplica automáticamente si ha pasado un año desde el último interés aplicado

    // ==================== CONSTANTES ====================
    // Constantes según los requisitos
    // interestRate por defecto 0.0025 (0.25%) y máximo 0.5 (50%)
    // minimumBalance por defecto 1000 y mínimo 100
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.0025");
    private static final BigDecimal MAX_INTEREST_RATE = new BigDecimal("0.5");
    private static final BigDecimal DEFAULT_MINIMUM_BALANCE = new BigDecimal("1000");
    private static final BigDecimal MIN_MINIMUM_BALANCE = new BigDecimal("100");

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "minimum_balance_currency", length = 3, nullable = false))
    })
    private Money minimumBalance; // Balance mínimo requerido

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal interestRate; // Tasa de interés anual

    @Column(name = "last_interest_date")
    private LocalDate lastInterestDate; // Fecha del último interés aplicado


    // ==================== CONSTRUCTORES ====================

    // Constructor con propietario principal
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner, AccountType.SAVINGS);
        initializeDefaults(balance.getCurrencyCode());
    }

    // Constructor de cuenta con dos propietario
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner, AccountType.SAVINGS);
        initializeDefaults(balance.getCurrencyCode());
    }

    // Constructor de cuenta con propietario principal y tasa de interés personalizada
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner, AccountType.SAVINGS);
        initializeDefaults(balance.getCurrencyCode());
        setInterestRate(interestRate); // Usar el setter para validar la tasa de interés
    }

    // Constructor de cuenta con dos propietarios y tasa de interés personalizada
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner, secondaryOwner, AccountType.SAVINGS);
        initializeDefaults(balance.getCurrencyCode());
        setInterestRate(interestRate); // Usa setter para validación
    }


    // ==================== MÉTODOS DE INICIALIZACIÓN ====================

    private void initializeDefaults(Currency currency) {
        this.minimumBalance = new Money(DEFAULT_MINIMUM_BALANCE, currency);
        this.interestRate = DEFAULT_INTEREST_RATE;
        this.lastInterestDate = getCreationDate();
    }

    // ==================== VALIDACIONES ====================

    // Comprueba si el balance está por debajo del mínimo requerido
    public boolean isBelowMinimumBalance() {
        return this.getBalance().getAmount().compareTo(getMinimumBalance().getAmount()) < 0;
    }

    // Verifica si hay suficiente balance para una operación sin caer por debajo del mínimo
    public boolean hasSufficientBalance(BigDecimal amount) {
        BigDecimal balanceAfterOperation  = this.getBalance().getAmount().subtract(amount);
        return balanceAfterOperation.compareTo(this.getMinimumBalance().getAmount()) >= 0;
    }

    // ==================== SETTERS CON VALIDACIÓN -> (CONSTRUCTORES) ====================

    // * Valida que el interés esté dentro del rango permitido\
    public boolean isValidInterestRate(BigDecimal rate) {
        return rate != null &&
                rate.compareTo(BigDecimal.ZERO) > 0 &&
                rate.compareTo(MAX_INTEREST_RATE) <= 0;
    }
    // * Establece la tasa de interés con validación (entre 0.0001 y 0.5)
    public void setInterestRate(BigDecimal interestRate) {
        if (!isValidInterestRate(interestRate)) {
            throw new IllegalArgumentException(
                    String.format("Interest rate must be between 0.0001 and %s. Provided: %s",
                            MAX_INTEREST_RATE, interestRate)
            );
        }
        this.interestRate = interestRate;
    }

    public boolean isValidMinimumBalance(Money minBalance) {
        return minBalance != null &&
                minBalance.getAmount().compareTo(MIN_MINIMUM_BALANCE) >= 0;
    }
    // * Establece el balance mínimo con validación (entre 100 y 1000)
    public void setMinimumBalance(Money minimumBalance) {
        if (!isValidMinimumBalance(minimumBalance)) {
            throw new IllegalArgumentException(
                    String.format("Minimum balance must be at least %s. Provided: %s",
                            MIN_MINIMUM_BALANCE, minimumBalance.getAmount())
            );
        }
        this.minimumBalance = minimumBalance;
    }

    // ==================== LÓGICA DE INTERESES ====================
    // Esto deveria ejecutarse automáticamente una vez al año
    // Podría implementarse con un servicio programado (scheduler)

    // * Verifica si debe aplicarse el interés anual (ha pasado un año)
    public boolean shouldApplyAnnualInterest() {
        if (lastInterestDate == null) {
            return true; // Primer año
        }
        return lastInterestDate.plusYears(1).isBefore(LocalDate.now());
    }

    // * Calcula el interés anual basado en el balance actual
    public Money calculateAnnualInterest() {
        BigDecimal interestAmount = getBalance().getAmount().multiply(interestRate);
        return new Money(interestAmount, getBalance().getCurrencyCode());
    }
    // * Aplica el interés anual si corresponde y actualiza la fecha del último interés aplicado
    public boolean applyAnnualInterest() {
        if(!shouldApplyAnnualInterest()) {
            return false; // No ha pasado un año, no se aplica interés
        }
        Money interest = calculateAnnualInterest();
        // Actualiza el balance sumando el interés
        getBalance().increaseAmount(interest);
        this.lastInterestDate=LocalDate.now();
        return true;
    }
    // ==================== INFORMACIÓN ====================

    // * Obtiene la próxima fecha en la que se debe aplicar el interés anual
    public LocalDate getNextInterestDate() {
        if (lastInterestDate == null) {
            return LocalDate.now();
        }
        return lastInterestDate.plusYears(1);
    }


    // * Información de cuenta
    @Override
    public String getAccountTypeInfo() {
        return "Cuenta de Ahorro - Saldo mínimo requerido, Interés anual aplicado automáticamente, Penalización por saldo bajo";
    }

    @Override
    public String toString() {
        return String.format("Cuenta de Ahorro [ID: %d, Saldo: %s, Saldo Mínimo: %s, Interés: %s%%, Titular: %s, Estado: %s]",
                getId(),
                getBalance(),
                minimumBalance,
                interestRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN),
                getPrimaryOwner().getName(),
                getStatus());
    }

}