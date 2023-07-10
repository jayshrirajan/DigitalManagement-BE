package com.msys.digitalwallet.wallet.apirequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaidAccessTokenRequest {
    @NotNull(message = "{publicToken.not.null}")
    @NotEmpty(message = "{publicToken.not.empty}")
    private String publicToken;
}
