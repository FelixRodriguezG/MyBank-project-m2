package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Currency;

@Data
@NoArgsConstructor
@Entity
@Table(name = "checking_accounts")
public class Checking extends Account {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",       column = @Column(name = "minimum-balance_amount", precision =
                    19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "minimum-balance_currency", length = 3,
                    nullable = false))
    })
    @Min(250)
    private Money minimumBalance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",       column = @Column(name = "monthly-maintenance-fee_amount",
                    precision =
                    19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "monthly-maintenance-fee_currency", length = 3,
                    nullable = false))
    })
    private Money monthlyMaintenanceFee;



    // Constructor con propietario principal y currency personalizado
    public Checking(Money balance, AccountHolder primaryOwner, String currencyCode) {
        super(balance, primaryOwner);
        Currency currency = Currency.getInstance(currencyCode);
        this.minimumBalance = new Money(new java.math.BigDecimal("250"), currency);
        this.monthlyMaintenanceFee = new Money(new java.math.BigDecimal("12"), currency);
    }

    // Constructor con propietario principal y secundario y currency personalizado
    public Checking(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String currencyCode) {
        super(balance, primaryOwner, secondaryOwner);
        Currency currency = Currency.getInstance(currencyCode);
        this.minimumBalance = new Money(new java.math.BigDecimal("250"), currency);
        this.monthlyMaintenanceFee = new Money(new java.math.BigDecimal("12"), currency);
    }

    // Constructor solo con propietario principal y Money ya configurado
    public Checking(Money balance, AccountHolder primaryOwner) {
        super(balance, primaryOwner);
        Currency currency = balance.getCurrencyCode();
        this.minimumBalance = new Money(new java.math.BigDecimal("250"), currency);
        this.monthlyMaintenanceFee = new Money(new java.math.BigDecimal("12"), currency);
    }

    // Constructor con propietario principal, secundario y Money ya configurado
    public Checking(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, primaryOwner, secondaryOwner);
        Currency currency = balance.getCurrencyCode();
        this.minimumBalance = new Money(new java.math.BigDecimal("250"), currency);
        this.monthlyMaintenanceFee = new Money(new java.math.BigDecimal("12"), currency);
    }

}
