package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.entity.Transaction;
import io.pismo.interview.creditcard.exception.InvalidOperationException;
import io.pismo.interview.creditcard.exception.InvalidOperationTypeException;
import io.pismo.interview.creditcard.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    final TransactionRepository transactionRepository;
    final OperationTypeService operationTypeService;
    final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, OperationTypeService operationTypeService, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.operationTypeService = operationTypeService;
        this.accountService = accountService;
    }

    public Transaction createTransaction(Transaction transaction) throws Exception {
        Transaction transactionValidated = validate(transaction);

        accountService.updateCreditLimit(transactionValidated);
        return transactionRepository.save(transactionValidated);
    }

    private Transaction validate(Transaction transaction) throws Exception {
        validateOperationType(transaction);
        validateAccountAvailableLimit(transaction);

        return transaction;
    }

    private void validateOperationType(Transaction transaction) throws InvalidOperationTypeException {
        OperationType operationType = operationTypeService.getById(transaction.getOperationType().getOperationTypeId());

        if (operationType.getAllowNegative() && operationType.getAllowPositive()) {
            return;
        } else if (operationType.getAllowPositive()) {
            if (transaction.getAmount() < 0) {
                throw new InvalidOperationTypeException(String.format("Operation type (%s) does not allow negative amount value (%s).",
                        operationType.getDescription(), transaction.getAmount()));
            }
        } else if (operationType.getAllowNegative()) {
            if (transaction.getAmount() >= 0) {
                throw new InvalidOperationTypeException(String.format("Operation type (%s) does not allow positive amount value (%s).",
                        operationType.getDescription(), transaction.getAmount()));
            }
        } else {
            throw new InvalidOperationTypeException(String.format("Operation type (%s) does not allow any amount value (%s).",
                    operationType.getDescription(), transaction.getAmount()));
        }
    }

    private void validateAccountAvailableLimit(Transaction transaction) throws InvalidOperationException {
        Account account = accountService.getById(transaction.getAccount().getAccountId());
        transaction.setAccount(account);

        double newLimit = account.getAvailableCreditLimit() + transaction.getAmount();
        if (newLimit < 0) {
            throw new InvalidOperationException("Operation denied due credit limit.");
        }
    }
}
