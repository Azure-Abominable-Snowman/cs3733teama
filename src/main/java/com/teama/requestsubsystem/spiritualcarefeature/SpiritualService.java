package com.teama.requestsubsystem.spiritualcarefeature;

/**
 * Created by jakepardue on 12/10/17.
 */
public enum SpiritualService {
    BAPTISMS("Baptisms"), BLESSINGS("Blessings"), MEMORIALSERVICES("Memorial Services"),
    MARRIAGES("Marriages"), ENDOFLIFERITUALS("End of Life Rituals"), CONSULTATIONS("Consultations"),
    CODEBLUE("Code Blue"),CODETRAUMA("Code Trauma");

    private String type;
    SpiritualService(String s){
        this.type = s;
    }

    public String toString(){ return type;}

    public static SpiritualService getSpiritualService(String str) throws IllegalAccessException {
        for(SpiritualService s: SpiritualService.values()){
            if(s.toString().equals(str)){
                return s;
            }
        }
        throw new IllegalAccessException("No such service exists: " + str);
    }
}
