package org.example.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GenerarEstados {
    private String concepto;
    private String importe;
    private String porcentaje_inicial;
    private String importe_final;
    private String porcentaje_final;
}
