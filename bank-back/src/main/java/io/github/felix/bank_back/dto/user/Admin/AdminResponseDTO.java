package io.github.felix.bank_back.dto.user.Admin;

import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.model.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private String name;
    private String username;
    private Role role;
    private UserStatus status;
}
