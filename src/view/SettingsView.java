package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.SpringLayout;
import controller.Calculations.Units;
import model.AITask;

/**
 * Creates a pop-up frame which displays settings options
 */
public class SettingsView extends JFrame{

    //constants used for formatting
    private final int VERTICAL_BUFFER;
    private final int HORIZONTAL_BUFFER;

    private final String[] MEASUREMENTS = {"English", "Metric"};

    private JComboBox<String> defaultUnitSelectionBox;
    private Units currentUnitSystem;
    private JTextField gaugeLengthField;
    private JButton saveButton;
    private JButton closeButton;
    private JLabel gaugeLengthLabel;
    private JComboBox <Integer> forceChannelComboBox;
    private JComboBox <Integer> elongationChannelComboBox;
    private JComboBox <String> forceModeComboBox;
    private JComboBox <String> elongationModeComboBox;
    private final String [] MODE_OPTIONS = {"RSE", "Differential"};
    private final Integer [] CHANNEL_OPTIONS = {0,1,2,3,4,5,6,7};  //Starts with maximum number of channels
    private JTextField forceVoltage2UnitConstant;
    private JTextField elongationVoltage2UnitConstant;

    public SettingsView (Scanner userInput) {
        setTitle("Settings");

        HORIZONTAL_BUFFER = MainWindow.HORIZONTAL_BUFFER;
        VERTICAL_BUFFER = MainWindow.VERTICAL_BUFFER;

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

        outerPanel.add(Box.createVerticalStrut(VERTICAL_BUFFER));
        outerPanel.add(createMachineSettingsPanel());
        outerPanel.add(Box.createVerticalStrut(VERTICAL_BUFFER));

        outerPanel.add(createInputSettingsPanel(userInput));
        outerPanel.add(createSaveButtonPanel());
        outerPanel.add(Box.createVerticalGlue());

        add(outerPanel);

        setResizable(false);
        setVisible(false);
        pack();
        setLocationRelativeTo(null);
    }

    /*
     * Creates fields that allow the user to determine the settings
     * that will be used throughout the test. Those settings include sample rate, units, and gauge length
     * Takes a scanner to aid in persisting the settings throughout
     */
    private JPanel createInputSettingsPanel(Scanner input){
        JPanel northPanel = new JPanel(new SpringLayout());
        JLabel unitsSelectionLabel = new JLabel("Unit System:");
        gaugeLengthLabel = new JLabel("");
        boolean readSucceeded = false;
        if(input != null)  { //if user input values on settings window, they remain through closing and reopening
            try{
                String selectedUnitType = input.next();
                if (selectedUnitType.equals("English")){
                    currentUnitSystem = Units.ENGLISH;
                    //update the units displayed on input fields
                    gaugeLengthLabel.setText("Gauge Length (in):");
                }else{
                    currentUnitSystem = Units.METRIC;
                    //update the units displayed on input fields
                    gaugeLengthLabel.setText("Gauge Length (mm):");
                }

                double gaugeLength = input.nextDouble();
                gaugeLengthField = new JTextField(String.valueOf(gaugeLength), 12);
                gaugeLengthField.setToolTipText("Preferred default value. Saved upon close");

                defaultUnitSelectionBox = new JComboBox<>(MEASUREMENTS);
                defaultUnitSelectionBox.setToolTipText("Default unit system, not used in calculations. Saved upon close");
                defaultUnitSelectionBox.setSelectedItem(selectedUnitType);

                int forceChannel = input.nextInt();
                forceChannelComboBox.setSelectedItem(forceChannel);
                String forceMode = input.next();
                forceModeComboBox.setSelectedItem(forceMode);
                double forceConstant = input.nextDouble();
                forceVoltage2UnitConstant.setText(String.valueOf(forceConstant));

                int elongationChannel = input.nextInt();
                elongationChannelComboBox.setSelectedItem(elongationChannel);
                String elongationMode = input.next();
                elongationModeComboBox.setSelectedItem(elongationMode);
                double elongationConstant = input.nextDouble();
                elongationVoltage2UnitConstant.setText(String.valueOf(elongationConstant));

                readSucceeded = true;
            }
            catch( NoSuchElementException | IllegalStateException e) {
                //do nothing
            }
        }

        if(!readSucceeded){ //if no values input default values show
            gaugeLengthLabel.setText("Gauge Length (in):");
            gaugeLengthField = new JTextField("0.5");
            defaultUnitSelectionBox = new JComboBox<>(MEASUREMENTS);
            currentUnitSystem = Units.ENGLISH;
            forceVoltage2UnitConstant.setText("1960.574197");
            elongationVoltage2UnitConstant.setText("0.041814743");
            forceModeComboBox.setSelectedItem("Differential");
            forceChannelComboBox.setSelectedItem(3);
            elongationModeComboBox.setSelectedItem("RSE");
            elongationChannelComboBox.setSelectedItem(1);
        }

        northPanel.add(unitsSelectionLabel);
        unitsSelectionLabel.setLabelFor(defaultUnitSelectionBox);
        northPanel.add(defaultUnitSelectionBox);

        northPanel.add(gaugeLengthLabel);

        gaugeLengthLabel.setLabelFor(gaugeLengthField);
        northPanel.add(gaugeLengthField);
        SpringUtilities.makeCompactGrid(northPanel,2,2,HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER,VERTICAL_BUFFER);

        return northPanel;
    }

    /*
     * Creates a save button that will allow a user to
     * save the settings that were decided on and will persist throughout the test
     */
    private JPanel createSaveButtonPanel(){
        JPanel southPanel = new JPanel();
        saveButton = new JButton("Save");
        closeButton = new JButton("Close");
        southPanel.add(saveButton);
        southPanel.add(closeButton);
        return southPanel;
    }

