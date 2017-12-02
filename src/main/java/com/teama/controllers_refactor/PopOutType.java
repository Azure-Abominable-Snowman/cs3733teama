package com.teama.controllers_refactor;

public enum PopOutType {
    EDITOR("Editor"), REQUESTS("Requests"), STAFF("Staff");
    private final String name;
    PopOutType(String s){name=s;}
    public String toString() {return this.name;}
}
