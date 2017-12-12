package com.teama.requestsubsystem.securityfeature;

import com.teama.requestsubsystem.PriorityLevel;

import java.util.Set;

public class SecurityInfo {

    private int id = 0;
    private Set<SecurityType> type;
    private PriorityLevel pLevel;

    public SecurityInfo(Set<SecurityType> types, PriorityLevel urgency){
        type = types;
        this.pLevel = urgency;
    }
    public SecurityInfo(int id, Set<SecurityType> types, PriorityLevel urgency){
        this.id = id;
        type = types;
        this.pLevel = urgency;
    }

    public int getStaffID() {return id;}
    public Set<SecurityType> getSecType(){return type;}
    public void setSecType(Set<SecurityType> types){ this.type = types;}

    public PriorityLevel getUrgency(){return pLevel;}

}
