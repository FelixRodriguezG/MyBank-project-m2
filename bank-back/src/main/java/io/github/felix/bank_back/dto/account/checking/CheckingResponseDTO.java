package io.github.felix.bank_back.dto.account.checking;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CheckingResponseDTO extends AccountResponseDTO {

    @NotNull
    @Positive
    private BigDecimal minimumBalance;
}
