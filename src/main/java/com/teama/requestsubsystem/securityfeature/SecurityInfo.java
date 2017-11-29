package com.teama.requestsubsystem.securityfeature;

import java.util.Set;

public class SecurityInfo {

    private int id = 0;
    private Set<SecurityType> type;
    private Set<SecurityLevel> urgency;

    public SecurityInfo(Set<SecurityType> types, Set<SecurityLevel> urgency){
        type = types;
        this.urgency = urgency;
    }
    public SecurityInfo(int id, Set<SecurityType> types, Set<SecurityLevel> urgency){
        this.id = id;
        type = types;
        this.urgency = urgency;
    }

    public int getStaffID() {return id;}
    public Set<SecurityType> getSecType(){return type;}
    public void setSecType(Set<SecurityType> types){ this.type = types;}

    public Set<SecurityLevel> getUrgency(){return urgency;}
}
