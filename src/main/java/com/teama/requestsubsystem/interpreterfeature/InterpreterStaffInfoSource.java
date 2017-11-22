package com.teama.requestsubsystem.interpreterfeature;

import java.util.Set;

/**
 * Created by aliss on 11/22/2017.
 */
public interface InterpreterStaffInfoSource {
    Set<InterpreterStaff> findQualified(Language lang);
    //TODO: return a list of all staff
    boolean addStaff(InterpreterStaff s);
    boolean updateStaff(InterpreterStaff s);
    boolean deleteStaff(int id);
    void close();


}
