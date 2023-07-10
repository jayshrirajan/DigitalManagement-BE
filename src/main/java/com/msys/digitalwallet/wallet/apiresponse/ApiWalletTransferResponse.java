package com.msys.digitalwallet.wallet.apiresponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiWalletTransferResponse {
    private String transactionId;

    private String currency;

    private String fromWalletAccountId;

    private String toWalletAccountId;
    private BigDecimal amount;

    private LocalDateTime transactionDate;


    private String reason;

    private String status;

}
