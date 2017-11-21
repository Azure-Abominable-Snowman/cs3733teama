package com.teama.requestsubsystem.interpreterfeature;

import com.teama.controllers.SceneEngine;

/**
 * Created by aliss on 11/21/2017.
 */
public class InterpreterSubsystem {
    private InterpreterRequestDB requestDB;
    private InterpreterStaffDB staffDB;
    private InterpreterSubsystem() {
        requestDB = new InterpreterRequestDB(SceneEngine.getURL());
        staffDB = new InterpreterStaffDB(SceneEngine.getURL());
    }
    private static class InterpreterHelper {
        InterpreterSubsystem _instance = new InterpreterSubsystem();
    }

    public InterpreterSubsystem getInstance() {

    }
}
