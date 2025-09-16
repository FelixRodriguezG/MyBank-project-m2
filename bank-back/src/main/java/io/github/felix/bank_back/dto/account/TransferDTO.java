package io.github.felix.bank_back.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    private Long senderId;
    private Long receiverId;
    private Double amount;
    private String secretKey;

}
