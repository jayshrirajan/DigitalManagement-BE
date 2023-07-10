package com.msys.digitalwallet.usermanagement.model;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"dateOfBirth"})
@Document(collection = "user")
public class User implements Serializable {
    @Id
    private String userId;
    @NotBlank(message = "{user.name.not.blank.message}")
    @Size(max = 20, message = "{user.name.size.message}")
    @Indexed(unique = true)
    private String username;
    @Valid
    private Name name;
    @NotBlank(message = "{user.password.not.null.message}")
    @Size(max = 120)
    @Encrypted
    private String password;
    @NotNull(message = "{user.dateOfBirth.not.null.message}")
    @Field
    private LocalDate dateOfBirth;
    @Valid
    private Address address;
    @NotBlank(message = "{user.mobileNumber.not.null.message}")
    private String mobileNumber;
    @NotBlank(message = "{user.emailAddress.not.null.message}")
    @Size(max = 50)
    @Email(regexp = "^(.+)@(\\S+)$",message = "Invalid Email")
    private String emailAddress;
    private String kycIdentiyNumber;
    private String kycIdentityType;
    private String kycIdentityExpiration;
    private List<String> hasAdminRole;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;
    @CreatedBy
    @Field
    private String createdBy = "WEB";
    @LastModifiedBy
    @Field
    private String updatedBy = "WEB";
}