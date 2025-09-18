package io.github.felix.bank_back.dto.transaction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyTransactionDTO {
    private Long accountId;
    private String secretKey;
    private String amount; // usar String para evitar problemas de coma flotante
}
