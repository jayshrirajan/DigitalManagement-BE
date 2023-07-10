package com.msys.digitalwallet.wallet.model;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "Ledger")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ledger {

    @Id
    @Field
    private String ledgerId;

    @Field(name="USER_ID")
    //@DocumentReference(lazy = true)
    @NotNull(message = "User id can not be blank")
    private String userId;

    @Field(name="ACCOUNT_ID")
    @DocumentReference(lazy = true,collection = "user-bank-account")
    @NotNull(message = "Account id can not be blank")
    private UserBankAccount userBankAccount;

    @Field(name="TRANSACTION_DATE")
    @NotNull(message = "Transaction date can not be blank")
    private LocalDateTime transactionDate;

    @Field(name="TRANSACTION_AMOUNT")
    @NotNull(message = "Transaction amount can not be blank")
    private Double transactionAmount;

    @Field(name="CURRENCY")
    @NotNull(message = "Currency can not be blank")
    private String currency;

    @Field(name="JOURNAL_ID")
    @DocumentReference(lazy = true, collection = "JOURNAL_ENTRY")
    @NotNull(message = "Journal id can not be blank")
    private JournalEntry journalId;
}
