package org.example.users.repository;

import org.example.users.model.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaContableRepository extends JpaRepository<CuentaContable, String> {

    @Query(value = "SELECT *  FROM dbmaster.cuentaContable WHERE codigo_agrupador='171'", nativeQuery = true)
    List<CuentaContable> getValuestoBlanaceActivoFijo(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentaContable WHERE codigo_agrupador='184'", nativeQuery = true)
    List<CuentaContable> getValuesBalanceActivoDiferido(String account_id, String initial, String final_date);

    @Query(value = "SELECT * FROM dbmaster.cuentaContable cc WHERE cc.codigo_agrupador =:codigo_agrupador ", nativeQuery = true)
    CuentaContable getCuantaContable(String codigo_agrupador);

    @Query(value = "SELECT cc.nombre_cuenta FROM dbmaster.cuentaContable cc WHERE cc.codigo_agrupador =:codigo_agrupador ", nativeQuery = true)
    String getCuentaContableVenta(String codigo_agrupador);

    @Query(value = "SELECT cc.nombre_cuenta FROM dbmaster.cuentaContable cc WHERE cc.codigo_agrupador =:codigo_agrupador ", nativeQuery = true)
    String getCuantaContableMethod(String codigo_agrupador);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='401'", nativeQuery = true)
    CuentaContable getValuesOf401(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='613'", nativeQuery = true)
    CuentaContable getValuesOf613(String account_id, String initial, String final_date);


    @Query(value = "SELECT SUM(saldo_final)  FROM dbmaster.policy WHERE cuenta=:account_id AND fecha BETWEEN :initial AND :final_date", nativeQuery = true)
    String getValuesMontly(String account_id, String initial, String final_date);

    @Query(value = "SELECT SUM(saldo_final)  FROM dbmaster.policy WHERE cuenta=:account_id  AND fecha BETWEEN :initial AND :final_date", nativeQuery = true)
    String getValuesAnual(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.policy WHERE cuentas='401' AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getValuesOf401Anual(String initial_date, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.policy WHERE cuenta='401' AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getImporte_mensual401(String initial_date, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.policy WHERE cuenta='401' AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getPorcentaje_mensual401(String initial_date, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.policy WHERE cuenta='401' AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getImporte_anual401(String initial_date, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.policy WHERE cuenta='401' AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    String getPorcentaje_anual401(String initial_date, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='501'", nativeQuery = true)
    CuentaContable getValuesOf501(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='503'", nativeQuery = true)
    CuentaContable getValuesOf503(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='605'", nativeQuery = true)
    CuentaContable getValuesOf605(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='601'", nativeQuery = true)
    CuentaContable getValuesOf601(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='704'", nativeQuery = true)
    CuentaContable getValuesOf704(String account_id, String initial, String final_date);

    @Query(value = "SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='701'", nativeQuery = true)
    CuentaContable getValuesOf701(String account_id, String initial, String final_date);

    @Query(value = "SELECT DISTINCT cuenta FROM  dbmaster.policy  where cuenta LIKE :account_id% AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    List<String> getAccountsOfTable(String account_id, String initial_date, String final_date);

    @Query(value = "SELECT SUM(saldo_final) FROM  dbmaster.policy  where cuenta LIKE :account_id% AND fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
   String getValuesOfTable(String account_id, String initial_date, String final_date);

}
