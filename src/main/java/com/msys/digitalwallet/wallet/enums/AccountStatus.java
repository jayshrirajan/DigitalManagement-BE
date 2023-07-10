package com.msys.digitalwallet.wallet.enums;

public enum AccountStatus {
  ACTIVE(0),
  CLOSED(1),
  UNKNOWN(2);

  private final int status;

  AccountStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
