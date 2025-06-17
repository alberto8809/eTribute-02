package org.example.users.util;

public enum CFDI {
    G01("G01"),
    G02("G02"),
    G03("G03"),
    I01("I01"),
    I02("I02"),
    I03("I03"),
    I04("I04"),
    I05("I05"),
    I06("I06"),
    I07("I07"),
    I08("I08"),
    S01("S01"),
    CP01("CP01"),
    P01("P01"),
    D01("D01"),
    D02("D02"),
    D03("D03"),
    D04("D04"),
    D05("D05"),
    D06("D06"),
    D07("D07"),
    D08("D08"),
    D09("D09"),
    D10("D10"),
    CN01("CN01"),
    P("P"),
    I("I"),
    E("E"),
    N("N"),
    PPD("PPD"),
    PUE("PUE"),
    RETENCIONES("RETENCIONES"),
    EGRESOS("EGRESOS"),
    INGRESOS("INGRESOS"),
    OTRASFORMAS("Otras Formas de pago"),
    BANCOS("Bancos Nacionales"),
    TAX01("001"),
    TAX02("002"),
    TAX03("003"),
    TAX04("004"),
    TAX05("005"),
    TAX06("006");

    private String value;

    CFDI(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
