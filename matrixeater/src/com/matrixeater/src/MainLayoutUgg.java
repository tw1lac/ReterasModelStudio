package com.matrixeater.src;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class MainLayoutUgg {
    final static boolean VERTICAL = false;
    final static boolean HORIZONTAL = true;

    final MainPanel mainPanel;
    EditTab editTab;

    TabWindow startupTabWindow;

    View previewView;
    View animationControllerView;

    View modelDataView;
    View modelComponentView;

//    View viewportControllerWindowView;

    public MainLayoutUgg(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        startupTabWindow = createMainLayout(mainPanel);
    }

    TabWindow createMainLayout(MainPanel mainPanel) {

        editTab = new EditTab(mainPanel);
        final SplitWindow editingTab = editTab.createEditTab();

        final SplitWindow viewingTab = CreateViewTab(mainPanel);

        final SplitWindow modelTab = createModelTab();

        final TabWindow startupTabWindow = new TabWindow(new DockingWindow[]{viewingTab, editingTab, modelTab});

        return startupTabWindow;
    }

    private SplitWindow CreateViewTab(MainPanel mainPanel) {
        ImageIcon imageIcon  = new ImageIcon(MainFrame.MAIN_PROGRAM_ICON.getScaledInstance(16, 16, Image.SCALE_FAST));
        final View mpqBrowserView = MPQBrowser.createMPQBrowser(mainPanel, imageIcon);
        final View unitBrowserView = UnitBrowser.createUnitBrowser(mainPanel, imageIcon);
        final TabWindow tabWindow = new TabWindow(new DockingWindow[]{unitBrowserView, mpqBrowserView});
        tabWindow.setSelectedTab(0);

        animationControllerView = new View("Animation Controller", null, new JPanel());
        previewView = new View("Preview", null, new JPanel());

        final SplitWindow viewingTab = new SplitWindow(HORIZONTAL, 0.8f,
                new SplitWindow(HORIZONTAL, 0.8f, previewView, animationControllerView), tabWindow);
        viewingTab.getWindowProperties().setTitleProvider(arg0 -> "View");
        viewingTab.getWindowProperties().setCloseEnabled(false);
        return viewingTab;
    }


    private SplitWindow createModelTab() {
        SplitWindow modelTab;
        final JPanel contentsDummy = new JPanel();
        contentsDummy.add(new JLabel("..."));
        modelDataView = new View("Contents", null, contentsDummy);
        modelComponentView = new View("Component", null, new JPanel());

        modelTab = new SplitWindow(HORIZONTAL, 0.2f, modelDataView, modelComponentView);
        modelTab.getWindowProperties().setTitleProvider(arg0 -> "Model");
        return modelTab;
    }
 }
