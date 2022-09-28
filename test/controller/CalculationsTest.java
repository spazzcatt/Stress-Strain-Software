package controller;

import org.junit.jupiter.api.Test;
import controller.Calculations.Units;

import static org.junit.jupiter.api.Assertions.*;

class CalculationsTest {

    private static final double DELTA = 0.001;

    @Test
    void convertLengthZero() {
        try{
            Calculations.convertLength(Units.ENGLISH, Units.ENGLISH, 0.0);
            fail();
        }catch(IllegalArgumentException e){
            //it should have failed
        }
    }

    @Test
    void convertLengthEnglishToMetric(){
        assertEquals(1894.84, Calculations.convertLength(Units.ENGLISH, Units.METRIC, 74.6), DELTA);
    }

    @Test
    void convertLengthEnglishToEnglish(){
        assertEquals(6.0, Calculations.convertLength(Units.ENGLISH, Units.ENGLISH, 6.0), DELTA);//added delta
    }

    @Test
    void convertLengthMetricToMetric(){
        assertEquals(3.0, Calculations.convertLength(Units.METRIC, Units.METRIC, 3.0), DELTA);//added delta
    }

    @Test
    void convertLengthMetricToEnglish(){
        assertEquals(0.11811, Calculations.convertLength(Units.METRIC, Units.ENGLISH, 3.0), DELTA);
    }

    @Test
    void convertForceZero() {
        try{
            Calculations.convertForce(Units.ENGLISH, Units.ENGLISH, 0.0);
            fail();
        }catch(IllegalArgumentException e){
            //it should have failed
        }
    }

    @Test
    void convertForceEnglishToMetric(){
        assertEquals(314.4893, Calculations.convertForce(Units.ENGLISH, Units.METRIC, 70.7), DELTA);
    }

    @Test
    void convertForceEnglishToEnglish(){
        assertEquals(31.0, Calculations.convertForce(Units.ENGLISH, Units.ENGLISH, 31.0), DELTA);//added delta
    }

    @Test
    void convertForceMetricToMetric(){
        assertEquals(70.3, Calculations.convertForce(Units.METRIC, Units.METRIC, 70.3), DELTA);//added delta
    }

    @Test
    void convertForceMetricToEnglish(){
        assertEquals(1.30389, Calculations.convertForce(Units.METRIC, Units.ENGLISH, 5.8), DELTA);
    }

    @Test
    void convertPressureZero() {
        try{
            Calculations.convertPressure(Units.ENGLISH, Units.ENGLISH, 0.0);
            fail();
        }catch(IllegalArgumentException e){
            //it should have failed
        }
    }

    @Test
    void convertPressureEnglishToMetric(){
        assertEquals(139.2741, Calculations.convertPressure(Units.ENGLISH, Units.METRIC, 20.2), DELTA);
    }

    @Test
    void convertPressureEnglishToEnglish(){
        assertEquals(27.5, Calculations.convertPressure(Units.ENGLISH, Units.ENGLISH, 27.5), DELTA);//added delta
    }

    @Test
    void convertPressureMetricToMetric(){
        assertEquals(139.2741, Calculations.convertPressure(Units.METRIC, Units.METRIC, 139.2741), DELTA);//added delta
    }

    @Test
    void convertPressureMetricToEnglish(){
        assertEquals(20.200, Calculations.convertPressure(Units.METRIC, Units.ENGLISH, 139.2741), DELTA);
    }

    @Test
    void calculateStrain() {
        assertEquals(0.788, Calculations.calculateStrain(0.394, 0.5), DELTA);
    }

    @Test
    void calculateStress() {
        assertEquals(121.72, Calculations.calculateStress(30.43, .25), DELTA);
    }

    @Test
    void calculateAreaRectangleWidthZero() {
        try{
            Calculations.calculateArea(0.0, 1.0);
            fail();
        }catch(IllegalArgumentException e){
            //it should have failed
        }
    }

    @Test
    void calculateAreaRectangleDepthZero() {
        try{
            Calculations.calculateArea(4.0, 0.0);
            fail();
        }catch(IllegalArgumentException e){
            //it should have failed
        }
    }

    @Test
    void calculateAreaRectangle(){
        assertEquals(7.5*3.192, Calculations.calculateArea(7.5, 3.192), DELTA);
    }//added delta

    @Test
    void calculateAreaCircleZero(){
        try{
            Calculations.calculateArea(0.0);
            fail();
        }catch(IllegalArgumentException e){
            //it should have failed
        }
    }

    @Test
    void calculateAreaCircle() {
        assertEquals(Math.PI * 3.254 * 3.254, Calculations.calculateArea(6.508), DELTA);
    }//added delta
}