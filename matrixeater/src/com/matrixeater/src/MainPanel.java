package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.animedit.TimeEnvironmentImpl;
import com.hiveworkshop.wc3.gui.modeledit.*;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.activity.ActivityDescriptor;
import com.hiveworkshop.wc3.gui.modeledit.activity.ModelEditorChangeActivityListener;
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
        implements ActionListener, ModelEditorChangeActivityListener, ModelPanelCloseListener {
//    JMenuBar menuBar;
//    JMenu fileMenu, editMenu, mirrorSubmenu, tweaksSubmenu, importMenu, addMenu,
//            scriptsMenu, animationMenu, singleAnimationMenu, aboutMenu, fetch, viewMenu;
    JMenu recentMenu, toolsMenu, windowMenu, addParticle;

    JCheckBoxMenuItem mirrorFlip, fetchPortraitsToo, showNormals, textureModels, showVertexModifyControls;

    JMenuItem cut, copy, paste;
    final List<RecentItem> recentItems = new ArrayList<>();
//    UndoMenuItem undo;
//    RedoMenuItem redo;

    JRadioButtonMenuItem wireframe, solid;

    File currentFile;
    ImportPanel importPanel;
    static final ImageIcon POWERED_BY_HIVE = new ImageIcon(MainPanel.class.getResource("ImageBin/powered_by_hive.png"));
    protected static final boolean OLDMODE = false;
    final List<ModelPanel> modelPanels = new ArrayList<>();
    ModelPanel currentModelPanel;
    MainLayoutUgg mainLayoutUgg;
    ModelPanelUgg modelPanelUgg;
    MenuBar menuBar;

    boolean cheatShift = false;
    boolean cheatAlt = false;
    SaveProfile profile = SaveProfile.get();
    ProgramPreferences prefs = profile.getPreferences();

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
    final RootWindow rootWindow;

    ActivityDescriptor currentActivity;

    public MainPanel() {
        super();
        ToolBar toolBar = new ToolBar(this);
        add(toolBar.toolBar);

        modelPanelUgg = new ModelPanelUgg();
        menuBar = MainFrame.getMBar();

        animatedRenderEnvironment = new TimeEnvironmentImpl();
        animatedRenderEnvironment.addChangeListener(MainPanelActions.animatedRenderEnvironmentChangeListener(this));

//        creatorPanel = new CreatorModelingPanel(newType -> {actionTypeGroup.maybeSetButtonType(newType); MainPanel.this.changeActivity(newType);}, prefs, actionTypeGroup, activeViewportWatcher, animatedRenderEnvironment);

        mainLayoutUgg = new MainLayoutUgg(this);
//        mainLayoutUgg.createEditTabViews(this);

        actionTypeGroup.addToolbarButtonListener(MainPanelActions.createActionTypeGroupButtonListener(this));
        actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);



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

        final GroupLayout layout = new GroupLayout(this);

        rootWindow.setWindow(mainLayoutUgg.startupTabWindow);
        rootWindow.getRootWindowProperties().getFloatingWindowProperties().setUseFrame(true);
        mainLayoutUgg.startupTabWindow.setSelectedTab(0);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(toolBar.toolBar).addComponent(rootWindow));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(toolBar.toolBar).addComponent(rootWindow));
        setLayout(layout);


        selectionItemTypeGroup.addToolbarButtonListener(MainPanelActions.createSelectionItemTypesButtonListener(this));


        final JLabel[] divider = new JLabel[3];
        for (int i = 0; i < divider.length; i++) {
            divider[i] = new JLabel("----------");
        }

        BLPPanel blpPanel = new BLPPanel(null);
    }

    @Override
    public void changeActivity(final ActivityDescriptor newType) {
        this.currentActivity = newType;
        for (final ModelPanel modelPanel : modelPanels) {
            modelPanel.changeActivity(newType);
        }
        mainLayoutUgg.editTab.creatorPanel.changeActivity(newType);
    }

    static final Quaternion IDENTITY = new Quaternion();
    final TimeEnvironmentImpl animatedRenderEnvironment;
    JButton snapButton;
    protected ModelEditorActionType actionType;

//    CreatorModelingPanel creatorPanel;


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

        root.getActionMap().put("Delete", new FunctionalAction(e -> MainPanelActions.deleteAction(this)));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("DELETE"), "Delete");

