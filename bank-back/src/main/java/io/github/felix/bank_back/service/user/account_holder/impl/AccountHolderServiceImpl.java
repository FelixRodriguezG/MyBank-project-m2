package io.github.felix.bank_back.service.user.account_holder.impl;

import io.github.felix.bank_back.dto.user.account_holder.AccountHolderCreateDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.model.user.enums.Role;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.service.user.account_holder.interfaces.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountHolderServiceImpl implements AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Override
    public AccountHolderDTO createAccountHolder(AccountHolderCreateDTO dto) {
        AccountHolder ah = new AccountHolder();
        ah.setName(dto.getName());
        ah.setPersonalData(dto.getPersonalData());
        ah.setPrimaryAddress(dto.getPrimaryAddress());
        ah.setMailingAddress(dto.getMailingAddress() != null ? dto.getMailingAddress() : dto.getPrimaryAddress());
        ah.setRole(Role.ACCOUNT_HOLDER);
        ah.setStatus(UserStatus.ACTIVE);
        ah.setPassword(dto.getPassword()); // hashea internamente
        AccountHolder saved = accountHolderRepository.save(ah);
        return AccountHolderDTO.fromEntity(saved);
    }

    @Override
    public AccountHolderDTO getAccountHolderById(Long id) {
        return accountHolderRepository.findById(id)
                .map(AccountHolderDTO::fromEntity)
                .orElse(null);
    }

    @Override
    public List<AccountHolderDTO> getAllAccountHolders() {
        return accountHolderRepository.findAll().stream()
                .map(AccountHolderDTO::fromEntity)
                .toList();
    }

    @Override
    public List<AccountHolderDTO> findByFirstName(String firstName) { return List.of(); }

    @Override
    public List<AccountHolderDTO> findByLastName(String lastName) { return List.of(); }

    @Override
    public List<AccountHolderDTO> findByStatus(UserStatus status) { return List.of(); }

    @Override
    public List<AccountHolderDTO> findByDateOfBirth(java.time.LocalDate dateOfBirth) { return List.of(); }

    @Override
    public List<AccountHolderDTO> findByPrimaryCity(String city) { return List.of(); }

    @Override
    public List<AccountHolderDTO> findByMailingCity(String city) { return List.of(); }

    @Override
    public AccountHolderDTO updateAccountHolder(Long id, AccountHolderCreateDTO updatedData) { return null; }

    @Override
    public void deleteAccountHolder(Long id) { }

    @Override
    public Page<AccountHolderDTO> findByStatus(UserStatus status, Pageable pageable) { return null; }
}