    private JPanel createMachineSettingsPanel(){
        JPanel machineSettingsPanel = new JPanel( new BorderLayout());
        JPanel forceMachineSettings = new JPanel(new SpringLayout());
        JPanel elongationMachineSettings = new JPanel(new SpringLayout());
        JLabel modeLabel = new JLabel("Mode: ");
        JLabel modeLabel2 = new JLabel("Mode: ");
        JLabel channelLabel = new JLabel("Channel: ");
        JLabel channelLabel2 = new JLabel("Channel: ");
        JLabel voltageConstantLabel = new JLabel("Voltage to Units Constant: ");
        JLabel voltageConstantLabel2 = new JLabel("Voltage to Units Constant: ");

        forceModeComboBox = new JComboBox<>(MODE_OPTIONS);
        elongationModeComboBox = new JComboBox<>(MODE_OPTIONS);
        forceChannelComboBox = new JComboBox<>(CHANNEL_OPTIONS);
        elongationChannelComboBox = new JComboBox<>(CHANNEL_OPTIONS);
        forceVoltage2UnitConstant = new JTextField();
        elongationVoltage2UnitConstant = new JTextField();

        //Force Machine Settings
        forceMachineSettings.add(modeLabel);
        modeLabel.setLabelFor(forceModeComboBox);
        forceMachineSettings.add(forceModeComboBox);

        forceMachineSettings.add(channelLabel);
        channelLabel.setLabelFor(forceChannelComboBox);
        forceMachineSettings.add(forceChannelComboBox);

        forceMachineSettings.add(voltageConstantLabel);
        voltageConstantLabel.setLabelFor(forceVoltage2UnitConstant);
        forceMachineSettings.add(forceVoltage2UnitConstant);

        SpringUtilities.makeCompactGrid(forceMachineSettings,3,2, HORIZONTAL_BUFFER, VERTICAL_BUFFER, HORIZONTAL_BUFFER, VERTICAL_BUFFER);


        //Elongation Machine Settings
        elongationMachineSettings.add(modeLabel2);
        modeLabel2.setLabelFor(elongationModeComboBox);
        elongationMachineSettings.add(elongationModeComboBox);

        elongationMachineSettings.add(channelLabel2);
        channelLabel2.setLabelFor(elongationChannelComboBox);
        elongationMachineSettings.add(elongationChannelComboBox);

        elongationMachineSettings.add(voltageConstantLabel2);
        voltageConstantLabel2.setLabelFor(elongationVoltage2UnitConstant);
        elongationMachineSettings.add(elongationVoltage2UnitConstant);
        SpringUtilities.makeCompactGrid(elongationMachineSettings,3,2, HORIZONTAL_BUFFER, VERTICAL_BUFFER, HORIZONTAL_BUFFER, VERTICAL_BUFFER);

        forceMachineSettings.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Force Machine Settings"), BorderFactory.createEmptyBorder(VERTICAL_BUFFER,HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER)));
        elongationMachineSettings.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Elongation Machine Settings"), BorderFactory.createEmptyBorder(VERTICAL_BUFFER,HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER)));


        machineSettingsPanel.add(forceMachineSettings,BorderLayout.NORTH);
        machineSettingsPanel.add(elongationMachineSettings, BorderLayout.SOUTH);
        return machineSettingsPanel;
    }

    public AITask.Mode getForceMode(){
        if(forceModeComboBox.getSelectedItem().equals("RSE")){
            return  AITask.Mode.RSE;
        } else  {
            return AITask.Mode.DIFFERENTIAL;
        }
    }

    public AITask.Mode getElongationMode(){
        if(elongationModeComboBox.getSelectedItem().equals("RSE")){
            return  AITask.Mode.RSE;
        } else  {
            return AITask.Mode.DIFFERENTIAL;
        }
    }

    public ArrayList<String> getInput(){ //for getting all the inputs from the fields before closing
        return null;
    }

    public JButton getSaveButton(){ return saveButton;  }

    public JButton getCloseButton(){
        return closeButton;
    }

    public JComboBox<String> getDefaultUnitSelectionBox(){return defaultUnitSelectionBox; }

    public JTextField getDefaultGaugeLengthField(){ return gaugeLengthField; }

    public double getDefaultGaugeLength(){ return Double.parseDouble(gaugeLengthField.getText().trim()); }

    public String getDefaultUnits(){
        return (String) defaultUnitSelectionBox.getSelectedItem();
    }

    public JLabel getGaugeLengthLabel(){
        return gaugeLengthLabel;
    }

    public Units getCurrentUnitSystem(){ return currentUnitSystem; }

    public void setCurrentUnitSystem(Units currentUnitSystem){ this.currentUnitSystem = currentUnitSystem; }

    public JComboBox<Integer> getForceChannelComboBox() { return forceChannelComboBox; }

    public JComboBox<Integer> getElongationChannelComboBox() { return elongationChannelComboBox; }

    public JComboBox<String> getForceModeComboBox() { return forceModeComboBox; }

    public JComboBox<String> getElongationModeComboBox() { return elongationModeComboBox; }

    public double getForceVoltage2UnitConstant() { return Double.parseDouble(forceVoltage2UnitConstant.getText().trim()); }

    public double getElongationVoltage2UnitConstant() { return Double.parseDouble(elongationVoltage2UnitConstant.getText().trim()); }

    public JTextField getForceVoltage2UnitConstantField(){
        return forceVoltage2UnitConstant;
    }

    public JTextField getElongationVoltage2UnitConstantField(){
        return elongationVoltage2UnitConstant;
    }

    public int getForceChannel(){
        return (Integer) forceChannelComboBox.getSelectedItem();
    }

    public int getElongationChannel(){
        return (int) elongationChannelComboBox.getSelectedItem();
    }
}
