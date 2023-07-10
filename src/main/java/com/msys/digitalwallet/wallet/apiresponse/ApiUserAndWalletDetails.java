package com.msys.digitalwallet.wallet.apiresponse;

import lombok.Data;

import java.util.UUID;

@Data
public class ApiUserAndWalletDetails {

    private String userId;

    private String username;

    private String mobileNumber;

    private String emailAddress;

    private UUID walletId;
}
