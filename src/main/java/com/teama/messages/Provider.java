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
}
