package com.teama.requestsubsystem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Created by aliss on 12/8/2017.
 */
public class LineChartReport {
    LineChart<Number, Number> report;
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    XYChart.Series data = new XYChart.Series<>();


    /**
     *
     * @param title
     * @param xAxis - label of xAxis
     * @param yAxis - label of yAxis
     */
    public LineChartReport(String title, String xAxis, String yAxis) {
        this.xAxis.setLabel(xAxis);
        this.yAxis.setLabel(yAxis);
        report = new LineChart(this.xAxis, this.yAxis);
        report.setTitle(title);

    }

    public void addData(double xData, double yData) {
        data.getData().add(new XYChart.Data(xData, yData));
        report.getData().add(data);
    }
}
