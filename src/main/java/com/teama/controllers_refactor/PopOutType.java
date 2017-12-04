package com.teama.controllers_refactor;

public enum PopOutType {
    LOGIN("Login"), EDITOR("Editor"), REQUESTS("Requests"), STAFFDIRECTORY("Directory"), DIRECTIONS("Directions");
    private final String name;
    PopOutType(String s){name=s;}
    public String toString() {return this.name;}
}
