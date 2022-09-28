package controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class RealTimeChartTest extends ChartPanel implements Runnable {
    private static TimeSeries timeSeries;
    private long value=0;

    public RealTimeChartTest(String chartContent,String title,String yaxisName)
    {
        super(createChart(chartContent,title,yaxisName));
    }

    private static JFreeChart createChart(String chartContent,String title,String yaxisName){
        //Create a sequence diagram object
                timeSeries = new TimeSeries(chartContent,Millisecond.class);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "time (seconds)", yaxisName, timeseriescollection, true, true, false);
        XYPlot xyplot = jfreechart.getXYPlot();
                 // ordinate setting
        ValueAxis valueaxis = xyplot.getDomainAxis();
                 // Automatically set the data axis data range
        valueaxis.setAutoRange(true);
                 // Data axis fixed data range 30s
        valueaxis.setFixedAutoRange(30000D);

        valueaxis = xyplot.getRangeAxis();
        //valueaxis.setRange(0.0D,200D);

        return jfreechart;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                timeSeries.add(new Millisecond(), randomNum());
                Thread.sleep(300);
            }
            catch (InterruptedException e)  {   }
        }
    }

    private long randomNum()
    {
        System.out.println((Math.random()*20+80));
        return (long)(Math.random()*20+80);
    }

}
