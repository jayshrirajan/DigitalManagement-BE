package com.msys.digitalwallet.wallet.apirequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkBankAccountUserIdRequest {
    @NotNull(message = "{userId.not.null}")
    @NotEmpty(message = "{userId.not.empty}")
    private String userId;
}
