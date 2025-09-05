package io.github.felix.bank_back.model.account;

import io.github.felix.bank_back.model.embedded.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "student_checking_accounts")
public class StudentChecking extends Account{
}
