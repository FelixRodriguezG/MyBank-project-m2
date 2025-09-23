package io.github.felix.bank_back.dto.user.third_party;

import io.github.felix.bank_back.model.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyResponseDTO {
    private Long id;
    private String name;
    private String hashedKey;
    private UserStatus status;
}

