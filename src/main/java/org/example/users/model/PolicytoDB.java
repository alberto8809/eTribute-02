package org.example.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "policy")
public class PolicytoDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id_policy;
    private String cliente;
    private String usuario;
    private String tipo;
    private String poliza;
    private double cuenta;
    private String descripcion;
    private String debe;
    private String haber;
    private String fecha;
    private String proveedor;
    private String referencia;
    private String total;
    private String saldo_inicial;
    private String saldo_final;
    private int account_id;
    private String file_name;

}
