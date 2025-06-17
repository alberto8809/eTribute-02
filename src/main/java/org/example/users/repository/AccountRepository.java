package org.example.users.repository;


import org.example.users.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {


    @Query(value = "SELECT cc.account_id , cc.account_name , cc.rfc , cc.regimen ,cc.cer , cc.key_value ,cc.e_firma ,cc.user_id  FROM dbmaster.account cc, dbmaster.user u  WHERE cc.user_id=u.user_id and u.token =:token", nativeQuery = true)
    List<Account> getListOfAccountByToken(String token);

    @Query(value = "SELECT *  FROM dbmaster.account cc WHERE cc.user_id=:id", nativeQuery = true)
    List<Account> getListOfAccountById(int id);

    @Query(value = "SELECT account_id  FROM dbmaster.account where rfc=:rfc", nativeQuery = true)
    int getAccountByAccount_id(String rfc);

    @Query(value = "SELECT rg.moral from dbmaster.account ac, dbmaster.regimen rg where ac.regimen=rg.regimen_id and ac.account_id=:accountId", nativeQuery = true)
    String getMoralByAccountId(int accountId);



}
