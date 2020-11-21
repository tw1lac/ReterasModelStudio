package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.animedit.TimeEnvironmentImpl;
import com.hiveworkshop.wc3.gui.animedit.TimeSliderPanel;
import com.hiveworkshop.wc3.gui.modeledit.*;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.activity.ActivityDescriptor;
import com.hiveworkshop.wc3.gui.modeledit.activity.ModelEditorChangeActivityListener;
import com.hiveworkshop.wc3.gui.modeledit.creator.CreatorModelingPanel;
import com.hiveworkshop.wc3.gui.modeledit.cutpaste.ViewportTransferHandler;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.ModelEditorManager;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.ModelEditorActionType;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.listener.ClonedNodeNamePicker;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionMode;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarActionButtonType;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarButtonGroup;
import com.hiveworkshop.wc3.gui.mpqbrowser.BLPPanel;
import com.hiveworkshop.wc3.jworldedit.models.BetterUnitEditorModelSelector;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorSettings;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mdl.v2.ModelView;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.units.GameObject;
import com.hiveworkshop.wc3.units.ModelOptionPane;
import com.hiveworkshop.wc3.units.ModelOptionPane.ModelElement;
import com.hiveworkshop.wc3.units.StandardObjectData;
import com.hiveworkshop.wc3.units.UnitOptionPane;
import com.hiveworkshop.wc3.units.fields.UnitFields;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData.MutableGameObject;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData.WorldEditorDataType;
import com.hiveworkshop.wc3.units.objectdata.WTSFile;
import com.hiveworkshop.wc3.units.objectdata.War3ObjectDataChangeset;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.hiveworkshop.wc3.user.WarcraftDataSourceChangeListener.WarcraftDataSourceChangeNotifier;
import com.matrixeater.imp.AnimationTransfer;
import de.wc3data.stream.BlizzardDataInputStream;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.StringViewMap;
import net.infonode.tabbedpanel.TabAreaVisiblePolicy;
import net.infonode.tabbedpanel.titledtab.TitledTabBorderSizePolicy;
import net.infonode.tabbedpanel.titledtab.TitledTabSizePolicy;
import org.lwjgl.util.vector.Quaternion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Write a description of class MainPanel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class MainPanel extends JPanel
        implements ActionListener, UndoHandler, ModelEditorChangeActivityListener, ModelPanelCloseListener {
    JMenuBar menuBar;
//    JMenu fileMenu, editMenu, mirrorSubmenu, tweaksSubmenu, importMenu, addMenu,
//            scriptsMenu, animationMenu, singleAnimationMenu, aboutMenu, fetch, viewMenu;
    JMenu recentMenu, toolsMenu, windowMenu, addParticle;

    JCheckBoxMenuItem mirrorFlip, fetchPortraitsToo, showNormals, textureModels, showVertexModifyControls;

    JMenuItem cut, copy, paste;
    final List<RecentItem> recentItems = new ArrayList<>();
    UndoMenuItem undo;
    RedoMenuItem redo;

    JMenu viewMode;
    JRadioButtonMenuItem wireframe, solid;
    ButtonGroup viewModes;

    File currentFile;
    ImportPanel importPanel;
    static final ImageIcon MDLIcon = new ImageIcon(MainPanel.class.getResource("ImageBin/MDLIcon_16.png"));
    static final ImageIcon POWERED_BY_HIVE = new ImageIcon(MainPanel.class.getResource("ImageBin/powered_by_hive.png"));
    public static final ImageIcon AnimIcon = new ImageIcon(MainPanel.class.getResource("ImageBin/Anim.png"));
    protected static final boolean OLDMODE = false;
    final List<ModelPanel> modelPanels = new ArrayList<>();
    ModelPanel currentModelPanel;
    MainLayoutUgg mainLayoutUgg;

    JScrollPane geoControl;
    JScrollPane geoControlModelData;
    JTextField[] mouseCoordDisplay = new JTextField[3];
    boolean cheatShift = false;
    boolean cheatAlt = false;
    SaveProfile profile = SaveProfile.get();
    ProgramPreferences prefs = profile.getPreferences();


    TimeSliderPanel timeSliderPanel;
    JButton setKeyframe;
    JButton setTimeBounds;
    ModeButton animationModeButton;
    boolean animationModeState = false;

    final ActiveViewportWatcher activeViewportWatcher = new ActiveViewportWatcher();

    final WarcraftDataSourceChangeNotifier directoryChangeNotifier = new WarcraftDataSourceChangeNotifier();

    int contextClickedTab = 0;

    final AbstractAction undoAction = new UndoActionImplementation("Undo", this);
    final AbstractAction redoAction = new RedoActionImplementation("Redo", this);
    final ClonedNodeNamePicker namePicker = new ClonedNodeNamePickerImplementation(this);

    ToolbarButtonGroup<SelectionItemTypes> selectionItemTypeGroup;
    ToolbarButtonGroup<SelectionMode> selectionModeGroup;
    ToolbarButtonGroup<ToolbarActionButtonType> actionTypeGroup;
    final ModelStructureChangeListener modelStructureChangeListener;
    final ViewportTransferHandler viewportTransferHandler;
    final RootWindow rootWindow;
    View viewportControllerWindowView;
    View toolView;
    ActivityDescriptor currentActivity;

    public MainPanel() {
        super();
        ToolBar toolBar = new ToolBar(this);
        add(toolBar.toolBar);

        animatedRenderEnvironment = new TimeEnvironmentImpl();
        animatedRenderEnvironment.addChangeListener(MainPanelActions.animatedRenderEnvironmentChangeListener(this));

        creatorPanel = new CreatorModelingPanel(newType -> {actionTypeGroup.maybeSetButtonType(newType); MainPanel.this.changeActivity(newType);}, prefs, actionTypeGroup, activeViewportWatcher, animatedRenderEnvironment);

        actionTypeGroup.addToolbarButtonListener(MainPanelActions.createActionTypeGroupButtonListener(this));
        actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);

        mainLayoutUgg = new MainLayoutUgg(this);
        mainLayoutUgg.createEditTabViews(this);


        StringViewMap viewMap = new StringViewMap();
        rootWindow = new RootWindow(viewMap);
        rootWindow.addListener(MainPanelActions.createDockingWindowListener(this));
        rootWindow.getWindowProperties().getTabProperties().getTitledTabProperties().setSizePolicy(TitledTabSizePolicy.EQUAL_SIZE);
        rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setVisible(true);
        rootWindow.getWindowProperties().getTabProperties().getTitledTabProperties().setBorderSizePolicy(TitledTabBorderSizePolicy.EQUAL_SIZE);
        rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getTabAreaProperties().setTabAreaVisiblePolicy(TabAreaVisiblePolicy.MORE_THAN_ONE_TAB);
        rootWindow.setBackground(Color.GREEN);
        rootWindow.setForeground(Color.GREEN);

        final Runnable fixit = () -> {
            DockingWindowUtils.traverseAndReset(rootWindow);
            DockingWindowUtils.traverseAndFix(rootWindow);
        };
        rootWindow.addListener(MainPanelActions.createDockingWindowListener(fixit));


        modelStructureChangeListener = new ModelStructureChangeListenerImplementation(this, () -> currentModelPanel.getModel());

        createAnimationPanelStuff();

        final GroupLayout layout = new GroupLayout(this);

        final TabWindow startupTabWindow = mainLayoutUgg.createMainLayout(this);
        rootWindow.setWindow(startupTabWindow);
        rootWindow.getRootWindowProperties().getFloatingWindowProperties().setUseFrame(true);
        startupTabWindow.setSelectedTab(0);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(toolBar.toolBar).addComponent(rootWindow));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(toolBar.toolBar).addComponent(rootWindow));
        setLayout(layout);

        selectionItemTypeGroup.addToolbarButtonListener(MainPanelActions.createSelectionItemTypesButtonListener(this));


        final JLabel[] divider = new JLabel[3];
        for (int i = 0; i < divider.length; i++) {
            divider[i] = new JLabel("----------");
        }

        BLPPanel blpPanel = new BLPPanel(null);



        viewportTransferHandler = new ViewportTransferHandler();
        coordDisplayListener = MainPanel.this::setMouseCoordDisplay;
    }

    private void createAnimationPanelStuff() {
        timeSliderPanel = new TimeSliderPanel(animatedRenderEnvironment, modelStructureChangeListener, prefs);
        timeSliderPanel.setDrawing(false);
        timeSliderPanel.addListener(MainPanelActions.createTimeSliderTimeListener(this));
//		timeSliderPanel.addListener(creatorPanel);

        setKeyframe = new JButton(GlobalIcons.SET_KEYFRAME_ICON);
        setKeyframe.setMargin(new Insets(0, 0, 0, 0));
        setKeyframe.setToolTipText("Create Keyframe");
        setKeyframe.addActionListener(MainPanelActions.createKeyframeAction(this));

        setTimeBounds = new JButton(GlobalIcons.SET_TIME_BOUNDS_ICON);
        setTimeBounds.setMargin(new Insets(0, 0, 0, 0));
        setTimeBounds.setToolTipText("Choose Time Bounds");
        setTimeBounds.addActionListener(MainPanelActions.timeBoundChooserPanel(this));

        animationModeButton = new ModeButton("Animate");
        animationModeButton.setVisible(false);// TODO remove this if unused

//        toolbar.setMaximumSize(new Dimension(80000, 48));

//        modelPanels = new ArrayList<>();
        final JPanel toolsPanel = new JPanel();
        toolsPanel.setMaximumSize(new Dimension(30, 999999));


        mainLayoutUgg.animationControllerView = new View("Animation Controller", null, new JPanel());

        for (int i = 0; i < mouseCoordDisplay.length; i++) {
            mouseCoordDisplay[i] = new JTextField("");
            mouseCoordDisplay[i].setMaximumSize(new Dimension(80, 18));
            mouseCoordDisplay[i].setMinimumSize(new Dimension(50, 15));
            mouseCoordDisplay[i].setEditable(false);
        }

        final JPanel timeSliderAndExtra = new JPanel();
        final GroupLayout tsaeLayout = new GroupLayout(timeSliderAndExtra);
        final Component horizontalGlue = Box.createHorizontalGlue();
        final Component verticalGlue = Box.createVerticalGlue();
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
        mainLayoutUgg.timeSliderView = new View("Footer", null, timeSliderAndExtra);
    }

    @Override
    public void changeActivity(final ActivityDescriptor newType) {
        this.currentActivity = newType;
        for (final ModelPanel modelPanel : modelPanels) {
            modelPanel.changeActivity(newType);
        }
        creatorPanel.changeActivity(newType);
    }

    static final Quaternion IDENTITY = new Quaternion();
    final TimeEnvironmentImpl animatedRenderEnvironment;
    JButton snapButton;
    final CoordDisplayListener coordDisplayListener;
    protected ModelEditorActionType actionType;
