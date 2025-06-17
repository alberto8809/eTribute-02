package org.example.users.repository;


import org.example.users.model.CFDI_use;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CFDIRepository extends JpaRepository<CFDI_use, String> {

    @Query(value = "SELECT * FROM dbmaster.CFDI_use cc WHERE cc.CFDI_use_id =:cfdi " ,nativeQuery = true)
    CFDI_use usoCFDI(String cfdi);
}
