package com.msys.digitalwallet.wallet.model;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "JOURNAL_ENTRY")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JournalEntry {

    @Id
    @Field
    private String journalEntryId;

    @Field(name = "TRANSACTION_TIME")
    @NotNull(message = "transactionTime can not be blank")
    private LocalDateTime transactionTime;

    @Field(name = "ACCOUNT_ID_1")
    @NotNull(message = "accountIDOne can not be blank")
    private String accountIDOne;

    @Field(name = "ACT1_AMOUNT_DEBIT")
    @NotNull(message = "Account One amount debit can not be blank")
    private Double accountOneDebitAmount;

    @Field(name = "ACCOUNT_ID_2")
    @NotNull(message = "accountIDTwo can not be blank")
    private String accountIdTwo;

    @Field(name = "ACT2_AMOUNT_CREDIT")
    @NotNull(message = "Account Two amount credit can not be blank")
    private Double accountTwoCreditAmount;

    @Field(name = "CURRENCY")
    @NotNull(message = "Currency can not be blank")
    private String currency;

    @Field(name = "TRANSACTION_DESC")
    @NotNull(message = "Transaction Description can not be blank")
    private String transactionDescription;
}
