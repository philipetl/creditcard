package io.pismo.interview.creditcard.controller;

import io.pismo.interview.creditcard.domain.AccountDTO;
import io.pismo.interview.creditcard.entity.Account;
import io.pismo.interview.creditcard.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public AccountController(AccountService accountService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/{accountId}")
    public AccountDTO get(@PathVariable(value = "accountId") Long accountId) {
        return modelMapper.map(accountService.getById(accountId), AccountDTO.class);
    }

    @PostMapping
    public AccountDTO create(@RequestBody AccountDTO accountDTO) {
        Account account = modelMapper.map(accountDTO, Account.class);
        return modelMapper.map(accountService.createAccount(account), AccountDTO.class);
    }
}
