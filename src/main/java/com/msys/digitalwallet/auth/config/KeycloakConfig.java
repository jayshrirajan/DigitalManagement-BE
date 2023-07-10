package com.msys.digitalwallet.auth.config;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Autowired
    KeyCloakProperties KeyCloakProperties;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(KeyCloakProperties.getServerUrl())
                .realm(KeyCloakProperties.getRealmsName())
                .clientId(KeyCloakProperties.getClientId())
                .username(KeyCloakProperties.getUsername())
                .password(KeyCloakProperties.getPassword())
                .grantType(KeyCloakProperties.getGrantType())
                .resteasyClient(new ResteasyClientBuilder()
                .connectionPoolSize(10)
                .build())
                .build();
    }
}
