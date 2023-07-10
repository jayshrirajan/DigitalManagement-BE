package com.msys.digitalwallet.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("User")
public class UserDto {

    String id;
    String firstName;

    String lastName;

    String username;

    String email;

    Boolean enabled;

    List<Credentials> credentials;


    public UserDto(String firstName, String lastName, String username, String email, Boolean enabled, List<Credentials> credentials) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.credentials = credentials;
    }

}
