package com.teama.requestsubsystem.transportationfeature;

import java.util.Set;

/**
 * Created by jakepardue on 12/3/17.
 */
public class TransportInfo {
    private int id = 0;
    private Set<ModeOfTransportation> modeOfTransportation;

    public TransportInfo(int id, Set<ModeOfTransportation> mode){
        this.id = id;
        this.modeOfTransportation = mode;
    }
    public TransportInfo(Set<ModeOfTransportation> m){
        this.modeOfTransportation = m;
    }

    public int getStaffID(){
        return this.id;
    }

    public Set<ModeOfTransportation> getModeOfTransportation(){
        return this.modeOfTransportation;
    }

    public void setModeOfTransportation(Set<ModeOfTransportation> mode){
        this.modeOfTransportation = mode;
    }



}
