package com.msys.digitalwallet.auth.service;


import com.msys.digitalwallet.auth.config.Credentials;
import com.msys.digitalwallet.auth.config.KeyCloakProperties;
import com.msys.digitalwallet.auth.dto.UserDto;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class KeyCloakService {

    @Autowired
    Keycloak keycloak;
    @Autowired
    com.msys.digitalwallet.auth.config.KeyCloakProperties KeyCloakProperties;

    public void addUser(UserDto userDTO){
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials("password");
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);
        RealmsResource rel = keycloak.realms();
        rel.findAll();
        UsersResource  users = keycloak.realm(KeyCloakProperties.getRealmsName()).users();
        int c = users.count();
        users.create(user);


    }




}
