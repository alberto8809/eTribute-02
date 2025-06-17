package org.example.users.repository;


import org.example.users.model.PolicytoDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaveObjRepository extends JpaRepository<PolicytoDB, Long> {

    @Query(value = "SELECT file_name FROM policy p where p.usuario=:usuario AND p.tipo=:type AND p.fecha BETWEEN :initial_date AND :final_date", nativeQuery = true)
    List<String> getObjFromDB(String usuario, String type, String initial_date, String final_date);
}
