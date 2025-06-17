package org.example.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "claveProdServ")
public class ClaveProductoServ {
    @Id
    @Column(name = "c_claveprodserv")
    String c_claveprodserv;
    @Column(name = "descripcion")
    String descripcion;
    @Column(name = "c_contable")
    String c_contable;

}