//        root.getActionMap().put("CloneSelection", MainPanelActions.cloneActionUgg(this));
        root.getActionMap().put("CloneSelection", new FunctionalAction(e -> MainPanelActions.cloneAction(this)));

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
                    mainLayoutUgg.editTab.timeSliderPanel.jumpRight();
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
                    mainLayoutUgg.editTab.timeSliderPanel.jumpLeft();
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
                mainLayoutUgg.editTab.timeSliderPanel.play();
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

        root.getActionMap().put("shiftSelect", new FunctionalAction(e -> MainPanelActions.getShiftSelectAction(this,  selectionModeGroup.getActiveButtonType() == SelectionMode.SELECT, SelectionMode.ADD, true)));
        root.getActionMap().put("altSelect", new FunctionalAction(e -> MainPanelActions.getAltSelectAction(this, selectionModeGroup.getActiveButtonType() == SelectionMode.SELECT, SelectionMode.DESELECT, true)));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) .put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        // .put(KeyStroke.getKeyStroke("control pressed CONTROL"), "shiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt pressed ALT"), "altSelect");

        root.getActionMap().put("unShiftSelect", new FunctionalAction(e -> MainPanelActions.getShiftSelectAction(this, (selectionModeGroup.getActiveButtonType() == SelectionMode.ADD) && cheatShift, SelectionMode.SELECT, false)));
        root.getActionMap().put("unAltSelect", new FunctionalAction(e -> MainPanelActions.getAltSelectAction(this, (selectionModeGroup.getActiveButtonType() == SelectionMode.DESELECT) && cheatAlt, SelectionMode.SELECT, false)));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released SHIFT"), "unShiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released
        // CONTROL"),
        // "unShiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released ALT"), "unAltSelect");

        root.getActionMap().put("Select All", MainPanelActions.selectAllAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control A"), "Select All");

        root.getActionMap().put("Invert Selection", MainPanelActions.invertSelectAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control I"), "Invert Selection");

        root.getActionMap().put("Expand Selection", new FunctionalAction(e -> MainPanelActions.expandSelectionAction(this)));
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
                    mainLayoutUgg.editTab.timeSliderPanel.jumpFrames(deltaFrames);
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
        menuBar.refreshUndo();
    }

    void nullModelUgg() {
        FileUtils.nullModelFile(this);
        modelPanelUgg.refreshController();
    }

    void scaleAnimationsUgg() {
        final AnimationFrame aFrame = new AnimationFrame(currentModelPanel, mainLayoutUgg.editTab.timeSliderPanel::revalidateKeyframeDisplay);
        aFrame.setVisible(true);
    }

    void fetchAndAddAnimationFromFile(String filepath) {
        final EditableModel current = currentMDL();
        final String mdxFilepath = MenuBar.convertPathToMDX(filepath);
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


    void dataSourcesChanged() {
        for (final ModelPanel modelPanel : modelPanels) {
            final PerspDisplayPanel pdp = modelPanel.getPerspArea();
            pdp.reloadAllTextures();
            modelPanel.getAnimationViewer().reloadAllTextures();
        }
        directoryChangeNotifier.dataSourcesChanged();
    }

    GameObject fetchUnit() {
        final GameObject choice = UnitOptionPane.show(this);
        if (choice != null) {
            String filepath = choice.getField("file");

            try {
                filepath = MenuBar.convertPathToMDX(filepath);
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

    ModelOptionPane.ModelElement fetchModel() {
        final ModelOptionPane.ModelElement model = ModelOptionPane.showAndLogIcon(this);
        if (model == null) {
            return null;
        }
        String filepath = model.getFilepath();
        if (filepath != null) {
            try {
                filepath = MenuBar.convertPathToMDX(filepath);
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
            filepath = MenuBar.convertPathToMDX(filepath);
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

//    @Override
//    public void refreshUndo() {
//        menuBar.undo.setEnabled(menuBar.undo.funcEnabled());
//        menuBar.redo.setEnabled(menuBar.redo.funcEnabled());
//    }

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
            mainPanel.modelPanelUgg.setCurrentModel(mainPanel, lastUnclosedModelPanel);
        }
    }

    protected void repaintSelfAndChildren(final ModelPanel mpanel) {
        repaint();
        modelPanelUgg.geoControl.repaint();
        modelPanelUgg.geoControlModelData.repaint();
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
