package com.msys.digitalwallet.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    @Value("${PLAID_CLIENT_ID}")
    private String clientId;
    @Value("${PLAID_SECRET}")
    private String secret;
    private String recipientId;
    private String amount;
    private String currency;
}
