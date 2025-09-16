package io.github.felix.bank_back.repository.account;

import io.github.felix.bank_back.model.account.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

}
