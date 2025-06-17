package org.example.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Response {

    private String url_xml;
    private Descripcion descripcion;
    private String url_pdf;


}
