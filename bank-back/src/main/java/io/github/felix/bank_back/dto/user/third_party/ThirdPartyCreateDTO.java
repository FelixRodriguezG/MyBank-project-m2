package io.github.felix.bank_back.dto.user.third_party;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ThirdPartyCreateDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String key; // clave en texto plano (se almacenará hasheada)
}
