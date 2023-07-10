package com.msys.digitalwallet.wallet.apiresponse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApiUserWalletAccount {


  private UUID id;

  private String userId;

  private String plaidWalletId;

  private String plaidAccountNumber;

  private String plaidSortCode;

  private String plaidRecipientId;

  private String plaidRequestId;
  private LocalDateTime accountCreatedDate;

  private BigDecimal accountBalance;

  private String status;

  private String currency;
}
