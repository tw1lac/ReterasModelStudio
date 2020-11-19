package com.matrixeater.src;

import javax.swing.*;

public class RedoMenuItem extends JMenuItem {

    private MainPanel mainPanel;

    public RedoMenuItem(MainPanel mainPanel, final String text) {
        super(text);
        this.mainPanel = mainPanel;
    }

    @Override
    public String getText() {
        if (funcEnabled()) {
            return "Redo " + ModelPanelUgg.currentModelPanel(mainPanel.currentModelPanel).getUndoManager().getRedoText();// +"
            // Ctrl+Y";
        } else {
            return "Can't redo";// +" Ctrl+Y";
        }
    }

    public boolean funcEnabled() {
        try {
            return !ModelPanelUgg.currentModelPanel(mainPanel.currentModelPanel).getUndoManager().isRedoListEmpty();
        } catch (final NullPointerException e) {
            return false;
        }
    }
}
