package controller;

import controller.Calculations.Units;
import kirkwood.nidaq.access.NiDaqException;
import view.MainWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Root of the entire program, controls all the main window functionality and some interactions between windows
 * NOTE: This should be the only main method ever actually run
 */
public class MainController {

    private final MainWindow mainWindow;
    private final InputController inputController;
    private final SettingsController settingsController;
    private final ExportController exportController;
    private boolean isStart = true;
    private GraphUpdater updater;

    //These are the values we are only going to populate when we start pulling data
    private String unitSystem;
    private double width;
    private double depth;
    private double diameter;
    private double gaugeLength;

    public MainController(){
        setLookAndFeel();
        mainWindow = new MainWindow();
        inputController = new InputController(this);
        settingsController = new SettingsController(inputController, this);
        exportController = new ExportController(mainWindow.getSeries(), this, inputController);

        mainWindow.getInput().addActionListener(e ->inputController.getInputWindow().setVisible(true));
        mainWindow.getSettings().addActionListener(e -> settingsController.getSettingsWindow().setVisible(true));

        mainWindow.getReset().addActionListener(e -> {
            if(exportController.isUnsaved){
                if(warnUnsavedData() == JOptionPane.NO_OPTION){
                    reset();
                }
            }else{
                int option = JOptionPane.showOptionDialog(null, "Do you want to reset?", "Reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    reset();
                }
            }
        });

        mainWindow.getExport().addActionListener(e -> exportController.getExportWindow().setVisible(true));
        mainWindow.getInputButton().addActionListener(e -> inputController.getInputWindow().setVisible(true));
        mainWindow.getExportButton().addActionListener(e -> exportController.getExportWindow().setVisible(true));

        mainWindow.getExit().addActionListener(e -> {
            if(!isStart){
                exitMidPull();
            }else if(exportController.isUnsaved){
                if(warnUnsavedData() == JOptionPane.NO_OPTION){
                    disposeAll();
                }
            }else{
                disposeAll();
            }
        });

        mainWindow.getClearButton().addActionListener(e -> {
            if(exportController.isUnsaved) {
                if (warnUnsavedData() == JOptionPane.NO_OPTION) {
                    clearGraph();
                }
            }else{
                clearGraph();
            }
        });

        mainWindow.getStartButton().addActionListener(e -> {
            if(isStart){
                try {
                    if (updater == null) {
                        updater = new GraphUpdater(mainWindow.getSeries(), this, settingsController);
                        updater.start();
                    }
                    //if no input values at all give a warning
                    if (!inputController.haveInputs()) {
                        JOptionPane.showMessageDialog(null, "No cross section inputs given!", "Input Warning", JOptionPane.ERROR_MESSAGE);
                        //if the input values are from the previous round
                    } else if (areInputsFromPreviousRun()) {
                        int option = JOptionPane.showOptionDialog(null, "Input values have not been changed.\nDo you want to update them?\n", "Input Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Update", "Continue"}, JOptionPane.YES_OPTION);
                        if (option == JOptionPane.NO_OPTION) {
                            getInputValues();
                            startDataCollection();
                        } else {
                            inputController.getInputWindow().setVisible(true);
                        }
                    } else {
                        getInputValues();
                        startDataCollection();
                    }
                }catch (NiDaqException exception){
                    updater = null;
                    JOptionPane.showMessageDialog(null, "Failed to start data collection.\nCheck that National Instruments device is connected via USB port, then restart this program.", "Device Connected?", JOptionPane.ERROR_MESSAGE);
                    disposeAll();
                }
            }else {
                stopDataCollection();
            }
        });

