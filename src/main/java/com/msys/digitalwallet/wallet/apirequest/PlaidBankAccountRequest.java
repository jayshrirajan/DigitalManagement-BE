package com.msys.digitalwallet.wallet.apirequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaidBankAccountRequest {
    @NotNull(message = "{accessToken.not.null}")
    private String accessToken;
}
