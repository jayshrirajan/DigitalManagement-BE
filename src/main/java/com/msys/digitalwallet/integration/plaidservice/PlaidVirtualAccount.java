package com.msys.digitalwallet.integration.plaidservice;

import com.msys.digitalwallet.integration.plaidservice.response.VirtualAccountResponse;

import java.io.IOException;

public interface PlaidVirtualAccount {


    VirtualAccountResponse createWallet() throws IOException;

    VirtualAccountResponse getWallet(String walletId);
}
