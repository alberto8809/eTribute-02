package org.example.users.repository;


import org.example.users.model.Auxiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuxiliarRepository extends JpaRepository<Auxiliar, String> {

    @Query(value = "SELECT * from dbmaster.policy WHERE account_id=:account_id AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    List<Auxiliar> getAuxiliar(String account_id, String initial_date, String final_date);

    @Query(value = "SELECT * FROM dbmaster.policy pof WHERE pof.cuenta=:cuenta AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    List<Auxiliar> valuesOfTable(String cuenta, String initial_date, String final_date);
    @Query(value = "SELECT SUM(pof.debe) FROM  dbmaster.policy pof where pof.cuenta=:cuenta", nativeQuery = true)
    String getSumInicial(String cuenta);

    @Query(value = "SELECT * FROM dbmaster.policy pof WHERE pof.cuenta=:cuenta AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    List<Auxiliar> valuesOfPDF(String cuenta, String initial_date, String final_date);

    @Query(value = "SELECT SUM(pof.debe) FROM  dbmaster.policy pof where pof.cuenta=:cuenta", nativeQuery = true)
    String getSumCargo(String cuenta);

    @Query(value = "SELECT SUM(pof.haber) FROM  dbmaster.policy pof where pof.cuenta=:cuenta", nativeQuery = true)
    String getSumAbono(String cuenta);

    /**/
    @Query(value = "SELECT SUM(pof.debe) FROM  dbmaster.policy pof where pof.cuenta=:account_id", nativeQuery = true)
    String getSumDebeByAccountId(String account_id);

    @Query(value = "SELECT SUM(pof.haber) FROM  dbmaster.policy pof where pof.cuenta=:account_id", nativeQuery = true)
    String getSumHaberByAccountId(String account_id);

    @Query(value = "SELECT SUM(pof.saldo_final) FROM  dbmaster.policy pof where pof.cuenta=:account_id", nativeQuery = true)
    String getSaldoFinal(String account_id);

    @Query(value = "SELECT SUM(pof.saldo_final) FROM  dbmaster.policy pof where pof.cuenta LIKE :account_id%", nativeQuery = true)
    String getSaldoFinalBalance(String account_id);

    @Query(value = "SELECT SUM(pof.debe) FROM  dbmaster.policy pof where pof.cuenta=:account_id AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getDeudor_inicial(String account_id, String initial_date, String final_date);
    @Query(value = "SELECT SUM(pof.haber) FROM  dbmaster.policy pof where pof.cuenta=:account_id AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getAcredor_final(String account_id, String initial_date, String final_date);

}
