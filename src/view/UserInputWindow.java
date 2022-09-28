package view;

import javax.swing.*;
import controller.Calculations.Units;

/**
 * Creates a pop-up frame for user to input values used in calculations
 */
public class UserInputWindow extends JFrame {

    //constants used for formatting
    private final int VERTICAL_BUFFER;
    private final int HORIZONTAL_BUFFER;

    private final String[] MEASUREMENTS = {"English", "Metric"};

    private JComboBox<String> unitSelectionBox;
    private Units currentUnitSystem;
    private JPanel rectangularInputPanel;
    private JPanel circularInputPanel;
    private JTextField gaugeLengthInputField;
    private JTextField diameterInputField;
    private JTextField widthInputField;
    private JTextField depthInputField;
    private JButton ok;
    private JButton cancel;
    private JRadioButton circular;
    private JRadioButton rectangular;
    private  JLabel gaugeLengthLabel;
    private JLabel diameterLabel;
    private JLabel widthLabel;
    private JLabel depthLabel;

    public UserInputWindow(){
        setTitle("Measurements Input");

        HORIZONTAL_BUFFER = MainWindow.HORIZONTAL_BUFFER;
        VERTICAL_BUFFER = MainWindow.VERTICAL_BUFFER;

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

        outerPanel.add(createUnitSelectionPanel());
        outerPanel.add(createCrossSectionInputPanel());
        outerPanel.add(createGaugeLengthInputPanel());
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(createOptionsPanel());

        setResizable(false);
        add(outerPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(false);
    }

    /*
     * Has the radio button for choosing the appropriate type for the
     * cross section of the specimen and then the associated panel for input
     */
    private JPanel createCrossSectionInputPanel(){
        JPanel crossSectionInputPanel = new JPanel();
        crossSectionInputPanel.setLayout(new BoxLayout(crossSectionInputPanel, BoxLayout.Y_AXIS));
        crossSectionInputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Cross Section Type"), BorderFactory.createEmptyBorder(VERTICAL_BUFFER,HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER)));
        setupRectangularInputPanel();
        setupCircularInputPanel();

        crossSectionInputPanel.add(createRadioButtonPanel());
        crossSectionInputPanel.add(Box.createVerticalStrut(VERTICAL_BUFFER));
        crossSectionInputPanel.add(Box.createVerticalGlue());
        crossSectionInputPanel.add(rectangularInputPanel);
        crossSectionInputPanel.add(circularInputPanel);
        circularInputPanel.setVisible(false);//because we have rectangular selected as default always

        return crossSectionInputPanel;
    }

    /*
     * Panel for formatting the radio button locations
     */
    private JPanel createRadioButtonPanel(){
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));

        ButtonGroup crossSectionButtons = new ButtonGroup();
        circular = new JRadioButton("Circular");
        rectangular = new JRadioButton("Rectangular");
        crossSectionButtons.add(rectangular);
        crossSectionButtons.add(circular);

        crossSectionButtons.setSelected(rectangular.getModel(), true);

        radioButtonPanel.add(rectangular);
        radioButtonPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        radioButtonPanel.add(circular);
        radioButtonPanel.add(Box.createHorizontalGlue());

