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

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Account account = Account.builder().documentNumber("12345").availableCreditLimit(new BigDecimal("100.0")).build();

        accountService.createAccount(account);

        TransactionDTO requestTransactionDTO = TransactionDTO.builder().operationTypeId(3L).amount(new BigDecimal("100.1")).build();

        ResponseEntity<String> actualResponse = this.restTemplate
                .postForEntity("http://localhost:" + port + "/transactions", requestTransactionDTO, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actualResponse.getStatusCode());
    }

    @Test
    void createShouldReturnTransactionWhenPassingAccountWithLimitToSpendAndCorrectOperationType() {
        // TODO: include test body
    }
}
