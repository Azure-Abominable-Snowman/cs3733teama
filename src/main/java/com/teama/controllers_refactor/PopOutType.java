package com.teama.controllers_refactor;

public enum PopOutType {
    EDITOR("Editor"), REQUESTS("Requests"), STAFF("Staff"), TEST("Test");
    private final String name;
    private PopOutType(String s){name=s;}
    public String toString() {return this.name;}
}
