package com.msys.digitalwallet.usermanagement.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Name implements Serializable {
    @NotBlank(message = "{user.firstName.not.null.message}")
    private String firstName;
    private String middleName;
    @NotBlank(message = "{user.lastName.not.null.message}")
    private String lastName;

    public Name(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

}
