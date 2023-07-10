package com.msys.digitalwallet.wallet.apirequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkBankAccountRequest {

    @NotNull(message = "{user.id.not.null.message}")
    @NotEmpty(message = "{user.id.not.empty.message}")
    private String userId;

    @NotNull(message = "{bank.name.not.null.message}")
    @NotEmpty(message = "{bank.name.not.empty.message}")
    private String bankName;

    @NotNull(message = "{bankRoutingNumber.not.null.message}")
    @NotEmpty(message = "{bankRoutingNumber.not.empty.message}")
    private String bankRoutingNumber;

    @NotNull(message = "{bankAccountId.not.null.message}")
    @NotEmpty(message = "{bankAccountId.not.empty.message}")
    private String bankAccountId;

    private Double accountBalance;

}
