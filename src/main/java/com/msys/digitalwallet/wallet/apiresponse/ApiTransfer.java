package com.msys.digitalwallet.wallet.apiresponse;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiTransfer {


  private String transactionID;


  private String userId;


  private Double amount;


  private String currency;


  private LocalDateTime transacDateTime;


  private String transactionStatus;

  private String accountIDFrom;


  private String accountIDFromType;

  private String accountIDTo;

  private String accountIdToType;
}
