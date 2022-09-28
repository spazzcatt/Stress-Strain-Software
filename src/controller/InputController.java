package controller;

import view.UserInputWindow;
import controller.Calculations.Units;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Sets up and controls the functions of the UserInputWindow
 */
public class InputController {

    private final UserInputWindow inputWindow;
    private final MainController mainController;

    //These are the values that will be updated before the program starts pulling data
    private String unitSystem;
    private double width;
    private double depth;
    private double diameter;
    private double gaugeLength;

    public InputController(MainController mainController){
        this.mainController = mainController;

        inputWindow = new UserInputWindow();

        inputWindow.getCancelButton().addActionListener(e -> {
            inputWindow.setVisible(false);
            updateInputFields();//revert back to previous inputs
        });

        inputWindow.getOkButton().addActionListener(e -> {
            inputWindow.setVisible(false);
            if(mainController.getUpdater() == null || mainController.getUpdater().getSeries().isEmpty()){
                mainController.getMainWindow().getStartButton().setEnabled(true);
            }
            pullInputValues(); //actually store the inputs
        });

        inputWindow.getCircularButton().addActionListener(e -> {
            inputWindow.getCircularInputPanel().setVisible(true);
            inputWindow.getRectangularInputPanel().setVisible(false);
            inputWindow.getDiameterInputField().setText("0.0");
        });

        inputWindow.getRectangularButton().addActionListener(e -> {
            inputWindow.getCircularInputPanel().setVisible(false);
            inputWindow.getRectangularInputPanel().setVisible(true);
            inputWindow.getDepthInputField().setText("0.0");
            inputWindow.getWidthInputField().setText("0.0");
        });

        inputWindow.getUnitSelectionBox().addActionListener(e -> onUnitSystemChange());

        inputWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                updateInputFields();
            }
        });
    }

    /*
     * Resets all the text fields back to their initial values
     * Resets all stored input values to 0.0
     */
    protected void clear(){
        inputWindow.getWidthInputField().setText("0.0");
        inputWindow.getDepthInputField().setText("0.0");
        inputWindow.getDiameterInputField().setText("0.0");

        width = 0.0;
        depth = 0.0;
        diameter = 0.0;
    }

    /*
     * Converts the gauge length to the appropriate units and updates all labels
     */
    protected void onUnitSystemChange(){
        //convert the values to the correct unit system
        double convertedValue;
        if (inputWindow.getUnitSelectionBox().getSelectedItem().equals("English")) {
            if(mainController.getUpdater() != null){
                mainController.getUpdater().updateGraphUnits(inputWindow.getCurrentUnitSystem(), Units.ENGLISH, mainController.getMainWindow().getSeries());
            }
            convertedValue = Calculations.convertLength(inputWindow.getCurrentUnitSystem(), Units.ENGLISH, getGaugeLengthInput());
            inputWindow.setCurrentUnitSystem(Units.ENGLISH);

            //update the graph labels
            mainController.getMainWindow().getChart().getXYPlot().getDomainAxis().setLabel("Strain (in/in)");
            mainController.getMainWindow().getChart().getXYPlot().getRangeAxis().setLabel("Stress (KSI)");

            //update the units displayed on input fields
            inputWindow.getGaugeLengthLabel().setText("Gauge Length (in): ");
            inputWindow.getDepthLabel().setText("Depth (in): ");
            inputWindow.getDiameterLabel().setText("Diameter (in): ");
            inputWindow.getWidthLabel().setText("Width (in): ");
        }else {
            if(mainController.getUpdater() != null) {
                mainController.getUpdater().updateGraphUnits(inputWindow.getCurrentUnitSystem(), Units.METRIC, mainController.getMainWindow().getSeries());
            }
            convertedValue = Calculations.convertLength(inputWindow.getCurrentUnitSystem(), Units.METRIC, getGaugeLengthInput());
            inputWindow.setCurrentUnitSystem(Units.METRIC);

            //update the graph labels
            mainController.getMainWindow().getChart().getXYPlot().getDomainAxis().setLabel("Strain (mm/mm)");
            mainController.getMainWindow().getChart().getXYPlot().getRangeAxis().setLabel("Stress (MPa)");

            //update the units displayed on input fields
            inputWindow.getGaugeLengthLabel().setText("Gauge Length (mm): ");
            inputWindow.getDepthLabel().setText("Depth (mm): ");
            inputWindow.getDiameterLabel().setText("Diameter (mm): ");
            inputWindow.getWidthLabel().setText("Width (mm): ");
        }
        inputWindow.getGaugeLengthInputField().setText(String.format("%.10f", convertedValue));
    }

    /**
     * Determines if the cross section rectangular radio button is selected,
     * if not the circular is selected
     * @return if cross section is rectangular
     */
    public boolean isRectangularSelected() {
        return inputWindow.getRectangularButton().isSelected();
    }

    /**
     * Returns if the user has input values for the appropriate cross section type
     * @return if values have been input
     */
    public boolean haveInputs(){
        if (isRectangularSelected()){
            return getWidthInput() != 0.0 && getDepthInput() != 0.0;
        }else{
            return getDiameterInput() != 0.0;
        }
    }

    /*
     * Stores the input values locally from the input window
     */
    protected void pullInputValues(){
        width = getWidthInput();
        depth = getDepthInput();
        diameter = getDiameterInput();
        gaugeLength = getGaugeLengthInput();
        unitSystem = (String) inputWindow.getUnitSelectionBox().getSelectedItem();
    }

    /*
     * Updates the input fields to be what the stored values are
     * This will likely be reverting the fields back to what they were previously after the cancel is selected
     */
    private void updateInputFields(){
        inputWindow.getWidthInputField().setText(String.valueOf(width));
        inputWindow.getDepthInputField().setText(String.valueOf(depth));
        inputWindow.getDiameterInputField().setText(String.valueOf(diameter));
        inputWindow.getUnitSelectionBox().setSelectedItem(unitSystem);
    }

    /*
     * Gets the width that was input from the user
     */
    private double getWidthInput(){
        return Double.parseDouble(inputWindow.getWidthInputField().getText().trim());
    }

    /*
     * Gets the depth that was input from the user
     */
    private double getDepthInput(){
        return Double.parseDouble(inputWindow.getDepthInputField().getText().trim());
    }

    /*
     * Gets the diameter that was input from the user
     */
    private double getDiameterInput(){
        return Double.parseDouble(inputWindow.getDiameterInputField().getText().trim());
    }

    /*
     * Gets the gauge length that was input from the user (more likely this is still just the default value)
     */
    private double getGaugeLengthInput(){
        return Double.parseDouble(inputWindow.getGaugeLengthInputField().getText().trim());
    }

    //getters and setters

    public double getWidth() {
        return width;
    }

    public double getDepth() {
        return depth;
    }

    public double getDiameter() {
        return diameter;
    }

    public double getGaugeLength(){
        return gaugeLength;
    }

    public String getUnitSystem() { return unitSystem; }

    public UserInputWindow getInputWindow(){
        return inputWindow;
    }
}
