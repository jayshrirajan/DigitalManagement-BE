package com.msys.digitalwallet.integration;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@Data
public class WalletIntegrationConfig {

    @Value("${plaid.clientId}")
    private String plaidClientId;

    @Value("${plaid.clientSecret}")
    private String plaidSecret;

    @Value("${plaid.environment:sandbox}")
    private String plaidEnv;


    @Bean
    public PlaidApi plaidClient() {
        HashMap<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", plaidClientId);
        apiKeys.put("secret", plaidSecret);
        ApiClient apiClient = new ApiClient(apiKeys);
        apiClient.setPlaidAdapter(ApiClient.Sandbox);
        return apiClient.createService(PlaidApi.class);

    }
}
