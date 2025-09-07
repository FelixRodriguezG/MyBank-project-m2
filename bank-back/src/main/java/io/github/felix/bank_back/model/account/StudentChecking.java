package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class StudentChecking extends Account{

    // Las cuentas estudiantiles NO tienen saldo mínimo ni comisión mensual
    // Solo heredan los campos básicos de Account
    // * Constructor con AccountHolder(propietario principal)
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        validateStudentEligibility(primaryOwner);
    }

    // * Constructor con AccountHolder(propietario principal) y AccountHolder(propietario secundario)
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        validateStudentEligibility(primaryOwner);
        if (secondaryOwner != null) {
            validateStudentEligibility(secondaryOwner);
        }
    }

    //* Constructor con currency personalizado y propietario principal
    //* El balance ya viene con la currency, pero validamos que sea consistente
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, String currencyCode) {
        super(balance, secretKey, primaryOwner);
        validateStudentEligibility(primaryOwner);
        // El balance ya viene con la currency, pero validamos que sea consistente
        Currency currency = Currency.getInstance(currencyCode);
        if (!balance.getCurrencyCode().equals(currency)) {
            throw new IllegalArgumentException("Currency code mismatch between balance and specified currency");
        }
    }

    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner,
                           AccountHolder secondaryOwner, String currencyCode) {
        super(balance, secretKey, primaryOwner, secondaryOwner);
        validateStudentEligibility(primaryOwner);
        if (secondaryOwner != null) {
            validateStudentEligibility(secondaryOwner);
        }

        Currency currency = Currency.getInstance(currencyCode);
        if (!balance.getCurrencyCode().equals(currency)) {
            throw new IllegalArgumentException("Currency code mismatch between balance and specified currency");
        }
    }
    // * Método privado para validar la elegibilidad del titular de la cuenta
    private void validateStudentEligibility(AccountHolder accountHolder) {
        if (!accountHolder.isEligibleForStudentAccount()) {
            throw new IllegalArgumentException(
                    String.format("Account holder '%s' is not eligible for student account (age: %d). Must be under 24.",
                            accountHolder.getName(), accountHolder.getAge())
            );
        }
    }

    /**
     * Las cuentas estudiantiles no tienen restricciones de saldo mínimo
     * Solo verifica que no se retire más de lo disponible
     */
    public boolean hasSufficientBalanceForWithdrawal(Money amount) {
        return getBalance().getAmount().compareTo(amount.getAmount()) >= 0;
    }


}
