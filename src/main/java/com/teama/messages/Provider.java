package com.teama.messages;

/**
 * Created by jakepardue on 11/14/17.
 */
public enum Provider {
    VERIZON("Verizon"), SPRINT("Sprint"), ATT("ATT"), TMOBILE("TMobile");

    public String provider;
    Provider(String provider){
        this.provider = provider;
    }

    public String toString() {
        return this.provider;
    }

    public static Provider getFromString(String val) {
        for(Provider p : Provider.values()) {
            if(p.toString().equals(val)) {
                return p;
            }
        }
        return null;
    }
}
