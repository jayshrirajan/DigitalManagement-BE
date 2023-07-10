package com.msys.digitalwallet.wallet.apirequest;

import com.msys.digitalwallet.wallet.enums.Currency;
import com.msys.digitalwallet.wallet.enums.IdentifierType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiWalletTransferRequest {

    @NotNull
    private IdentifierType identifierType;

    @NotEmpty
    private String identifier;

    @NotNull
    @Positive
    private BigDecimal amount;

    private Currency currency;

    private String reason;

}
