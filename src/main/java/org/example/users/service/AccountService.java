package org.example.users.service;


import org.example.users.model.Account;
import org.example.users.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;



    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
    public List<Account> getAccountById(int id) {
        return accountRepository.getListOfAccountById(id);
    }

    public List<Account> getAccountByToken(String token){
        return accountRepository.getListOfAccountByToken(token);
    }

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();

    }
}
