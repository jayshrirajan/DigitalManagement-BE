package com.msys.digitalwallet.auth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak.admin")
@Data
public class KeyCloakProperties  {
    private String username;

    private String password;

    private String realmsName;

    private String grantType;

    private String clientId;

    private String tokenUrl;

    private String serverUrl;


}
