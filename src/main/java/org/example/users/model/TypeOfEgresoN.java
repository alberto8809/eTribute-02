package org.example.users.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TypeOfEgresoN {
    private String cuenta;
    private String descripcion;
    private String abonoIMSS;
    private String abonoDescipcion;
    private String isr;
    private String isrDescripcion;
    private List<String> percepciones;
}
