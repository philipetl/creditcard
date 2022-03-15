package io.pismo.interview.creditcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pismo.interview.creditcard.domain.TransactionDTO;
import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.entity.Transaction;
import io.pismo.interview.creditcard.exception.InvalidOperationTypeException;
import io.pismo.interview.creditcard.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TransactionService mockTransactionService;

    @Test
    void createShouldReturnTransactionWhenCreatingNonExistentTransactionWithValidAccountAndOperationType() throws Exception {
        long accountId = 10;
        long operationTypeId = 1;
        BigDecimal amount = new BigDecimal("10.50");

        Transaction mockTransaction = Transaction.builder()
                .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                .account(Account.builder().accountId(accountId).build())
                .amount(amount).build();

        when(mockTransactionService.createTransaction(mockTransaction)).thenReturn(mockTransaction);

        TransactionDTO requestTransactionDTO = TransactionDTO.builder()
                .accountId(accountId)
                .operationTypeId(operationTypeId)
                .amount(amount)
                .build();

        TransactionDTO actualTransactionDTO = new ObjectMapper().readValue(mockMvc.perform(post("/transactions")
                .content(new ObjectMapper().writeValueAsString(requestTransactionDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), TransactionDTO.class);

        assertEquals(mockTransaction.getTransactionId(), actualTransactionDTO.getTransactionId());
        assertEquals(mockTransaction.getAccount().getAccountId(), actualTransactionDTO.getAccountId());
        assertEquals(mockTransaction.getOperationType().getOperationTypeId(), actualTransactionDTO.getOperationTypeId());
        assertEquals(mockTransaction.getAmount(), actualTransactionDTO.getAmount());
    }

    @Test
    void createShouldReturnUnprocessableEntityWhenInvalidOperationTypeException() throws Exception {
        when(mockTransactionService.createTransaction(any(Transaction.class))).thenThrow(
                new InvalidOperationTypeException(""));

        TransactionDTO requestTransactionDTO = TransactionDTO.builder().build();
        mockMvc.perform(post("/transactions")
                .content(new ObjectMapper().writeValueAsString(requestTransactionDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}
