package com.teama.requestsubsystem.spiritualcarefeature;

/**
 * Created by jakepardue on 12/10/17.
 */
public enum Religion {
    CHRISTIANITY("Christianity"), CATHOLICISM("Catholicism"), MUSLIM("Muslim"), SIKHISM("Sikhism"),
    JEWISH("Jewish"), HINDUISM("Hinduism"), BUDDHISM("Buddhism"), ISLAM("Islam"), OTHER("Other");

    private String religion;
    Religion(String string){
        this.religion = string;
    }

    public String toString(){ return religion; }

    public static Religion getReligion(String str) throws IllegalAccessException {
        for(Religion r: Religion.values()){
            if(r.toString().equals(str)){
                return r;
            }
        }
        throw new IllegalAccessException("Sorry your religion isn't available here" + str);
    }

}
