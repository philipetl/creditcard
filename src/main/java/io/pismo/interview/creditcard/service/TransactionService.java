package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.entity.OperationType;
import io.pismo.interview.creditcard.entity.Transaction;
import io.pismo.interview.creditcard.exception.InvalidOperationException;
import io.pismo.interview.creditcard.exception.InvalidOperationTypeException;
import io.pismo.interview.creditcard.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

        Account account = accountService.getById(transaction.getAccount().getAccountId());
        transaction.setAccount(account);

        BigDecimal newLimit = account.getAvailableCreditLimit().add(transaction.getAmount());
        if(newLimit.doubleValue() < 0){
            throw new InvalidOperationException("Operation denied due credit limit.");
        }

        return transaction;
    }
}
