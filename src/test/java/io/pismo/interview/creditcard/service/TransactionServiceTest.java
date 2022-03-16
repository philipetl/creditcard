package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.entity.Transaction;
import io.pismo.interview.creditcard.exception.InvalidOperationTypeException;
import io.pismo.interview.creditcard.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @MockBean
    TransactionRepository mockTransactionRepository;

    @MockBean
    OperationTypeService mockOperationTypeService;

    @Test
    void createShouldReturnTransactionWhenValidNegativeAmount() throws InvalidOperationTypeException {
        long operationTypeId = 1;
        BigDecimal amount = new BigDecimal("10.5").negate();

        OperationType mockOperationType = OperationType.builder()
                .operationTypeId(operationTypeId)
                .description("COMPRA A VISTA")
                .allowNegative(true)
                .allowPositive(false)
                .build();

        Transaction mockTransaction = Transaction.builder()
                .amount(amount)
                .account(Account.builder().accountId(1L).build())
                .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                .build();

        when(mockOperationTypeService.getById(operationTypeId)).thenReturn(mockOperationType);
        when(mockTransactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

        Transaction actualTransaction = transactionService.createTransaction(mockTransaction);

        assertEquals(mockTransaction, actualTransaction);
    }

    @Test
    void createShouldReturnTransactionWhenValidPositiveAmount() throws InvalidOperationTypeException {
        long operationTypeId = 1;
        BigDecimal amount = new BigDecimal("10.5");

        OperationType mockOperationType = OperationType.builder()
                .operationTypeId(operationTypeId)
                .description("PAGAMENTO")
                .allowNegative(false)
                .allowPositive(true)
                .build();

        Transaction mockTransaction = Transaction.builder()
                .amount(amount)
                .account(Account.builder().accountId(1L).build())
                .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                .build();

        when(mockOperationTypeService.getById(operationTypeId)).thenReturn(mockOperationType);
        when(mockTransactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

        Transaction actualTransaction = transactionService.createTransaction(mockTransaction);

        assertEquals(mockTransaction, actualTransaction);
    }

    @Test
    void createShouldReturnTransactionWhenAnyAmountIsAllowed() {
        Stream.of(new BigDecimal("10.5").negate(), new BigDecimal("10.5")).forEach(amount -> {
            long operationTypeId = 10;
            OperationType mockOperationType = OperationType.builder()
                    .operationTypeId(operationTypeId)
                    .description("POSSIBLE ANY AMOUNT VALUE")
                    .allowNegative(true)
                    .allowPositive(true)
                    .build();

            Transaction mockTransaction = Transaction.builder()
                    .amount(amount)
                    .account(Account.builder().accountId(1L).build())
                    .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                    .build();

            when(mockOperationTypeService.getById(operationTypeId)).thenReturn(mockOperationType);
            when(mockTransactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

            Transaction actualTransaction = null;
            try {
                actualTransaction = transactionService.createTransaction(mockTransaction);
            } catch (InvalidOperationTypeException e) {
                e.printStackTrace();
            }

            assertEquals(mockTransaction, actualTransaction);
        });
    }

    @Test
    void createShouldThrowInvalidOperationTypeExceptionWhenAnyValueAmountIsNotAllowed() {
        Stream.of(new BigDecimal("10.5").negate(), new BigDecimal("10.5")).forEach(amount -> {
            long operationTypeId = 10;
            OperationType mockOperationType = OperationType.builder()
                    .operationTypeId(operationTypeId)
                    .description("POSSIBLE NONE AMOUNT VALUE")
                    .allowNegative(false)
                    .allowPositive(false)
                    .build();

            Transaction mockTransaction = Transaction.builder()
                    .amount(amount)
                    .account(Account.builder().accountId(1L).build())
                    .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                    .build();

            when(mockOperationTypeService.getById(operationTypeId)).thenReturn(mockOperationType);
            when(mockTransactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

            InvalidOperationTypeException actualException = assertThrows(
                    InvalidOperationTypeException.class,
                    () -> transactionService.createTransaction(mockTransaction));

            String expectedExceptionMessage = String.format("Operation type (%s) does not allow any amount value (%s).", mockOperationType.getDescription(), amount);
            assertEquals(expectedExceptionMessage, actualException.getMessage());
        });
    }

    @Test
    void createShouldThrowInvalidOperationTypeExceptionWhenInvalidPositiveAmount() {
        long operationTypeId = 1;
        BigDecimal amount = new BigDecimal("10.5");

        OperationType mockOperationType = OperationType.builder()
                .operationTypeId(operationTypeId)
                .description("COMPRA A VISTA")
                .allowNegative(true)
                .allowPositive(false)
                .build();

        Transaction mockTransaction = Transaction.builder()
                .amount(amount)
                .account(Account.builder().accountId(1L).build())
                .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                .build();

        when(mockOperationTypeService.getById(operationTypeId)).thenReturn(mockOperationType);

        InvalidOperationTypeException actualException = assertThrows(
                InvalidOperationTypeException.class,
                () -> transactionService.createTransaction(mockTransaction));

        String expectedExceptionMessage = String.format("Operation type (%s) does not allow positive amount value (%s).", mockOperationType.getDescription(), amount);
        assertEquals(expectedExceptionMessage, actualException.getMessage());
    }

    @Test
    void createShouldThrowInvalidOperationTypeExceptionWhenInvalidNegativeAmount() {
        long operationTypeId = 1;
        BigDecimal amount = new BigDecimal("10.5").negate();

        OperationType mockOperationType = OperationType.builder()
                .operationTypeId(operationTypeId)
                .description("PAGAMENTO")
                .allowNegative(false)
                .allowPositive(true)
                .build();

        Transaction mockTransaction = Transaction.builder()
                .amount(amount)
                .account(Account.builder().accountId(1L).build())
                .operationType(OperationType.builder().operationTypeId(operationTypeId).build())
                .build();

        when(mockOperationTypeService.getById(operationTypeId)).thenReturn(mockOperationType);

        InvalidOperationTypeException actualException = assertThrows(
                InvalidOperationTypeException.class,
                () -> transactionService.createTransaction(mockTransaction));

        String expectedExceptionMessage = String.format("Operation type (%s) does not allow negative amount value (%s).", mockOperationType.getDescription(), amount);
        assertEquals(expectedExceptionMessage, actualException.getMessage());
    }
}
