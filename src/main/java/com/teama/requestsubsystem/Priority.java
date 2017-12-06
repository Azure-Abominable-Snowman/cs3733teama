package com.teama.requestsubsystem;

import com.teama.requestsubsystem.interpreterfeature.Language;

/**
 * Created by jakepardue on 12/4/17.
 */
public enum Priority {
    ONE(1),TWO(2), THREE(3), FOUR(4), FIVE(5);

    private final int priorityLevel;

    Priority(int num){
        this.priorityLevel = num;
    }

    public String toString(){
        return Integer.toString(priorityLevel);
    }

    public static Priority getPriorityValue(int num) {
        for (Priority p: Priority.values()) {
            if (p.toString().equals(Integer.toString(num))) {
                return p;
            }
        }
        throw new IllegalArgumentException("No such Priority value exists, " + num);
    }
}
