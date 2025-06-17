package org.example.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Balance {
    private String cuenta;
    private String nombre;
    private String deudor_inicial;
    private String acredor_inicial;
    private String debe;
    private String haber;
    private String deudor_final;
    private String acredor_final;

}
