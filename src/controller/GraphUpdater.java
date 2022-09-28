package controller;

import kirkwood.nidaq.access.NiDaqException;
import model.AITask;
import org.jfree.data.xy.XYSeries;
import controller.Calculations.Units;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Live graphs data
 */
public class GraphUpdater extends Thread{
    private final XYSeries series;
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final AtomicBoolean run = new AtomicBoolean(false);
    private final AITask aiTask;
    private final MainController mainController;
    private double stressZero = 0.0; //force 0 -> stress
    private double strainZero = 0.0; //elongation -> strain (Extensometer)

    private static double LBS_PER_VOLT;
    private static double INCHES_PER_VOLT;

    public GraphUpdater(XYSeries series, MainController mainController, SettingsController settingsController) throws NiDaqException {
        aiTask = new AITask();
        // DO NOT CHANGE THE ORDER OF THE NEXT 2 LINES
        aiTask.createAIChannel(settingsController.getSettingsWindow().getForceChannel(), settingsController.getSettingsWindow().getForceMode()); //Force
        aiTask.createAIChannel(settingsController.getSettingsWindow().getElongationChannel(), settingsController.getSettingsWindow().getElongationMode()); //Elongation
        aiTask.readyToRun();

        LBS_PER_VOLT = settingsController.getSettingsWindow().getForceVoltage2UnitConstant();
        INCHES_PER_VOLT = settingsController.getSettingsWindow().getElongationVoltage2UnitConstant();

        this.mainController = mainController;
        this.series = series;
    }

    /**
     * Runs the graph updater thread.
     * Adds data from National Instruments Chip to the series
     */
    @Override
    public void run() {
        SlidingAverage forceAveraged = new SlidingAverage(AITask.UPDATES_PER_SECOND);
        SlidingAverage elongationAveraged = new SlidingAverage(AITask.UPDATES_PER_SECOND);
        while(!done.get()) {//call code from class that gets data from the chip.
            try {
                if(!run.get()){
                    synchronized (this){
                        wait();
                    }
                }

                Thread.sleep(50);
            } catch (InterruptedException e) {
                //do nothing
            }

            if(done.get()) {
                return;
            }

            aiTask.collectData();
            double force = aiTask.getChannelData(0);
            double length = aiTask.getChannelData(1); // raw voltage data

            double forceValue = (LBS_PER_VOLT * (forceAveraged.addData(force)  - stressZero));
            double elongationValue = (INCHES_PER_VOLT * (elongationAveraged.addData(length)  - strainZero));

            if(mainController.getUnitSystem().equals("Metric")){
                forceValue = Calculations.convertForce(Calculations.Units.ENGLISH, Calculations.Units.METRIC,forceValue);
                elongationValue = Calculations.convertLength(Calculations.Units.ENGLISH, Calculations.Units.METRIC, elongationValue);
            }else{
                forceValue /= 1000;
            }

            double stressValue = Calculations.calculateStress(forceValue, mainController.findArea());
            double strainValue = Calculations.calculateStrain(elongationValue, mainController.getGaugeLength());

            series.add(strainValue, stressValue, true);
        }
    }

    /**
     * Pauses the graph updater thread
     */
    public void pause(){
        run.set(false);
        aiTask.pause();

    }

    /**
     * Resumes the graph updater thread
     */
    public synchronized void collect(){
        run.set(true);
        notifyAll();
    }

    /**
     * Stops the tread permanently
     */
    public synchronized void terminate() {
        done.set(true);
        notifyAll();
    }

    /*
     * Updates the zero points for the voltages
     */
    protected void updateZeros(){
        double forceTotal = 0.0;
        double elongationTotal = 0.0;
        aiTask.collectData();

        for(int i = 0; i < AITask.UPDATES_PER_SECOND; i++){
            double channel0 = aiTask.getChannelData(AITask.FORCE_CHANNEL);
            double channel1 = aiTask.getChannelData(AITask.LENGTH_CHANNEL);

            forceTotal += channel0;
            elongationTotal += channel1;
        }

        stressZero = forceTotal / AITask.UPDATES_PER_SECOND;
        strainZero = elongationTotal / AITask.UPDATES_PER_SECOND;
    }

    /**
     * Updates the graph units based on starting and ending units
     * @param startingUnits the units that currently appear on the graph
     * @param endingUnits the units a user wants to convert to
     * @param series that data in (x,y) format that is shown on the graph
     */
    public void updateGraphUnits(Units startingUnits, Units endingUnits, XYSeries series){
        for(int i = 0; i < series.getItemCount(); i++){
            double yValue = series.getY(i).doubleValue();
            yValue = Calculations.convertPressure(startingUnits, endingUnits, yValue);
            series.updateByIndex(i, yValue);
        }
    }

    public XYSeries getSeries() {
        return series;
    }
}
