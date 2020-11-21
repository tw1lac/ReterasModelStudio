package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.animedit.TimeSliderPanel;
import com.hiveworkshop.wc3.gui.modeledit.ModeButton;
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
    TimeSliderPanel timeSliderPanel;
    JButton setKeyframe;
    JButton setTimeBounds;
    ModeButton animationModeButton;

    TabWindow startupTabWindow;
    CreatorModelingPanel creatorPanel;
    JTextField[] mouseCoordDisplay = new JTextField[3];
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
        createAnimationPanelStuff(mainPanel);
        final JPanel timeSliderAndExtra = createTimeSliderExtras(mainPanel);
        timeSliderView = new View("Footer", null, timeSliderAndExtra);

        creatorPanel = new CreatorModelingPanel(newType -> {mainPanel.actionTypeGroup.maybeSetButtonType(newType); mainPanel.changeActivity(newType);}, mainPanel.prefs, mainPanel.actionTypeGroup, mainPanel.activeViewportWatcher, mainPanel.animatedRenderEnvironment);
        createEditTabViews(mainPanel);
        startupTabWindow = createMainLayout(mainPanel);
    }

    static void createMouseCoordinatesDisplay(JTextField[] mouseCoordDisplay) {
        for (int i = 0; i < mouseCoordDisplay.length; i++) {
            mouseCoordDisplay[i] = new JTextField("");
            mouseCoordDisplay[i].setMaximumSize(new Dimension(80, 18));
            mouseCoordDisplay[i].setMinimumSize(new Dimension(50, 15));
            mouseCoordDisplay[i].setEditable(false);
        }
    }

    JPanel createTimeSliderExtras(MainPanel mainPanel) {
        createMouseCoordinatesDisplay(mainPanel.mouseCoordDisplay);
        final JPanel timeSliderAndExtra = new JPanel();
        final GroupLayout tsaeLayout = new GroupLayout(timeSliderAndExtra);
        final Component horizontalGlue = Box.createHorizontalGlue();
        final Component verticalGlue = Box.createVerticalGlue();
        System.out.println(timeSliderPanel);
        tsaeLayout.setHorizontalGroup(
                tsaeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(timeSliderPanel)
                        .addGroup(tsaeLayout.createSequentialGroup()
                                .addComponent(mainPanel.mouseCoordDisplay[0])
                                .addComponent(mainPanel.mouseCoordDisplay[1])
                                .addComponent(mainPanel.mouseCoordDisplay[2])
                                .addComponent(horizontalGlue)
                                .addComponent(setKeyframe)
                                .addComponent(setTimeBounds)));
        tsaeLayout.setVerticalGroup(tsaeLayout.createSequentialGroup()
                .addComponent(timeSliderPanel)
                .addGroup(tsaeLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(mainPanel.mouseCoordDisplay[0])
                        .addComponent(mainPanel.mouseCoordDisplay[1])
                        .addComponent(mainPanel.mouseCoordDisplay[2])
                        .addComponent(horizontalGlue)
                        .addComponent(setKeyframe)
                        .addComponent(setTimeBounds)));
        timeSliderAndExtra.setLayout(tsaeLayout);
        return timeSliderAndExtra;
    }

    void createAnimationPanelStuff(MainPanel mainPanel) {
        timeSliderPanel = new TimeSliderPanel(mainPanel.animatedRenderEnvironment, mainPanel.modelStructureChangeListener, mainPanel.prefs);
        timeSliderPanel.setDrawing(false);
        timeSliderPanel.addListener(MainPanelActions.createTimeSliderTimeListener(mainPanel));
//		timeSliderPanel.addListener(creatorPanel);

        setKeyframe = new JButton(GlobalIcons.SET_KEYFRAME_ICON);
        setKeyframe.setMargin(new Insets(0, 0, 0, 0));
        setKeyframe.setToolTipText("Create Keyframe");
        setKeyframe.addActionListener(MainPanelActions.createKeyframeAction(mainPanel));

        setTimeBounds = new JButton(GlobalIcons.SET_TIME_BOUNDS_ICON);
        setTimeBounds.setMargin(new Insets(0, 0, 0, 0));
        setTimeBounds.setToolTipText("Choose Time Bounds");
        setTimeBounds.addActionListener(MainPanelActions.timeBoundChooserPanel(mainPanel));

        animationModeButton = new ModeButton("Animate");
        animationModeButton.setVisible(false);// TODO remove this if unused

//        toolbar.setMaximumSize(new Dimension(80000, 48));

//        modelPanels = new ArrayList<>();
        final JPanel toolsPanel = new JPanel();
        toolsPanel.setMaximumSize(new Dimension(30, 999999));


        animationControllerView = new View("Animation Controller", null, new JPanel());
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

        creatorView = new View("Modeling", null, creatorPanel);

        leftView = new View("Side", null, new JPanel());
        frontView = new View("Front", null, new JPanel());
        bottomView = new View("Bottom", null, new JPanel());
        perspectiveView = new View("Perspective", null, new JPanel());
        previewView = new View("Preview", null, new JPanel());

        mainPanel.toolView = new View("Tools", null, new JPanel());
    }
}
