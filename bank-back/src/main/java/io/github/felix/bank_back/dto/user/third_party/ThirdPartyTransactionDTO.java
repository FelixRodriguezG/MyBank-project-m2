package io.github.felix.bank_back.dto.user.third_party;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyTransactionDTO {
    private Long accountId;
    private String secretKey;
    private String amount; // usar String para evitar problemas de coma flotante
}
