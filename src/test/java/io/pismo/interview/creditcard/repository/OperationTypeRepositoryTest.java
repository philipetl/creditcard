package io.pismo.interview.creditcard.repository;

import io.pismo.interview.creditcard.entity.OperationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OperationTypeRepositoryTest {

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
    public void getReturnOperationTypeWhenExists() {
        long operationTypeId = 4;
        Optional<OperationType> actualOperationType = operationTypeRepository.findById(operationTypeId);

        OperationType expectedOperationType = OperationType.builder()
                .operationTypeId(operationTypeId).description("PAGAMENTO")
                .allowNegative(false).allowPositive(true).build();

        assertEquals(expectedOperationType, actualOperationType.get());
    }

    @Test
    public void getShouldReturnEmptyOptionWhenWhenAccountDoesNotExist() {
        Optional<OperationType> actualOperationType = operationTypeRepository.findById(999L);
        assertEquals(Optional.empty(), actualOperationType);
    }
}
