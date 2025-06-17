package org.example.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BalanceTotal {
    private String deudorInicial_total;
    private String acredorinical_total;
    private String debe_total;
    private String haber_total;
    private String deudorFinal_total;
    private String acredorFinal_total;
}
