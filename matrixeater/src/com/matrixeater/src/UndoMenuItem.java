package com.matrixeater.src;

import javax.swing.*;

public class UndoMenuItem extends JMenuItem {
    private MainPanel mainPanel;

    public UndoMenuItem(MainPanel mainPanel, final String text) {
        super(text);
        this.mainPanel = mainPanel;
    }

    @Override
    public String getText() {
        if (funcEnabled()) {
            return "Undo " + mainPanel.currentModelPanel.getUndoManager().getUndoText();// +"
            // Ctrl+Z";
        } else {
            return "Can't undo";// +" Ctrl+Z";
        }
    }

    public boolean funcEnabled() {
        try {
            System.out.println(mainPanel);
            System.out.println(mainPanel.currentModelPanel);
            System.out.println(mainPanel.currentModelPanel.getUndoManager());
            return !mainPanel.currentModelPanel.getUndoManager().isUndoListEmpty();
        } catch (final NullPointerException e) {
            return false;
        }
    }

}
