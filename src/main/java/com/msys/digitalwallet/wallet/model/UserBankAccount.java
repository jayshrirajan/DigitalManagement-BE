package com.msys.digitalwallet.wallet.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "user-bank-account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBankAccount implements Serializable {

    @Id
    @Field(name = "USER_ACCOUNT_ID")
    private String userAccountId;

    @Field(name = "USER_ID")
    @NotNull(message = "Username should not be blank")
    private String userId;

    @Field(name = "BANK_NAME")
    @NotNull(message = "{bank.name.not.blank.message}")
    private String bankName;

    @Field(name = "BANK_ACCOUNT_ID")
    @NotNull(message = "{bank.accountid.not.blank.message}")
    private String bankAccountId;

    @Field(name = "ACCOUNT_CREATED_DATE")
    @NotBlank(message = "{bank.account.createdate.not.blank.message}")
    private LocalDateTime accountCreatedDate;

    @Field(name = "ACCOUNT_BALANCE")
    private Double accountBalance;

    @Field(name = "STATUS")
    @NotBlank(message = "{bank.account.link.status.not.blank.message}")
    private int status;

}
