package com.msys.digitalwallet.wallet.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document(collection = "msys_wallet_account")
public class UserWalletAccount {

  @Id private UUID id;

  private String userId;

  private String plaidWalletId;

  private String plaidAccountNumber;

  private String plaidSortCode;

  private String plaidRecipientId;

  private String plaidRequestId;

  private LocalDateTime accountCreatedDate;

  private BigDecimal accountBalance;

  /** Use defined values from {@link com.msys.digitalwallet.wallet.enums.AccountStatus} enum */
  private String status;

  private String currency;

}
