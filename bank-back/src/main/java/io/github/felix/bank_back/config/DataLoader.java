package io.github.felix.bank_back.config;

import io.github.felix.bank_back.model.account.*;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.model.user.ThirdParty;
import io.github.felix.bank_back.model.user.embedded.Address;
import io.github.felix.bank_back.model.user.embedded.PersonalData;
import io.github.felix.bank_back.model.user.enums.Role;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.repository.account.AccountRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.repository.user.ThirdPartyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final AccountHolderRepository accountHolderRepository;
    private final ThirdPartyRepository thirdPartyRepository;
    private final AccountRepository accountRepository;

    public DataLoader(AccountHolderRepository accountHolderRepository,
                      ThirdPartyRepository thirdPartyRepository,
                      AccountRepository accountRepository) {
        this.accountHolderRepository = accountHolderRepository;
        this.thirdPartyRepository = thirdPartyRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (accountHolderRepository.count() == 0) {
            seedUsers();
        }
        if (thirdPartyRepository.count() == 0) {
            seedThirdParty();
        }
        if (accountRepository.count() == 0) {
            seedAccounts();
        }
    }

    // --- Helpers de seed ---
    private void seedUsers() {
        // Adultos
        AccountHolder ah1 = buildHolder(
                "Alice Smith", "alice1234",
                new PersonalData("Alice", "Smith", LocalDate.now().minusYears(30), "600111222", "alice@example.com"),
                address("Main St 1", "Madrid", "28001", "ES")
        );
        AccountHolder ah2 = buildHolder(
                "Bob Johnson", "bob1234",
                new PersonalData("Bob", "Johnson", LocalDate.now().minusYears(28), "600333444", "bob@example.com"),
                address("Second St 2", "Barcelona", "08002", "ES")
        );
        // Elegibles StudentChecking (>=18 y <24)
        AccountHolder ah3 = buildHolder(
                "Carol Young", "carol1234",
                new PersonalData("Carol", "Young", LocalDate.now().minusYears(20), "600555666", "carol@example.com"),
                address("Third Ave 3", "Valencia", "46003", "ES")
        );
        AccountHolder ah4 = buildHolder(
                "Dave Student", "dave1234",
                new PersonalData("Dave", "Student", LocalDate.now().minusYears(22), "600777888", "dave@example.com"),
                address("Fourth Rd 4", "Sevilla", "41004", "ES")
        );

        accountHolderRepository.saveAll(List.of(ah1, ah2, ah3, ah4));
    }

    private void seedThirdParty() {
        // Clave en texto: "tp-secret" (almacenada hasheada en hashedKey)
        ThirdParty tp = new ThirdParty("AcmePayments", "tp-secret");
        tp.setStatus(UserStatus.ACTIVE);
        thirdPartyRepository.save(tp);
    }

    private void seedAccounts() {
        List<AccountHolder> holders = accountHolderRepository.findAll();
        if (holders.size() < 4) return; // seguridad
        AccountHolder ah1 = holders.get(0);
        AccountHolder ah2 = holders.get(1);
        AccountHolder ah3 = holders.get(2);
        AccountHolder ah4 = holders.get(3);

        // Checking (2)
        Account chk1 = new Checking(new Money(new BigDecimal("1500.00")), "1234", ah1);
        Account chk2 = new Checking(new Money(new BigDecimal("900.00")), "2345", ah2, ah1);

        // Savings (2)
        Account sav1 = new Savings(new Money(new BigDecimal("5000.00")), "3456", ah1);
        Account sav2 = new Savings(new Money(new BigDecimal("1200.00")), "4567", ah2, ah3);

        // CreditCard (2) – balances pequeños para empezar
        Account cc1 = new CreditCard(new Money(new BigDecimal("0.00")), "5678", ah2);
        Account cc2 = new CreditCard(new Money(new BigDecimal("-50.00")), "6789", ah1, ah2);

        // StudentChecking (2) – solo con titulares elegibles (<24)
        Account st1 = new StudentChecking(new Money(new BigDecimal("300.00")), "7890", ah3);
        Account st2 = new StudentChecking(new Money(new BigDecimal("700.00")), "8901", ah4, ah3);

        accountRepository.saveAll(List.of(chk1, chk2, sav1, sav2, cc1, cc2, st1, st2));
    }

    private AccountHolder buildHolder(String name, String rawPassword, PersonalData pd, Address addr) {
        AccountHolder ah = new AccountHolder(name, rawPassword, pd, addr, Role.ACCOUNT_HOLDER, UserStatus.ACTIVE);
        // Re-hash explícitamente para asegurar hash BCrypt en DB
        ah.setPassword(rawPassword);
        return ah;
    }

    private Address address(String street, String city, String postal, String country) {
        Address a = new Address();
        a.setStreet(street);
        a.setCity(city);
        a.setPostalCode(postal);
        a.setCountry(country);
        return a;
    }
}
