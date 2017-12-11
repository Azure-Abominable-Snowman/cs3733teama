package com.teama.requestsubsystem;

/**
 * Created by aliss on 12/8/2017.
 */
public interface ReportSubject {
    void notifyObservers();
    void attachObserver(RequestDatabaseObserver obs);

}
