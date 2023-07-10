package com.msys.digitalwallet.wallet.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.repository.UserRepository;
import com.msys.digitalwallet.wallet.apiresponse.ApiResponse;
import com.msys.digitalwallet.wallet.service.PlaidService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.Map;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PlaidControllerTest {

    @Autowired
    private PlaidService plaidService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void getPlaidLinkTokenTest() throws Exception {

        User user = userRepository.findByUsername("tests2").get();
        String jsonRequest = objectMapper.writeValueAsString(user.getUserId());

        MvcResult mvcResult = mockMvc.perform(post("/plaid-service/link-token")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(result, ApiResponse.class);
        Assert.assertEquals(response.isSuccess(), Boolean.TRUE);
    }

    @Test
    public void getPublicTokenTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/plaid-service/public-token"))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(result, ApiResponse.class);
        Assert.assertEquals(response.isSuccess(), Boolean.TRUE);
    }

    @Test
    public void getAccessTokenTest() throws Exception {
        //Get Public Token
        Map<String, Object> publicTokenMap = plaidService.getPublicToken().block();

        String publicToken = publicTokenMap.get("publicToken").toString();

        String jsonRequest = objectMapper.writeValueAsString(publicToken);

        MvcResult mvcResult = mockMvc.perform(post("/plaid-service/access-token")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(result, ApiResponse.class);
        Assert.assertEquals(Boolean.TRUE, response.isSuccess());
    }

    @Test
    public void getPlaidAccountDetailsTest() throws Exception {

        Map<String, Object> publicTokenMap = plaidService.getPublicToken().block();

        Map<String, Object> accessTokenMap = plaidService.getAccessToken(publicTokenMap.get("publicToken").toString()).block();

        String accessToken = accessTokenMap.get("accessToken").toString();

        String jsonRequest = objectMapper.writeValueAsString(accessToken);

        MvcResult mvcResult = mockMvc.perform(post("/plaid-service/account-details")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(result, ApiResponse.class);
        Assert.assertEquals(response.isSuccess(), (boolean) Boolean.TRUE);
    }
}
