package org.example.users.controller;


import jakarta.transaction.Transactional;
import org.example.users.model.Account;
import org.example.users.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/")
public class AccountController {

    @Autowired
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping( "account/{account_id}")
    public List<Account> getAccountById(@PathVariable(name="account_id")  int account_id){
        return accountService.getAccountById(account_id);
    }

    @GetMapping( "accounts/{token}")
    public List<Account> getAccountByToken(@PathVariable(name="token")  String token){
        return accountService.getAccountByToken(token);
    }


    @GetMapping( "accounts")
    public List<Account> getAllAccount(){
        return accountService.getAllAccounts();
    }

    @Transactional
    @PostMapping(value = "accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> addNewAccount(@RequestBody Account userAccount)  {
        return new ResponseEntity<>(accountService.createAccount(userAccount), HttpStatus.CREATED);
    }

}
