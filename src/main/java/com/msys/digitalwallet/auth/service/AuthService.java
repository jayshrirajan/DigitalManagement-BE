package com.msys.digitalwallet.auth.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.msys.digitalwallet.auth.dto.UserDto;
import com.msys.digitalwallet.auth.util.KeyCloakUtilService;
import com.msys.digitalwallet.common.config.WebClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private WebClientConfig webClientConfig;

    @Value("${services.authService.baseUrl}")
    private String baseUrl;

    @Value("${services.authService.keycloakBaseUrl}")
    private String keycloakBaseUrl;

    @Value("${services.authService.realmsName}")
    private String realmsName;
    @Value("${keycloak.admin.tokenUrl}")
    private String tokenUrl;

    @Value("${keycloak.admin.clientId}")
    private String clientId;

    @Value("${keycloak.admin.grantType}")
    private String grantType;

    @Autowired
    private KeyCloakUtilService keyCloakUtil;


    public Flux<UserDto> getAllUser() {
        try {
            Mono<String> token = keyCloakUtil.getAccessToken();
            Flux<UserDto> users = webClientConfig.webClientAuthService().get().uri(baseUrl + keycloakBaseUrl + realmsName + "/users").headers(httpHeaders -> httpHeaders.setBearerAuth(token.block())).retrieve().bodyToFlux(UserDto.class);
            return users;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

   // @Cacheable(cacheNames = "User", key = "#username")

    public Flux<UserDto> getUserByUserName(String username) {
        try {
            Mono<String> token = keyCloakUtil.getAccessToken();
            Flux<UserDto> user = webClientConfig.webClientAuthService().get().uri(u -> u.path(keycloakBaseUrl + realmsName + "/users").queryParam("username", username).queryParam("exact", true).build()).headers(httpHeaders -> httpHeaders.setBearerAuth(token.block())).retrieve().bodyToFlux(UserDto.class);
            log.debug("getUserByUserName {}",user);
            return user;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public UserDto createUser(UserDto userDto) {
        Mono<String> token = keyCloakUtil.getAccessToken();
        UserDto user = webClientConfig.webClientAuthService().post().uri(baseUrl + keycloakBaseUrl + realmsName + "/users").body(Mono.just(userDto), UserDto.class).headers(httpHeaders -> httpHeaders.setBearerAuth(token.block())).retrieve().bodyToMono(UserDto.class).block();
        return user;
    }

    public String getUserIdByUserName(String username) {

        String userId = keyCloakUtil.getIdByUserName(username);
        return userId;
    }

    public UserDto updateUser(UserDto userDto, String id) {

        Mono<String> token = keyCloakUtil.getAccessToken();

        UserDto user = webClientConfig.webClientAuthService().put().uri(baseUrl + keycloakBaseUrl + realmsName + "/users/" + id).body(Mono.just(userDto), UserDto.class).headers(httpHeaders -> httpHeaders.setBearerAuth(token.block())).retrieve().bodyToMono(UserDto.class).block();
        return user;
    }

    public UserDto deleteUser(String id) {

        Mono<String> token = keyCloakUtil.getAccessToken();

        UserDto user = webClientConfig.webClientAuthService().delete().uri(baseUrl + keycloakBaseUrl + realmsName + "/users/" + id)

                .headers(httpHeaders -> httpHeaders.setBearerAuth(token.block())).retrieve().bodyToMono(UserDto.class).block();
        return user;

    }

    public Mono<String> getToken(String username, String password) {
        return webClientConfig.webClientAuthService().post().uri(baseUrl+tokenUrl).body(
                        BodyInserters.fromFormData("username", username)
                                .with("password", password)
                                .with("grant_type", grantType)
                                .with("client_id", clientId)
                )
                .retrieve().bodyToMono(String.class)
                .map(res -> {
                    try {
                        JsonParser parser = new ObjectMapper().getFactory().createParser(res);
                        while (!parser.isClosed()) {
                            JsonToken token = parser.nextToken();
                            if (JsonToken.FIELD_NAME.equals(token) && "access_token" .equals(parser.getCurrentName())) {
                                parser.nextToken();
                                return parser.getValueAsString();
                            }
                        }
                        throw new RuntimeException();
                    } catch (JsonParseException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }
}
