package com.msys.digitalwallet.wallet.mapper;

import com.msys.digitalwallet.wallet.apiresponse.ApiTransfer;
import com.msys.digitalwallet.wallet.enums.Currency;
import com.msys.digitalwallet.wallet.model.Transfer;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface TransferMapper {


    ApiTransfer fromTransfer(Transfer transfer);

    default Transfer toTransfer(String userId, String transactionId, String fromAccountId, String toAccountId, BigDecimal amount, Currency currency, String reason, String status){
        return Transfer.builder()
                .accountIDFrom(fromAccountId)
                .accountIDTo(toAccountId)
                .accountIDFromType("Wallet")
                .accountIdToType("Wallet")
                .amount(amount.doubleValue())
                .currency(currency.name())
                .userId(userId)
                .transactionID(transactionId)
                .reason(reason)
                .transacDateTime(LocalDateTime.now())
                .transactionStatus(status)
                .build();
    }

}