//    JMenu teamColorMenu;
    CreatorModelingPanel creatorPanel;


    /**
     * Right now this is a plug to the statics to load unit data. However, it's a
     * non-static method so that we can have it load from an opened map in the
     * future -- the MutableObjectData class can parse map unit data!
     */
    public MutableObjectData getUnitData() {
        final War3ObjectDataChangeset editorData = getWar3ObjectDataChangeset('u', "war3map.w3u");
        return new MutableObjectData(WorldEditorDataType.UNITS, StandardObjectData.getStandardUnits(),
                StandardObjectData.getStandardUnitMeta(), editorData);
    }

    public MutableObjectData getDoodadData() {
        final War3ObjectDataChangeset editorData = getWar3ObjectDataChangeset('d', "war3map.w3d");
        return new MutableObjectData(WorldEditorDataType.DOODADS, StandardObjectData.getStandardDoodads(),
                StandardObjectData.getStandardDoodadMeta(), editorData);
    }

    private War3ObjectDataChangeset getWar3ObjectDataChangeset(char expectedKind, String s) {
        final War3ObjectDataChangeset editorData = new War3ObjectDataChangeset(expectedKind);
        try {
            final MpqCodebase mpqCodebase = MpqCodebase.get();
            if (mpqCodebase.has(s)) {
                editorData.load(new BlizzardDataInputStream(mpqCodebase.getResourceAsStream(s)),
                        mpqCodebase.has("war3map.wts") ? new WTSFile(mpqCodebase.getResourceAsStream("war3map.wts"))
                                : null,
                        true);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return editorData;
    }

    public void init() {
        final JRootPane root = getRootPane();
        linkActions(root);
        updateUIFromProgramPreferences();
    }

    void linkActions(final JComponent root) {
        root.getActionMap().put("Undo", undoAction);
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Z"),
                "Undo");

        root.getActionMap().put("Redo", redoAction);
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Y"),
                "Redo");

        root.getActionMap().put("Delete", MainPanelActions.deleteAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("DELETE"), "Delete");

        root.getActionMap().put("CloneSelection", MainPanelActions.cloneAction(this));

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("SPACE"), "MaximizeSpacebar");
        root.getActionMap().put("MaximizeSpacebar", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                final View focusedView = rootWindow.getFocusedView();
                if (focusedView != null) {
                    if (focusedView.isMaximized()) {
                        rootWindow.setMaximizedWindow(null);
                    } else {
                        focusedView.maximize();
                    }
                }
            }
        });

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("RIGHT"), "PressRight");
        root.getActionMap().put("PressRight", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpRight();
                }
            }
        });

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("LEFT"), "PressLeft");
        root.getActionMap().put("PressLeft", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpLeft();
                }
            }
        });

        makeTimeSliderShortcut(root, "UP", "PressUp", 1);

        makeTimeSliderShortcut(root, "shift UP", "PressShiftUp", 10);

        makeTimeSliderShortcut(root, "DOWN", "PressDown", -1);

        makeTimeSliderShortcut(root, "shift DOWN", "PressShiftDown", -10);

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control SPACE"), "PlayKeyboardKey");
        root.getActionMap().put("PlayKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                timeSliderPanel.play();
            }
        });

        makeActionShortcut(root, "W", "QKeyboardKey", 0);

        makeActionShortcut(root, "E", "WKeyboardKey", 1);

        makeActionShortcut(root, "R", "EKeyboardKey", 2);

        makeActionShortcut(root, "T", "RKeyboardKey", 3, true);

        makeActionShortcut(root, "Y", "TKeyboardKey", 4, true);

        makeShortCutKey(root, "A", "AKeyboardKey", 0);

        makeShortCutKey(root, "S", "SKeyboardKey", 1);

        makeShortCutKey(root, "D", "DKeyboardKey", 2);

        makeShortCutKey(root, "F", "FKeyboardKey", 3);

        makeShortCutKey(root, "G", "GKeyboardKey", 4);

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Z"), "ZKeyboardKey");
        root.getActionMap().put("ZKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                prefs.setViewMode(prefs.getViewMode() == 1 ? 0 : 1);
            }
        });

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control F"), "CreateFaceShortcut");
        root.getActionMap().put("CreateFaceShortcut", ModelUtils.getCreateFaceShortcut(this));

        for (int i = 1; i <= 9; i++) {
            root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke("alt pressed " + i), i + "KeyboardKey");
            final int index = i;
            root.getActionMap().put(i + "KeyboardKey", new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final DockingWindow window = rootWindow.getWindow();
                    if (window instanceof TabWindow) {
                        final TabWindow tabWindow = (TabWindow) window;
                        final int tabCount = tabWindow.getChildWindowCount();
                        if ((index - 1) < tabCount) {
                            tabWindow.setSelectedTab(index - 1);
                        }
                    }
                }
            });
        }
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control
        // V"), null);
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control
        // V"),
        // "CloneSelection");

        root.getActionMap().put("shiftSelect", MainPanelActions.getShiftSelectAction(this, "shiftSelect", selectionModeGroup.getActiveButtonType() == SelectionMode.SELECT, SelectionMode.ADD, true));
        root.getActionMap().put("altSelect", MainPanelActions.getAltSelectAction(this, "altSelect", selectionModeGroup.getActiveButtonType() == SelectionMode.SELECT, SelectionMode.DESELECT, true));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) .put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        // .put(KeyStroke.getKeyStroke("control pressed CONTROL"), "shiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt pressed ALT"), "altSelect");

        root.getActionMap().put("unShiftSelect", MainPanelActions.getShiftSelectAction(this, "unShiftSelect", (selectionModeGroup.getActiveButtonType() == SelectionMode.ADD) && cheatShift, SelectionMode.SELECT, false));
        root.getActionMap().put("unAltSelect", MainPanelActions.getAltSelectAction(this, "unAltSelect", (selectionModeGroup.getActiveButtonType() == SelectionMode.DESELECT) && cheatAlt, SelectionMode.SELECT, false));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released SHIFT"), "unShiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released
        // CONTROL"),
        // "unShiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released ALT"), "unAltSelect");

        root.getActionMap().put("Select All", MainPanelActions.selectAllAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control A"), "Select All");

        root.getActionMap().put("Invert Selection", MainPanelActions.invertSelectAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control I"), "Invert Selection");

        root.getActionMap().put("Expand Selection", MainPanelActions.expandSelectionAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control E"), "Expand Selection");

        root.getActionMap().put("RigAction", MainPanelActions.rigAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control W"), "RigAction");
    }

    private void makeTimeSliderShortcut(JComponent root, String keyStroke, String actionMapKey, int deltaFrames) {
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyStroke), actionMapKey);
        root.getActionMap().put(actionMapKey, new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpFrames(deltaFrames);
                }
            }
        });
    }

    private void makeActionShortcut(JComponent root, String keyStroke, String actionMapKey, int buttonType) {
        makeActionShortcut(root, keyStroke, actionMapKey, buttonType, false);
    }

    private void makeActionShortcut(JComponent root, String keyStroke, String actionMapKey, int buttonType, boolean checkAnimationModeState) {
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyStroke), actionMapKey);
        root.getActionMap().put(actionMapKey, new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (!checkAnimationModeState || !animationModeState) {
                    actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[buttonType]);
                }
            }
        });
    }
    private void makeShortCutKey(JComponent root, String keyStroke, String actionMapKey, int buttonType) {
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keyStroke), actionMapKey);
        root.getActionMap().put(actionMapKey, new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                selectionItemTypeGroup.setToolbarButtonType(selectionItemTypeGroup.getToolbarButtonTypes()[buttonType]);
            }
        });
    }

    void updateUIFromProgramPreferences() {
        showVertexModifyControls.setSelected(prefs.isShowVertexModifierControls());
        textureModels.setSelected(prefs.isTextureModels());
        showNormals.setSelected(prefs.isShowNormals());
        fetchPortraitsToo.setSelected(prefs.isLoadPortraits());
        switch (prefs.getViewMode()) {
            case 0:
                wireframe.setSelected(true);
                break;
            case 1:
                solid.setSelected(true);
                break;
            default:
                break;
        }
        for (final ModelPanel mpanel : modelPanels) {
            mpanel.getEditorRenderModel().setSpawnParticles((prefs.getRenderParticles() == null) || prefs.getRenderParticles());
            mpanel.getEditorRenderModel().setAllowInanimateParticles((prefs.getRenderStaticPoseParticles() == null) || prefs.getRenderStaticPoseParticles());
            mpanel.getAnimationViewer().setSpawnParticles((prefs.getRenderParticles() == null) || prefs.getRenderParticles());
        }
    }


    @Override
    public void actionPerformed(final ActionEvent e) {
        // Open, off of the file menu:
        refreshUndo();
    }

    void nullModelUgg() {
        FileUtils.nullModelFile(this);
        refreshController();
    }

    void scaleAnimationsUgg() {
        final AnimationFrame aFrame = new AnimationFrame(currentModelPanel, timeSliderPanel::revalidateKeyframeDisplay);
        aFrame.setVisible(true);
    }

    void fetchAndAddAnimationFromFile(String filepath) {
        final EditableModel current = currentMDL();
        final String mdxFilepath = convertPathToMDX(filepath);
        if (mdxFilepath != null) {
            final EditableModel animationSource = EditableModel.read(MpqCodebase.get().getFile(mdxFilepath));
            addSingleAnimation(current, animationSource);
        }
    }

    void riseFallBirth() {
        final int confirmed = JOptionPane.showConfirmDialog(this,
                "This will permanently alter model. Are you sure?", "Confirmation",
                JOptionPane.OK_CANCEL_OPTION);
        if (confirmed != JOptionPane.OK_OPTION) {
            return;
        }

        final ModelView disp = currentModelPanel.getModelViewManager();
        final EditableModel model = disp.getModel();

        replaceOrUseOldAnimation(model, "Birth");
        replaceOrUseOldAnimation(model, "Death");

        JOptionPane.showMessageDialog(this, "Done!");
    }


    private void setAnimationVisibilityFlag(Animation animation, Animation stand, VisibilitySource source) {
        final AnimFlag dummy = new AnimFlag("dummy");
        final AnimFlag af = source.getVisibilityFlag();
        dummy.copyFrom(af);
        af.deleteAnim(animation);
        af.copyFrom(dummy, stand.getStart(), stand.getEnd(), animation.getStart(), animation.getEnd());
        af.setEntry(animation.getEnd(), 0);
    }

    private void addAnimationFlags(Animation animation, IdObject obj) {
        if (obj instanceof Bone) {
            final Bone b = (Bone) obj;
            AnimFlag trans = null;
            boolean globalSeq = false;
            for (final AnimFlag af : b.getAnimFlags()) {
                if (af.getTypeId() == AnimFlag.TRANSLATION) {
                    if (af.hasGlobalSeq()) {
                        globalSeq = true;
                    } else {
                        trans = af;
                    }
                }
            }
            if (globalSeq) {
                return;
            }
            if (trans == null) {
                final ArrayList<Integer> times = new ArrayList<>();
                final ArrayList<Integer> values = new ArrayList<>();
                trans = new AnimFlag("Translation", times, values);
                trans.addTag("Linear");
                b.getAnimFlags().add(trans);
            }
            trans.addEntry(animation.getStart(), new Vertex(0, 0, 0));
            trans.addEntry(animation.getEnd(), new Vertex(0, 0, -300));
        }
    }


    private void replaceOrUseOldAnimation(EditableModel model, String animationName) {
        final Animation lastAnim = model.getAnim(model.getAnimsSize() - 1);
        Animation animation = new Animation(animationName, lastAnim.getEnd() + 300, lastAnim.getEnd() + 2300);

        boolean removeOldAnimation = false;
        final Animation oldAnimation = model.findAnimByName(animationName);

        if (oldAnimation != null) {
            final String KEEP_NEW = "Keep new";
            final String KEEP_OLD = "Keep old";
            final String[] choices = {"Cancel operation", KEEP_OLD, KEEP_NEW};
            final Object x = JOptionPane.showInputDialog(this,
                    "Existing " + animationName + " detected. What should be done with it?", "Question",
                    JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
            if (x == KEEP_NEW) {
                removeOldAnimation = true;
            } else if (x == KEEP_OLD) {
                animation = oldAnimation;
            }
            else {
                return;
            }
        }
        if(removeOldAnimation){
            model.remove(oldAnimation);
        }

        final Animation stand = model.findAnimByName("stand");

        final List<IdObject> roots = new ArrayList<>();
        for (final IdObject obj : model.getIdObjects()) {
            if (obj.getParent() == null) {
                roots.add(obj);
            }
        }
        for (final AnimFlag af : model.getAllAnimFlags()) {
            af.deleteAnim(animation);
        }
        for (final IdObject obj : roots) {
            addAnimationFlags(animation, obj);
        }

        // visibility
        for (final VisibilitySource source : model.getAllVisibilitySources()) {
            setAnimationVisibilityFlag(animation, stand, source);
        }

        if (!animation.getTags().contains("NonLooping")) {
            animation.addTag("NonLooping");
        }

        if (!model.contains(animation)) {
            model.add(animation);
        }

    }

    void simplifyKeyframesButtonResponse() {
        final int x = JOptionPane.showConfirmDialog(this,
                "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify keyframes?",
                "Warning: Simplify Keyframes", JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            simplifyKeyframes();
        }
    }

    void linearizeAnimations() {
        final int x = JOptionPane.showConfirmDialog(this,
                "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify animations?",
                "Warning: Linearize Animations", JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            final List<AnimFlag> allAnimFlags = currentMDL().getAllAnimFlags();
            for (final AnimFlag flag : allAnimFlags) {
                flag.linearize();
            }
        }
    }

    void importScript() {
        final JFrame frame = new JFrame("Animation Transferer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new AnimationTransfer(frame));
        frame.setIconImage(MainPanel.AnimIcon.getImage());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void fetchUnitButtonResponse() {
        final GameObject unitFetched = fetchUnit();
        if (unitFetched != null) {
            ImageIcon icon = unitFetched.getScaledIcon(0.25f);

            loadAsMDXAndEnableToolsMenu(icon, unitFetched.getField("file"));
        }
    }

    void fetchModelButtonResponse() {
        final ModelElement model = fetchModel();
        if (model != null) {
            final ImageIcon icon = model.hasCachedIconPath() ?
                    new ImageIcon(BLPHandler.get().getGameTex(model.getCachedIconPath())
                            .getScaledInstance(16, 16, Image.SCALE_FAST)) : MDLIcon;

            loadAsMDXAndEnableToolsMenu(icon, model.getFilepath());
        }
    }

    void fetchObjectButtonResponse() {
        final MutableGameObject objectFetched = fetchObject();
        if (objectFetched != null) {
            ImageIcon icon = new ImageIcon(BLPHandler.get()
                    .getGameTex(objectFetched.getFieldAsString(UnitFields.INTERFACE_ICON, 0))
                    .getScaledInstance(16, 16, Image.SCALE_FAST));

            loadAsMDXAndEnableToolsMenu(icon, objectFetched.getFieldAsString(UnitFields.MODEL_FILE, 0));
        }
    }

    private void loadAsMDXAndEnableToolsMenu(ImageIcon icon, String path) {
        final String filepath = convertPathToMDX(path);

        if (filepath != null) {
            FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(filepath), true, true, icon);

            final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait" + filepath.substring(filepath.lastIndexOf('.'));

            if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                FileUtils.loadStreamMdx(this, MpqCodebase.get()
                        .getResourceAsStream(portrait), true, false, icon);
            }
            toolsMenu.getAccessibleContext().setAccessibleDescription(
                    "Allows the user to control which parts of the model are displayed for editing.");
            toolsMenu.setEnabled(true);
        }
    }

    void importUnit() {
        final GameObject fetchUnitResult = fetchUnit();
        if (fetchUnitResult == null) {
            return;
        }
        final String filepath = convertPathToMDX(fetchUnitResult.getField("file"));
        final EditableModel current = currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(this, animationSource);
        }
        refreshController();
    }

    void importGameModel() {
        final ModelElement fetchModelResult = fetchModel();
        if (fetchModelResult == null) {
            return;
        }
        final String filepath = convertPathToMDX(fetchModelResult.getFilepath());
        final EditableModel current = currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(this, animationSource);
        }
        refreshController();
    }

    void importGameObject() {
        final MutableGameObject fetchObjectResult = fetchObject();
        if (fetchObjectResult == null) {
            return;
        }
        final String filepath = convertPathToMDX(fetchObjectResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
        final EditableModel current = currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(this, animationSource);
        }
        refreshController();
    }

    void importFromWorkspace() {
        final List<EditableModel> optionNames = new ArrayList<>();
        for (final ModelPanel modelPanel : modelPanels) {
            final EditableModel model = modelPanel.getModel();
            optionNames.add(model);
        }
        final EditableModel choice = (EditableModel) JOptionPane.showInputDialog(this,
                "Choose a workspace item to import data from:", "Import from Workspace",
                JOptionPane.OK_CANCEL_OPTION, null, optionNames.toArray(), optionNames.get(0));
        if (choice != null) {
            FileUtils.importFile(this, EditableModel.deepClone(choice, choice.getHeaderName()));
        }
        refreshController();
    }

    void showVertexModifyControls() {
        final boolean selected = showVertexModifyControls.isSelected();
        prefs.setShowVertexModifierControls(selected);
        // SaveProfile.get().setShowViewportButtons(selected);
        for (final ModelPanel panel : modelPanels) {
            panel.getFrontArea().setControlsVisible(selected);
            panel.getBotArea().setControlsVisible(selected);
            panel.getSideArea().setControlsVisible(selected);
            final UVPanel uvPanel = panel.getEditUVPanel();
            if (uvPanel != null) {
                uvPanel.setControlsVisible(selected);
            }
        }
    }


    void dataSourcesChanged() {
        for (final ModelPanel modelPanel : modelPanels) {
            final PerspDisplayPanel pdp = modelPanel.getPerspArea();
            pdp.reloadAllTextures();
            modelPanel.getAnimationViewer().reloadAllTextures();
        }
        directoryChangeNotifier.dataSourcesChanged();
    }

    private void simplifyKeyframes() {
        final EditableModel currentMDL = currentMDL();
        currentMDL.simplifyKeyframes();
    }

    JSpinner getjSpinner(String title) {
        final SpinnerNumberModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        final JSpinner spinner = new JSpinner(sModel);
        final int userChoice = JOptionPane.showConfirmDialog(this, spinner, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (userChoice != JOptionPane.OK_OPTION) {
            return null;
        }
        return spinner;
    }

    GameObject fetchUnit() {
        final GameObject choice = UnitOptionPane.show(this);
        if (choice != null) {
            String filepath = choice.getField("file");

            try {
                filepath = convertPathToMDX(filepath);
                // modelDisp = new MDLDisplay(toLoad, null);
            } catch (final Exception exc) {
                exc.printStackTrace();
                // bad model!
                JOptionPane.showMessageDialog(MainFrame.frame, "The chosen model could not be used.", "Program Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return choice;
        } else {
            return null;
        }
    }

    static String convertPathToMDX(String filepath) {
        if (filepath.endsWith(".mdl")) {
            filepath = filepath.replace(".mdl", ".mdx");
        } else if (!filepath.endsWith(".mdx")) {
            filepath = filepath.concat(".mdx");
        }
        return filepath;
    }

    ModelOptionPane.ModelElement fetchModel() {
        final ModelOptionPane.ModelElement model = ModelOptionPane.showAndLogIcon(this);
        if (model == null) {
            return null;
        }
        String filepath = model.getFilepath();
        if (filepath != null) {
            try {
                filepath = convertPathToMDX(filepath);
            } catch (final Exception exc) {
                exc.printStackTrace();
                // bad model!
                JOptionPane.showMessageDialog(MainFrame.frame, "The chosen model could not be used.", "Program Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return model;
        } else {
            return null;
        }
    }

    MutableGameObject fetchObject() {
        final BetterUnitEditorModelSelector selector = new BetterUnitEditorModelSelector(getUnitData(),
                new UnitEditorSettings());
        final int x = JOptionPane.showConfirmDialog(this, selector, "Object Editor - Select Unit",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        final MutableGameObject choice = selector.getSelection();
        if ((choice == null) || (x != JOptionPane.OK_OPTION)) {
            return null;
        }

        String filepath = choice.getFieldAsString(UnitFields.MODEL_FILE, 0);

        try {
            filepath = convertPathToMDX(filepath);
        } catch (final Exception exc) {
            exc.printStackTrace();
            // bad model!
            JOptionPane.showMessageDialog(MainFrame.frame, "The chosen model could not be used.", "Program Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return choice;
    }

    void addSingleAnimation(final EditableModel current, final EditableModel animationSourceModel) {
        Animation choice = null;
        choice = (Animation) JOptionPane.showInputDialog(this, "Choose an animation!", "Add Animation",
                JOptionPane.QUESTION_MESSAGE, null, animationSourceModel.getAnims().toArray(),
                animationSourceModel.getAnims().get(0));
        if (choice == null) {
            JOptionPane.showMessageDialog(this, "Bad choice. No animation added.");
            return;
        }
        final Animation visibilitySource = (Animation) JOptionPane.showInputDialog(this,
                "Which animation from THIS model to copy visiblity from?", "Add Animation",
                JOptionPane.QUESTION_MESSAGE, null, current.getAnims().toArray(), current.getAnims().get(0));
        if (visibilitySource == null) {
            JOptionPane.showMessageDialog(this, "No visibility will be copied.");
        }
        final List<Animation> animationsAdded = current.addAnimationsFrom(animationSourceModel,
                Collections.singletonList(choice));
        for (final Animation anim : animationsAdded) {
            current.copyVisibility(visibilitySource, anim);
        }
        JOptionPane.showMessageDialog(this, "Added " + animationSourceModel.getName() + "'s " + choice.getName()
                + " with " + visibilitySource.getName() + "'s visibility  OK!");
        modelStructureChangeListener.animationsAdded(animationsAdded);
    }

    interface OpenViewGetter {
        View getView();
    }


    interface ModelReference {
        EditableModel getModel();
    }

    static class RecentItem extends JMenuItem {
        public RecentItem(final String what) {
            super(what);
        }

        String filepath;
    }

    public void updateRecent() {
        final List<String> recent = SaveProfile.get().getRecent();
        for (final RecentItem recentItem : recentItems) {
            recentMenu.remove(recentItem);
        }
        recentItems.clear();
        for (int i = 0; i < recent.size(); i++) {
            final String fp = recent.get(recent.size() - i - 1);
            if ((recentItems.size() <= i) || (recentItems.get(i).filepath != fp)) {
                // String[] bits = recent.get(i).split("/");

                final RecentItem item = new RecentItem(new File(fp).getName());
                item.filepath = fp;
                recentItems.add(item);
                item.addActionListener(e -> {

                    currentFile = new File(item.filepath);
                    profile.setPath(currentFile.getParent());
                    // frontArea.clearGeosets();
                    // sideArea.clearGeosets();
                    // botArea.clearGeosets();
                    toolsMenu.getAccessibleContext().setAccessibleDescription(
                            "Allows the user to control which parts of the model are displayed for editing.");
                    toolsMenu.setEnabled(true);
                    SaveProfile.get().addRecent(currentFile.getPath());
                    updateRecent();
                    FileUtils.loadFile(this, currentFile);
                });
                recentMenu.add(item, recentMenu.getItemCount() - 2);
            }
        }
    }

    public EditableModel currentMDL() {
        if (currentModelPanel != null) {
            return currentModelPanel.getModel();
        } else {
            return null;
        }
    }

    public ModelEditorManager currentMDLDisp() {
        if (currentModelPanel != null) {
            return currentModelPanel.getModelEditorManager();
        } else {
            return null;
        }
    }

    @Override
    public void refreshUndo() {
        undo.setEnabled(undo.funcEnabled());
        redo.setEnabled(redo.funcEnabled());
    }

    public void refreshController() {
        if (geoControl != null) {
            geoControl.repaint();
        }
        if (geoControlModelData != null) {
            geoControlModelData.repaint();
        }
    }

    public void setMouseCoordDisplay(final byte dim1, final byte dim2, final double value1, final double value2) {
        for (JTextField jTextField : mouseCoordDisplay) {
            jTextField.setText("");
        }
        mouseCoordDisplay[dim1].setText((float) value1 + "");
        mouseCoordDisplay[dim2].setText((float) value2 + "");
    }

    public static void closeOthers(MainPanel mainPanel, final ModelPanel panelToKeepOpen) {
        boolean success = true;
        final Iterator<ModelPanel> iterator = mainPanel.modelPanels.iterator();
        boolean closedCurrentPanel = false;
        ModelPanel lastUnclosedModelPanel = null;
        while (iterator.hasNext()) {
            final ModelPanel panel = iterator.next();
            if (panel == panelToKeepOpen) {
                lastUnclosedModelPanel = panel;
                continue;
            }
            if (success = panel.close(mainPanel)) {
                mainPanel.windowMenu.remove(panel.getMenuItem());
                iterator.remove();
                if (panel == mainPanel.currentModelPanel) {
                    closedCurrentPanel = true;
                }
            } else {
                lastUnclosedModelPanel = panel;
                break;
            }
        }
        if (closedCurrentPanel) {
            ModelPanelUgg.setCurrentModel(mainPanel, lastUnclosedModelPanel);
        }
    }

    protected void repaintSelfAndChildren(final ModelPanel mpanel) {
        repaint();
        geoControl.repaint();
        geoControlModelData.repaint();
        mpanel.repaintSelfAndRelatedChildren();
    }

    final TextureExporterImpl textureExporter = new TextureExporterImpl(this);

    @Override
    public void save(final EditableModel model) {
        if (model.getFile() != null) {
            model.saveFile();
        } else {
            FileUtils.onClickSaveAs(this, model);
        }
    }

    Component getFocusedComponent() {
        final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        final Component focusedComponent = kfm.getFocusOwner();
        return focusedComponent;
    }

    boolean focusedComponentNeedsTyping(final Component focusedComponent) {
        return (focusedComponent instanceof JTextArea) || (focusedComponent instanceof JTextField);
    }
}
