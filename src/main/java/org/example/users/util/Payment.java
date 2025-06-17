package org.example.users.util;

public enum Payment {

    PAY01("01"),
    PAY02("02"),
    PAY03("03"),
    PAY04("04"),
    PAY05("05"),
    PAY06("06"),
    PAY08("08"),
    PAY12("12"),
    PAY13("13"),
    PAY14("14"),
    PAY15("15"),
    PAY17("17"),
    PAY23("23"),
    PAY24("24"),
    PAY25("25"),
    PAY26("26"),
    PAY27("27"),
    PAY28("28"),
    PAY29("29"),
    PAY30("30"),
    PAY31("31"),
    PAY99("99");


    private String value;

    Payment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
