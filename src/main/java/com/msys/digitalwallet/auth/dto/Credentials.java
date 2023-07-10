package com.msys.digitalwallet.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {

    String type = "password";

    String value;    //user password

    Boolean temporary = false;

}
