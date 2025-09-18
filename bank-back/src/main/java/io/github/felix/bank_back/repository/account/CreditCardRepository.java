package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    List<CreditCard> findByPrimaryOwner_Id(Long ownerId);
    List<CreditCard> findBySecondaryOwner_Id(Long ownerId);
}
