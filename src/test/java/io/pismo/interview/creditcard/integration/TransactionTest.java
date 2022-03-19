package io.pismo.interview.creditcard.integration;

import io.pismo.interview.creditcard.CreditcardApplication;
import io.pismo.interview.creditcard.domain.TransactionDTO;
import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.repository.OperationTypeRepository;
import io.pismo.interview.creditcard.repository.TransactionRepository;
import io.pismo.interview.creditcard.service.AccountService;
import io.pismo.interview.creditcard.service.OperationTypeService;
import io.pismo.interview.creditcard.service.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = CreditcardApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

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
    void createShouldReturnUnprocessableEntityWhenOccursInvalidOperationDueLimit() {
        Account existentAccount = createAccount("111111", 1000.0);

        TransactionDTO requestTransactionDTO = TransactionDTO.builder()
                .operationTypeId(3L)
                .amount(-1000.1)
                .accountId(existentAccount.getAccountId())
                .build();

        ResponseEntity<String> actualResponse = this.restTemplate
                .postForEntity("http://localhost:" + port + "/transactions", requestTransactionDTO, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actualResponse.getStatusCode());
        assertEquals("{\"message\":\"Operation denied due credit limit.\"}", actualResponse.getBody());
    }

    @Test
    void createShouldReturnCreatedTransactionWhenThereIsLimitToSpend() {
        Account existentAccount = createAccount("222222", 1000.0);

        TransactionDTO requestTransactionDTO = TransactionDTO.builder()
                .operationTypeId(2L)
                .amount(-100.1)
                .accountId(existentAccount.getAccountId())
                .build();

        TransactionDTO actualTransactionDTO = this.restTemplate
                .postForObject("http://localhost:" + port + "/transactions", requestTransactionDTO, TransactionDTO.class);

        Account accountAfterTransaction = accountService.getById(existentAccount.getAccountId());
        assertEquals(899.90, accountAfterTransaction.getAvailableCreditLimit());

        assertEquals(-100.10, actualTransactionDTO.getAmount());
        assertNotNull(actualTransactionDTO.getEventDate());
    }

    @Test
    void createShouldReturnCreatedPaymentTransaction() {
        Account existentAccount = createAccount("333333", 1000.0);

        TransactionDTO requestTransactionDTO = TransactionDTO.builder()
                .operationTypeId(4L)
                .amount(300.0)
                .accountId(existentAccount.getAccountId())
                .build();

        TransactionDTO actualTransactionDTO = this.restTemplate
                .postForObject("http://localhost:" + port + "/transactions", requestTransactionDTO, TransactionDTO.class);

        Account accountAfterTransaction = accountService.getById(existentAccount.getAccountId());
        assertEquals(1300.0, accountAfterTransaction.getAvailableCreditLimit());

        assertEquals(300.0, actualTransactionDTO.getAmount());
        assertNotNull(actualTransactionDTO.getEventDate());
    }

    private Account createAccount(String documentNumber, double availableCreditLimit) {
        Account account = accountService.createAccount(Account.builder().documentNumber(documentNumber).availableCreditLimit(availableCreditLimit).build());
        return accountService.createAccount(account);
    }
}
