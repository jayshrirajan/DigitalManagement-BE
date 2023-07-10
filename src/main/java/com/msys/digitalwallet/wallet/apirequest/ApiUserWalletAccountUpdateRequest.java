package com.msys.digitalwallet.wallet.apirequest;

import com.msys.digitalwallet.wallet.enums.AccountStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ApiUserWalletAccountUpdateRequest {
  private AccountStatus status;
  private BigDecimal amount;
}
