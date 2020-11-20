package com.matrixeater.src;

import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTree;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class MainLayoutUgg {

    static TabWindow createMainLayout(MainPanel mainPanel) {
        final TabWindow leftHandTabWindow = new TabWindow(new DockingWindow[]{mainPanel.viewportControllerWindowView, mainPanel.toolView});
        leftHandTabWindow.setSelectedTab(0);
//		leftHandTabWindow.getWindowProperties().setCloseEnabled(false);
        final SplitWindow editingTab = new SplitWindow(false, 0.875f,
                new SplitWindow(true, 0.2f, leftHandTabWindow,
                        new SplitWindow(true, 0.8f,
                                new SplitWindow(false, new SplitWindow(true, mainPanel.frontView, mainPanel.bottomView),
                                        new SplitWindow(true, mainPanel.leftView, mainPanel.perspectiveView)),
                                mainPanel.creatorView)),
                mainPanel.timeSliderView);
        editingTab.getWindowProperties().setCloseEnabled(false);
        editingTab.getWindowProperties().setTitleProvider(arg0 -> "Edit");
        ImageIcon imageIcon;
        imageIcon = new ImageIcon(MainFrame.MAIN_PROGRAM_ICON.getScaledInstance(16, 16, Image.SCALE_FAST));

        final View mpqBrowserView = MPQBrowser.createMPQBrowser(mainPanel, imageIcon);

        final UnitEditorTree unitEditorTree = mainPanel.createUnitEditorTree();
        final TabWindow tabWindow = new TabWindow(new DockingWindow[]{
                new View("Unit Browser", imageIcon, new JScrollPane(unitEditorTree)), mpqBrowserView});
        tabWindow.setSelectedTab(0);
        final SplitWindow viewingTab = new SplitWindow(true, 0.8f,
                new SplitWindow(true, 0.8f, mainPanel.previewView, mainPanel.animationControllerView), tabWindow);
        viewingTab.getWindowProperties().setTitleProvider(arg0 -> "View");
        viewingTab.getWindowProperties().setCloseEnabled(false);

        final SplitWindow modelTab = new SplitWindow(true, 0.2f, mainPanel.modelDataView, mainPanel.modelComponentView);
        modelTab.getWindowProperties().setTitleProvider(arg0 -> "Model");

        final TabWindow startupTabWindow = new TabWindow(new DockingWindow[]{viewingTab, editingTab, modelTab});
        mainPanel.traverseAndFix(startupTabWindow);
        return startupTabWindow;
    }
}