        return radioButtonPanel;
    }

    /*
     * Input for a rectangular cross section
     */
    private void setupRectangularInputPanel(){
        rectangularInputPanel = new JPanel();
        rectangularInputPanel.setLayout(new BoxLayout(rectangularInputPanel, BoxLayout.X_AXIS));

        widthLabel = new JLabel("Width: ");
        depthLabel = new JLabel("Depth: ");

        widthInputField = new JTextField("0.0", 10);
        depthInputField = new JTextField("0.0", 10);

        rectangularInputPanel.add(widthLabel);
        rectangularInputPanel.add(widthInputField);
        rectangularInputPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        rectangularInputPanel.add(depthLabel);
        rectangularInputPanel.add(depthInputField);
        rectangularInputPanel.add(Box.createHorizontalGlue());
    }

    /*
     * Input for a circular cross section
     */
    private void setupCircularInputPanel(){
        circularInputPanel = new JPanel();
        circularInputPanel.setLayout(new BoxLayout(circularInputPanel, BoxLayout.X_AXIS));

        diameterLabel = new JLabel("Diameter: ");

        diameterInputField = new JTextField("0.0",10);

        circularInputPanel.add(diameterLabel);
        circularInputPanel.add(diameterInputField);
        circularInputPanel.add(Box.createHorizontalGlue());
    }

    /*
     * Initial gauge length input, populated from default value
     */
    private JPanel createGaugeLengthInputPanel(){
        JPanel gaugeLengthInputPanel = new JPanel();
        gaugeLengthInputPanel.setLayout(new BoxLayout(gaugeLengthInputPanel, BoxLayout.X_AXIS));
        gaugeLengthInputPanel.setBorder(BorderFactory.createEmptyBorder(VERTICAL_BUFFER, HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER));

        gaugeLengthLabel = new JLabel("Gauge Length:");
        gaugeLengthInputField = new JTextField("", 10);
        gaugeLengthInputField.setToolTipText("Actual gauge length used in calculations. It is unlikely that this needs to be changed");

        gaugeLengthInputPanel.add(gaugeLengthLabel);
        gaugeLengthInputPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        gaugeLengthInputPanel.add(gaugeLengthInputField);
        gaugeLengthInputPanel.add(Box.createHorizontalGlue());

        return gaugeLengthInputPanel;
    }

    /*
     * Ok and Cancel options
     */
    private JPanel createOptionsPanel(){
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(VERTICAL_BUFFER,HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER));

        ok = new JButton("OK");
        cancel = new JButton("Cancel");

        optionsPanel.add(Box.createHorizontalGlue());
        optionsPanel.add(ok);
        optionsPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        optionsPanel.add(cancel);

        return optionsPanel;
    }

    /*
     * Unit system selection options
     */
    private JPanel createUnitSelectionPanel(){
        JPanel unitSelectionPanel = new JPanel();
        unitSelectionPanel.setLayout(new BoxLayout(unitSelectionPanel, BoxLayout.X_AXIS));
        unitSelectionPanel.setBorder(BorderFactory.createEmptyBorder(VERTICAL_BUFFER, HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER));

        JLabel unitSelectionLabel = new JLabel("Unit System: ");
        unitSelectionBox = new JComboBox<>(MEASUREMENTS);
        unitSelectionBox.setToolTipText("Current unit system");

        unitSelectionPanel.add(unitSelectionLabel);
        unitSelectionPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        unitSelectionPanel.add(unitSelectionBox);
        unitSelectionPanel.add(Box.createHorizontalGlue());

        return unitSelectionPanel;
    }

    //Getters
    public JRadioButton getCircularButton(){
        return circular;
    }

    public JRadioButton getRectangularButton(){
        return rectangular;
    }

    public JPanel getRectangularInputPanel(){
        return rectangularInputPanel;
    }

    public JPanel getCircularInputPanel(){
        return circularInputPanel;
    }

    public JTextField getGaugeLengthInputField(){ return gaugeLengthInputField;}

    public JTextField getWidthInputField(){ return widthInputField;}

    public JTextField getDiameterInputField(){ return diameterInputField;}

    public JTextField getDepthInputField(){ return depthInputField;}

    public JComboBox<String> getUnitSelectionBox(){ return unitSelectionBox; }

    public JButton getOkButton(){
        return ok;
    }

    public JButton getCancelButton(){
        return cancel;
    }

    public JLabel getGaugeLengthLabel(){
        return gaugeLengthLabel;
    }

    public JLabel getDiameterLabel(){
        return diameterLabel;
    }

    public JLabel getWidthLabel(){
        return widthLabel;
    }

    public JLabel getDepthLabel() {
        return depthLabel;
    }

    public Units getCurrentUnitSystem(){
        return currentUnitSystem;
    }

    public void setCurrentUnitSystem(Units currentUnitSystem){
        this.currentUnitSystem = currentUnitSystem;
    }
}

