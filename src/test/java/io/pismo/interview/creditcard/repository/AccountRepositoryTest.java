package io.pismo.interview.creditcard.repository;

import io.pismo.interview.creditcard.entity.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @BeforeAll
    public void setup() {
        accountRepository.saveAll(Arrays.asList(
                Account.builder().accountId(1L).documentNumber("111111").availableCreditLimit(1000.0).build(),
                Account.builder().accountId(2L).documentNumber("222222").availableCreditLimit(2000.0).build(),
                Account.builder().accountId(3L).documentNumber("333333").availableCreditLimit(3000.0).build()));
    }

    @Test
    public void getReturnAccountWhenExists() {
        Optional<Account> actualAccount = accountRepository.findById(1L);

        assertEquals(1L, actualAccount.get().getAccountId());
        assertEquals("111111", actualAccount.get().getDocumentNumber());
    }

    @Test
    public void getShouldReturnEmptyOptionWhenWhenAccountDoesNotExist() {
        Optional<Account> actualAccount = accountRepository.findById(999L);
        assertEquals(Optional.empty(), actualAccount);
    }

    @Test
    public void createShouldReturnAccountWhenCreatingNonExistentDocumentNumber() {
        String documentNumber = "123456";
        Account accountToCreate = Account.builder().documentNumber(documentNumber).availableCreditLimit(1000.0).build();

        Account actualAccount = accountRepository.save(accountToCreate);

        assertEquals(accountToCreate.getDocumentNumber(), actualAccount.getDocumentNumber());
        assertNotNull(actualAccount.getAccountId());
    }

    @Test
    public void getShouldThrowExceptionWhenCreatingAccountWithExistentDocumentNumber() {
        String documentNumber = "111111";
        Account accountToCreate = Account.builder().documentNumber(documentNumber).build();

        assertThrows(DataIntegrityViolationException.class, () -> accountRepository.save(accountToCreate));
    }
}
