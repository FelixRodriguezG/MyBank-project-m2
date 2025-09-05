package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "savings_accounts")
public class Savings extends Account {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "minimum_balance_currency", length = 3, nullable = false))
    })
    private Money minimumBalance;

    @Column(nullable = false)
    private BigDecimal interestRate;

    // Constructo de cuenta con propietario principal y divisa(currentCode) personalizada
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, String currencyCode,
                   BigDecimal interestRate) {
        super(balance,secretKey, primaryOwner);
        Currency currency = Currency.getInstance(currencyCode);
        this.minimumBalance = new Money(BigDecimal.valueOf(1000), currency);
        this.interestRate = interestRate;
    }

    // Constructor de cuenta con dos propietarios y divisa(currentCode) personalizada
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, String currencyCode, BigDecimal interestRate) {
        super(balance,secretKey, primaryOwner, secondaryOwner);
        Currency currency = Currency.getInstance(currencyCode);
        this.minimumBalance = new Money(BigDecimal.valueOf(1000), currency);
        this.interestRate = interestRate;
    }

    // Constructor de cuenta solo con propietario principal y divisa por defecto en Money
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, BigDecimal interestRate) {
        super(balance,secretKey, primaryOwner);
        Currency currency = balance.getCurrencyCode();
        this.minimumBalance = new Money(BigDecimal.valueOf(1000), currency);
        this.interestRate = interestRate;
    }

    // Constructor de cuenta con dos propietarios y divisa por defecto en Money
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal interestRate) {
        super(balance,secretKey, primaryOwner, secondaryOwner);
        Currency currency = balance.getCurrencyCode();
        this.minimumBalance = new Money(BigDecimal.valueOf(1000), currency);
        this.interestRate = interestRate;
    }
}