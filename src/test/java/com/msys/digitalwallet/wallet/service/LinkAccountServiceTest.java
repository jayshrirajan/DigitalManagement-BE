package com.msys.digitalwallet.wallet.service;

import com.msys.digitalwallet.wallet.constants.USER_BANK_ACCONUT_LINK_STATUS;
import com.msys.digitalwallet.wallet.model.UserBankAccount;
import com.msys.digitalwallet.wallet.repository.LinkAccountRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class LinkAccountServiceTest {

    @MockBean
    private LinkAccountRepository linkAccountRepository;

    @Autowired
    private LinkAccountService linkAccountService;

    private UserBankAccount userBankAccount;
    private Mono<UserBankAccount> userBankAccountMono;

    @Test
    public void save_user_bank_account_success(){
        String userId = UUID.randomUUID().toString();
        String bankAccountId = UUID.randomUUID().toString();

        userBankAccount =
                UserBankAccount
                        .builder()
                        .userId(userId)
                        .bankName("HFBC")
                        .bankAccountId(bankAccountId)
                        .accountCreatedDate(LocalDateTime.now())
                        .accountBalance(0.0D)
                        .status(USER_BANK_ACCONUT_LINK_STATUS.ACTIVE.ordinal())
                        .build();
        userBankAccountMono = Mono.just(userBankAccount);
        when(linkAccountRepository.save(userBankAccount)).thenReturn(userBankAccountMono);
        Assert.assertNotNull(userBankAccountMono);
        Assert.assertEquals("HFBC",userBankAccountMono.block().getBankName());
        Assert.assertEquals(USER_BANK_ACCONUT_LINK_STATUS.ACTIVE.ordinal(),userBankAccountMono.block().getStatus());
        Assert.assertEquals(userId,userBankAccountMono.block().getUserId());
        Assert.assertEquals(bankAccountId,userBankAccountMono.block().getBankAccountId());
    }
}
