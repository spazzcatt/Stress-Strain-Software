package view;

import javax.swing.*;

/**
 * Creates a pop-up frame for user to select exporting options
 */
public class ExportWindow extends JFrame {

    //constants used for formatting
    private final int VERTICAL_BUFFER;
    private final int HORIZONTAL_BUFFER;

    private JPanel exportValuesWithDataPanel;
    private JButton export;
    private JButton cancel;
    private JCheckBox exportValuesCheckBox;

    public ExportWindow(){
        setTitle("Export");

        HORIZONTAL_BUFFER = MainWindow.HORIZONTAL_BUFFER;
        VERTICAL_BUFFER = MainWindow.VERTICAL_BUFFER;

        JPanel outerPanel =  new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBorder(BorderFactory.createEmptyBorder(VERTICAL_BUFFER,HORIZONTAL_BUFFER,VERTICAL_BUFFER,HORIZONTAL_BUFFER));

        setupExportValuesWithDataPanel();
        outerPanel.add(createExportDataPanel());
        outerPanel.add(exportValuesWithDataPanel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(createButtonPanel());
        outerPanel.add(Box.createVerticalGlue());

        add(outerPanel);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(false);
    }

    /*
     * Panel with export data check box
     */
    private JPanel createExportDataPanel(){
        JPanel exportDataPanel = new JPanel();
        exportDataPanel.setLayout(new BoxLayout(exportDataPanel, BoxLayout.X_AXIS));

        JLabel exportData = new JLabel("Export Data (.csv)");
        exportData.setToolTipText("csv files are opened in excel by default");

        exportDataPanel.add(exportData);
        exportDataPanel.add(Box.createHorizontalGlue());

        return exportDataPanel;
    }

    /*
     * Panel with check box that allows the user to export critical
     * and input values if checked
     */
    private void setupExportValuesWithDataPanel(){
        exportValuesWithDataPanel = new JPanel();
        exportValuesWithDataPanel.setLayout(new BoxLayout(exportValuesWithDataPanel, BoxLayout.X_AXIS));

        exportValuesCheckBox = new JCheckBox("Include associated input values");
        exportValuesCheckBox.setSelected(true);
        exportValuesCheckBox.setToolTipText("Includes unit system, width and depth, diameter, and gauge length");

        exportValuesWithDataPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        exportValuesWithDataPanel.add(exportValuesCheckBox);
        exportValuesWithDataPanel.add(Box.createHorizontalGlue());
    }

    /*
     * Export and Cancel options
     */
    private JPanel createButtonPanel(){
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(VERTICAL_BUFFER,HORIZONTAL_BUFFER,0,HORIZONTAL_BUFFER));

        export = new JButton("Export");
        cancel = new JButton("Cancel");

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(export);
        buttonPanel.add(Box.createHorizontalStrut(HORIZONTAL_BUFFER));
        buttonPanel.add(cancel);
        buttonPanel.add(Box.createHorizontalGlue());

        return buttonPanel;
    }

    //getters

    public JButton getExport() {
        return export;
    }

    public JButton getCancel() {
        return cancel;
    }

    public JCheckBox getExportValuesCheckBox() {
        return exportValuesCheckBox;
    }
}
