package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.repository.OperationTypeRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class OperationTypeService {

    final OperationTypeRepository operationTypeRepository;

    public OperationTypeService(OperationTypeRepository operationTypeRepository) {
        this.operationTypeRepository = operationTypeRepository;
    }

    public OperationType getById(Long operationTypeId) {
        return operationTypeRepository.findById(operationTypeId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Operation type with id %d not found.", operationTypeId)));
    }
}
