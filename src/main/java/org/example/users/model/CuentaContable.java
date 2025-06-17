package org.example.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CuentaContable {

    @Id
    @Column(name = "nivel")
    private String nivel;
    @Column(name = "codigo_agrupador")
    private String codigo_agrupador;
    @Column(name = "nombre_cuenta")
    private String nombre_cuenta;

}
