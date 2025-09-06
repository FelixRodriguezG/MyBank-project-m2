package io.github.felix.bank_back.model.account.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Currency;

@Data
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Money implements Serializable {

    private static final Currency USD = Currency.getInstance("USD");
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    @NotNull
    private BigDecimal amount;
    @NotNull
    private Currency currencyCode;

    /**
     * Class constructor specifying amount, currency, and rounding
     **/

    public Money(BigDecimal amount, Currency currency, RoundingMode rounding) {
        this.currencyCode = currency;
        setAmount(amount.setScale(currency.getDefaultFractionDigits(), rounding));
    }

    /**
     * Class constructor specifying amount, and currency. Uses default RoundingMode HALF_EVEN.
     **/
    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, DEFAULT_ROUNDING);
    }

    /**
     * Class constructor specifying amount. Uses default RoundingMode HALF_EVEN and default currency USD.
     **/
    public Money(BigDecimal amount) {
        this(amount, USD, DEFAULT_ROUNDING);
    }

    public BigDecimal increaseAmount(Money money) {
        setAmount(this.amount.add(money.amount));
        return this.amount;
    }

    public BigDecimal increaseAmount(BigDecimal addAmount) {
        setAmount(this.amount.add(addAmount));
        return this.amount;
    }

    public BigDecimal decreaseAmount(Money money) {
        setAmount(this.amount.subtract(money.getAmount()));
        return this.amount;
    }

    public BigDecimal decreaseAmount(BigDecimal addAmount) {
        setAmount(this.amount.subtract(addAmount));
        return this.amount;
    }

    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String toString() {
        return getCurrencyCode().getSymbol() + " " + getAmount();
    }
}
