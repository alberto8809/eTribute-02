package org.example.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "policyObjFile")
public class Auxiliar {
    @Id
    @Column(name = "id_policy")
    private int id_policy;
    @Column(name = "cliente")
    private String cliente;
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "poliza")
    private String poliza;
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "debe")
    private String debe;
    @Column(name = "haber")
    private String haber;
    @Column(name = "fecha")
    private String fecha;
    @Column(name = "proveedor")
    private String proveedor;
    @Column(name = "referencia")
    private String referencia;
    @Column(name = "total")
    private String total;
    @Column(name = "saldo_inicial")
    private String saldo_inicial;
    @Column(name = "saldo_final")
    private String saldo_final;


}
