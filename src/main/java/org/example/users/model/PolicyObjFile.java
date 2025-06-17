package org.example.users.model;


import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PolicyObjFile {

    private String id_policy;
    private PolicyObjParser policyObj;
    private String nameFile;
    private String client;
    private String date;
    private String typeOf;
    private String cuenta_method;
    private String description_methods;
    private List<String> tax_id;
    private List<String> tax_description;
    private String folio;
    private String cuenta;

    public PolicyObjFile(PolicyObjParser policyObjParser, String nameFile, String client, String date, String typeOf) {
        this.policyObj = policyObjParser;
        this.nameFile = nameFile;
        this.client = client;
        this.date = date;
        this.typeOf = typeOf;
    }

}
