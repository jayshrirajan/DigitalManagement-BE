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

@Document(collection = "transfer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transfer {

    @Id
    @Field(name = "Transaction id can not be blank")
    private String transactionID;

    @Field(name = "USER_ID")
    @NotNull(message = "User ID can not be blank")
    private String userId;

    @Field(name = "Amount")
    @NotNull(message = "Amount can not be null")
    private Double amount;

    @Field(name = "CURRENCY")
    @NotNull(message = "Currency can not be null")
    private String currency;

    @Field(name = "TRANSACTION_DATE")
    @NotNull(message = "Transaction date can not be null")
    private LocalDateTime transacDateTime;

    @Field(name = "TRANSACTION_STATUS")
    @NotNull(message = "Transaction Status can not be null")
    private String transactionStatus;

    @Field(name = "ACCOUNT_ID_FROM")
    @NotNull(message = "From Account can not be null")
    private String accountIDFrom;

    @Field(name = "ACCOUNT_ID_FROM_TYPE")
    @NotNull(message = "From Account can not be null")
    private String accountIDFromType;

    @Field(name = "ACCOUNT_ID_TO")
    @NotNull(message = "To Account can not be null")
    private String accountIDTo;

    @Field(name = "ACCOUNT_ID_TO_TYPE")
    @NotNull(message = "To Account Type can not be null")
    private String accountIdToType;

    @Field(name = "REASON")
    private String reason;

    @Field(name = "JOURNAL_ENTRY_ID")
    @DocumentReference(lazy = true, collection = "JOURNAL_ENTRY")
    @NotNull(message = "Journal Entry can not be null")
    private JournalEntry journalEntry;

}
