package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    /**
     * Constructor que crea balance con currency específico
     */
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

    private void initializeDefaults(Currency currency) {
        this.creditLimit = new Money(DEFAULT_CREDIT_LIMIT, currency);
        this.interestRate = DEFAULT_INTEREST_RATE;
        this.lastInterestDate = getCreationDate();
    }

    // ==================== VALIDACIONES ====================

    //* Valida que el límite de crédito esté en el rango permitido
    public boolean isValidCreditLimit(Money creditLimit) {
        if (creditLimit == null) return false;
        BigDecimal amount = creditLimit.getAmount();
        return amount.compareTo(DEFAULT_CREDIT_LIMIT) >= 0 &&
                amount.compareTo(MAX_CREDIT_LIMIT) <= 0;
    }

    //* Valida que la tasa de interés esté en el rango permitido
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

    // Obtiene la deuda actual (cuánto se debe pagar) si el balance es negativo
    public Money getCurrentDebt() {
        BigDecimal balanceAmount = getBalance().getAmount();
        if (balanceAmount.compareTo(BigDecimal.ZERO) >= 0) {
            return new Money(BigDecimal.ZERO, getBalance().getCurrencyCode());
        }
        return new Money(balanceAmount.abs(), getBalance().getCurrencyCode());
}
