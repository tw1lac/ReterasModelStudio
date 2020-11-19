package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.animedit.ControllableTimeBoundProvider;
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
import com.hiveworkshop.wc3.gui.mpqbrowser.MPQBrowser;
import com.hiveworkshop.wc3.jworldedit.models.BetterUnitEditorModelSelector;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorSettings;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTree;
import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTreeBrowser;
import com.hiveworkshop.wc3.jworldedit.objects.UnitTabTreeBrowserBuilder;
import com.hiveworkshop.wc3.mdl.*;
import com.hiveworkshop.wc3.mdl.v2.ModelView;
import com.hiveworkshop.wc3.mdx.MdxModel;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.resources.WEString;
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
import de.wc3data.stream.BlizzardDataOutputStream;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.StringViewMap;
import net.infonode.tabbedpanel.TabAreaVisiblePolicy;
import net.infonode.tabbedpanel.titledtab.TitledTabBorderSizePolicy;
import net.infonode.tabbedpanel.titledtab.TitledTabSizePolicy;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.lwjgl.util.vector.Quaternion;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
    JMenu fileMenu, recentMenu, editMenu, toolsMenu, mirrorSubmenu, tweaksSubmenu, viewMenu, importMenu, addMenu,
            scriptsMenu, windowMenu, addParticle, animationMenu, singleAnimationMenu, aboutMenu, fetch;
    JCheckBoxMenuItem mirrorFlip, fetchPortraitsToo, showNormals, textureModels, showVertexModifyControls;
    //	ArrayList geoItems = new ArrayList();
    JMenuItem newModel, open, fetchUnit, fetchModel, fetchObject, save, close, exit, revert, mergeGeoset, saveAs,
            importButton, importUnit, importGameModel, importGameObject, importFromWorkspace, importButtonScript,
            newDirectory, creditsButton, changelogButton, clearRecent, nullmodelButton, selectAll, invertSelect,
            expandSelection, snapNormals, snapVertices, flipAllUVsU, flipAllUVsV, inverseAllUVs, mirrorX, mirrorY,
            mirrorZ, insideOut, insideOutNormals, showMatrices, editUVs, exportTextures, editTextures, scaleAnimations,
            animationViewer, animationController, modelingTab, mpqViewer, hiveViewer, unitViewer, preferencesWindow,
            linearizeAnimations, sortBones, simplifyKeyframes, rigButton, duplicateSelection, riseFallBirth,
            animFromFile, animFromUnit, animFromModel, animFromObject, teamColor, teamGlow;
    JMenuItem cut, copy, paste;
    final List<RecentItem> recentItems = new ArrayList<>();
    UndoMenuItem undo;
    RedoMenuItem redo;

    JMenu viewMode;
    JRadioButtonMenuItem wireframe, solid;
    ButtonGroup viewModes;

    final JFileChooser fc;
    final JFileChooser exportTextureDialog;
    final FileFilter filter;
    final File filterFile;
    File currentFile;
    ImportPanel importPanel;
    static final ImageIcon MDLIcon = new ImageIcon(MainPanel.class.getResource("ImageBin/MDLIcon_16.png"));
    static final ImageIcon POWERED_BY_HIVE = new ImageIcon(MainPanel.class.getResource("ImageBin/powered_by_hive.png"));
    public static final ImageIcon AnimIcon = new ImageIcon(MainPanel.class.getResource("ImageBin/Anim.png"));
    protected static final boolean OLDMODE = false;
    boolean loading;
    final List<ModelPanel> modelPanels;
    ModelPanel currentModelPanel;
    final View frontView;
    final View leftView;
    final View bottomView;
    final View perspectiveView;
    final View timeSliderView;
    private final View hackerView;
    final View previewView;
    final View creatorView;
    final View animationControllerView;
    JScrollPane geoControl;
    JScrollPane geoControlModelData;
    final JTextField[] mouseCoordDisplay = new JTextField[3];
    boolean cheatShift = false;
    boolean cheatAlt = false;
    final SaveProfile profile = SaveProfile.get();
    final ProgramPreferences prefs = profile.getPreferences();

    JToolBar toolbar;

    final TimeSliderPanel timeSliderPanel;
    final JButton setKeyframe;
    final JButton setTimeBounds;
    final ModeButton animationModeButton;
    boolean animationModeState = false;

    final ActiveViewportWatcher activeViewportWatcher = new ActiveViewportWatcher();

    final WarcraftDataSourceChangeNotifier directoryChangeNotifier = new WarcraftDataSourceChangeNotifier();

    final JMenuItem contextClose;
    final JMenuItem contextCloseAll;
    final JMenuItem contextCloseOthers;
    int contextClickedTab = 0;
    final JPopupMenu contextMenu;

    final AbstractAction undoAction = new UndoActionImplementation("Undo", this);
    final AbstractAction redoAction = new RedoActionImplementation("Redo", this);
    final ClonedNodeNamePicker namePicker = new ClonedNodeNamePickerImplementation(this);


    final AbstractAction openAnimationViewerAction;
    final AbstractAction openAnimationControllerAction;
    final AbstractAction openModelingTabAction;
    final AbstractAction openPerspectiveAction;
    final AbstractAction openOutlinerAction;
    final AbstractAction openSideAction;
    final AbstractAction openTimeSliderAction;
    final AbstractAction openFrontAction;
    final AbstractAction openBottomAction;
    final AbstractAction openToolsAction;
    final AbstractAction openModelDataContentsViewAction;
    final AbstractAction hackerViewAction;

    ToolbarButtonGroup<SelectionItemTypes> selectionItemTypeGroup;
    ToolbarButtonGroup<SelectionMode> selectionModeGroup;
    ToolbarButtonGroup<ToolbarActionButtonType> actionTypeGroup;
    final ModelStructureChangeListener modelStructureChangeListener;
    final ViewportTransferHandler viewportTransferHandler;
    final RootWindow rootWindow;
    final View viewportControllerWindowView;
    final View toolView;
    final View modelDataView;
    final View modelComponentView;
    ControllableTimeBoundProvider timeBoundProvider;
    ActivityDescriptor currentActivity;

    public MainPanel() {
        super();

        StringViewMap viewMap = new StringViewMap();
        rootWindow = new RootWindow(viewMap);
        rootWindow.addListener(MainPanelActions.createDockingWindowListener(this));

        add(ToolBar.createJToolBar(this));
        // testArea = new PerspDisplayPanel("Graphic Test",2,0);
        // //botArea.setViewport(0,1);
        // add(testArea);

        final JLabel[] divider = new JLabel[3];
        for (int i = 0; i < divider.length; i++) {
            divider[i] = new JLabel("----------");
        }
        for (int i = 0; i < mouseCoordDisplay.length; i++) {
            mouseCoordDisplay[i] = new JTextField("");
            mouseCoordDisplay[i].setMaximumSize(new Dimension(80, 18));
            mouseCoordDisplay[i].setMinimumSize(new Dimension(50, 15));
            mouseCoordDisplay[i].setEditable(false);
        }
        modelStructureChangeListener = new ModelStructureChangeListenerImplementation(this, () -> ModelPanelUgg.currentModelPanel(currentModelPanel).getModel());
        animatedRenderEnvironment = new TimeEnvironmentImpl();
        BLPPanel blpPanel = new BLPPanel(null);
        timeSliderPanel = new TimeSliderPanel(animatedRenderEnvironment, modelStructureChangeListener, prefs);
        timeSliderPanel.setDrawing(false);
        timeSliderPanel.addListener(MainPanelActions.createTimeSliderTimeListener(this));
//		timeSliderPanel.addListener(creatorPanel);
        animatedRenderEnvironment.addChangeListener(MainPanelActions.animatedRenderEnvironmentChangeListener(this));
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

        //TODO stuff that should be created using functions
        contextMenu = new JPopupMenu();
        contextClose = new JMenuItem("Close");
        contextClose.addActionListener(this);
        contextMenu.add(contextClose);

        contextCloseOthers = new JMenuItem("Close Others");
        contextCloseOthers.addActionListener(this);
        contextMenu.add(contextCloseOthers);

        contextCloseAll = new JMenuItem("Close All");
        contextCloseAll.addActionListener(this);
        contextMenu.add(contextCloseAll);

        modelPanels = new ArrayList<>();
        final JPanel toolsPanel = new JPanel();
        toolsPanel.setMaximumSize(new Dimension(30, 999999));
        final GroupLayout layout = new GroupLayout(this);
        toolbar.setMaximumSize(new Dimension(80000, 48));
        final JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("..."));
        viewportControllerWindowView = new View("Outliner", null, jPanel);// GlobalIcons.geoIcon
