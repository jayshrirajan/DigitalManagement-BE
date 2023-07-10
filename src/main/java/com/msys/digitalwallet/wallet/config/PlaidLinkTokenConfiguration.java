package com.msys.digitalwallet.wallet.config;

import com.plaid.client.model.CountryCode;
import com.plaid.client.model.Products;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@Data
public class PlaidLinkTokenConfiguration {
    @Value("${PLAID_CLIENT_NAME}")
    public String plaidClientName;

    @Value("${PLAID_PRODUCTS}")
    public Products[] plaidProducts;


    @Value("${PLAID_COUNTRY_CODES}")
    public CountryCode[] plaidCountryCodes;

    @Value("${PLAID_LAN}")
    public String plaidLanguage;
    @Value("${PLAID_INSTITUTION_ID}")
    public String plaidInstitutionId;

}
