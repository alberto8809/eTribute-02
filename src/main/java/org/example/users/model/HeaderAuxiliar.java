package org.example.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class HeaderAuxiliar {
    private String cuenta;
    private String nombre;
    private String saldoInicial;
    private String debe;
    private String haber;
    private String saldoFinal;

}
