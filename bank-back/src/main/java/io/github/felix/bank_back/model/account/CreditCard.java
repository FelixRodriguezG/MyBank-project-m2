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


// Límite de crédito por defecto: 100
// Máximo configurable: 100000
// Interés por defecto: 0.2
// Mínimo configurable: 0.1
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class CreditCard extends Account {

    // Constantes según los requisitos
    private static final BigDecimal DEFAULT_CREDIT_LIMIT = new BigDecimal("100");
    private static final BigDecimal MAX_CREDIT_LIMIT = new BigDecimal("100000");
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.2");
    private static final BigDecimal MIN_INTEREST_RATE = new BigDecimal("0.1");

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "credit_limit_currency", length = 3, nullable = false))
    })
    private Money creditLimit;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "last_interest_date")
    private LocalDate lastInterestDate;


    // ==================== CONSTRUCTORES ====================

    // * Constructor con propietario principal y valores por defecto
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        initializeDefaults(balance.getCurrencyCode());
    }

    // * Constructor con dos propietarios y valores por defecto
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        initializeDefaults(balance.getCurrencyCode());
    }

    // * Constructor con propietario principal, límite de crédito e interés personalizados
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner,
                      Money creditLimit, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        setCreditLimit(creditLimit); // Validación automática
        setInterestRate(interestRate); // Validación automática
        this.lastInterestDate = getCreationDate();
    }

    // * Constructor con dos propietarios, límite de crédito e interés personalizados
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner,
                      Money creditLimit, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        setCreditLimit(creditLimit); // Validación automática
        setInterestRate(interestRate); // Validación automática
        this.lastInterestDate = getCreationDate();
    }


    // * Constructor que crea balance con currency específico
    public CreditCard(BigDecimal balanceAmount, String secretKey, AccountHolder primaryOwner,
                      String currencyCode, BigDecimal creditLimitAmount, BigDecimal interestRate) {
        Currency currency = Currency.getInstance(currencyCode);
        Money balance = new Money(balanceAmount, currency);
        Money creditLimit = new Money(creditLimitAmount, currency);

        this.setBalance(balance);
        this.setSecretKey(secretKey);
        this.setPrimaryOwner(primaryOwner);
        this.setCreationDate(LocalDate.now());
        this.setStatus(io.github.felix.bank_back.model.account.enums.AccountStatus.ACTIVE);
        this.setPenaltyFee(new Money(new BigDecimal("40"), currency));

        setCreditLimit(creditLimit);
        setInterestRate(interestRate);
        this.lastInterestDate = getCreationDate();
    }

    // ==================== INICIALIZACIÓN ====================

    // * Inicializa los valores por defecto para límite de crédito e interés
    private void initializeDefaults(Currency currency) {
        this.creditLimit = new Money(DEFAULT_CREDIT_LIMIT, currency);
        this.interestRate = DEFAULT_INTEREST_RATE;
        this.lastInterestDate = getCreationDate();
    }

    // ==================== VALIDACIONES ====================

    // * Valida que el límite de crédito esté en el rango permitido
    public boolean isValidCreditLimit(Money creditLimit) {
        if (creditLimit == null) return false;
        BigDecimal amount = creditLimit.getAmount();
        return amount.compareTo(DEFAULT_CREDIT_LIMIT) >= 0 &&
                amount.compareTo(MAX_CREDIT_LIMIT) <= 0;
    }

    // * Valida que la tasa de interés esté en el rango permitido
    public boolean isValidInterestRate(BigDecimal interestRate) {
        if (interestRate == null) return false;
        return interestRate.compareTo(MIN_INTEREST_RATE) >= 0 &&
                interestRate.compareTo(new BigDecimal("1.0")) <= 0; // Máximo 100%
    }

    // ==================== SETTERS CON VALIDACIÓN ====================

    //* Establece el límite de crédito con validación
    public void setCreditLimit(Money creditLimit) {
        if (!isValidCreditLimit(creditLimit)) {
            throw new IllegalArgumentException(
                    String.format("Credit limit must be between %s and %s. Provided: %s",
                            DEFAULT_CREDIT_LIMIT, MAX_CREDIT_LIMIT,
                            creditLimit != null ? creditLimit.getAmount() : "null")
            );
        }
        this.creditLimit = creditLimit;
    }

    //* Establece la tasa de interés con validación
    public void setInterestRate(BigDecimal interestRate) {
        if (!isValidInterestRate(interestRate)) {
            throw new IllegalArgumentException(
                    String.format("Interest rate must be between %s and 1.0. Provided: %s",
                            MIN_INTEREST_RATE, interestRate)
            );
        }
        this.interestRate = interestRate;
    }

    // ==================== LOGICA DE CRÉDITO  ====================

    // * Obtiene el crédito disponible (cuánto más se puede gastar)
    public Money getAvailableCredit() {
        // Si balance es positivo: crédito disponible = límite + balance
        // Si balance es negativo: crédito disponible = límite - |balance|
        BigDecimal available = creditLimit.getAmount().add(getBalance().getAmount());
        return new Money(available, creditLimit.getCurrencyCode());
    }

    // * Obtiene la deuda actual (cuánto se debe pagar) solo si el balance es negativo
    public Money getCurrentDebt() {
        BigDecimal balanceAmount = getBalance().getAmount();

        // Si el balance es positivo o cero, no hay deuda devuelve 0
        if (balanceAmount.compareTo(BigDecimal.ZERO) >= 0) {
            return new Money(BigDecimal.ZERO, getBalance().getCurrencyCode());
        }
        // Si el balance es negativo, la deuda es el valor absoluto del balance negativo
        return new Money(balanceAmount.abs(), getBalance().getCurrencyCode());
    }

    // * Verifica si se puede realizar una compra con el monto solicitado
    public boolean canMakePurchase(Money amount) {
        Money availableCredit = getAvailableCredit(); // Crédito disponible
        // Compara si el crédito disponible es suficiente para la cantidad solicitada
        return availableCredit.getAmount().compareTo(amount.getAmount()) >= 0;
    }
    // * Permite realizar una compra si hay crédito disponible
    public boolean makePurchase(Money amount) {
        if (!canMakePurchase(amount)) {
            return false;
        }
        getBalance().decreaseAmount(amount);
        return true;
    }

    // * Permite pagar la deuda de la tarjeta de crédito
    public void payCreditCardDebt(Money amount) {
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        getBalance().increaseAmount(amount);
    }

    // * Debe aplicarse el interés mensual si ha pasado al menos un mes desde la última aplicación
    public boolean shouldApplyMonthlyInterest() {
        if (lastInterestDate == null) return true;
        return lastInterestDate.plusMonths(1).isBefore(LocalDate.now());
    }


    // * Calcula el interés mensual basado en el balance actual y la tasa de interés
    public Money calculateMonthlyInterest() {
        BigDecimal currentBalance = getBalance().getAmount();

        // Solo se cobra interés sobre saldos negativos (deuda)
        if (currentBalance.compareTo(BigDecimal.ZERO) >= 0) {
            return new Money(BigDecimal.ZERO, getBalance().getCurrencyCode());
        }

        // Interés = |balance| * interestRate
        BigDecimal interestAmount = currentBalance.abs()
                .multiply(interestRate)
                .setScale(2, RoundingMode.HALF_EVEN);

        return new Money(interestAmount, getBalance().getCurrencyCode());
    }

    // * Aplica el interés mensual si corresponde y actualiza la fecha
    public boolean applyMonthlyInterest() {
        if (!shouldApplyMonthlyInterest()) {
            return false;
        }

        Money interest = calculateMonthlyInterest();
        if (interest.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            // El interés reduce más el balance (aumenta la deuda)
            getBalance().decreaseAmount(interest);
        }

        this.lastInterestDate = LocalDate.now();
        return true;
    }

    // ==================== INFORMACIÓN Y UTILIDADES ====================
    // * Obtiene la fecha del próximo cargo de interés
    public LocalDate getNextInterestDate() {
        if (lastInterestDate == null) {
            return LocalDate.now();
        }
        return lastInterestDate.plusMonths(1);
    }

    // * Verifica si la cuenta tiene deuda
    public boolean hasDebt() {
        return getBalance().getAmount().compareTo(BigDecimal.ZERO) < 0;
    }

    // * Obtiene el porcentaje de crédito utilizado
    public BigDecimal getCreditUtilizationPercentage() {
        Money debt = getCurrentDebt();
        if (debt.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return debt.getAmount()
                .divide(creditLimit.getAmount(), 4, RoundingMode.HALF_EVEN)
                .multiply(new BigDecimal("100"));
    }

    // * Retorna el tipo de cuenta
    public String getType() {
        return "CreditCard";
    }

    // * Obtiene información específica del tipo de cuenta
    public String getAccountTypeInfo() {
        return String.format("Credit Card - Limit: %s, Available: %s, Debt: %s, Interest: %s%% monthly",
                creditLimit,
                getAvailableCredit(),
                getCurrentDebt(),
                interestRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Override
    public String toString() {
        return String.format("Credit Card [ID: %d, Balance: %s, Limit: %s, Available: %s, Interest: %s%%, Owner: %s, Status: %s]",
                getId(),
                getBalance(),
                creditLimit,
                getAvailableCredit(),
                interestRate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN),
                getPrimaryOwner().getName(),
                getStatus());
    }
}


