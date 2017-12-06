package com.teama.requestsubsystem.transportationfeature;

/**
 * Created by jakepardue on 12/3/17.
 */
public enum ModeOfTransportation {
    HELICOPTER("Helicopter"), STRETCHER("Stretcher"), WHEELCHAIR("Wheelchair"), AMBULANCE("Ambulance"),
    PERSON("Person"), TAXI("Taxi");

    private final String type;

    ModeOfTransportation(String type){
        this.type = type;
    }

    @Override
    public String toString(){
        return type;
    }

    public static ModeOfTransportation getModeOfTransportation(String type) throws IllegalAccessException {
        for(ModeOfTransportation m : ModeOfTransportation.values()){
            if(m.toString().equals(type)){
                return m;
            }
        }
        throw new IllegalAccessException("This type of transportation does not exist: "+ type);
    }


}
