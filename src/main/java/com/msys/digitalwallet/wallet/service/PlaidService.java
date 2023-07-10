package com.msys.digitalwallet.wallet.service;

import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.repository.UserRepository;
import com.msys.digitalwallet.wallet.config.PlaidLinkTokenConfiguration;
import com.msys.digitalwallet.wallet.filter.LoggingFilter;
import com.msys.digitalwallet.wallet.model.PaymentRequest;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PlaidService {
    @Autowired
    private PlaidApi plaidClient;

    @Autowired
    private PlaidLinkTokenConfiguration plaidLinkConfiguration;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    public Mono<Map<String,Object>> getPlaidLinkToken(String clientUserId) throws IOException
        {
            LOGGER.info("getPlaidLinkToken service method started");
            Mono<Map<String,Object>> resultMap;
            Map<String,Object> resultTokenMap;
            Map<String,Object> errorMap = new HashMap<>();
            Optional<User> user = userRepository.findById(clientUserId);
          if(user.isPresent()){
              resultTokenMap =generateLinkToken(user.get().getUserId());
              resultMap = Mono.just(resultTokenMap);
          }else {
              errorMap.put("error" , "User Id not found");
              resultMap = Mono.just(errorMap);
          }
          LOGGER.info("getPlaidLinkToken service method ends");
          return resultMap;
    }

    public Map<String,Object> generateLinkToken(String userId) throws IOException {
        LOGGER.info("generateLinkToken service method starts");
        Map<String,Object> linkTokenMap = new HashMap<>();
        LinkTokenCreateRequestUser linkTokenCreateRequestUser = new LinkTokenCreateRequestUser()
                .clientUserId(userId);
        DepositoryFilter types = new DepositoryFilter()
                .accountSubtypes(Arrays.asList(DepositoryAccountSubtype.CHECKING));
        LinkTokenAccountFilters accountFilters = new LinkTokenAccountFilters()
                .depository(types);

        LinkTokenCreateRequest linkTokenCreateRequest = new LinkTokenCreateRequest()
                .user(linkTokenCreateRequestUser)
                .clientName(plaidLinkConfiguration.plaidClientName)
                .products(Arrays.asList(plaidLinkConfiguration.plaidProducts))
                .countryCodes(Arrays.asList(plaidLinkConfiguration.plaidCountryCodes))
                .accountFilters(accountFilters)
                .language(plaidLinkConfiguration.plaidLanguage);
        Response<LinkTokenCreateResponse> linkTokenCreateResponseResponse = plaidClient
                .linkTokenCreate(linkTokenCreateRequest)
                .execute();
       if(linkTokenCreateResponseResponse.code() == 200) {
           linkTokenMap.put("linkToken", linkTokenCreateResponseResponse.body().getLinkToken());
           linkTokenMap.put("expiration", linkTokenCreateResponseResponse.body().getExpiration());
           linkTokenMap.put("requestId", linkTokenCreateResponseResponse.body().getRequestId());
       }else {
           linkTokenMap.put("error","Plaid error");
       }
        LOGGER.info("generateLinkToken service method ends");
        return linkTokenMap;
    }

    public Mono<Map<String,Object>> getPublicToken() throws IOException {
        LOGGER.info("getPublicToken service method started");
        Map<String,Object> publicTokenMap = new HashMap<>();
        Mono<Map<String,Object>> publicToken;
        SandboxPublicTokenCreateRequest publicTokenCreateRequest = new SandboxPublicTokenCreateRequest()
                .institutionId(plaidLinkConfiguration.plaidInstitutionId)
                .initialProducts(Arrays.asList(plaidLinkConfiguration.plaidProducts))
                .options(new SandboxPublicTokenCreateRequestOptions()
                        .webhook("https://www.genericwebhookurl.com/webhook"));
        Response<SandboxPublicTokenCreateResponse> publicTokenCreateResponseResponse = plaidClient
                .sandboxPublicTokenCreate(publicTokenCreateRequest)
                .execute();
        if(publicTokenCreateResponseResponse.code() == 200) {
            publicTokenMap.put("publicToken", publicTokenCreateResponseResponse.body().getPublicToken());
            publicTokenMap.put("requestId", publicTokenCreateResponseResponse.body().getRequestId());
        }else {
            publicTokenMap.put("error","Plaid error");
        }
        publicToken = Mono.just(publicTokenMap);
        LOGGER.info("getPublicToken service method ends");
        return publicToken;
    }

    public Mono<Map<String,Object>> getAccessToken(String publicToken) throws IOException {
        LOGGER.info("getAccessToken service method started");
        Mono<Map<String,Object>> accessToken;
        Map<String,Object> accessTokenMap = new HashMap<>();
        ItemPublicTokenExchangeRequest publicTokenExchangeRequest = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);

        Response<ItemPublicTokenExchangeResponse> publicTokenExchangeResponseResponse = plaidClient
                .itemPublicTokenExchange(publicTokenExchangeRequest)
                .execute();
        if(publicTokenExchangeResponseResponse.code() == 200) {
            accessTokenMap.put("accessToken", publicTokenExchangeResponseResponse.body().getAccessToken());
            accessTokenMap.put("requestId", publicTokenExchangeResponseResponse.body().getRequestId());
            accessTokenMap.put("requestId", publicTokenExchangeResponseResponse.body().getItemId());
        }else{
            accessTokenMap.put("error","Plaid error");
        }
        accessToken = Mono.just(accessTokenMap);
        LOGGER.info("getAccessToken service method ends");
        return accessToken;
    }

    public Mono<AccountsGetResponse> getAccountDetails(String accessToken) throws IOException {
        LOGGER.info("getAccountDetails service method started");
        Mono<AccountsGetResponse> plaidAccountData;
        AccountsBalanceGetRequest request = new AccountsBalanceGetRequest()
                .accessToken(accessToken);
        Response<AccountsGetResponse> response = plaidClient
                .accountsBalanceGet(request)
                .execute();
        plaidAccountData = Mono.just(response.body());
        LOGGER.info("getAccountDetails service method ends");
        return plaidAccountData;
    }

    public Mono<PaymentInitiationPaymentCreateResponse> createPayment(PaymentRequest paymentRequest) throws IOException {
        PaymentAmount paymentAmount = new PaymentAmount()
                .currency(PaymentAmountCurrency.fromValue(paymentRequest.getCurrency()))
                .value(Double.valueOf(paymentRequest.getAmount()));
        PaymentInitiationPaymentCreateRequest request = new PaymentInitiationPaymentCreateRequest()
                .recipientId(paymentRequest.getRecipientId())

                .amount(paymentAmount);
        Response<PaymentInitiationPaymentCreateResponse> response = plaidClient
                .paymentInitiationPaymentCreate(request)
                .execute();

        return  Mono.just(response.body());
    }

    public Mono<Institution> getInstitutionById(String institutionId) throws IOException {
        InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest()
                .institutionId(institutionId)
                .addCountryCodesItem(Arrays.asList(plaidLinkConfiguration.plaidCountryCodes).get(0));
        Response<InstitutionsGetByIdResponse> response = plaidClient
                .institutionsGetById(request).execute();
        Institution institution = response.body().getInstitution();
        return  Mono.just(institution);
    }
}
