package org.example.users.repository;


import org.example.users.model.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MethodOfPaymentRepository extends JpaRepository<CuentaContable, String> {

    @Query(value = "SELECT cc.cuentaContable FROM dbmaster.payment_methods cc WHERE cc.payment_id =:typeOfPayment " ,nativeQuery = true)
    String getCuentaContable(String typeOfPayment);


    @Query(value = "SELECT tx.cuenta_contable  FROM dbmaster.tax tx WHERE tx.tax_id =:tax", nativeQuery = true)
    String getCuentaContableByTax(String tax);



    @Query(value = "SELECT nm.codigo_agrupador  FROM dbmaster.nomina nm WHERE nm.c_TipoDeduccion =:tax_id", nativeQuery = true)
    String getCuentaContableByNomina(String tax_id);


}
