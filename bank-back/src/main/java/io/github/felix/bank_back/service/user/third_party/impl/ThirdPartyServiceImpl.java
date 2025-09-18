package io.github.felix.bank_back.service.user.third_party.impl;

import io.github.felix.bank_back.dto.transaction.ThirdPartyTransactionDTO;
import io.github.felix.bank_back.model.account.Account;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;
import io.github.felix.bank_back.model.user.ThirdParty;
import io.github.felix.bank_back.repository.account.AccountRepository;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
import io.github.felix.bank_back.repository.user.ThirdPartyRepository;
import io.github.felix.bank_back.service.user.third_party.interfaces.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private ThirdParty validateThirdParty(String hashedKey) {
        return thirdPartyRepository.findByHashedKey(hashedKey)
                .orElseThrow(() -> new SecurityException("Clave de tercero inválida"));
    }

    @Override
    public void deposit(String hashedKey, ThirdPartyTransactionDTO dto) {
        ThirdParty tp = validateThirdParty(hashedKey);
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Cuenta no encontrada"));
        if (!account.getSecretKey().equals(dto.getSecretKey())) {
            throw new SecurityException("Secret key inválida");
        }
        BigDecimal amount = new BigDecimal(dto.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
        account.setBalance(new Money(account.getBalance().increaseAmount(amount)));
        accountRepository.save(account);
        transactionRepository.save(new Transaction(new Money(amount, account.getBalance().getCurrencyCode()),
                TransactionType.DEPOSIT, account, tp, "Third-party deposit"));
    }

    @Override
    public void withdraw(String hashedKey, ThirdPartyTransactionDTO dto) {
        ThirdParty tp = validateThirdParty(hashedKey);
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new java.util.NoSuchElementException("Cuenta no encontrada"));
        if (!account.getSecretKey().equals(dto.getSecretKey())) {
            throw new SecurityException("Secret key inválida");
        }
        BigDecimal amount = new BigDecimal(dto.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
        if (account.getBalance().getAmount().compareTo(amount) < 0) {
            throw new IllegalStateException("Fondos insuficientes");
        }
        account.setBalance(new Money(account.getBalance().decreaseAmount(amount)));
        accountRepository.save(account);
        transactionRepository.save(new Transaction(new Money(amount, account.getBalance().getCurrencyCode()),
                TransactionType.WITHDRAWAL, account, tp, "Third-party withdrawal"));
    }
}

