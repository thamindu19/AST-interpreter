package Symbols;

import java.util.ArrayList;

public class Symbol {
    protected String value;

    public Symbol(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}