package controller;

public class Calculations {

    //used to ensure entered values for area are not zero
    private static final double EPSILON = 0.0001;

    public enum Units{
        ENGLISH,
        METRIC
    }

    /**
     * Converts length value from one unit system to a different unit system.
     * Note that in the english system length is measured in inches
     * and in the metric system length is measured in millimeters.
     * @param startingUnits the current unit system of the passed value
     * @param endingUnits the unit system wanted
     * @param value the current length value in the starting unit system
     * @return the length value converted to the ending unit system
     */
    public static double convertLength(Units startingUnits, Units endingUnits, double value){
        switch (startingUnits) {
            case ENGLISH:
            default :
                switch (endingUnits) {
                    case METRIC:
                        return value * 25.4;//inches to mm
                    case ENGLISH:
                    default:
                        return value;
                }
            case METRIC:
                switch (endingUnits) {
                    case METRIC:
                    default:
                        return value;
                    case ENGLISH:
                        return value / 25.4;//mm to inches
                }
        }
    }

    /**
     * Converts force value from one unit system to a different unit system.
     * Note that in the english system force is measured in pounds
     * and in the metric system force is measured in newtons.
     * @param startingUnits the current unit system of the passed value
     * @param endingUnits the unit system wanted
     * @param value the current force value in the starting unit system
     * @return the force value converted to the ending unit system
     */
    public static double convertForce(Units startingUnits, Units endingUnits, double value){
        switch (startingUnits) {
            case ENGLISH:
            default :
                switch (endingUnits) {
                    case METRIC:
                        return value * 4.4482216;//pounds to newtons
                    case ENGLISH:
                    default :
                        return value;
                }
            case METRIC:
                switch (endingUnits) {
                    case METRIC:
                    default :
                        return value;
                    case ENGLISH:
                        return value / 4.4482216;//newtons to pounds
                }
        }
    }

    /**
     * Converts pressure value from one unit system to a different unit system.
     * Note that in the english system pressure is measured in KSI (Kip/squared inches - kip = 1,000 pounds)
     * and in the metric system pressure is measured in megapascals (1,000,000 pascals - newtons/squared meters).
     * @param startingUnits the current unit system of the passed value
     * @param endingUnits the unit system wanted
     * @param value the current pressure value in the starting unit system
     * @return the pressure value converted to the ending unit system
     */
    public static double convertPressure(Units startingUnits, Units endingUnits, double value){
        switch (startingUnits) {
            case ENGLISH:
            default :
                switch (endingUnits) {
                    case METRIC:
                        return value * 6.89475728 ;//KSI to MPa
                    case ENGLISH:
                    default :
                        return value;
                }
            case METRIC:
                switch (endingUnits) {
                    case METRIC:
                    default :
                        return value;
                    case ENGLISH:
                        return value / 6.89475728 ;//MPa to KSI
                }
        }
    }

    /**
     * Calculate strain
     * @param elongation elongation measured
     * @param gaugeLength the gauge length
     * @return strain
     */
    public static double calculateStrain(double elongation, double gaugeLength){
        return elongation/gaugeLength;
    }

    /**
     * Calculate stress
     * @param force force measured
     * @param area area calculated
     * @return stress
     */
    public static double calculateStress(double force, double area){
        return force/area;
    }

    /**
     * Calculate the area of the cross section for a rectangular piece
     * @param width width of the cross section
     * @param depth depth of the cross section
     * @return area
     */
    public static double calculateArea(double width, double depth){
        if (width <= EPSILON){
            throw new IllegalArgumentException("Width cannot be zero");
        }else if(depth <= EPSILON){
            throw new IllegalArgumentException("Depth cannot be zero");
        }
        return width*depth;
    }

    /**
     * Calculate the area of the cross section for a circular piece
     * @param diameter diameter of the cross section
     * @return area
     */
    public static double calculateArea(double diameter){
        if (diameter <= EPSILON){
            throw new IllegalArgumentException("Diameter cannot be zero");
        }
        return Math.PI * (diameter/2) * (diameter/2);
    }
}
