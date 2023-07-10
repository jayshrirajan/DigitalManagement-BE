package com.msys.digitalwallet.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credential {

    String type = "password";

    String value;

    Boolean temporary = false;

}