        //disposes of all windows when the main window is closed
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(!isStart){
                    exitMidPull();
                }else if(exportController.isUnsaved){
                    if(warnUnsavedData() == JOptionPane.NO_OPTION){
                        disposeAll();
                    }
                }else {
                    disposeAll();
                }
            }
        });
    }

    /*
     * Set the look and feel for all the windows
     */
    private void setLookAndFeel(){
        try {
            //Set system look and feel
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*
     * Disposes of all windows and terminates any current graphing to end the program
     */
    private void disposeAll(){
        inputController.getInputWindow().dispose();
        settingsController.getSettingsWindow().dispose();
        exportController.getExportWindow().dispose();
        mainWindow.dispose();
        if(updater != null){
            updater.terminate();
        }
    }

    /*
     * Starts collecting data
     */
    private void startDataCollection(){
        mainWindow.getStartButton().setText("Stop");
        mainWindow.getClearButton().setEnabled(false);
        mainWindow.getInputButton().setEnabled(false);
        //do not allow any of these while data is being pulled
        mainWindow.getReset().setEnabled(false);
        mainWindow.getSettings().setEnabled(false);
        mainWindow.getInput().setEnabled(false);
        updater.updateZeros();
        updater.pause();
        updater.collect();
        isStart = false;
        exportController.isUnsaved = true;
    }

    /*
     * Stops collecting data
     */
    private void stopDataCollection(){
        mainWindow.getStartButton().setText("Start");
        mainWindow.getClearButton().setEnabled(true);
        mainWindow.getStartButton().setEnabled(false);
        //enable these once we stop pulling data
        mainWindow.getReset().setEnabled(true);
        mainWindow.getInput().setEnabled(true);
        mainWindow.getExport().setEnabled(true);
        mainWindow.getInputButton().setEnabled(true);
        mainWindow.getExportButton().setEnabled(true);
        setInputFieldsEnabled(false);
        isStart = true;
        if(updater != null) {
            updater.pause();
        }
    }

    /*
     * Clear the graph and reset buttons appropriately
     */
    private void clearGraph(){
        mainWindow.getSeries().clear();
        mainWindow.getStartButton().setEnabled(true);
        mainWindow.getClearButton().setEnabled(false);
        mainWindow.getInputButton().setEnabled(true);
        mainWindow.getExportButton().setEnabled(false);
        mainWindow.getExport().setEnabled(false);
        mainWindow.getSettings().setEnabled(true);
        setInputFieldsEnabled(true);
        exportController.isUnsaved = false;
    }

    /*
     * Resets the graph, data, and input values
     */
    private void reset(){
        stopDataCollection();
        clearGraph();
        diameter = 0.0;
        depth = 0.0;
        width = 0.0;
        unitSystem = "";
        //clear the inputs convert back to the default units from the settings and make text fields re-editable
        inputController.clear();
        setInputFieldsEnabled(true);
        mainWindow.getStartButton().setEnabled(false);
        settingsController.updateUnitsSystem();
        inputController.onUnitSystemChange();
    }

    /*
     * Update input fields from editable to not editable and vice versa
     */
    private void setInputFieldsEnabled(boolean b){
        if(b){
            inputController.getInputWindow().getGaugeLengthInputField().setEditable(true);
            inputController.getInputWindow().getWidthInputField().setEditable(true);
            inputController.getInputWindow().getDepthInputField().setEditable(true);
            inputController.getInputWindow().getDiameterInputField().setEditable(true);
            inputController.getInputWindow().getCircularButton().setEnabled(true);
            inputController.getInputWindow().getRectangularButton().setEnabled(true);
        }else{
            inputController.getInputWindow().getGaugeLengthInputField().setEditable(false);
            inputController.getInputWindow().getWidthInputField().setEditable(false);
            inputController.getInputWindow().getDepthInputField().setEditable(false);
            inputController.getInputWindow().getDiameterInputField().setEditable(false);
            inputController.getInputWindow().getCircularButton().setEnabled(false);
            inputController.getInputWindow().getRectangularButton().setEnabled(false);
        }

    }

    /*
     * Confirm exit of program while actively pulling data
     */
    private void exitMidPull() {
        //display yes/no message box
        int exitMessage = JOptionPane.showOptionDialog(null,"You are currently pulling data. \nAre you sure you want to close the program?","Confirm Close",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null, new Object[] {"Yes", "No"},JOptionPane.YES_OPTION);
        //if yes call close method
        if(exitMessage == JOptionPane.YES_OPTION) {
            stopDataCollection();
            disposeAll();
        }
    }

    /*
     * Checks if the values input from the user are the same as the previous time the data was run
     */
    private boolean areInputsFromPreviousRun(){
        if(inputController.isRectangularSelected()){
            return width == inputController.getWidth() && depth == inputController.getDepth();
        }else{
            return diameter == inputController.getDiameter();
        }
    }

    /*
     * Option box warning the user of unsaved data
     */
    private int warnUnsavedData(){
        int result = JOptionPane.showOptionDialog(null,"Export your data?","Unsaved Changes" ,JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null, new Object[] {"Export", "Continue"},JOptionPane.YES_OPTION);
        if(result == JOptionPane.YES_OPTION){
            exportController.getExportWindow().setVisible(true);
        }
        return result;
    }

    /*
     * Get the input values that we want to use in our calculations
     */
    private void getInputValues(){
        width = inputController.getWidth();
        depth = inputController.getDepth();
        diameter = inputController.getDiameter();
        gaugeLength = inputController.getGaugeLength();
        unitSystem = inputController.getUnitSystem();
    }

    public Units stringToUnits(String unitSystem){
        if(unitSystem.equals("English")){
            return Units.ENGLISH;
        }else{
            return Units.METRIC;
        }
    }

    //getters
    public MainWindow getMainWindow() {
        return mainWindow;
    }

    public double getGaugeLength() {
        return gaugeLength;
    }

    public double findArea(){
        if(inputController.isRectangularSelected()){
            return Calculations.calculateArea(width, depth);
        }else{
            return Calculations.calculateArea(diameter);
        }
    }
    public String getUnitSystem() { return unitSystem; }

    public double getWidth() { return width; }

    public double getDepth() { return depth; }

    public double getDiameter() { return diameter; }

    public GraphUpdater getUpdater() { return updater; }

    public static void main(String[] args){
        try{
            Thread.sleep(1000);
        }
        catch(InterruptedException e){ }
        new MainController();
    }
}
