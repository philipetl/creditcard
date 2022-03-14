package io.pismo.interview.creditcard.controller;

import io.pismo.interview.creditcard.domain.TransactionDTO;
import io.pismo.interview.creditcard.entity.Transaction;
import io.pismo.interview.creditcard.exception.InvalidOperationTypeException;
import io.pismo.interview.creditcard.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    final TransactionService transactionService;
    private final ModelMapper modelMapper;

    public TransactionController(TransactionService transactionService, ModelMapper modelMapper) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public TransactionDTO create(@RequestBody TransactionDTO transactionDTO) throws InvalidOperationTypeException {
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        return modelMapper.map(transactionService.createTransaction(transaction), TransactionDTO.class);
    }
}
