package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "credit_cards")
public class CreditCard extends Account {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "credit_limit_currency", length = 3, nullable = false))
    })
    private Money creditLimit;

    @Column(nullable = false)
    private BigDecimal interestRate;

    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner, Money creditLimit,
                      BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
    }
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner,AccountHolder secondaryOwner,
                      Money creditLimit,
                      BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner,secondaryOwner);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
    }
}
