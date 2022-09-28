package input.test;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import java.io.*;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * This Class creates an interactive text based calibration tool for finding the Volts to Units constant.
 */
public class Calibration {

    /**
     * NiDaq middle layer to call NiDaq function.
     */
    private static NiDaq daq = new NiDaq();

    // 5 min :      1500000
    // 2.5 min :     750000
    private static final int seconds = 1;

    private static final double samplesPerSecond = 100.0;
    private static final int inputBufferSize = (int) Math.ceil(seconds * samplesPerSecond);
    private static final int samplesInChannel = inputBufferSize;
    private double [] voltageValues;
    private double [] actualValues;
    private int index = 0;
    private double voltageToUnitsConstant = 0.0;
    private LinearRegression linearRegression;

    public Calibration (){
        voltageValues = new double[1024];
        actualValues = new double[1024];
    }

    /**
     * Actually Calls the internal functions to get a finite amount of raw samples
     * @param channel Channel on National Instruments Device that corresponds to the device you are trying to calibrate
     * @param mode The type of signal output from the machine that needs calibrated
     * @return a double filled with raw voltage values to be processed
     * @throws NiDaqException
     * @throws InterruptedException
     */
    private static double[] analogInputTest(String channel, int mode) throws NiDaqException {
        Pointer aiTask = null;
        try {
            String physicalChan = "Dev1/ai" + channel + "\0";
            aiTask = daq.createTask("AITask\0");
            System.out.println("aiTask assigned" + aiTask);
            daq.createAIVoltageChannel(aiTask, physicalChan, "\0", mode, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts, null);
            daq.cfgSampClkTiming(aiTask, "\0", samplesPerSecond, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, samplesInChannel);
            daq.startTask(aiTask);
            System.out.println("Task Started!");
            Integer read = new Integer(0);
            double[] buffer = new double[inputBufferSize];

            DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
            IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[] {read} );
            daq.readAnalogF64(aiTask, -1, -1, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer, inputBufferSize, samplesPerChannelRead);
            daq.stopTask(aiTask);
            daq.clearTask(aiTask);
            System.out.println("Buffer:\t" + buffer);
            return buffer;

        } catch(NiDaqException e) {
            try {
                System.out.println("Trying to stop task");
                System.out.println(e.getMessage());
                e.printStackTrace();
                daq.stopTask(aiTask);
                daq.clearTask(aiTask);
                return null;
            } catch(NiDaqException e2) {}
            throw(e);
        }
    }

    public double add(String channel, int mode, double value){
        double [] rawVoltageData = new double[100];
        try {
            rawVoltageData = analogInputTest(channel, mode);
        } catch (NiDaqException e) {
            e.printStackTrace();
        }
        double total = 0.0;
        for(int i = 0; i < rawVoltageData.length; i++){
            total += rawVoltageData[i];
        }
        double voltageValue = total/rawVoltageData.length;
        voltageValues[index] = voltageValue;
        actualValues[index] = value;
        index++;
        return voltageValue;
    }

    /**
     * this method uses the Linear Regression class from Robert Sedgewick and Kevin Wayne's code
     *
     * @return the voltage to units constant (slope of the data)
     */
    public double getVoltageToUnitsConstant(){
        linearRegression = new LinearRegression(voltageValues, actualValues);
        voltageToUnitsConstant = linearRegression.slope();
        return voltageToUnitsConstant;
    }

    public void remove(){
        if(index > 0){
            voltageValues[index - 1] = 0.0;
            actualValues[index - 1] = 0.0;
            index--;
        }
    }



    public static void main (String[] args) {
        //Starts Program with description and prompts user to set up calibration

        String header =
                "-------------------------Tensile Testing Assistant: Calibration Tool-------------------------\n" +
                "Developed by Computer Science Students from Otterbein University\n" +
                "Connor May '21  & Julia VanLandingham '21 & Chris Geidans '22 & Kaitlin Dosch '22\n" +
                "---------------------------------------------------------------------------------------------\n" +
                "Description: This tool is made to aid users in finding a voltage to units constant to use in\n" +
                        "\tthe Tensile Testing Assistant software. First you will need to have a way to get a \n" +
                        "\treading from the machine you are trying to test. This method should also be able to\n" +
                        "\tstop for one second while the data is retrieved from the National Instruments Device.\n" +
                        "\tOnce all of this is ready you will steadily increase the values, stopping at increments\n" +
                        "\tof your choosing. NOTE: it is possible that the more increments you use the more\n" +
                        "\taccurate the data will be. When you stop at an increment use the add command to add a\n" +
                        "\tvalue to the dataset. Once complete type the command 'finished' in and the tool will\n" +
                        "\tcalculate a linear regression of the points added. The constant will be displayed in\n" +
                        "\tthe terminal. Once you have the constant you will need to update the Settings in the\n" +
                        "\tmain software. See the User Manual for information on how to do that.\n\n";

        String usageString = "USAGE:\n" +
                "\tadd [value]\t\t\t-> This takes 1 second of data and averages into one voltage \n" +
                "\t\t\t\t\t\t\t\tvalue and then adds it with the value given, where the \n" +
                "\t\t\t\t\t\t\t\tvalue given is what the machine should be reading \n" +
                "\t\t\t\t\t\t\t\t(the value that you are reading from the machine)\n" +
                "\tremove\t\t\t\t-> This removes the last reading if needed\n" +
                "\tfinished\t\t\t-> This does the Linear Regression and outputs the voltage to units constant\n" +
                "\t\t\t\t\t\t\t\tto the terminal\n" +
                "\tquit\t\t\t\t-> Quits the program" +
                "\tlist\t\t\t\t-> Lists all the current values stored";

        System.out.println(header + usageString);
        System.out.println("---------------------------------------------------------------------------------------------");
        boolean hasQuit = false;
        boolean hasFinished = false;
        Scanner input = new Scanner(System.in);
        String userInput = "";
        System.out.print("Enter the channel number you would like to read from: ");
        String channel = input.next();
        System.out.print("Enter the mode you would like (DIFF/RSE): ");
        String modeString = input.next();
        int mode = -1;
        if(modeString.equals("DIFF")){
            System.out.println("Mode set to Differential");
            mode = Nicaiu.DAQmx_Val_Diff;
        }else {
            System.out.println("Mode set to RSE");
            mode = Nicaiu.DAQmx_Val_RSE;
        }

        //Starts Listening to User Commands
        Calibration calibration = new Calibration();
        while(!hasQuit){
            System.out.print("Enter Command: ");
            userInput = input.next();
            String command = userInput.split(" ")[0];
            switch (command){
                case "add":
                    String arguments = input.next();
                    double realValue = Double.parseDouble(arguments);
                    System.out.println("Trying to add voltage value corresponding with: " + realValue);
                    double value = calibration.add(channel, mode, realValue);
                    System.out.println("Added value: " + value);
                    break;
                case "remove":
                    calibration.remove();
                    break;
                case "list":
                    System.out.println("This many values are stored currently: " + calibration.actualValues.length);
                    System.out.println("Listing currently stored values:");
                    System.out.println("Voltage Values\t\t\tActual Values");
                    for(int i = 0; i < calibration.index; i++){
                        System.out.println(calibration.voltageValues[i] + "\t" + calibration.actualValues[i]);
                    }
                    break;
                case "finished":
                    System.out.println("Getting Linear Regression of current dataset");
                    double voltageToUnitsConstant = calibration.getVoltageToUnitsConstant();
                    System.out.println("Voltage Constant of given data set is: " + voltageToUnitsConstant);
                    hasFinished = true;
                    break;
                case "quit":
                    if(hasFinished){
                        System.out.println("Quitting Program...");
                        hasQuit = true;
                    }else{
                        System.out.print("You have not calculated a Voltage to Units Constant. Are you sure you want to quit? (y/n): ");
                        String confirmation = "";
                        confirmation = input.next();

                        if(confirmation.equals("y") || confirmation.equals("yes") || confirmation.equals("Yes")){
                            System.out.println("Quitting Program...");
                            hasQuit = true;
                        }else{
                            hasQuit = false;
                        }
                    }
                    break;

                default:
                    System.out.println("Command unrecognized...");
                    System.out.println(usageString);
                    break;
            }
        }

    }
}
