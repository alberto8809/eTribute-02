package org.example.users.util;

public enum CuentaContable {
    R500("80131500"),
    R501("80131501"),
    R502("80131502"),
    R503("80131503"),
    R505("80131505"),
    R506("80131506"),
    R841("84111505"),
    R010("01010101");

    private String value;

    CuentaContable(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
