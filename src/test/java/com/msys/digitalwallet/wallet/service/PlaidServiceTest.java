package com.msys.digitalwallet.wallet.service;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.repository.UserRepository;
import com.plaid.client.model.AccountsGetResponse;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class PlaidServiceTest {

    @Autowired
    private PlaidService plaidService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void getPlaidLinkTokenTest() throws IOException{
        User user = userRepository.findByUsername("tests2").get();
        Map<String,Object> linkTokenMap
                = plaidService.getPlaidLinkToken(user.getUserId()).block();
        Assert.assertNotNull(linkTokenMap.get("linkToken").toString());
    }

    @Test
    public void getPlaidLinkTokenFailureTest() throws IOException{
        Map<String,Object> linkTokenMap
                = plaidService.getPlaidLinkToken(UUID.randomUUID().toString()).block();
        Assert.assertEquals("User Id not found", linkTokenMap.get("error").toString());
    }

    @Test
    public void getPublicTokenTest() throws IOException{
        Map<String,Object> publicTokenMap = plaidService.getPublicToken().block();
        Assert.assertNotNull(publicTokenMap.get("publicToken").toString());
    }

    @Test
    public void getAccessTokenTest() throws IOException{
        Map<String,Object> publicTokenMap = plaidService.getPublicToken().block();

        Mono<Map<String,Object>> accessToken =
                plaidService.getAccessToken(publicTokenMap.get("publicToken").toString());
        Assert.assertNotNull(accessToken.block().get("accessToken"));
    }

    @Test
    public void getAccessTokenFailureTest() throws IOException{
        Map<String,Object> publicTokenMap = plaidService.getPublicToken().block();

        Mono<Map<String,Object>> accessToken =
                plaidService.getAccessToken(publicTokenMap.get("publicToken").toString()+1);
        Assert.assertEquals("Plaid error",accessToken.block().get("error"));
    }

    @Test
    public void getAccountDetailsTest() throws IOException{
        User user = userRepository.findByUsername("tests2").get();
        Map<String,Object> linkTokenMap
                = plaidService.getPlaidLinkToken(user.getUserId()).block();

        Map<String,Object> publicTokenMap = plaidService.getPublicToken().block();

        Map<String,Object> accessToken = plaidService.getAccessToken(publicTokenMap.get("publicToken").toString()).block();

        Mono<AccountsGetResponse> plaidAccountData = plaidService.getAccountDetails(accessToken.get("accessToken").toString());

        Assert.assertNotNull(plaidAccountData.block().getAccounts().size());
    }

}
