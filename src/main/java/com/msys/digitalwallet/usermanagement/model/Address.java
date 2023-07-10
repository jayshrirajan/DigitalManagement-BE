package com.msys.digitalwallet.usermanagement.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Address implements Serializable {
    @NotBlank(message = "{user.address1.not.null.message}")
    private String address1;
    private String address2;
    @NotBlank(message = "{user.city.not.null.message}")
    private String city;
    @NotBlank(message = "{user.state.not.null.message}")
    private String state;
    @NotBlank(message = "{user.country.not.null.message}")
    private String country;
    @NotBlank(message = "{user.zip.not.null.message}")
    private String zip;

    public Address(String address1, String address2, String city, String state, String country, String zip) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
    }
}
