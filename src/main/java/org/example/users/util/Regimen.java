package org.example.users.util;

public enum Regimen {

    RG601("601"),
    RG603("603"),
    RG620("620"),
    RG622("622"),
    RG623("623"),
    RG624("624"),
    RG605("605"),
    RG607("607"),
    RG608("608"),
    RG611("611"),
    RG614("614"),
    RG615("615"),
    RG606("606"),
    RG612("612"),
    RG625("625"),
    RG626("626"),
    RG610("610"),
    RG616("616"),
    RG621("621");
    private String value;


    Regimen(String value) {
        this.value = value;
    }

    public String getRegimen() {
        return value;
    }

    public void setRegimen(String value) {
        this.value = value;
    }
}
