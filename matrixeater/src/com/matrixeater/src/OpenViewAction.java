package com.matrixeater.src;

import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OpenViewAction extends AbstractAction {
    private final MainPanel.OpenViewGetter openViewGetter;
    RootWindow rootWindow;

    OpenViewAction(RootWindow rootWindow, final String name, final MainPanel.OpenViewGetter openViewGetter) {
        super(name);
        this.openViewGetter = openViewGetter;
        this.rootWindow = rootWindow;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final View view = openViewGetter.getView();
        if ((view.getTopLevelAncestor() == null) || !view.getTopLevelAncestor().isVisible()) {
            final FloatingWindow createFloatingWindow = rootWindow.createFloatingWindow(rootWindow.getLocation(),
                    new Dimension(640, 480), view);
            createFloatingWindow.getTopLevelAncestor().setVisible(true);
        }
    }
}
