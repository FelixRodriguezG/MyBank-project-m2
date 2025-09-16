package io.github.felix.bank_back.dto.account.student_checking;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentCheckingResponseDTO extends AccountResponseDTO {
    // No campos extra, solo los heredados
}