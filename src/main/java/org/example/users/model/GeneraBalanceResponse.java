package org.example.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GeneraBalanceResponse {
    private String cuenta_id;
    private String descripcion;
    private float total;

}
