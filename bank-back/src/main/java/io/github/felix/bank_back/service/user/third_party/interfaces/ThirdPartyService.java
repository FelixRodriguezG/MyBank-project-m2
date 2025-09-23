package io.github.felix.bank_back.service.user.third_party.interfaces;

import io.github.felix.bank_back.dto.transaction.ThirdPartyTransactionDTO;
import io.github.felix.bank_back.dto.user.third_party.ThirdPartyCreateDTO;
import io.github.felix.bank_back.dto.user.third_party.ThirdPartyResponseDTO;

public interface ThirdPartyService {
    void deposit(String hashedKey, ThirdPartyTransactionDTO dto);
    void withdraw(String hashedKey, ThirdPartyTransactionDTO dto);

    ThirdPartyResponseDTO create(ThirdPartyCreateDTO dto);
}
