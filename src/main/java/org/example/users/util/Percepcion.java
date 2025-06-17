package org.example.users.util;

public enum Percepcion {

    P001("001"),
    P002("002"),
    P003("003"),
    P004("004"),
    P005("005"),
    P006("006"),
    P009("009"),
    P010("010"),
    P011("011"),
    P012("012"),
    P013("013"),
    P014("014"),
    P015("015"),
    P019("019"),
    P020("020"),
    P021("021"),
    P022("022"),
    P023("023"),
    P024("024"),
    P025("025");
    private String value;

    Percepcion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
