package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.entity.Transaction;
import io.pismo.interview.creditcard.exception.InvalidOperationTypeException;
import io.pismo.interview.creditcard.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    final TransactionRepository transactionRepository;
    final OperationTypeService operationTypeService;

    public TransactionService(TransactionRepository transactionRepository, OperationTypeService operationTypeService) {
        this.transactionRepository = transactionRepository;
        this.operationTypeService = operationTypeService;
    }

    public Transaction createTransaction(Transaction transaction) throws InvalidOperationTypeException {
        return transactionRepository.save(validate(transaction));
    }

    private Transaction validate(Transaction transaction) throws InvalidOperationTypeException {
        OperationType operationType = operationTypeService.getById(transaction.getOperationType().getOperationTypeId());

        if (operationType.getAllowNegative() && operationType.getAllowPositive()) {
            return transaction;
        } else if (operationType.getAllowPositive()) {
            if (transaction.getAmount().doubleValue() < 0) {
                throw new InvalidOperationTypeException(String.format("Operation type (%s) does not allow negative amount value (%s).",
                        operationType.getDescription(), transaction.getAmount()));
            }
        } else if (operationType.getAllowNegative()) {
            if (transaction.getAmount().doubleValue() >= 0) {
                throw new InvalidOperationTypeException(String.format("Operation type (%s) does not allow positive amount value (%s).",
                        operationType.getDescription(), transaction.getAmount()));
            }
        } else {
            throw new InvalidOperationTypeException(String.format("Operation type (%s) does not allow any amount value (%s).",
                    operationType.getDescription(), transaction.getAmount()));
        }

        return transaction;
    }
}
