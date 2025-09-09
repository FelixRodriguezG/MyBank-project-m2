package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Currency;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Savings extends Account {

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
    private Money minimumBalance;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "last_interest_date")
    private LocalDate lastInterestDate;


    // ==================== CONSTRUCTORES ====================

    // Constructor con propietario principal y divisa(currentCode) personalizada
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        initializeDefaults(balance.getCurrencyCode());
    }

    // Constructor de cuenta con dos propietarios y divisa(currentCode) personalizada
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        initializeDefaults(balance.getCurrencyCode());
    }

    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        initializeDefaults(balance.getCurrencyCode());
        setInterestRate(interestRate); // Usar el setter para validar la tasa de interés
    }

    // Constructor de cuenta con dos propietarios y divisa por defecto en Money
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        initializeDefaults(balance.getCurrencyCode());
        setInterestRate(interestRate); // Usa setter para validación
    }

    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, String currencyCode, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        Currency currency = Currency.getInstance(currencyCode);

        // VALIDAR que el currency del balance coincida con el solicitado
        if (!balance.getCurrencyCode().equals(currency)) {
            throw new IllegalArgumentException(
                    String.format("Divisa incorrecta: la divisa del balance es %s pero la solicitada es %s",
                            balance.getCurrencyCode().getCurrencyCode(), currencyCode)
            );
        }

        initializeDefaults(currency);
        setInterestRate(interestRate);
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

    // Valida que el interés esté dentro del rango permitido\
    public boolean isValidInterestRate(BigDecimal rate) {
        return rate != null &&
               rate.compareTo(BigDecimal.ZERO) > 0 &&
               rate.compareTo(MAX_INTEREST_RATE) <= 0;
    }

    public boolean isValidMinimumBalance(Money minBalance) {
        return minBalance != null &&
                minBalance.getAmount().compareTo(MIN_MINIMUM_BALANCE) >= 0;
    }

    // ==================== SETTERS CON VALIDACIÓN -> (CONSTRUCTORES) ====================

    // * Establece la tasa de interés con validación
    public void setInterestRate(BigDecimal interestRate) {
        if (!isValidInterestRate(interestRate)) {
            throw new IllegalArgumentException(
                    String.format("Interest rate must be between 0.0001 and %s. Provided: %s",
                            MAX_INTEREST_RATE, interestRate)
            );
        }
        this.interestRate = interestRate;
    }

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
    // ==================== MÉTODOS DE IN ====================

    // * Obtiene la próxima fecha en la que se debe aplicar el interés anual
    public LocalDate getNextInterestDate() {
        if (lastInterestDate == null) {
            return LocalDate.now();
        }
        return lastInterestDate.plusYears(1);
    }

    // Devuelve el tipo de cuenta como String
    public String getAccountType() {
        return "Savings";
    }

    // Información detallada del tipo de cuenta
    public String getAccountTypeInfo() {
        return String.format("Savings Account - Min Balance: %s, Annual Interest: %s%%, Next Interest: %s",
                minimumBalance,
                interestRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN),
                getNextInterestDate());
    }

    @Override
    public String toString() {
        return String.format("Savings Account [ID: %d, Balance: %s, Min Balance: %s, Interest: %s%%, Owner: %s, Status: %s]",
                getId(),
                getBalance(),
                minimumBalance,
                interestRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN),
                getPrimaryOwner().getName(),
                getStatus());
    }

}