package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.cutpaste.ViewportTransferHandler;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.Vertex;
import com.hiveworkshop.wc3.util.ModelUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class NewModelPanel {
    static void newModel(MainPanel mainPanel) {
        final JPanel newModelPanel = new JPanel();
        newModelPanel.setLayout(new MigLayout());
        newModelPanel.add(new JLabel("Model Name: "), "cell 0 0");
        final JTextField newModelNameField = new JTextField("MrNew", 25);
        newModelPanel.add(newModelNameField, "cell 1 0");
        final JRadioButton createEmptyButton = new JRadioButton("Create Empty", true);
        newModelPanel.add(createEmptyButton, "cell 0 1");
        final JRadioButton createPlaneButton = new JRadioButton("Create Plane");
        newModelPanel.add(createPlaneButton, "cell 0 2");
        final JRadioButton createBoxButton = new JRadioButton("Create Box");
        newModelPanel.add(createBoxButton, "cell 0 3");
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(createBoxButton);
        buttonGroup.add(createPlaneButton);
        buttonGroup.add(createEmptyButton);

        final int userDialogResult = JOptionPane.showConfirmDialog(mainPanel, newModelPanel, "New Model",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (userDialogResult == JOptionPane.OK_OPTION) {
            final EditableModel mdl = new EditableModel(newModelNameField.getText());
            if (createBoxButton.isSelected()) {
                final JSpinner spinner = mainPanel.getjSpinner("Box: Choose Segments");
                if (spinner == null) return;
                com.hiveworkshop.wc3.util.ModelUtils.createBox(mdl, new Vertex(64, 64, 128), new Vertex(-64, -64, 0),
                        ((Number) spinner.getValue()).intValue());
            } else if (createPlaneButton.isSelected()) {
                final JSpinner spinner = mainPanel.getjSpinner("Plane: Choose Segments");
                if (spinner == null) return;
                ModelUtils.createGroundPlane(mdl, new Vertex(64, 64, 0), new Vertex(-64, -64, 0),
                        ((Number) spinner.getValue()).intValue());
            }
            final ModelPanel temp = createModelPanel(mainPanel, mdl, GlobalIcons.MDL_ICON, false);
            mainPanel.modelPanelUgg.loadModel(mainPanel, true, true, temp);
        }

    }

    static ModelPanel createModelPanel(MainPanel mainPanel, EditableModel model, ImageIcon icon, boolean specialBLPModel) {
        final ViewportTransferHandler viewportTransferHandler =  new ViewportTransferHandler();
        return new ModelPanel(mainPanel, model, mainPanel.prefs, mainPanel, mainPanel.selectionItemTypeGroup,
                mainPanel.selectionModeGroup, mainPanel.modelStructureChangeListener, mainPanel.mainLayoutUgg.editTab.coordDisplayListener, viewportTransferHandler,
                mainPanel.activeViewportWatcher, icon, specialBLPModel, mainPanel.textureExporter);
    }
}