//		viewportControllerWindowView.getWindowProperties().setCloseEnabled(false);
//		viewportControllerWindowView.getWindowProperties().setMaximizeEnabled(true);
//		viewportControllerWindowView.getWindowProperties().setMinimizeEnabled(true);
//		viewportControllerWindowView.getWindowProperties().setRestoreEnabled(true);
        toolView = new View("Tools", null, new JPanel());
        final JPanel contentsDummy = new JPanel();
        contentsDummy.add(new JLabel("..."));
        modelDataView = new View("Contents", null, contentsDummy);
        modelComponentView = new View("Component", null, new JPanel());
//		toolView.getWindowProperties().setCloseEnabled(false);
        rootWindow.getWindowProperties().getTabProperties().getTitledTabProperties().setSizePolicy(TitledTabSizePolicy.EQUAL_SIZE);
        rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setVisible(true);
        rootWindow.getWindowProperties().getTabProperties().getTitledTabProperties().setBorderSizePolicy(TitledTabBorderSizePolicy.EQUAL_SIZE);
        rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getTabAreaProperties().setTabAreaVisiblePolicy(TabAreaVisiblePolicy.MORE_THAN_ONE_TAB);
        rootWindow.setBackground(Color.GREEN);
        rootWindow.setForeground(Color.GREEN);
        final Runnable fixit = () -> {
            traverseAndReset(rootWindow);
            traverseAndFix(rootWindow);
        };
        rootWindow.addListener(MainPanelActions.createDockingWindowListener(fixit));

        leftView = new View("Side", null, new JPanel());
        frontView = new View("Front", null, new JPanel());
        bottomView = new View("Bottom", null, new JPanel());
        perspectiveView = new View("Perspective", null, new JPanel());
        previewView = new View("Preview", null, new JPanel());
        final JPanel timeSliderAndExtra = new JPanel();
        final GroupLayout tsaeLayout = new GroupLayout(timeSliderAndExtra);
        final Component horizontalGlue = Box.createHorizontalGlue();
        final Component verticalGlue = Box.createVerticalGlue();
        tsaeLayout.setHorizontalGroup(
                tsaeLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(timeSliderPanel)
                        .addGroup(tsaeLayout.createSequentialGroup().addComponent(mouseCoordDisplay[0])
                                .addComponent(mouseCoordDisplay[1]).addComponent(mouseCoordDisplay[2])
                                .addComponent(horizontalGlue).addComponent(setKeyframe).addComponent(setTimeBounds)));
        tsaeLayout.setVerticalGroup(tsaeLayout.createSequentialGroup().addComponent(timeSliderPanel).addGroup(
                tsaeLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(mouseCoordDisplay[0])
                        .addComponent(mouseCoordDisplay[1]).addComponent(mouseCoordDisplay[2])
                        .addComponent(horizontalGlue).addComponent(setKeyframe).addComponent(setTimeBounds)));
        timeSliderAndExtra.setLayout(tsaeLayout);

        timeSliderView = new View("Footer", null, timeSliderAndExtra);
        final JPanel hackerPanel = new JPanel(new BorderLayout());
        final RSyntaxTextArea matrixEaterScriptTextArea = new RSyntaxTextArea(20, 60);
        matrixEaterScriptTextArea.setCodeFoldingEnabled(true);
        matrixEaterScriptTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        hackerPanel.add(new RTextScrollPane(matrixEaterScriptTextArea), BorderLayout.CENTER);
        final JButton run = new JButton("Run",
                new ImageIcon(BLPHandler.get().getGameTex("ReplaceableTextures\\CommandButtons\\BTNReplay-Play.blp")
                        .getScaledInstance(24, 24, Image.SCALE_FAST)));
        run.addActionListener(MainPanelActions.createBtnReplayPlayActionListener(this, matrixEaterScriptTextArea));
        hackerPanel.add(run, BorderLayout.NORTH);
        hackerView = new View("Matrix Eater Script", null, hackerPanel);
        creatorPanel = new CreatorModelingPanel(newType -> {
            actionTypeGroup.maybeSetButtonType(newType);
            MainPanel.this.changeActivity(newType);
        }, prefs, actionTypeGroup, activeViewportWatcher, animatedRenderEnvironment);

        creatorView = new View("Modeling", null, creatorPanel);
        animationControllerView = new View("Animation Controller", null, new JPanel());

        final TabWindow startupTabWindow = MainLayoutUgg.createMainLayout(this);
        rootWindow.setWindow(startupTabWindow);
        rootWindow.getRootWindowProperties().getFloatingWindowProperties().setUseFrame(true);
        startupTabWindow.setSelectedTab(0);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(toolbar)
                .addComponent(rootWindow));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(toolbar).addComponent(rootWindow));
        setLayout(layout);
        // Create a file chooser
        fc = new JFileChooser();
        filterFile = new File("", ".mdl");
        filter = new MDLFilter();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Binary Model '-.mdx'", "mdx"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Warcraft III Texture '-.blp'", "blp"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image '-.png'", "png"));
        fc.addChoosableFileFilter(filter);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Wavefront OBJ '-.obj'", "obj"));
        exportTextureDialog = new JFileChooser();
        exportTextureDialog.setDialogTitle("Export Texture");
        final String[] imageTypes = ImageIO.getWriterFileSuffixes();
        for (final String suffix : imageTypes) {
            exportTextureDialog
                    .addChoosableFileFilter(new FileNameExtensionFilter(suffix.toUpperCase() + " Image File", suffix));
        }

        // getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
        // Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo" );

        // getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
        // Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo" );

        // setFocusable(true);
        // selectButton.requestFocus();
        selectionItemTypeGroup.addToolbarButtonListener(MainPanelActions.createSelectionItemTypesButtonListener(this));

        actionTypeGroup.addToolbarButtonListener(MainPanelActions.createActionTypeGroupButtonListener(this));
        actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);
        viewportTransferHandler = new ViewportTransferHandler();
        coordDisplayListener = MainPanel.this::setMouseCoordDisplay;
        openAnimationViewerAction = new OpenViewAction(rootWindow, "Animation Preview", () -> previewView);
        openAnimationControllerAction = new OpenViewAction(rootWindow, "Animation Controller", () -> animationControllerView);
        openModelingTabAction = new OpenViewAction(rootWindow, "Modeling", () -> creatorView);
        openPerspectiveAction = new OpenViewAction(rootWindow, "Perspective", () -> perspectiveView);
        openOutlinerAction = new OpenViewAction(rootWindow, "Outliner", () -> viewportControllerWindowView);
        openSideAction = new OpenViewAction(rootWindow, "Side", () -> leftView);
        openTimeSliderAction = new OpenViewAction(rootWindow, "Footer", () -> timeSliderView);
        openFrontAction = new OpenViewAction(rootWindow, "Front", () -> frontView);
        openBottomAction = new OpenViewAction(rootWindow, "Bottom", () -> bottomView);
        openToolsAction = new OpenViewAction(rootWindow, "Tools", () -> toolView);
        openModelDataContentsViewAction = new OpenViewAction(rootWindow, "Model", () -> modelDataView);
        hackerViewAction = new OpenViewAction(rootWindow, "Matrix Eater Script", () -> hackerView);
    }

    void traverseAndFix(final DockingWindow window) {
        final boolean tabWindow = window instanceof TabWindow;
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndFix(childWindow);
            if (tabWindow && (childWindowCount != 1) && (childWindow instanceof View)) {
                final View view = (View) childWindow;
                view.getViewProperties().getViewTitleBarProperties().setVisible(false);
            }
        }
    }

    void traverseAndReset(final DockingWindow window) {
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndReset(childWindow);
            if (childWindow instanceof View) {
                final View view = (View) childWindow;
                view.getViewProperties().getViewTitleBarProperties().setVisible(true);
            }
        }
    }

    void traverseAndReloadData(final DockingWindow window) {
        final int childWindowCount = window.getChildWindowCount();
        for (int i = 0; i < childWindowCount; i++) {
            final DockingWindow childWindow = window.getChildWindow(i);
            traverseAndReloadData(childWindow);
            if (childWindow instanceof View) {
                final View view = (View) childWindow;
                final Component component = view.getComponent();
                if (component instanceof JScrollPane) {
                    final JScrollPane pane = (JScrollPane) component;
                    final Component viewportView = pane.getViewport().getView();
                    if (viewportView instanceof UnitEditorTree) {
                        final UnitEditorTree unitEditorTree = (UnitEditorTree) viewportView;
                        final WorldEditorDataType dataType = unitEditorTree.getDataType();
                        if (dataType == WorldEditorDataType.UNITS) {
                            System.out.println("saw unit tree");
                            unitEditorTree.setUnitDataAndReloadVerySlowly(getUnitData());
                        } else if (dataType == WorldEditorDataType.DOODADS) {
                            System.out.println("saw doodad tree");
                            unitEditorTree.setUnitDataAndReloadVerySlowly(getDoodadData());
                        }
                    }
                } else if (component instanceof MPQBrowser) {
                    System.out.println("saw mpq tree");
                    final MPQBrowser comp = (MPQBrowser) component;
                    comp.refreshTree();
                }
            }
        }
    }

    UnitEditorTree createUnitEditorTree() {
        final UnitEditorTree unitEditorTree = new UnitEditorTreeBrowser(getUnitData(), new UnitTabTreeBrowserBuilder(),
                getUnitEditorSettings(), WorldEditorDataType.UNITS, (mdxFilePath, b, c, icon) -> FileUtils.loadStreamMdx(MainPanel.this, MpqCodebase.get().getResourceAsStream(mdxFilePath), b, c, icon), prefs);
        return unitEditorTree;
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
    JMenu teamColorMenu;
    CreatorModelingPanel creatorPanel;

    //	public void reloadGUI() {
//		refreshUndo();
//		refreshController();
//		refreshAnimationModeState();
//		reloadGeosetManagers(currentModelPanel());
//	}

    /**
     * Right now this is a plug to the statics to load unit data. However, it's a
     * non-static method so that we can have it load from an opened map in the
     * future -- the MutableObjectData class can parse map unit data!
     */
    public MutableObjectData getUnitData() {
        final War3ObjectDataChangeset editorData = new War3ObjectDataChangeset('u');
        try {
            final MpqCodebase mpqCodebase = MpqCodebase.get();
            if (mpqCodebase.has("war3map.w3u")) {
                editorData.load(new BlizzardDataInputStream(mpqCodebase.getResourceAsStream("war3map.w3u")),
                        mpqCodebase.has("war3map.wts") ? new WTSFile(mpqCodebase.getResourceAsStream("war3map.wts"))
                                : null,
                        true);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MutableObjectData(WorldEditorDataType.UNITS, StandardObjectData.getStandardUnits(),
                StandardObjectData.getStandardUnitMeta(), editorData);
    }

    public MutableObjectData getDoodadData() {
        final War3ObjectDataChangeset editorData = new War3ObjectDataChangeset('d');
        try {
            final MpqCodebase mpqCodebase = MpqCodebase.get();
            if (mpqCodebase.has("war3map.w3d")) {
                editorData.load(new BlizzardDataInputStream(mpqCodebase.getResourceAsStream("war3map.w3d")),
                        mpqCodebase.has("war3map.wts") ? new WTSFile(mpqCodebase.getResourceAsStream("war3map.wts"))
                                : null,
                        true);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MutableObjectData(WorldEditorDataType.DOODADS, StandardObjectData.getStandardDoodads(),
                StandardObjectData.getStandardDoodadMeta(), editorData);
    }

    public UnitEditorSettings getUnitEditorSettings() {
        return new UnitEditorSettings();
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
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("SPACE"),
                "MaximizeSpacebar");

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
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("RIGHT"),
                "PressRight");
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
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("LEFT"),
                "PressLeft");
        root.getActionMap().put("PressUp", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpFrames(1);
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("UP"), "PressUp");
        root.getActionMap().put("PressShiftUp", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpFrames(10);
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("shift UP"),
                "PressShiftUp");
        root.getActionMap().put("PressDown", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpFrames(-1);
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("DOWN"),
                "PressDown");
        root.getActionMap().put("PressShiftDown", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (animationModeState) {
                    timeSliderPanel.jumpFrames(-10);
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("shift DOWN"),
                "PressShiftDown");

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control SPACE"),
                "PlayKeyboardKey");
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
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("W"),
                "QKeyboardKey");
        root.getActionMap().put("QKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("E"),
                "WKeyboardKey");
        root.getActionMap().put("WKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[1]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("R"),
                "EKeyboardKey");
        root.getActionMap().put("EKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[2]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("T"),
                "RKeyboardKey");
        root.getActionMap().put("RKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (!animationModeState) {
                    actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[3]);
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Y"),
                "TKeyboardKey");
        root.getActionMap().put("TKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (!animationModeState) {
                    actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[4]);
                }
            }
        });

        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("A"),
                "AKeyboardKey");
        root.getActionMap().put("AKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                selectionItemTypeGroup.setToolbarButtonType(selectionItemTypeGroup.getToolbarButtonTypes()[0]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("S"),
                "SKeyboardKey");
        root.getActionMap().put("SKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                selectionItemTypeGroup.setToolbarButtonType(selectionItemTypeGroup.getToolbarButtonTypes()[1]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("D"),
                "DKeyboardKey");
        root.getActionMap().put("DKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                selectionItemTypeGroup.setToolbarButtonType(selectionItemTypeGroup.getToolbarButtonTypes()[2]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F"),
                "FKeyboardKey");
        root.getActionMap().put("FKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                selectionItemTypeGroup.setToolbarButtonType(selectionItemTypeGroup.getToolbarButtonTypes()[3]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("G"),
                "GKeyboardKey");
        root.getActionMap().put("GKeyboardKey", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                selectionItemTypeGroup.setToolbarButtonType(selectionItemTypeGroup.getToolbarButtonTypes()[4]);
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Z"),
                "ZKeyboardKey");
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
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control F"),
                "CreateFaceShortcut");
        root.getActionMap().put("CreateFaceShortcut", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (!animationModeState) {
                    try {
                        final ModelPanel modelPanel = ModelPanelUgg.currentModelPanel(currentModelPanel);
                        if (modelPanel != null) {
                            final Viewport viewport = activeViewportWatcher.getViewport();
                            final Vertex facingVector = viewport == null ? new Vertex(0, 0, 1)
                                    : viewport.getFacingVector();
                            final UndoAction createFaceFromSelection = modelPanel.getModelEditorManager()
                                    .getModelEditor().createFaceFromSelection(facingVector);
                            modelPanel.getUndoManager().pushAction(createFaceFromSelection);
                        }
                    } catch (final FaceCreationException exc) {
                        JOptionPane.showMessageDialog(MainPanel.this, exc.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (final Exception exc) {
                        ExceptionPopup.display(exc);
                    }
                }
            }
        });
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
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        // .put(KeyStroke.getKeyStroke("control pressed CONTROL"), "shiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt pressed ALT"),
                "altSelect");

        root.getActionMap().put("unShiftSelect", MainPanelActions.getShiftSelectAction(this, "unShiftSelect", (selectionModeGroup.getActiveButtonType() == SelectionMode.ADD) && cheatShift, SelectionMode.SELECT, false));
        root.getActionMap().put("unAltSelect", MainPanelActions.getAltSelectAction(this, "unAltSelect", (selectionModeGroup.getActiveButtonType() == SelectionMode.DESELECT) && cheatAlt, SelectionMode.SELECT, false));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released SHIFT"),
                "unShiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released
        // CONTROL"),
        // "unShiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released ALT"),
                "unAltSelect");

        root.getActionMap().put("Select All", MainPanelActions.selectAllAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control A"),
                "Select All");

        root.getActionMap().put("Invert Selection", MainPanelActions.invertSelectAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control I"),
                "Invert Selection");

        root.getActionMap().put("Expand Selection", MainPanelActions.expandSelectionAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control E"),
                "Expand Selection");

        root.getActionMap().put("RigAction", MainPanelActions.rigAction(this));
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control W"),
                "RigAction");
    }

    void updateUIFromProgramPreferences() {
        // prefs.setShowVertexModifierControls(showVertexModifyControls.isSelected());
        showVertexModifyControls.setSelected(prefs.isShowVertexModifierControls());
        // prefs.setTextureModels(textureModels.isSelected());
        textureModels.setSelected(prefs.isTextureModels());
        // prefs.setShowNormals(showNormals.isSelected());
        showNormals.setSelected(prefs.isShowNormals());
        // prefs.setLoadPortraits(true);
        fetchPortraitsToo.setSelected(prefs.isLoadPortraits());
        // prefs.setUseNativeMDXParser(useNativeMDXParser.isSelected());
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
            mpanel.getEditorRenderModel()
                    .setSpawnParticles((prefs.getRenderParticles() == null) || prefs.getRenderParticles());
            mpanel.getEditorRenderModel().setAllowInanimateParticles(
                    (prefs.getRenderStaticPoseParticles() == null) || prefs.getRenderStaticPoseParticles());
            mpanel.getAnimationViewer()
                    .setSpawnParticles((prefs.getRenderParticles() == null) || prefs.getRenderParticles());
        }
    }

    private static JMenuItem createMenuItem(MainPanel mainPanel, String itemText, int keyEvent, JMenu menu) {
        return createMenuItem(itemText, keyEvent, menu, mainPanel, null);
    }

    private static JMenuItem createMenuItem(MainPanel mainPanel, String itemText, int keyEvent, JMenu menu, KeyStroke keyStroke) {
        return createMenuItem(itemText, keyEvent, menu, mainPanel, keyStroke);
    }

    private static JMenuItem createMenuItem(String itemText, int keyEvent, JMenu menu, ActionListener actionListener) {
        return createMenuItem(itemText, keyEvent, menu, actionListener, null);
    }

    private static JMenuItem createMenuItem(String itemText, int keyEvent, JMenu menu, ActionListener actionListener, KeyStroke keyStroke) {
        JMenuItem menuItem;
        menuItem = new JMenuItem(itemText);
        menuItem.setMnemonic(keyEvent);
        menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        return menuItem;
    }

    void createTeamColorMenuItems() {
        for (int i = 0; i < 25; i++) {
            final String colorNumber = String.format("%2s", i).replace(' ', '0');
            try {
                final String colorName = WEString.getString("WESTRING_UNITCOLOR_" + colorNumber);
                final JMenuItem menuItem = new JMenuItem(colorName, new ImageIcon(BLPHandler.get()
                        .getGameTex("ReplaceableTextures\\TeamColor\\TeamColor" + colorNumber + ".blp")));
                teamColorMenu.add(menuItem);
                final int teamColorValueNumber = i;
                menuItem.addActionListener(e -> {
                    Material.teamColor = teamColorValueNumber;
                    final ModelPanel modelPanel = ModelPanelUgg.currentModelPanel(currentModelPanel);
                    if (modelPanel != null) {
                        modelPanel.getAnimationViewer().reloadAllTextures();
                        modelPanel.getPerspArea().reloadAllTextures();

                        ModelPanelUgg.reloadComponentBrowser(geoControlModelData, modelPanel);
                    }
                    profile.getPreferences().setTeamColor(teamColorValueNumber);
                });
            } catch (final Exception ex) {
                // load failed
                break;
            }
        }
    }




    @Override
    public void actionPerformed(final ActionEvent e) {
        // Open, off of the file menu:
        refreshUndo();
        try {
            if (e.getSource() == newModel) {
                NewModelPanel.newModel(this);
            } else if (e.getSource() == open) {
                onClickOpen();
            } else if (e.getSource() == close) {
                onClickClose();
            } else if (e.getSource() == fetchUnit) {
                fetchUnitButtonResponse();
            } else if (e.getSource() == fetchModel) {
                fetchModelButtonResponse();
            } else if (e.getSource() == fetchObject) {
                fetchObjectButtonResponse();
            } else if (e.getSource() == importButton) {
                importFromFile();
            } else if (e.getSource() == importUnit) {
                importUnit();
            } else if (e.getSource() == importGameModel) {
                importGameModel();
            } else if (e.getSource() == importGameObject) {
                importGameObject();
            } else if (e.getSource() == importFromWorkspace) {
                importFromWorkspace();
            } else if (e.getSource() == importButtonScript) {
                importScript();
            } else if (e.getSource() == mergeGeoset) {
                mergeGeoset();
            } else if (e.getSource() == nullmodelButton) {
                FileUtils.nullModelFile(this);
                refreshController();
            } else if ((e.getSource() == save) && (currentMDL() != null) && (currentMDL().getFile() != null)) {
                onClickSave();
            } else if (e.getSource() == saveAs) {
                onClickSaveAs();
            } else if (e.getSource() == contextCloseAll) {
                MainPanel.closeAll(this);
            } else if (e.getSource() == contextCloseOthers) {
                MainPanel.closeOthers(this, currentModelPanel);
            } else if (e.getSource() == textureModels) {
                prefs.setTextureModels(textureModels.isSelected());
            } else if (e.getSource() == showNormals) {
                prefs.setShowNormals(showNormals.isSelected());
            } else if (e.getSource() == editUVs) {
                ModelPanelUgg.editUVs(this);
            } else if (e.getSource() == exportTextures) {
                exportTextures();
            } else if (e.getSource() == scaleAnimations) {
                // if( disp.animpanel == null )
                // {
                // AnimationPanel panel = new UVPanel(disp);
                // disp.setUVPanel(panel);
                // panel.showFrame();
                // }
                // else if(!disp.uvpanel.frameVisible() )
                // {
                // disp.uvpanel.showFrame();
                // }
                final AnimationFrame aFrame = new AnimationFrame(ModelPanelUgg.currentModelPanel(currentModelPanel), timeSliderPanel::revalidateKeyframeDisplay);
                aFrame.setVisible(true);
            } else if (e.getSource() == linearizeAnimations) {
                linearizeAnimations();
            } else if (e.getSource() == duplicateSelection) {
                ModelPanelUgg.duplicateSelection(namePicker, currentModelPanel);
            } else if (e.getSource() == simplifyKeyframes) {
                simplifyKeyframesButtonResponse();
            } else if (e.getSource() == riseFallBirth) {
                riseFallBirth();
            } else if (e.getSource() == animFromFile) {
                animFromFile();
            } else if (e.getSource() == animFromUnit) {
                if (animFromUnit()) return;
            } else if (e.getSource() == animFromModel) {
                if (animFromModel()) return;
            } else if (e.getSource() == animFromObject) {
                if (animFromObject()) return;
            } else if (e.getSource() == creditsButton) {
                CreditsPanel.showCreditsButtonResponse("credits.rtf", "About");
            } else if (e.getSource() == changelogButton) {
                CreditsPanel.showCreditsButtonResponse("changelist.rtf", "Changelog");
                // JOptionPane.showMessageDialog(this,new JScrollPane(epane));
            }
            // for( int i = 0; i < geoItems.size(); i++ )
            // {
            // JCheckBoxMenuItem geoItem = (JCheckBoxMenuItem)geoItems.get(i);
            // if( e.getSource() == geoItem )
            // {
            // frontArea.setGeosetVisible(i,geoItem.isSelected());
            // frontArea.setGeosetHighlight(i,false);
            // }
            // repaint();
            // }
        } catch (

                final Exception exc) {
            ExceptionPopup.display(exc);
        }
    }

    private boolean animFromObject() {
        fc.setDialogTitle("Animation Source");
        final MutableGameObject fetchResult = fetchObject();
        if (fetchResult != null) {
            return fetchAndAddAnimationFromFile(fetchResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
        }
        return true;
    }

    private boolean animFromModel() {
        fc.setDialogTitle("Animation Source");
        final ModelElement fetchResult = fetchModel();
        if (fetchResult != null) {
            return fetchAndAddAnimationFromFile(fetchResult.getFilepath());
        }
        return true;
    }

    private boolean fetchAndAddAnimationFromFile(String filepath) {
        final EditableModel current = currentMDL();
        final String mdxFilepath = convertPathToMDX(filepath);
        if (mdxFilepath != null) {
            final EditableModel animationSource = EditableModel.read(MpqCodebase.get().getFile(mdxFilepath));
            addSingleAnimation(current, animationSource);
        }
        return false;
    }

    private boolean animFromUnit() {
        fc.setDialogTitle("Animation Source");
        final GameObject fetchResult = fetchUnit();
        if (fetchResult != null) {
            return fetchAndAddAnimationFromFile(fetchResult.getField("file"));
        }
        return true;
    }

    private void animFromFile() {
        fc.setDialogTitle("Animation Source");
        final EditableModel current = currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (profile.getPath() != null) {
            fc.setCurrentDirectory(new File(profile.getPath()));
        }
        final int returnValue = fc.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFile = fc.getSelectedFile();
            profile.setPath(currentFile.getParent());
            final EditableModel animationSourceModel = EditableModel.read(currentFile);
            addSingleAnimation(current, animationSourceModel);
        }

        fc.setSelectedFile(null);

        refreshController();
    }

    private void riseFallBirth() {
        final int confirmed = JOptionPane.showConfirmDialog(this,
                "This will permanently alter model. Are you sure?", "Confirmation",
                JOptionPane.OK_CANCEL_OPTION);
        if (confirmed != JOptionPane.OK_OPTION) {
            return;
        }

        final ModelView disp = ModelPanelUgg.currentModelPanel(currentModelPanel).getModelViewManager();
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

    private void simplifyKeyframesButtonResponse() {
        final int x = JOptionPane.showConfirmDialog(this,
                "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify keyframes?",
                "Warning: Simplify Keyframes", JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            simplifyKeyframes();
        }
    }

    private void linearizeAnimations() {
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

    private void importScript() {
        final JFrame frame = new JFrame("Animation Transferer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new AnimationTransfer(frame));
        frame.setIconImage(MainPanel.AnimIcon.getImage());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void importFromFile() {
        fc.setDialogTitle("Import");
        final EditableModel current = currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (profile.getPath() != null) {
            fc.setCurrentDirectory(new File(profile.getPath()));
        }
        final int returnValue = fc.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFile = fc.getSelectedFile();
            profile.setPath(currentFile.getParent());
            toolsMenu.getAccessibleContext().setAccessibleDescription(
                    "Allows the user to control which parts of the model are displayed for editing.");
            toolsMenu.setEnabled(true);
            FileUtils.importFile(this, currentFile);
        }

        fc.setSelectedFile(null);
        refreshController();
    }

    private void onClickClose() {
        final ModelPanel modelPanel = ModelPanelUgg.currentModelPanel(currentModelPanel);
        final int oldIndex = modelPanels.indexOf(modelPanel);
        if (modelPanel != null) {
            if (modelPanel.close(this)) {
                modelPanels.remove(modelPanel);
                windowMenu.remove(modelPanel.getMenuItem());
                if (modelPanels.size() > 0) {
                    final int newIndex = Math.min(modelPanels.size() - 1, oldIndex);
                    ModelPanelUgg.setCurrentModel(this, modelPanels.get(newIndex));
                } else {
                    // TODO remove from notifiers to fix leaks
                    ModelPanelUgg.setCurrentModel(this, null);
                }
            }
        }
    }

    private void fetchUnitButtonResponse() {
        final GameObject unitFetched = fetchUnit();
        if (unitFetched != null) {
            final String filepath = convertPathToMDX(unitFetched.getField("file"));
            if (filepath != null) {
                FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(filepath), true, true,
                        unitFetched.getScaledIcon(0.25f));
                final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
                        + filepath.substring(filepath.lastIndexOf('.'));
                if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                    FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(portrait), true, false,
                            unitFetched.getScaledIcon(0.25f));
                }
                toolsMenu.getAccessibleContext().setAccessibleDescription(
                        "Allows the user to control which parts of the model are displayed for editing.");
                toolsMenu.setEnabled(true);
            }
        }
    }

    private void fetchModelButtonResponse() {
        final ModelElement model = fetchModel();
        if (model != null) {
            final String filepath = convertPathToMDX(model.getFilepath());
            if (filepath != null) {

                final ImageIcon icon = model.hasCachedIconPath() ? new ImageIcon(BLPHandler.get()
                        .getGameTex(model.getCachedIconPath()).getScaledInstance(16, 16, Image.SCALE_FAST))
                        : MDLIcon;
                FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(filepath), true, true, icon);
                final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
                        + filepath.substring(filepath.lastIndexOf('.'));
                if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                    FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(portrait), true, false, icon);
                }
                toolsMenu.getAccessibleContext().setAccessibleDescription(
                        "Allows the user to control which parts of the model are displayed for editing.");
                toolsMenu.setEnabled(true);
            }
        }
    }

    private void fetchObjectButtonResponse() {
        final MutableGameObject objectFetched = fetchObject();
        if (objectFetched != null) {
            final String filepath = convertPathToMDX(objectFetched.getFieldAsString(UnitFields.MODEL_FILE, 0));
            if (filepath != null) {
                FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(filepath), true, true,
                        new ImageIcon(BLPHandler.get()
                                .getGameTex(objectFetched.getFieldAsString(UnitFields.INTERFACE_ICON, 0))
                                .getScaledInstance(16, 16, Image.SCALE_FAST)));
                final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
                        + filepath.substring(filepath.lastIndexOf('.'));
                if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                    FileUtils.loadStreamMdx(this, MpqCodebase.get().getResourceAsStream(portrait), true, false,
                            new ImageIcon(BLPHandler.get()
                                    .getGameTex(objectFetched.getFieldAsString(UnitFields.INTERFACE_ICON, 0))
                                    .getScaledInstance(16, 16, Image.SCALE_FAST)));
                }
                toolsMenu.getAccessibleContext().setAccessibleDescription(
                        "Allows the user to control which parts of the model are displayed for editing.");
                toolsMenu.setEnabled(true);
            }
        }
    }

    private boolean importUnit() {
        final GameObject fetchUnitResult = fetchUnit();
        if (fetchUnitResult == null) {
            return true;
        }
        final String filepath = convertPathToMDX(fetchUnitResult.getField("file"));
        final EditableModel current = currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(this, animationSource);
        }
        refreshController();
        return false;
    }

    private boolean importGameModel() {
        final ModelElement fetchModelResult = fetchModel();
        if (fetchModelResult == null) {
            return true;
        }
        final String filepath = convertPathToMDX(fetchModelResult.getFilepath());
        final EditableModel current = currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(this, animationSource);
        }
        refreshController();
        return false;
    }

    private boolean importGameObject() {
        final MutableGameObject fetchObjectResult = fetchObject();
        if (fetchObjectResult == null) {
            return true;
        }
        final String filepath = convertPathToMDX(fetchObjectResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
        final EditableModel current = currentMDL();
        if (filepath != null) {
            final File animationSource = MpqCodebase.get().getFile(filepath);
            FileUtils.importFile(this, animationSource);
        }
        refreshController();
        return false;
    }

    private void importFromWorkspace() {
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

    private void mergeGeoset() {
        fc.setDialogTitle("Merge Single Geoset (Oinker-based)");
        final EditableModel current = currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (profile.getPath() != null) {
            fc.setCurrentDirectory(new File(profile.getPath()));
        }
        final int returnValue = fc.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFile = fc.getSelectedFile();
            final EditableModel geoSource = EditableModel.read(currentFile);
            profile.setPath(currentFile.getParent());
            boolean going = true;
            Geoset host = null;
            while (going) {
                final String s = JOptionPane.showInputDialog(this,
                        "Geoset into which to Import: (1 to " + current.getGeosetsSize() + ")");
                try {
                    final int x = Integer.parseInt(s);
                    if ((x >= 1) && (x <= current.getGeosetsSize())) {
                        host = current.getGeoset(x - 1);
                        going = false;
                    }
                } catch (final NumberFormatException ignored) {

                }
            }
            Geoset newGeoset = null;
            going = true;
            while (going) {
                final String s = JOptionPane.showInputDialog(this,
                        "Geoset to Import: (1 to " + geoSource.getGeosetsSize() + ")");
                try {
                    final int x = Integer.parseInt(s);
                    if (x <= geoSource.getGeosetsSize()) {
                        newGeoset = geoSource.getGeoset(x - 1);
                        going = false;
                    }
                } catch (final NumberFormatException ignored) {

                }
            }
            newGeoset.updateToObjects(current);
            System.out.println("putting " + newGeoset.numUVLayers() + " into a nice " + host.numUVLayers());
            for (int i = 0; i < newGeoset.numVerteces(); i++) {
                final GeosetVertex ver = newGeoset.getVertex(i);
                host.add(ver);
                ver.setGeoset(host);// geoset = host;
                // for( int z = 0; z < host.n.numUVLayers(); z++ )
                // {
                // host.getUVLayer(z).addTVertex(newGeoset.getVertex(i).getTVertex(z));
                // }
            }
            for (int i = 0; i < newGeoset.numTriangles(); i++) {
                final Triangle tri = newGeoset.getTriangle(i);
                host.add(tri);
                tri.setGeoRef(host);
            }
        }

        fc.setSelectedFile(null);
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

    private void exportTextures() {
        final DefaultListModel<Material> materials = new DefaultListModel<>();
        for (int i = 0; i < currentMDL().getMaterials().size(); i++) {
            final Material mat = currentMDL().getMaterials().get(i);
            materials.addElement(mat);
        }
        for (final ParticleEmitter2 emitter2 : currentMDL().sortedIdObjects(ParticleEmitter2.class)) {
            final Material dummyMaterial = new Material(
                    new Layer("Blend", currentMDL().getTexture(emitter2.getTextureID())));
        }

        final JList<Material> materialsList = new JList<>(materials);
        materialsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialsList.setCellRenderer(new MaterialListRenderer(currentMDL()));
        JOptionPane.showMessageDialog(this, new JScrollPane(materialsList));

        if (exportTextureDialog.getCurrentDirectory() == null) {
            final EditableModel current = currentMDL();
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                fc.setCurrentDirectory(current.getFile().getParentFile());
            } else if (profile.getPath() != null) {
                fc.setCurrentDirectory(new File(profile.getPath()));
            }
        }
        if (exportTextureDialog.getCurrentDirectory() == null) {
            exportTextureDialog.setSelectedFile(new File(exportTextureDialog.getCurrentDirectory()
                    + File.separator + materialsList.getSelectedValue().getName()));
        }

        final int x = exportTextureDialog.showSaveDialog(this);
        if (x == JFileChooser.APPROVE_OPTION) {
            final File file = exportTextureDialog.getSelectedFile();
            if (file != null) {
                try {
                    if (file.getName().lastIndexOf('.') >= 0) {
                        BufferedImage bufferedImage = materialsList.getSelectedValue()
                                .getBufferedImage(currentMDL().getWrappedDataSource());
                        String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1)
                                .toUpperCase();
                        if (fileExtension.equals("BMP") || fileExtension.equals("JPG")
                                || fileExtension.equals("JPEG")) {
                            JOptionPane.showMessageDialog(this,
                                    "Warning: Alpha channel was converted to black. Some data will be lost\nif you convert this texture back to Warcraft BLP.");
                            bufferedImage = BLPHandler.removeAlphaChannel(bufferedImage);
                        }
                        if (fileExtension.equals("BLP")) {
                            fileExtension = "blp";
                        }
                        final boolean write = ImageIO.write(bufferedImage, fileExtension, file);
                        if (!write) {
                            JOptionPane.showMessageDialog(this, "File type unknown or unavailable");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No file type was specified");
                    }
                } catch (final Exception e1) {
                    ExceptionPopup.display(e1);
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No output file was specified");
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

    boolean onClickSaveAs() {
        final EditableModel current = currentMDL();
        return onClickSaveAs(current);
    }

    private boolean onClickSaveAs(final EditableModel current) {
        try {
            fc.setDialogTitle("Save as");
            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
                fc.setCurrentDirectory(current.getFile().getParentFile());
                fc.setSelectedFile(current.getFile());
            } else if (profile.getPath() != null) {
                fc.setCurrentDirectory(new File(profile.getPath()));
            }
            final int returnValue = fc.showSaveDialog(this);
            File temp = fc.getSelectedFile();
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (temp != null) {
                    final FileFilter ff = fc.getFileFilter();
                    final String ext = ff.accept(new File("junk.mdl")) ? ".mdl" : ".mdx";
                    if (ff.accept(new File("junk.obj"))) {
                        throw new UnsupportedOperationException("OBJ saving has not been coded yet.");
                    }
                    final String name = temp.getName();
                    if (name.lastIndexOf('.') != -1) {
                        if (!name.substring(name.lastIndexOf('.')).equals(ext)) {
                            temp = new File(
                                    temp.getAbsolutePath().substring(0, temp.getAbsolutePath().lastIndexOf('.')) + ext);
                        }
                    } else {
                        temp = new File(temp.getAbsolutePath() + ext);
                    }
                    currentFile = temp;
                    if (temp.exists()) {
                        final Object[] options = {"Overwrite", "Cancel"};
                        final int n = JOptionPane.showOptionDialog(MainFrame.frame, "Selected file already exists.",
                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                                options[1]);
                        if (n == 1) {
                            fc.setSelectedFile(null);
                            return false;
                        }
                    }
                    profile.setPath(currentFile.getParent());
                    if (ext.equals(".mdl")) {
                        currentMDL().printTo(currentFile);
                    } else {
                        final MdxModel model = new MdxModel(currentMDL());
                        try (BlizzardDataOutputStream writer = new BlizzardDataOutputStream(currentFile)) {
                            model.save(writer);
                        } catch (final IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    currentMDL().setFileRef(currentFile);
                    // currentMDLDisp().resetBeenSaved();
                    // TODO reset been saved
                    ModelPanelUgg.currentModelPanel(currentModelPanel).getMenuItem().setName(currentFile.getName().split("\\.")[0]);
                    ModelPanelUgg.currentModelPanel(currentModelPanel).getMenuItem().setToolTipText(currentFile.getPath());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "You tried to save, but you somehow didn't select a file.\nThat is bad.");
                }
            }
            fc.setSelectedFile(null);
            return true;
        } catch (final Exception exc) {
            ExceptionPopup.display(exc);
        }
        refreshController();
        return false;
    }

    void onClickSave() {
        try {
            if (currentMDL() != null) {
                currentMDL().saveFile();
                profile.setPath(currentMDL().getFile().getParent());
                // currentMDLDisp().resetBeenSaved();
                // TODO reset been saved
            }
        } catch (final Exception exc) {
            ExceptionPopup.display(exc);
        }
        refreshController();
    }

    void onClickOpen() {
        fc.setDialogTitle("Open");
        final EditableModel current = currentMDL();
        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
            fc.setCurrentDirectory(current.getFile().getParentFile());
        } else if (profile.getPath() != null) {
            fc.setCurrentDirectory(new File(profile.getPath()));
        }

        final int returnValue = fc.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            FileUtils.openFile(this, fc.getSelectedFile());
        }

        fc.setSelectedFile(null);
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

    private GameObject fetchUnit() {
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

    private ModelOptionPane.ModelElement fetchModel() {
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

    private MutableGameObject fetchObject() {
        final BetterUnitEditorModelSelector selector = new BetterUnitEditorModelSelector(getUnitData(),
                getUnitEditorSettings());
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

    private void addSingleAnimation(final EditableModel current, final EditableModel animationSourceModel) {
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

    // @Override
    // public void mouseEntered(final MouseEvent e) {
    // refreshUndo();
    // }

    // @Override
    // public void mouseExited(final MouseEvent e) {
    // refreshUndo();
    // }

    // @Override
    // public void mousePressed(final MouseEvent e) {
    // refreshUndo();
    // }

    // @Override
    // public void mouseReleased(final MouseEvent e) {
    // refreshUndo();
    //
    // }

    // @Override
    // public void mouseClicked(final MouseEvent e) {
    // if (e.getSource() == tabbedPane && e.getButton() == MouseEvent.BUTTON3) {
    // for (int i = 0; i < tabbedPane.getTabCount(); i++) {
    // if (tabbedPane.getBoundsAt(i).contains(e.getX(), e.getY())) {
    // contextClickedTab = i;
    // contextMenu.show(tabbedPane, e.getX(), e.getY());
    // }
    // }
    // }
    // }

    // @Override
    // public void stateChanged(final ChangeEvent e) {
    // if (((ModelPanel) tabbedPane.getSelectedComponent()) != null) {
    // geoControl.setMDLDisplay(((ModelPanel)
    // tabbedPane.getSelectedComponent()).getModelViewManagingTree());
    // } else {
    // geoControl.setMDLDisplay(null);
    // }
    // }

    public void setMouseCoordDisplay(final byte dim1, final byte dim2, final double value1, final double value2) {
        for (JTextField jTextField : mouseCoordDisplay) {
            jTextField.setText("");
        }
        mouseCoordDisplay[dim1].setText((float) value1 + "");
        mouseCoordDisplay[dim2].setText((float) value2 + "");
    }

    public static boolean closeAll(MainPanel mainPanel) {
        boolean success = true;
        final Iterator<ModelPanel> iterator = mainPanel.modelPanels.iterator();
        boolean closedCurrentPanel = false;
        ModelPanel lastUnclosedModelPanel = null;
        while (iterator.hasNext()) {
            final ModelPanel panel = iterator.next();
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
        return success;
    }

    public static boolean closeOthers(MainPanel mainPanel, final ModelPanel panelToKeepOpen) {
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
        return success;
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
            onClickSaveAs(model);
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
