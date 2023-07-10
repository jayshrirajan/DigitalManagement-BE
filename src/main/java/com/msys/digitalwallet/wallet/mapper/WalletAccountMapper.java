package com.msys.digitalwallet.wallet.mapper;

import com.msys.digitalwallet.integration.plaidservice.response.VirtualAccountResponse;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.wallet.apiresponse.ApiUserAndWalletDetails;
import com.msys.digitalwallet.wallet.apiresponse.ApiUserWalletAccount;
import com.msys.digitalwallet.wallet.apiresponse.ApiWalletTransferResponse;
import com.msys.digitalwallet.wallet.model.Transfer;
import com.msys.digitalwallet.wallet.model.UserWalletAccount;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WalletAccountMapper {

    default UserWalletAccount toUserWalletAccount(String userId, VirtualAccountResponse virtualAccountResponse) {

        return UserWalletAccount.builder()
                .id(UUID.randomUUID())
                .accountBalance(virtualAccountResponse.getBalance())
                .plaidWalletId(virtualAccountResponse.getWalletId())
                .accountCreatedDate(LocalDateTime.now())
                .userId(userId)
                .plaidAccountNumber(virtualAccountResponse.getAccountNumber())
                .plaidRecipientId(virtualAccountResponse.getRecipientId())
                .plaidRequestId(virtualAccountResponse.getRequestId())
                .plaidSortCode(virtualAccountResponse.getSortCode())
                .status(virtualAccountResponse.getStatus().name())
                .currency(virtualAccountResponse.getCurrencyCode())
                .build();
    }

    ApiUserWalletAccount toApiUserWalletAccount(UserWalletAccount userWalletAccount);

    default ApiWalletTransferResponse toApiWalletTransferResponse(Transfer transfer){
        return ApiWalletTransferResponse.builder()
                .transactionId(transfer.getTransactionID())
                .toWalletAccountId(transfer.getAccountIDTo())
                .fromWalletAccountId(transfer.getAccountIDFrom())
                .amount(BigDecimal.valueOf(transfer.getAmount()))
                .reason(transfer.getReason())
                .transactionDate(transfer.getTransacDateTime())
                .status(transfer.getTransactionStatus())
                .currency(transfer.getCurrency())
                .build();
    }

    ApiUserAndWalletDetails toApiUserAndWalletDetails(User user, UUID walletId);
}
