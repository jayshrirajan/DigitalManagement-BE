package com.msys.digitalwallet.wallet.apirequest;

import com.msys.digitalwallet.wallet.enums.IdentifierType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiUserIdentifierRequest {

    @NotNull private IdentifierType identifierType;

    @NotNull private String identifier;

}

