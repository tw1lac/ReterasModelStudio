package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.animedit.TimeSliderPanel;
import com.hiveworkshop.wc3.gui.modeledit.CoordDisplayListener;
import com.hiveworkshop.wc3.gui.modeledit.ModeButton;
import com.hiveworkshop.wc3.gui.modeledit.creator.CreatorModelingPanel;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class EditTab {

    final static boolean VERTICAL = false;
    final static boolean HORIZONTAL = true;

    CoordDisplayListener coordDisplayListener;

    final MainPanel mainPanel;
    TimeSliderPanel timeSliderPanel;
    JButton setKeyframe;
    JButton setTimeBounds;
    ModeButton animationModeButton;

    CreatorModelingPanel creatorPanel;
    JTextField[] mouseCoordDisplay;
    View toolView;
    View frontView;
    View leftView;
    View bottomView;
//    View previewView;
    View creatorView;
    View perspectiveView;
    View timeSliderView;
//    View animationControllerView;

    View viewportControllerWindowView;

    public EditTab(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        createAnimationPanelStuff(mainPanel);
        final JPanel timeSliderAndExtra = createTimeSliderExtras();
        timeSliderView = new View("Footer", null, timeSliderAndExtra);

        creatorPanel = new CreatorModelingPanel(newType -> {mainPanel.actionTypeGroup.maybeSetButtonType(newType); mainPanel.changeActivity(newType);}, mainPanel.prefs, mainPanel.actionTypeGroup, mainPanel.activeViewportWatcher, mainPanel.animatedRenderEnvironment);
        createEditTabViews();
        coordDisplayListener = this::setMouseCoordDisplay;
    }

    public void setMouseCoordDisplay(final byte dim1, final byte dim2, final double value1, final double value2) {
        for (JTextField jTextField : mouseCoordDisplay) {
            jTextField.setText("");
        }
        mouseCoordDisplay[dim1].setText((float) value1 + "");
        mouseCoordDisplay[dim2].setText((float) value2 + "");
    }

    public SplitWindow createEditTab() {
        final TabWindow outlinerToolTabWindow = new TabWindow(new DockingWindow[]{viewportControllerWindowView, toolView});
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


    void createMouseCoordinatesDisplay() {
        mouseCoordDisplay = new JTextField[3];
        for (int i = 0; i < mouseCoordDisplay.length; i++) {
            mouseCoordDisplay[i] = new JTextField("");
            mouseCoordDisplay[i].setMaximumSize(new Dimension(80, 18));
            mouseCoordDisplay[i].setMinimumSize(new Dimension(50, 15));
            mouseCoordDisplay[i].setEditable(false);
        }
    }

    JPanel createTimeSliderExtras() {
        createMouseCoordinatesDisplay();
        final JPanel timeSliderAndExtra = new JPanel();
        final GroupLayout tsaeLayout = new GroupLayout(timeSliderAndExtra);
        final Component horizontalGlue = Box.createHorizontalGlue();
        final Component verticalGlue = Box.createVerticalGlue();
        System.out.println(timeSliderPanel);
        tsaeLayout.setHorizontalGroup(
                tsaeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(timeSliderPanel)
                        .addGroup(tsaeLayout.createSequentialGroup()
                                .addComponent(mouseCoordDisplay[0])
                                .addComponent(mouseCoordDisplay[1])
                                .addComponent(mouseCoordDisplay[2])
                                .addComponent(horizontalGlue)
                                .addComponent(setKeyframe)
                                .addComponent(setTimeBounds)));
        tsaeLayout.setVerticalGroup(tsaeLayout.createSequentialGroup()
                .addComponent(timeSliderPanel)
                .addGroup(tsaeLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(mouseCoordDisplay[0])
                        .addComponent(mouseCoordDisplay[1])
                        .addComponent(mouseCoordDisplay[2])
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

    }


    void createEditTabViews() {
        final JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("..."));
        viewportControllerWindowView = new View("Outliner", null, jPanel);// GlobalIcons.geoIcon

        creatorView = new View("Modeling", null, creatorPanel);

        leftView = new View("Side", null, new JPanel());
        frontView = new View("Front", null, new JPanel());
        bottomView = new View("Bottom", null, new JPanel());
        perspectiveView = new View("Perspective", null, new JPanel());


        toolView = new View("Tools", null, new JPanel());
    }
}
