package io.pismo.interview.creditcard.repository;

import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.entity.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    OperationTypeRepository operationTypeRepository;

    @Autowired
    AccountRepository accountRepository;

    private Account existentAccount;

    @BeforeAll
    public void setup() {
        operationTypeRepository.save(OperationType.builder()
                .operationTypeId(1L).description("COMPRA A VISTA")
                .allowNegative(true).allowPositive(false).build());

        existentAccount = accountRepository.save(Account.builder().documentNumber("100000").build());
    }

    @Test
    public void createShouldReturnTransaction() {
        Transaction validTransaction = Transaction.builder()
                .amount(new BigDecimal("10.0").negate())
                .operationType(OperationType.builder().operationTypeId(1L).build())
                .account(existentAccount)
                .build();

        Transaction actualTransaction = transactionRepository.save(validTransaction);

        Transaction expectedTransaction = validTransaction;
        assertEquals(1L, actualTransaction.getTransactionId());
        assertEquals(expectedTransaction.getOperationType(), actualTransaction.getOperationType());
        assertEquals(expectedTransaction.getAmount(), actualTransaction.getAmount());
        assertEquals(expectedTransaction.getAccount(), actualTransaction.getAccount());
        assertTrue(actualTransaction.getEventDate() != null);
    }

    @Test
    public void createShouldThrowExceptionWhenNotNullFieldsAreMissing() {
        Transaction missingFieldTransaction = Transaction.builder()
                .amount(new BigDecimal("10.0").negate())
                .operationType(OperationType.builder().operationTypeId(1L).build()).build();

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(missingFieldTransaction));
    }

    @Test
    public void createShouldThrowExceptionWhenNonExistentOperationType() {
        long nonExistentOperationTypeId = 10;
        Transaction missingFieldTransaction = Transaction.builder()
                .amount(new BigDecimal("10.0").negate())
                .operationType(OperationType.builder().operationTypeId(nonExistentOperationTypeId).build())
                .account(Account.builder().accountId(1L).build()).build();

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(missingFieldTransaction));
    }
}

