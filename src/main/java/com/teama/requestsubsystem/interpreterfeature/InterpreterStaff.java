package com.teama.requestsubsystem.interpreterfeature;

import com.teama.requestsubsystem.GenericStaffInfo;

public class InterpreterStaff  {
    private GenericStaffInfo info;
    private InterpreterInfo interpSpecs;
    //TODO: specify hours on duty perhaps
    public InterpreterStaff(GenericStaffInfo i, InterpreterInfo j) {
        info = i;
        interpSpecs = j;
    }
}
