package com.msys.digitalwallet.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msys.digitalwallet.wallet.apirequest.LinkBankAccountRequest;
import com.msys.digitalwallet.wallet.model.UserBankAccount;
import com.msys.digitalwallet.wallet.service.LinkAccountService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
class LinkAccountControllerTest {


    @MockBean
    private LinkAccountService linkAccountService;

    @MockBean
    private LinkAccountController LinkAccountController;

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
    public void save_user_bankAccount_success() throws Exception {
        String userId = UUID.randomUUID().toString();
        LinkBankAccountRequest linkBankAccountRequest = new LinkBankAccountRequest();
        linkBankAccountRequest.setUserId(userId);
        linkBankAccountRequest.setBankAccountId("56248902374");
        linkBankAccountRequest.setBankName("HFBC");
        linkBankAccountRequest.setBankRoutingNumber("HFBC6738");
        LocalDateTime accountDate = LocalDateTime.now();
        Double balance = 0.0D;
        UserBankAccount userBankAccount = new UserBankAccount("sampleBankAcoountId",userId,"sampleBankName","accountId",accountDate,balance,1);
        Mono<UserBankAccount> userBankAccountMono = Mono.just(userBankAccount);
        MvcResult mvcResult = mockMvc.perform(post("/digital-wallet/link-bank-account")
                        .content(asJsonString(linkBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse content = mvcResult.getResponse();
        assertEquals(200,content.getStatus());
        assertEquals(null,content.getErrorMessage());
    }

    @Test
    public void save_user_bankAccount_not_found() throws Exception {
        String userId = UUID.randomUUID().toString();
        LinkBankAccountRequest linkBankAccountRequest = new LinkBankAccountRequest();
        linkBankAccountRequest.setUserId(userId);
        linkBankAccountRequest.setBankAccountId("56248902374");
        linkBankAccountRequest.setBankName("HFBC");
        linkBankAccountRequest.setBankRoutingNumber("HFBC6738");
        LocalDateTime accountDate = LocalDateTime.now();
        Double balance = 0.0D;
        UserBankAccount userBankAccount = new UserBankAccount("sampleBankAcoountId",userId,"sampleBankName","accountId",accountDate,balance,1);
        Mono<UserBankAccount> userBankAccountMono = Mono.just(userBankAccount);
        MvcResult mvcResult = mockMvc.perform(post("/digital-wallet/link-bank-accoun")
                        .content(asJsonString(linkBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andReturn();
        MockHttpServletResponse content = mvcResult.getResponse();
        assertEquals(404,content.getStatus());
        assertEquals("",content.getContentAsString());
    }

    @Test
    public void save_user_bankAccount_no_request_body() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/digital-wallet/link-bank-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
        MockHttpServletResponse content = result.getResponse();
        String resultSTring = content.getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject resultData = (JSONObject) parser.parse(resultSTring);
        assertEquals(400, content.getStatus());
        assertEquals("Bad Request", resultData.get("error"));
    }

    @Test
    public void save_user_bankAccount_userId_null() throws Exception {
        LinkBankAccountRequest linkBankAccountRequest = new LinkBankAccountRequest();
        linkBankAccountRequest.setUserId(null);
        linkBankAccountRequest.setBankAccountId("56248902374");
        linkBankAccountRequest.setBankName("HFBC");
        linkBankAccountRequest.setBankRoutingNumber("HFBC6738");
        MvcResult result =mockMvc.perform(
                post("/digital-wallet/link-bank-account").
                        content(asJsonString(linkBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
        MockHttpServletResponse content = result.getResponse();
        String resultSTring = content.getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject resultData = (JSONObject) parser.parse(resultSTring);
        assertEquals(400, content.getStatus());
        assertEquals("Bad Request", resultData.get("error"));
        assertEquals("Validation errors", resultData.get("message"));
    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}