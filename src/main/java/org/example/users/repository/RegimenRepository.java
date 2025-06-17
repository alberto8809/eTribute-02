package org.example.users.repository;

import org.example.users.model.CuentaContable;
import org.example.users.model.Regimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegimenRepository extends JpaRepository<Regimen,String> {
    @Query(value = "SELECT * FROM dbmaster.regimen cc WHERE cc.regimen_id =:regimen " ,nativeQuery = true)
    CuentaContable getRegimen(String regimen);
}
