package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.modeledit.creator.CreatorModelingPanel;
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
    View modelDataView;
    View modelComponentView;

    public MainLayoutUgg(MainPanel mainPanel){
        this.mainPanel = mainPanel;
    }


    TabWindow createMainLayout(MainPanel mainPanel) {

        final SplitWindow editingTab = createEditTab(mainPanel);

        final SplitWindow viewingTab = CreateViewTab(mainPanel);

        final SplitWindow modelTab = createModelTab();

        final TabWindow startupTabWindow = new TabWindow(new DockingWindow[]{viewingTab, editingTab, modelTab});

        return startupTabWindow;
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

    private SplitWindow CreateViewTab(MainPanel mainPanel) {
        ImageIcon imageIcon  = new ImageIcon(MainFrame.MAIN_PROGRAM_ICON.getScaledInstance(16, 16, Image.SCALE_FAST));
        final View mpqBrowserView = MPQBrowser.createMPQBrowser(mainPanel, imageIcon);
        final View unitBrowserView = UnitBrowser.createUnitBrowser(mainPanel, imageIcon);
        final TabWindow tabWindow = new TabWindow(new DockingWindow[]{unitBrowserView, mpqBrowserView});
        tabWindow.setSelectedTab(0);

        final SplitWindow viewingTab = new SplitWindow(HORIZONTAL, 0.8f,
                new SplitWindow(HORIZONTAL, 0.8f, previewView, animationControllerView), tabWindow);
        viewingTab.getWindowProperties().setTitleProvider(arg0 -> "View");
        viewingTab.getWindowProperties().setCloseEnabled(false);
        return viewingTab;
    }

    private SplitWindow createEditTab(MainPanel mainPanel) {
        final TabWindow outlinerToolTabWindow = new TabWindow(new DockingWindow[]{mainPanel.viewportControllerWindowView, mainPanel.toolView});
        outlinerToolTabWindow.setSelectedTab(0);
//		outlinerToolTabWindow.getWindowProperties().setCloseEnabled(false);
        final SplitWindow frontAndBottom = new SplitWindow(HORIZONTAL, frontView, bottomView);
        final SplitWindow leftAndPerspective = new SplitWindow(HORIZONTAL, leftView, perspectiveView);
        final SplitWindow modelQuadView = new SplitWindow(VERTICAL, frontAndBottom, leftAndPerspective);
        final SplitWindow quadAndModeling = new SplitWindow(HORIZONTAL, 0.8f, modelQuadView, creatorView);

        final SplitWindow editingTab = new SplitWindow(VERTICAL, 0.875f,
                new SplitWindow(HORIZONTAL, 0.2f, outlinerToolTabWindow, quadAndModeling), timeSliderView);

        editingTab.getWindowProperties().setCloseEnabled(false);
        editingTab.getWindowProperties().setTitleProvider(arg0 -> "Edit");
        return editingTab;
    }

    void createEditTabViews(MainPanel mainPanel) {
        final JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("..."));
        mainPanel.viewportControllerWindowView = new View("Outliner", null, jPanel);// GlobalIcons.geoIcon

        creatorView = new View("Modeling", null, mainPanel.creatorPanel);

        leftView = new View("Side", null, new JPanel());
        frontView = new View("Front", null, new JPanel());
        bottomView = new View("Bottom", null, new JPanel());
        perspectiveView = new View("Perspective", null, new JPanel());
        previewView = new View("Preview", null, new JPanel());

        mainPanel.toolView = new View("Tools", null, new JPanel());
    }
}
