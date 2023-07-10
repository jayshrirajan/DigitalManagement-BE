package com.msys.digitalwallet.integration.plaidservice.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VirtualAccountResponse {

    private String requestId;
    private String walletId;
    private String recipientId;
    private String currencyCode;
    private BigDecimal balance;
    private String accountNumber;
    private String sortCode;
    private AccountStatus status;
}
