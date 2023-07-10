package com.msys.digitalwallet.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankAccountDto {
    private String userAccountId;
    private UUID userId;
    private String bankName;
    private String bankAccountId;
    private Date accountCreatedDate;
    private Double accountBalance;
    private int status;
    private Integer plaidId;
}
