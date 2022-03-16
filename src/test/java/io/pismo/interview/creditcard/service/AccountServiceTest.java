package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @MockBean
    AccountRepository mockAccountRepository;

    @Test
    void getShouldReturnAccount() {
        long existentAccountId = 123L;
        Account mockAccount = Account.builder().accountId(existentAccountId).documentNumber("123456").build();
        when(mockAccountRepository.findById(existentAccountId)).thenReturn(Optional.of(mockAccount));

        Account actualAccount = accountService.getById(existentAccountId);
        assertEquals(mockAccount, actualAccount);
    }

    @Test
    void getShouldThrowEntityNotFoundExceptionWhenAccountDoesNotExists() {
        long nonExistentAccountId = 123L;
        when(mockAccountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getById(nonExistentAccountId));

        assertEquals(String.format("Account with id %d not found.", nonExistentAccountId), actualException.getMessage());
    }
}
