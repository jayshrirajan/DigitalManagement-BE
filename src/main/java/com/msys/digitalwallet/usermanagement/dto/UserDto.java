package com.msys.digitalwallet.usermanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    String firstName;

    String lastName;

    String username;

    String email;

    Boolean enabled;

    List<Credential> credentials;

}
