package com.teama.requestsubsystem.interpreterfeature;

import com.teama.requestsubsystem.LineChartReport;
import com.teama.requestsubsystem.ReportSubject;
import com.teama.requestsubsystem.RequestDatabaseObserver;

/**
 * Created by aliss on 12/8/2017.
 *//*
public class ServiceTimeGraph extends RequestDatabaseObserver{

    private ReportSubject s;

    int numRequestsFilled = 0;

    LineChartReport serviceLineChart = new LineChartReport("Service Time", "Time", "Service Time (Minutes)");
    ServiceTimeGraph(ReportSubject s) {
        this.s = s;
        s.attachObserver(this);

    }

    public void update() {
        numRequestsFilled++;
        InterpreterRequest fulfilled = InterpreterSubsystem.getInstance().getReport();
        serviceLineChart.addData(numRequestsFilled, fulfilled.getServiceTime());
    }

}
*/