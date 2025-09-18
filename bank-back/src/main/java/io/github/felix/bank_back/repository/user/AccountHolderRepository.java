package io.github.felix.bank_back.repository.user;

import io.github.felix.bank_back.model.user.AccountHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import io.github.felix.bank_back.model.user.enums.UserStatus;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
    // Busca titulares por nombre (ignora mayúsculas/minúsculas) y apellido (ignorando mayúsculas/minúsculas)
    List<AccountHolder> findByNameIgnoreCaseAndId(String name, Long id);

    // Login: buscar por nombre exacto
    Optional<AccountHolder> findByName(String name);

    // Busca titulares por nombre (ignorando mayúsculas/minúsculas)
    List<AccountHolder> findByPersonalData_FirstNameIgnoreCase(String firstName);

    // Busca titulares por apellido (ignorando mayúsculas/minúsculas)
    List<AccountHolder> findByPersonalData_LastNameIgnoreCase(String lastName);

    // Busca titulares por estado (ACTIVE, FROZEN)
    List<AccountHolder> findByStatus(UserStatus status);

    // Busca titulares por fecha de nacimiento exacta
    List<AccountHolder> findByPersonalData_DateOfBirth(LocalDate dateOfBirth);

    // Busca titulares por ciudad en dirección principal
    List<AccountHolder> findByPrimaryAddress_CityIgnoreCase(String city);

    // Busca titulares por ciudad en dirección postal
    List<AccountHolder> findByMailingAddress_CityIgnoreCase(String city);

    // Verifica si existe un titular por nombre y apellido
    boolean existsByPersonalData_FirstNameAndPersonalData_LastName(String firstName, String lastName);

    // Busca titulares por rango de fechas de nacimiento
    List<AccountHolder> findByPersonalData_DateOfBirthBetween(LocalDate start, LocalDate end);

    // Ejemplo de paginación
    Page<AccountHolder> findByStatus(UserStatus status, Pageable pageable);
}
