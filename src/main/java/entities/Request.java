package entities;
//import java.util.List;

/**
 * Created by jakepardue on 11/10/17.
 */

public class Request {

    private Location toLocation;
    private Enum<TypeOfRequest> type;
    private Enum<PriorityLevel> priorityLevel;
    private String message;
    //private List<ServiceStaff> requiredStaff;*/

    public Request(Location toLocation, Enum<TypeOfRequest> type, Enum<PriorityLevel> priorityLevel, String message/*,List<ServiceStaff> staff*/){
        this.toLocation = toLocation;
        this.type = type;
        this.priorityLevel = priorityLevel;
        this.message = message;
        //this.requiredStaff = staff;
    }

    public Location getToLocation() {return this.toLocation; }

    public void setToLocation(Location newL){ this.toLocation = newL; }

    public String getMessage(){ return this.message; }

    public void setMessage(String newMessage){ this.message = newMessage; }

    public Enum<PriorityLevel> getPriorityLevel(){ return this.priorityLevel; }

    public void setPriorityLevel(Enum<PriorityLevel> newLevel){ this.priorityLevel = newLevel; }

    public Enum<TypeOfRequest> getTypeOfRequest(){ return this.type; }

    public void setTypeOfRequest(Enum<TypeOfRequest> newType){ this.type = newType; }

    //public List<ServiceStaff> getRequiredStaff(){ return this.requiredStaff;}
    //public void setRequiredStaff(List<ServiceStaff> newList){ this.requiredStaff = newList; }






}
