package com.msys.digitalwallet.integration.plaidservice.service;

import com.msys.digitalwallet.common.exception.BusinessException;
import com.msys.digitalwallet.integration.WalletIntegrationConfig;
import com.msys.digitalwallet.integration.plaidservice.PlaidVirtualAccount;
import com.msys.digitalwallet.integration.plaidservice.response.AccountStatus;
import com.msys.digitalwallet.integration.plaidservice.response.VirtualAccountResponse;
import com.msys.digitalwallet.wallet.enums.ErrorType;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VirtualAccountService implements PlaidVirtualAccount {

    @Autowired private WalletIntegrationConfig config;

    @Autowired private PlaidApi plaidApi;
    @Override
    public VirtualAccountResponse createWallet()  {
        try {
            WalletCreateRequest request = new WalletCreateRequest()
                    .clientId(config.getPlaidClientId())
                    .secret(config.getPlaidSecret())
                    .isoCurrencyCode(WalletISOCurrencyCode.GBP);

            Response<WalletCreateResponse> response = plaidApi
                    .walletCreate(request)
                    .execute();

            if (!response.isSuccessful())
                throw new BusinessException(ErrorType.PLAID_WALLET_CREATE_FAILED, "Unable to create wallet account on plaid");

            WalletCreateResponse walletCreateResponse = response.body();

            if (walletCreateResponse != null) {
                return VirtualAccountResponse.builder()
                        .walletId(walletCreateResponse.getWalletId())
                        .accountNumber(Objects.requireNonNull(walletCreateResponse.getNumbers().getBacs()).getAccount())
                        .balance(BigDecimal.valueOf(walletCreateResponse.getBalance().getCurrent()))
                        .requestId(walletCreateResponse.getRequestId())
                        .currencyCode(walletCreateResponse.getBalance().getIsoCurrencyCode())
                        .recipientId(walletCreateResponse.getRecipientId())
                        .sortCode(walletCreateResponse.getNumbers().getBacs().getSortCode())
                        .status(AccountStatus.valueOf(walletCreateResponse.getStatus().name()))
                        .build();
            }
        }catch (Exception e){
            throw new BusinessException(ErrorType.PLAID_API_FAILED,e.getMessage());
        }
        return null;

    }
    @Override
    public VirtualAccountResponse getWallet(String walletId) {
        try {
        WalletGetRequest request = new WalletGetRequest()
                .clientId(config.getPlaidClientId())
                .secret(config.getPlaidSecret())
                .walletId(walletId);

            Response<WalletGetResponse> response = plaidApi
                    .walletGet(request)
                    .execute();

            WalletGetResponse walletGetResponse = response.body();

            if (walletGetResponse != null) {
                return VirtualAccountResponse.builder()
                        .walletId(walletGetResponse.getWalletId())
                        .accountNumber(Objects.requireNonNull(walletGetResponse.getNumbers().getBacs()).getAccount())
                        .balance(BigDecimal.valueOf(walletGetResponse.getBalance().getCurrent()))
                        .requestId(walletGetResponse.getRequestId())
                        .currencyCode(walletGetResponse.getBalance().getIsoCurrencyCode())
                        .recipientId(walletGetResponse.getRecipientId())
                        .sortCode(walletGetResponse.getNumbers().getBacs().getSortCode())
                        .status(AccountStatus.valueOf(walletGetResponse.getStatus().name()))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorType.PLAID_API_FAILED,e.getMessage());
        }
        return null;
    }
}
