package io.pismo.interview.creditcard.service;

import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.repository.AccountRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class AccountService {
    final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account with id %d not found.", accountId)));
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
}
