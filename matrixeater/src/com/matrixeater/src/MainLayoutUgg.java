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
    View toolView;
    View frontView;
    View leftView;
    View bottomView;
    View previewView;
    View creatorView;
    View perspectiveView;
    View timeSliderView;
    View animationControllerView;

    public MainLayoutUgg(MainPanel mainPanel){
        this.mainPanel = mainPanel;
    }


    static TabWindow createMainLayout(MainPanel mainPanel) {

        final SplitWindow editingTab = createEditTab(mainPanel);

        final SplitWindow viewingTab = CreateViewTab(mainPanel);

        final SplitWindow modelTab = new SplitWindow(HORIZONTAL, 0.2f, mainPanel.modelDataView, mainPanel.modelComponentView);
        modelTab.getWindowProperties().setTitleProvider(arg0 -> "Model");

        final TabWindow startupTabWindow = new TabWindow(new DockingWindow[]{viewingTab, editingTab, modelTab});
        DockingWindowUtils.traverseAndFix(startupTabWindow);
        return startupTabWindow;
    }

    private static SplitWindow CreateViewTab(MainPanel mainPanel) {
        ImageIcon imageIcon  = new ImageIcon(MainFrame.MAIN_PROGRAM_ICON.getScaledInstance(16, 16, Image.SCALE_FAST));
        final View mpqBrowserView = MPQBrowser.createMPQBrowser(mainPanel, imageIcon);
        final View unitBrowserView = UnitBrowser.createUnitBrowser(mainPanel, imageIcon);
        final TabWindow tabWindow = new TabWindow(new DockingWindow[]{unitBrowserView, mpqBrowserView});
        tabWindow.setSelectedTab(0);

        final SplitWindow viewingTab = new SplitWindow(HORIZONTAL, 0.8f,
                new SplitWindow(HORIZONTAL, 0.8f, mainPanel.previewView, mainPanel.animationControllerView), tabWindow);
        viewingTab.getWindowProperties().setTitleProvider(arg0 -> "View");
        viewingTab.getWindowProperties().setCloseEnabled(false);
        return viewingTab;
    }

    private static SplitWindow createEditTab(MainPanel mainPanel) {
        final TabWindow outlinerToolTabWindow = new TabWindow(new DockingWindow[]{mainPanel.viewportControllerWindowView, mainPanel.toolView});
        outlinerToolTabWindow.setSelectedTab(0);
//		outlinerToolTabWindow.getWindowProperties().setCloseEnabled(false);
        final SplitWindow frontAndBottom = new SplitWindow(HORIZONTAL, mainPanel.frontView, mainPanel.bottomView);
        final SplitWindow leftAndPerspective = new SplitWindow(HORIZONTAL, mainPanel.leftView, mainPanel.perspectiveView);
        final SplitWindow modelQuadView = new SplitWindow(VERTICAL, frontAndBottom, leftAndPerspective);
        final SplitWindow quadAndModeling = new SplitWindow(HORIZONTAL, 0.8f, modelQuadView, mainPanel.creatorView);

        final SplitWindow editingTab = new SplitWindow(VERTICAL, 0.875f,
                new SplitWindow(HORIZONTAL, 0.2f, outlinerToolTabWindow, quadAndModeling), mainPanel.timeSliderView);

        editingTab.getWindowProperties().setCloseEnabled(false);
        editingTab.getWindowProperties().setTitleProvider(arg0 -> "Edit");
        return editingTab;
    }

    static void createEditTabViews(MainPanel mainPanel) {
        final JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("..."));
        mainPanel.viewportControllerWindowView = new View("Outliner", null, jPanel);// GlobalIcons.geoIcon

        mainPanel.creatorView = new View("Modeling", null, mainPanel.creatorPanel);

        mainPanel.leftView = new View("Side", null, new JPanel());
        mainPanel.frontView = new View("Front", null, new JPanel());
        mainPanel.bottomView = new View("Bottom", null, new JPanel());
        mainPanel.perspectiveView = new View("Perspective", null, new JPanel());
        mainPanel.previewView = new View("Preview", null, new JPanel());

        mainPanel.toolView = new View("Tools", null, new JPanel());
    }
}
