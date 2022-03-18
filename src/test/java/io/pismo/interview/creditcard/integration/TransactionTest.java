package io.pismo.interview.creditcard.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pismo.interview.creditcard.CreditcardApplication;
import io.pismo.interview.creditcard.controller.TransactionController;
import io.pismo.interview.creditcard.domain.TransactionDTO;
import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.repository.AccountRepository;
import io.pismo.interview.creditcard.repository.OperationTypeRepository;
import io.pismo.interview.creditcard.repository.TransactionRepository;
import io.pismo.interview.creditcard.service.AccountService;
import io.pismo.interview.creditcard.service.OperationTypeService;
import io.pismo.interview.creditcard.service.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TransactionController.class, AccountService.class, TransactionService.class, OperationTypeService.class,
    AccountRepository.class, OperationTypeRepository.class, TransactionRepository.class})
@WebMvcTest(CreditcardApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    OperationTypeService operationTypeService;

    @Autowired
    OperationTypeRepository operationTypeRepository;

    @BeforeAll
    public void setup() {
        operationTypeRepository.saveAll(Arrays.asList(
                OperationType.builder().operationTypeId(1L).description("COMPRA A VISTA").allowNegative(true).allowPositive(false).build(),
                OperationType.builder().operationTypeId(2L).description("COMPRA PARCELADA").allowNegative(true).allowPositive(false).build(),
                OperationType.builder().operationTypeId(3L).description("SAQUE").allowNegative(true).allowPositive(false).build(),
                OperationType.builder().operationTypeId(4L).description("PAGAMENTO").allowNegative(false).allowPositive(true).build()));
    }

    @Test
    void createException() throws Exception {
        Account account = Account.builder().documentNumber("12345").availableCreditLimit(new BigDecimal("100.0")).build();

        accountService.createAccount(account);

        TransactionDTO requestTransactionDTO = TransactionDTO.builder().operationTypeId(3L).amount(new BigDecimal("100.1")).build();

        mockMvc.perform(post("/transactions")
                .content(new ObjectMapper().writeValueAsString(requestTransactionDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}
