package io.github.felix.bank_back.service.account.credit_card.impl;

import io.github.felix.bank_back.dto.account.credit_card.CreditCardCreateDTO;
import io.github.felix.bank_back.dto.account.credit_card.CreditCardResponseDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.account.CreditCard;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.CreditCardRepository;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.service.account.credit_card.interfaces.CreditCardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final TransactionRepository transactionRepository;

    public CreditCardServiceImpl(CreditCardRepository creditCardRepository,
                                 AccountHolderRepository accountHolderRepository,
                                 TransactionRepository transactionRepository) {
        this.creditCardRepository = creditCardRepository;
        this.accountHolderRepository = accountHolderRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<CreditCardResponseDTO> findAll() {
        return creditCardRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public Optional<CreditCardResponseDTO> findById(Long id) {
        return creditCardRepository.findById(id).map(this::toDTO);
    }

    @Override
    public CreditCardResponseDTO create(CreditCardCreateDTO dto) {
        AccountHolder primary = accountHolderRepository.findById(dto.getPrimaryOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Primary owner no encontrado"));
        AccountHolder secondary = null;
        if (dto.getSecondaryOwnerId() != null) {
            secondary = accountHolderRepository.findById(dto.getSecondaryOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Secondary owner no encontrado"));
        }
        Currency currency = Currency.getInstance(dto.getCurrencyCode());
        CreditCard cc;
        if (dto.getCreditLimit() != null && dto.getInterestRate() != null) {
            cc = (secondary == null)
                    ? new CreditCard(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary,
                    new Money(dto.getCreditLimit(), currency), dto.getInterestRate())
                    : new CreditCard(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, secondary,
                    new Money(dto.getCreditLimit(), currency), dto.getInterestRate());
        } else {
            cc = (secondary == null)
                    ? new CreditCard(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary)
                    : new CreditCard(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, secondary);
        }
        CreditCard saved = creditCardRepository.save(cc);
        return toDTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        creditCardRepository.deleteById(id);
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        CreditCard cc = creditCardRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard no encontrada"));
        return cc.getBalance().getAmount();
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        CreditCard cc = creditCardRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard no encontrada"));
        Currency currency = cc.getBalance().getCurrencyCode();
        BigDecimal old = cc.getBalance().getAmount();
        cc.setBalance(new Money(newBalance, currency));
        creditCardRepository.save(cc);
        // Registrar transacción por ajuste de balance (depósito o retiro)
        BigDecimal diff = newBalance.subtract(old);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            transactionRepository.save(new Transaction(new Money(diff, currency), TransactionType.DEPOSIT, cc, "Balance update"));
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            transactionRepository.save(new Transaction(new Money(diff.abs(), currency), TransactionType.WITHDRAWAL, cc, "Balance update"));
        }
    }

    @Override
    public List<CreditCardResponseDTO> findByPrimaryOwnerId(Long ownerId) {
        return creditCardRepository.findByPrimaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<CreditCardResponseDTO> findBySecondaryOwnerId(Long ownerId) {
        return creditCardRepository.findBySecondaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public CreditCardResponseDTO applyInterest(Long accountId) {
        CreditCard cc = creditCardRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard no encontrada"));
        // Calcular interés a registrar
        Money interest = cc.calculateMonthlyInterest();
        if (cc.applyMonthlyInterest()) {
            cc = creditCardRepository.save(cc);
            if (interest.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                transactionRepository.save(new Transaction(new Money(interest.getAmount(), interest.getCurrencyCode()),
                        TransactionType.INTEREST_PAYMENT, cc, "Credit card monthly interest"));
            }
        }
        return toDTO(cc);
    }

    @Override
    public void checkCreditLimit(Long accountId, BigDecimal amount) {
        CreditCard cc = creditCardRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard no encontrada"));
        if (!cc.canMakePurchase(new Money(amount, cc.getBalance().getCurrencyCode()))) {
            throw new IllegalStateException("Límite de crédito insuficiente");
        }
    }

    // ================= MAPPER PRIVADO =================
    private CreditCardResponseDTO toDTO(CreditCard e) {
        CreditCardResponseDTO dto = new CreditCardResponseDTO();
        dto.setId(e.getId());
        dto.setBalance(e.getBalance().getAmount());
        dto.setCreationDate(e.getCreationDate());
        dto.setStatus(e.getStatus());
        dto.setAccountType(e.getAccountType());
        dto.setPenaltyFee(e.getPenaltyFee().getAmount());
        dto.setPrimaryOwner(AccountHolderDTO.fromEntity(e.getPrimaryOwner()));
        dto.setSecondaryOwner(AccountHolderDTO.fromEntity(e.getSecondaryOwner()));
        dto.setCreditLimit(e.getCreditLimit().getAmount());
        dto.setInterestRate(e.getInterestRate());
        return dto;
    }
}