package entities;

/**
 * Created by jakepardue on 11/10/17.
 */
public enum PriorityLevel {
    LOW(1),MED(2),HIGH(3);


    private int level;
    PriorityLevel(int i) {
        this.level = i;
    }

    public boolean equalsLevel(int j){
        return level == j;
    }

    public int returnLevel(){
        return this.level;
    }
}
