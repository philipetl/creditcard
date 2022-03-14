package io.pismo.interview.creditcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pismo.interview.creditcard.domain.AccountDTO;
import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.service.AccountService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @Test
    void getShouldReturnAccount() throws Exception {
        long accountId = 1;
        Account mockedAccount = Account.builder().accountId(accountId).documentNumber("123456").build();

        when(accountService.getById(accountId)).thenReturn(mockedAccount);

        AccountDTO actualAccount = new ObjectMapper().readValue(mockMvc.perform(get("/accounts/{accountId}", accountId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), AccountDTO.class);

        assertEquals(mockedAccount.getAccountId(), actualAccount.getAccountId());
        assertEquals(mockedAccount.getDocumentNumber(), actualAccount.getDocumentNumber());
    }

    @Test
    void getShouldReturnNotFoundStatusWhenAccountDoesNotExists() throws Exception {
        Long nonExistentAccountId = 666L;
        when(accountService.getById(nonExistentAccountId)).thenThrow(new EntityNotFoundException());

        mockMvc.perform(get("/accounts/{accountId}", nonExistentAccountId))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void createShouldReturnAccountWhenCreatingNonExistentAccount() throws Exception {
        long accountId = 1;
        String documentNumber = "123456";

        Account inAccount = Account.builder().documentNumber(documentNumber).build();
        Account mockedAccount = Account.builder().accountId(accountId).documentNumber(documentNumber).build();

        when(accountService.createAccount(inAccount)).thenReturn(mockedAccount);

        AccountDTO requestAccount = AccountDTO.builder().documentNumber(documentNumber).build();

        AccountDTO actualAccountDTO = new ObjectMapper().readValue(mockMvc.perform(post("/accounts")
                .content(new ObjectMapper().writeValueAsString(requestAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), AccountDTO.class);

        assertEquals(mockedAccount.getAccountId(), actualAccountDTO.getAccountId());
        assertEquals(mockedAccount.getDocumentNumber(), actualAccountDTO.getDocumentNumber());
    }

    @Test
    void createShouldReturnUnprocessableEntityWhenOccursDataIntegrityViolation() throws Exception {
        String documentNumber = "123456";

        Account inAccount = Account.builder().documentNumber(documentNumber).build();
        when(accountService.createAccount(inAccount)).thenThrow(
                new ConstraintViolationException("",
                        new SQLIntegrityConstraintViolationException(new SQLIntegrityConstraintViolationException("")), ""));

        AccountDTO requestAccount = AccountDTO.builder().documentNumber(documentNumber).build();

        mockMvc.perform(post("/accounts")
                .content(new ObjectMapper().writeValueAsString(requestAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}
