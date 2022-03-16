package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.repository.OperationTypeRepository;
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
class OperationTypeServiceTest {

    @Autowired
    OperationTypeService operationTypeService;

    @MockBean
    OperationTypeRepository mockOperationTypeRepository;

    @Test
    void getShouldReturnOperationType() {
        long existentOperationTypeId = 123;
        OperationType mockOperationType = OperationType.builder()
                .operationTypeId(existentOperationTypeId)
                .description("Pagamento")
                .allowNegative(false)
                .allowPositive(true)
                .build();
        when(mockOperationTypeRepository.findById(existentOperationTypeId)).thenReturn(Optional.of(mockOperationType));

        OperationType actualOperationType = operationTypeService.getById(existentOperationTypeId);
        assertEquals(mockOperationType, actualOperationType);
    }

    @Test
    void getShouldThrowEntityNotFoundExceptionWhenOperationTypeDoesNotExists() {
        long nonExistentOperationTypeId = 123;
        when(mockOperationTypeRepository.findById(nonExistentOperationTypeId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(
                EntityNotFoundException.class,
                () -> operationTypeService.getById(nonExistentOperationTypeId));

        assertEquals(String.format("Operation type with id %d not found.", nonExistentOperationTypeId), actualException.getMessage());
    }
}
