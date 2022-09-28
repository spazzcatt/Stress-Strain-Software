package input.test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class Used for Testing Purposes to analyze voltage data output from TestStressStrainInput.java
 *
 */
public class DataAnalytics {
    private static ArrayList <Double> dataSet;
    private static double[] cleanedData;


    private static void readData(){
        dataSet = new ArrayList<Double>();
        try {
            File myObj = new File("outfile.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextDouble()) {
                double num = myReader.nextDouble();
                dataSet.add(num);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Reduces Average data
    private static void cleanDataAverage(double[] input, int cleanFactor) {
        cleanedData = new double[input.length / 100];
        for(int i = 0; i < cleanedData.length; i++){
            for(int j = 0; j < cleanFactor; j++){
                cleanedData[i] += input[i * cleanFactor + j];
            }
            cleanedData[i] /= cleanFactor;
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        readData();
        double [] array = new double[dataSet.size()];
        for(int i = 0; i < dataSet.size(); i++){
            array[i] = dataSet.get(i);
        }
        System.out.println("DATA ANALYSIS");
        System.out.println("***************************************");
        int numberOfDataPoints = 0;
        double total = 0;

        for(Double num : array){
            numberOfDataPoints++;
            total += num;
        }
        System.out.println("Number of Data Points:\t\t" + numberOfDataPoints);
        System.out.println("Total:\t\t\t\t\t\t" + total);
        double average = total / (double) numberOfDataPoints;
        System.out.println("Average of Entire Dataset:\t" + average);
        System.out.println("***************************************");
        System.out.println("Cleaning data...");
        cleanDataAverage(array, 100);
        Arrays.sort(cleanedData);
        System.out.println("Cleaned Data Median: " + cleanedData[cleanedData.length/2]);
        try {
            PrintWriter outputFile = new PrintWriter(new FileOutputStream("outfile_cleaned.txt"));
            for(double num : cleanedData){
                outputFile.println(num);
            }
            outputFile.close();
            System.out.println("File created successfully");
        }
        catch ( IOException e) {
        }
        System.out.println("Output Cleaned Data.");
        System.out.println("***************************************");

        System.out.println("Testing Voltage Conversion:");
        double maxReading = 0.0;
        double m = 1960.57419686093; // linear regression number from excel
        // Equation is y = mx + b
        double b = -14.6854707573448;
        double [] actualData = new double[cleanedData.length];
        total = 0.0;
        PrintWriter outputFile = new PrintWriter(new FileOutputStream("actualData.txt"));
        for(int i = 0; i < cleanedData.length; i++){
            double y = (m * cleanedData[i]) + b;
            actualData[i] = y;
            outputFile.println(y);
            total += y;
            if(y > maxReading){
                maxReading = y;
            }
        }
        System.out.println("Median: " + actualData[actualData.length/2]);
        System.out.println("Average lbs: " + total/actualData.length);
        outputFile.close();
        System.out.println("Max lbs: "+ maxReading);

        System.out.println("End of Test");

    }


}
