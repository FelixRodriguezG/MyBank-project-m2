package io.github.felix.bank_back.service.user.account_holder.impl;

import io.github.felix.bank_back.dto.user.account_holder.AccountHolderCreateDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.repository.account.AccountRepository;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.service.user.account_holder.interfaces.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountHolderServiceImpl implements AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Override
    public AccountHolderDTO createAccountHolder(AccountHolderCreateDTO accountHolder) {
        return null;
    }

    @Override
    public AccountHolderDTO getAccountHolderById(Long id) {
        return null;
    }

    @Override
    public List<AccountHolderDTO> getAllAccountHolders() {
        return List.of();
    }

    @Override
    public List<AccountHolderDTO> findByFirstName(String firstName) {
        return List.of();
    }

    @Override
    public List<AccountHolderDTO> findByLastName(String lastName) {
        return List.of();
    }

    @Override
    public List<AccountHolderDTO> findByStatus(UserStatus status) {
        return List.of();
    }

    @Override
    public List<AccountHolderDTO> findByDateOfBirth(LocalDate dateOfBirth) {
        return List.of();
    }

    @Override
    public List<AccountHolderDTO> findByPrimaryCity(String city) {
        return List.of();
    }

    @Override
    public List<AccountHolderDTO> findByMailingCity(String city) {
        return List.of();
    }

    @Override
    public AccountHolderDTO updateAccountHolder(Long id, AccountHolderCreateDTO updatedData) {
        return null;
    }

    @Override
    public void deleteAccountHolder(Long id) {

    }

    @Override
    public Page<AccountHolderDTO> findByStatus(UserStatus status, Pageable pageable) {
        return null;
    }
}
