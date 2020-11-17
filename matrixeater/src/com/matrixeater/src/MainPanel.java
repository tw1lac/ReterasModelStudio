package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.animedit.ControllableTimeBoundProvider;
import com.hiveworkshop.wc3.gui.animedit.TimeBoundChooserPanel;
import com.hiveworkshop.wc3.gui.animedit.TimeEnvironmentImpl;
import com.hiveworkshop.wc3.gui.animedit.TimeSliderPanel;
import com.hiveworkshop.wc3.gui.datachooser.DataSourceDescriptor;
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
import com.hiveworkshop.wc3.jworldedit.objects.*;
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
import com.hiveworkshop.wc3.units.objectdata.War3ID;
import com.hiveworkshop.wc3.units.objectdata.War3ObjectDataChangeset;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.hiveworkshop.wc3.user.WarcraftDataSourceChangeListener.WarcraftDataSourceChangeNotifier;
import com.hiveworkshop.wc3.util.ModelUtils;
import com.matrixeater.imp.AnimationTransfer;
import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;
import net.infonode.docking.*;
import net.infonode.docking.util.StringViewMap;
import net.infonode.tabbedpanel.TabAreaVisiblePolicy;
import net.infonode.tabbedpanel.titledtab.TitledTabBorderSizePolicy;
import net.infonode.tabbedpanel.titledtab.TitledTabSizePolicy;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.lwjgl.util.vector.Quaternion;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private final View timeSliderView;
    private final View hackerView;
    private final View previewView;
    private final View creatorView;
    private final View animationControllerView;
    JScrollPane geoControl;
    JScrollPane geoControlModelData;
    final JTextField[] mouseCoordDisplay = new JTextField[3];
    boolean cheatShift = false;
    boolean cheatAlt = false;
    final SaveProfile profile = SaveProfile.get();
    final ProgramPreferences prefs = profile.getPreferences();

    JToolBar toolbar;

    final TimeSliderPanel timeSliderPanel;
    private final JButton setKeyframe;
    private final JButton setTimeBounds;
    private final ModeButton animationModeButton;
    boolean animationModeState = false;

    private final ActiveViewportWatcher activeViewportWatcher = new ActiveViewportWatcher();

    final WarcraftDataSourceChangeNotifier directoryChangeNotifier = new WarcraftDataSourceChangeNotifier();

    final JMenuItem contextClose;
    final JMenuItem contextCloseAll;
    final JMenuItem contextCloseOthers;
    int contextClickedTab = 0;
    final JPopupMenu contextMenu;

    final AbstractAction undoAction = new UndoActionImplementation("Undo", this);
    final AbstractAction redoAction = new RedoActionImplementation("Redo", this);
    final ClonedNodeNamePicker namePicker = new ClonedNodeNamePickerImplementation(this);


    AbstractAction cloneAction() {
        return new AbstractAction("CloneSelection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    try {
                        mpanel.getUndoManager().pushAction(
                                mpanel.getModelEditorManager().getModelEditor().cloneSelectedComponents(namePicker));
                    } catch (final Exception exc) {
                        ExceptionPopup.display(exc);
                    }
                }
                refreshUndo();
                repaintSelfAndChildren(mpanel);
            }
        };
    }

    AbstractAction deleteAction() {
        return new AbstractAction("Delete") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    if (animationModeState) {
                        timeSliderPanel.deleteSelectedKeyframes();
                    } else {
                        mpanel.getUndoManager()
                                .pushAction(mpanel.getModelEditorManager().getModelEditor().deleteSelectedComponents());
                    }
                }
                repaintSelfAndChildren(mpanel);
            }
        };
    }

    AbstractAction selectAllAction() {
        return new AbstractAction("Select All") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().selectAll());
                }
                repaint();
            }
        };
    }

    AbstractAction invertSelectAction() {
        return new AbstractAction("Invert Selection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().invertSelection());
                }
                repaint();
            }
        };
    }

    AbstractAction rigAction() {
        return new AbstractAction("Rig") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    boolean valid = false;
                    for (final Vertex v : mpanel.getModelEditorManager().getSelectionView().getSelectedVertices()) {
                        final int index = mpanel.getModel().getPivots().indexOf(v);
                        if (index != -1) {
                            if (index < mpanel.getModel().getIdObjects().size()) {
                                final IdObject node = mpanel.getModel().getIdObject(index);
                                if ((node instanceof Bone) && !(node instanceof Helper)) {
                                    valid = true;
                                }
                            }
                        }
                    }
                    if (valid) {
                        mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().rig());
                    } else {
                        System.err.println("NOT RIGGING, NOT VALID");
                    }
                }
                repaint();
            }
        };
    }

    AbstractAction expandSelectionAction() {
        return new AbstractAction("Expand Selection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().expandSelection());
                }
                repaint();
            }
        };
    }

    AbstractAction snapNormalsAction() {
        return new AbstractAction("Snap Normals") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().snapNormals());
                }
                repaint();
            }
        };
    }

    AbstractAction snapVerticesAction() {
        return new AbstractAction("Snap Vertices") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager()
                            .pushAction(mpanel.getModelEditorManager().getModelEditor().snapSelectedVertices());
                }
                repaint();
            }
        };
    }

    AbstractAction recalculateNormalsAction() {
        return new AbstractAction("RecalculateNormals") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().recalcNormals());
                }
                repaint();
            }
        };
    }

    AbstractAction recalculateExtentsAction() {
        return new AbstractAction("RecalculateExtents") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    final JPanel messagePanel = new JPanel(new MigLayout());
                    messagePanel.add(new JLabel("This will calculate the extents of all model components. Proceed?"),
                            "wrap");
                    messagePanel.add(new JLabel("(It may destroy existing extents)"), "wrap");
                    final JRadioButton considerAllBtn = new JRadioButton("Consider all geosets for calculation");
                    final JRadioButton considerCurrentBtn = new JRadioButton(
                            "Consider current editable geosets for calculation");
                    final ButtonGroup buttonGroup = new ButtonGroup();
                    buttonGroup.add(considerAllBtn);
                    buttonGroup.add(considerCurrentBtn);
                    considerAllBtn.setSelected(true);
                    messagePanel.add(considerAllBtn, "wrap");
                    messagePanel.add(considerCurrentBtn, "wrap");
                    final int userChoice = JOptionPane.showConfirmDialog(MainPanel.this, messagePanel, "Message",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (userChoice == JOptionPane.YES_OPTION) {
                        mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor()
                                .recalcExtents(considerCurrentBtn.isSelected()));
                    }
                }
                repaint();
            }
        };
    }

    AbstractAction flipAllUVsAxisAction(String s) {
        return new AbstractAction(s) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (final Geoset geo : currentMDL().getGeosets()) {
                    for (final UVLayer layer : geo.getUVLayers()) {
                        for (int i = 0; i < layer.numTVerteces(); i++) {
                            final TVertex tvert = layer.getTVertex(i);
                            if(s.endsWith("U")){
                                tvert.x = 1.0 - tvert.x;
                            }else {
                                tvert.y = 1.0 - tvert.y;
                            }
                        }
                    }
                }
                repaint();
            }
        };
    }

    static AbstractAction inverseAllUVsAction(final MainPanel mainPanel) {
        return new AbstractAction("Swap UVs U for V") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO this should be an action
                for (final Geoset geo : mainPanel.currentMDL().getGeosets()) {
                    for (final UVLayer layer : geo.getUVLayers()) {
                        for (int i = 0; i < layer.numTVerteces(); i++) {
                            final TVertex tvert = layer.getTVertex(i);
                            final double temp = tvert.x;
                            tvert.x = tvert.y;
                            tvert.y = temp;
                        }
                    }
                }
                mainPanel.repaint();
            }
        };
    }

//    AbstractAction inverseAllUVsAction() {
//        return new AbstractAction("Swap UVs U for V") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                // TODO this should be an action
//                for (final Geoset geo : currentMDL().getGeosets()) {
//                    for (final UVLayer layer : geo.getUVLayers()) {
//                        for (int i = 0; i < layer.numTVerteces(); i++) {
//                            final TVertex tvert = layer.getTVertex(i);
//                            final double temp = tvert.x;
//                            tvert.x = tvert.y;
//                            tvert.y = temp;
//                        }
//                    }
//                }
//                repaint();
//            }
//        };
//    }

    AbstractAction mirrorAxisAction(String s) {
        byte i = 0;
        if(s.endsWith("Y")){
            i = 1;
        }else if (s.endsWith("Z")){
            i = 2;
        }
        byte finalI = i;
        return new AbstractAction(s) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    final Vertex selectionCenter = mpanel.getModelEditorManager().getModelEditor().getSelectionCenter();
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().mirror(finalI,
                            mirrorFlip.isSelected(), selectionCenter.x, selectionCenter.y, selectionCenter.z));
                }
                repaint();
            }
        };
    }

    AbstractAction insideOutAction() {
        return new AbstractAction("Inside Out") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().flipSelectedFaces());
                }
                repaint();
            }
        };
    }

    AbstractAction insideOutNormalsAction() {
        return new AbstractAction("Inside Out Normals") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.getUndoManager()
                            .pushAction(mpanel.getModelEditorManager().getModelEditor().flipSelectedNormals());
                }
                repaint();
            }
        };
    }

    AbstractAction viewMatricesAction() {
        return new AbstractAction("View Matrices") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = currentModelPanel();
                if (mpanel != null) {
                    mpanel.viewMatrices();
                }
                repaint();
            }
        };
    }

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
    final AbstractAction openPreferencesAction = new AbstractAction("Open Preferences") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final ProgramPreferences programPreferences = new ProgramPreferences();
            programPreferences.loadFrom(prefs);
            final List<DataSourceDescriptor> priorDataSources = SaveProfile.get().getDataSources();
            final ProgramPreferencesPanel programPreferencesPanel = new ProgramPreferencesPanel(programPreferences,
                    priorDataSources);
            // final JFrame frame = new JFrame("Preferences");
            // frame.setIconImage(MainFrame.frame.getIconImage());
            // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // frame.setContentPane(programPreferencesPanel);
            // frame.pack();
            // frame.setLocationRelativeTo(MainPanel.this);
            // frame.setVisible(true);

            final int ret = JOptionPane.showConfirmDialog(MainPanel.this, programPreferencesPanel, "Preferences",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ret == JOptionPane.OK_OPTION) {
                prefs.loadFrom(programPreferences);
                final List<DataSourceDescriptor> dataSources = programPreferencesPanel.getDataSources();
                final boolean changedDataSources = (dataSources != null) && !dataSources.equals(priorDataSources);
                if (changedDataSources) {
                    SaveProfile.get().setDataSources(dataSources);
                }
                SaveProfile.save();
                if (changedDataSources) {
                    dataSourcesChanged();
                }
                updateUIFromProgramPreferences();
            }
        }
    };
    final AbstractAction openMPQViewerAction = new AbstractAction("Open MPQ Browser") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final View view = createMPQBrowser();
            rootWindow.setWindow(new SplitWindow(true, 0.75f, rootWindow.getWindow(), view));
        }
    };
    final AbstractAction openUnitViewerAction = new AbstractAction("Open Unit Browser") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final UnitEditorTree unitEditorTree = createUnitEditorTree();
            rootWindow.setWindow(new SplitWindow(true, 0.75f, rootWindow.getWindow(),
                    new View("Unit Browser",
                            new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)),
                            new JScrollPane(unitEditorTree))));
        }
    };
    final AbstractAction openDoodadViewerAction = new AbstractAction("Open Doodad Browser") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final UnitEditorTree unitEditorTree = new UnitEditorTree(getDoodadData(), new DoodadTabTreeBrowserBuilder(),
                    getUnitEditorSettings(), WorldEditorDataType.DOODADS);
            unitEditorTree.selectFirstUnit();
            // final FloatingWindow floatingWindow =
            // rootWindow.createFloatingWindow(rootWindow.getLocation(),
            // mpqBrowser.getPreferredSize(),
            // new View("MPQ Browser",
            // new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16,
            // Image.SCALE_FAST)),
            // mpqBrowser));
            // floatingWindow.getTopLevelAncestor().setVisible(true);
            unitEditorTree.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    try {
                        if (e.getClickCount() >= 2) {
                            final TreePath currentUnitTreePath = unitEditorTree.getSelectionPath();
                            if (currentUnitTreePath != null) {
                                final DefaultMutableTreeNode o = (DefaultMutableTreeNode) currentUnitTreePath
                                        .getLastPathComponent();
                                if (o.getUserObject() instanceof MutableGameObject) {
                                    final MutableGameObject obj = (MutableGameObject) o.getUserObject();
                                    final int numberOfVariations = obj.getFieldAsInteger(War3ID.fromString("dvar"), 0);
                                    if (numberOfVariations > 1) {
                                        for (int i = 0; i < numberOfVariations; i++) {
                                            final String path = convertPathToMDX(
                                                    obj.getFieldAsString(War3ID.fromString("dfil"), 0) + i + ".mdl");
                                            final String portrait = ModelUtils.getPortrait(path);
                                            final ImageIcon icon = new ImageIcon(com.hiveworkshop.wc3.util.IconUtils
                                                    .getIcon(obj, WorldEditorDataType.DOODADS)
                                                    .getScaledInstance(16, 16, Image.SCALE_DEFAULT));

                                            System.out.println(path);
                                            FileUtils.loadStreamMdx(MainPanel.this, MpqCodebase.get().getResourceAsStream(path), true, i == 0,
                                                    icon);
                                            if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                                                FileUtils.loadStreamMdx(MainPanel.this, MpqCodebase.get().getResourceAsStream(portrait), true,
                                                        false, icon);
                                            }
                                        }
                                    } else {
                                        final String path = convertPathToMDX(
                                                obj.getFieldAsString(War3ID.fromString("dfil"), 0));
                                        final String portrait = ModelUtils.getPortrait(path);
                                        final ImageIcon icon = new ImageIcon(com.hiveworkshop.wc3.util.IconUtils
                                                .getIcon(obj, WorldEditorDataType.DOODADS)
                                                .getScaledInstance(16, 16, Image.SCALE_DEFAULT));
                                        System.out.println(path);
                                        FileUtils.loadStreamMdx(MainPanel.this, MpqCodebase.get().getResourceAsStream(path), true, true, icon);
                                        if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
                                            FileUtils.loadStreamMdx(MainPanel.this, MpqCodebase.get().getResourceAsStream(portrait), true, false,
                                                    icon);
                                        }
                                    }
                                    toolsMenu.getAccessibleContext().setAccessibleDescription(
                                            "Allows the user to control which parts of the model are displayed for editing.");
                                    toolsMenu.setEnabled(true);
                                }
                            }
                        }
                    } catch (final Exception exc) {
                        exc.printStackTrace();
                        ExceptionPopup.display(exc);
                    }
                }
            });
            rootWindow.setWindow(new SplitWindow(true, 0.75f, rootWindow.getWindow(),
                    new View("Doodad Browser",
                            new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)),
                            new JScrollPane(unitEditorTree))));
        }
    };
    final AbstractAction openHiveViewerAction = new AbstractAction("Open Hive Browser") {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.BEFORE_FIRST_LINE, new JLabel(POWERED_BY_HIVE));
            // final JPanel resourceFilters = new JPanel();
            // resourceFilters.setBorder(BorderFactory.createTitledBorder("Resource
            // Filters"));
            // panel.add(BorderLayout.BEFORE_LINE_BEGINS, resourceFilters);
            // resourceFilters.add(new JLabel("Resource Type"));
            // resourceFilters.add(new JComboBox<>(new String[] { "Any" }));
            final JList<String> view = new JList<>(
                    new String[]{"Bongo Bongo (Phantom Shadow Beast)", "Other Model", "Other Model"});
            view.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(final javax.swing.JList<?> list, final Object value,
                                                              final int index, final boolean isSelected, final boolean cellHasFocus) {
                    final Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index,
                            isSelected, cellHasFocus);
                    final ImageIcon icon = new ImageIcon(MainPanel.class.getResource("ImageBin/deleteme.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(48, 32, Image.SCALE_DEFAULT)));
                    return listCellRendererComponent;
                }
            });
            panel.add(BorderLayout.BEFORE_LINE_BEGINS, new JScrollPane(view));

            final JPanel tags = new JPanel();
            tags.setBorder(BorderFactory.createTitledBorder("Tags"));
            tags.setLayout(new GridLayout(30, 1));
            tags.add(new JCheckBox("Results must include all selected tags"));
            tags.add(new JSeparator());
            tags.add(new JLabel("Types (Models)"));
            tags.add(new JSeparator());
            tags.add(new JCheckBox("Building"));
            tags.add(new JCheckBox("Doodad"));
            tags.add(new JCheckBox("Item"));
            tags.add(new JCheckBox("User Interface"));
            panel.add(BorderLayout.CENTER, tags);
            // final FloatingWindow floatingWindow =
            // rootWindow.createFloatingWindow(rootWindow.getLocation(),
            // mpqBrowser.getPreferredSize(),
            // new View("MPQ Browser",
            // new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16,
            // Image.SCALE_FAST)),
            // mpqBrowser));
            // floatingWindow.getTopLevelAncestor().setVisible(true);
            rootWindow.setWindow(new SplitWindow(true, 0.75f, rootWindow.getWindow(), new View("Hive Browser",
                    new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)), panel)));
        }
    };

    ToolbarButtonGroup<SelectionItemTypes> selectionItemTypeGroup;
    ToolbarButtonGroup<SelectionMode> selectionModeGroup;
    ToolbarButtonGroup<ToolbarActionButtonType> actionTypeGroup;
    final ModelStructureChangeListener modelStructureChangeListener;
    private final ViewportTransferHandler viewportTransferHandler;
    final RootWindow rootWindow;
    private final View viewportControllerWindowView;
    private final View toolView;
    private final View modelDataView;
    private final View modelComponentView;
    private ControllableTimeBoundProvider timeBoundProvider;
    ActivityDescriptor currentActivity;

    public MainPanel() {
        super();

        StringViewMap viewMap = new StringViewMap();
        rootWindow = new RootWindow(viewMap);
        rootWindow.addListener(new DockingWindowListener() {
            @Override
            public void windowUndocking(final DockingWindow arg0) {
            }

            @Override
            public void windowUndocked(final DockingWindow dockingWindow) {
                SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> {
                    if (dockingWindow instanceof View) {
                        final Component component = ((View) dockingWindow).getComponent();
                        if (component instanceof JComponent) {
                            linkActions(((JComponent) component).getRootPane());
                        }
                    }
                }));
            }

            @Override
            public void windowShown(final DockingWindow arg0) { }

            @Override
            public void windowRestoring(final DockingWindow arg0) { }

            @Override
            public void windowRestored(final DockingWindow arg0) { }

            @Override
            public void windowRemoved(final DockingWindow arg0, final DockingWindow arg1) { }

            @Override
            public void windowMinimizing(final DockingWindow arg0) { }

            @Override
            public void windowMinimized(final DockingWindow arg0) { }

            @Override
            public void windowMaximizing(final DockingWindow arg0) { }

            @Override
            public void windowMaximized(final DockingWindow arg0) { }

            @Override
            public void windowHidden(final DockingWindow arg0) { }

            @Override
            public void windowDocking(final DockingWindow arg0) { }

            @Override
            public void windowDocked(final DockingWindow arg0) { }

            @Override
            public void windowClosing(final DockingWindow arg0) { }

            @Override
            public void windowClosed(final DockingWindow arg0) { }

            @Override
            public void windowAdded(final DockingWindow arg0, final DockingWindow arg1) { }

            @Override
            public void viewFocusChanged(final View arg0, final View arg1) { }
        });

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
        modelStructureChangeListener = new ModelStructureChangeListenerImplementation(this, () -> currentModelPanel().getModel());
        animatedRenderEnvironment = new TimeEnvironmentImpl();
        BLPPanel blpPanel = new BLPPanel(null);
        timeSliderPanel = new TimeSliderPanel(animatedRenderEnvironment, modelStructureChangeListener, prefs);
        timeSliderPanel.setDrawing(false);
        timeSliderPanel.addListener(currentTime -> {
            animatedRenderEnvironment.setCurrentTime(currentTime - animatedRenderEnvironment.getStart());
            if (currentModelPanel() != null) {
                currentModelPanel().getEditorRenderModel().updateNodes(true, false);
                currentModelPanel().repaintSelfAndRelatedChildren();
            }
        });
//		timeSliderPanel.addListener(creatorPanel);
        animatedRenderEnvironment.addChangeListener((start, end) -> {
            final Integer globalSeq = animatedRenderEnvironment.getGlobalSeq();
            if (globalSeq != null) {
                creatorPanel.setChosenGlobalSeq(globalSeq);
            } else {
                final ModelPanel modelPanel = currentModelPanel();
                if (modelPanel != null) {
                    boolean foundAnim = false;
                    for (final Animation animation : modelPanel.getModel().getAnims()) {
                        if ((animation.getStart() == start) && (animation.getEnd() == end)) {
                            creatorPanel.setChosenAnimation(animation);
                            foundAnim = true;
                            break;
                        }
                    }
                    if (!foundAnim) {
                        creatorPanel.setChosenAnimation(null);
                    }
                }

            }
        });
        setKeyframe = new JButton(GlobalIcons.SET_KEYFRAME_ICON);
        setKeyframe.setMargin(new Insets(0, 0, 0, 0));
        setKeyframe.setToolTipText("Create Keyframe");
        setKeyframe.addActionListener(e -> {
            final ModelPanel mpanel = currentModelPanel();
            if (mpanel != null) {
                mpanel.getUndoManager()
                        .pushAction(mpanel.getModelEditorManager().getModelEditor().createKeyframe(actionType));
            }
            repaintSelfAndChildren(mpanel);
        });
        setTimeBounds = new JButton(GlobalIcons.SET_TIME_BOUNDS_ICON);
        setTimeBounds.setMargin(new Insets(0, 0, 0, 0));
        setTimeBounds.setToolTipText("Choose Time Bounds");
        setTimeBounds.addActionListener(e -> {
            final TimeBoundChooserPanel timeBoundChooserPanel = new TimeBoundChooserPanel(
                    currentModelPanel() == null ? null : currentModelPanel().getModelViewManager(),
                    modelStructureChangeListener);
            final int confirmDialogResult = JOptionPane.showConfirmDialog(MainPanel.this, timeBoundChooserPanel,
                    "Set Time Bounds", JOptionPane.OK_CANCEL_OPTION);
            if (confirmDialogResult == JOptionPane.OK_OPTION) {
                timeBoundChooserPanel.applyTo(animatedRenderEnvironment);
                if (currentModelPanel() != null) {
                    currentModelPanel().getEditorRenderModel().refreshFromEditor(animatedRenderEnvironment,
                            IDENTITY, IDENTITY, IDENTITY, currentModelPanel().getPerspArea().getViewport());
                    currentModelPanel().getEditorRenderModel().updateNodes(true, false);
                }
            }
        });

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
        rootWindow.getWindowProperties().getTabProperties().getTitledTabProperties()
                .setSizePolicy(TitledTabSizePolicy.EQUAL_SIZE);
        rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setVisible(true);
        rootWindow.getWindowProperties().getTabProperties().getTitledTabProperties()
                .setBorderSizePolicy(TitledTabBorderSizePolicy.EQUAL_SIZE);
        rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getTabAreaProperties()
                .setTabAreaVisiblePolicy(TabAreaVisiblePolicy.MORE_THAN_ONE_TAB);
        rootWindow.setBackground(Color.GREEN);
        rootWindow.setForeground(Color.GREEN);
        final Runnable fixit = () -> {
            traverseAndReset(rootWindow);
            traverseAndFix(rootWindow);
        };
        rootWindow.addListener(new DockingWindowListener() {

            @Override
            public void windowUndocking(final DockingWindow removedWindow) {
                if (OLDMODE) {
                    if (removedWindow instanceof View) {
                        final View view = (View) removedWindow;
                        view.getViewProperties().getViewTitleBarProperties().setVisible(true);
                        System.out.println(
                                view.getTitle() + ": (windowUndocking removedWindow as view) title bar visible now");
                    }
                } else {
                    SwingUtilities.invokeLater(fixit);
                }
            }

            @Override
            public void windowUndocked(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowShown(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowRestoring(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowRestored(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowRemoved(final DockingWindow removedFromWindow, final DockingWindow removedWindow) {
                if (OLDMODE) {
                    if (removedFromWindow instanceof TabWindow) {
                        if (removedWindow instanceof View) {
                            final View view = (View) removedWindow;
                            view.getViewProperties().getViewTitleBarProperties().setVisible(true);
                            System.out.println(view.getTitle() + ": (removedWindow as view) title bar visible now");
                        }
                        final TabWindow tabWindow = (TabWindow) removedFromWindow;
                        if (tabWindow.getChildWindowCount() == 1) {
                            final DockingWindow childWindow = tabWindow.getChildWindow(0);
                            if (childWindow instanceof View) {
                                final View singleChildView = (View) childWindow;
                                System.out.println(singleChildView.getTitle()
                                        + ": (singleChildView, windowRemoved()) title bar visible now");
                                singleChildView.getViewProperties().getViewTitleBarProperties().setVisible(true);
                            }
                        } else if (tabWindow.getChildWindowCount() == 0) {
                            System.out.println(
                                    tabWindow.getTitle() + ": force close because 0 child windows in windowRemoved()");
//						tabWindow.close();
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(fixit);
                }
            }

            @Override
            public void windowMinimizing(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowMinimized(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowMaximizing(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowMaximized(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowHidden(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDocking(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDocked(final DockingWindow arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(final DockingWindow closingWindow) {
                if (OLDMODE) {
                    if (closingWindow.getWindowParent() instanceof TabWindow) {
                        if (closingWindow instanceof View) {
                            final View view = (View) closingWindow;
                            view.getViewProperties().getViewTitleBarProperties().setVisible(true);
                            System.out.println(view.getTitle() + ": (closingWindow as view) title bar visible now");
                        }
                        final TabWindow tabWindow = (TabWindow) closingWindow.getWindowParent();
                        if (tabWindow.getChildWindowCount() == 1) {
                            final DockingWindow childWindow = tabWindow.getChildWindow(0);
                            if (childWindow instanceof View) {
                                final View singleChildView = (View) childWindow;
                                singleChildView.getViewProperties().getViewTitleBarProperties().setVisible(true);
                                System.out.println(singleChildView.getTitle()
                                        + ": (singleChildView, windowClosing()) title bar visible now");
                            }
                        } else if (tabWindow.getChildWindowCount() == 0) {
                            System.out.println(
                                    tabWindow.getTitle() + ": force close because 0 child windows in windowClosing()");
                            tabWindow.close();
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(fixit);
                }
            }

            @Override
            public void windowClosed(final DockingWindow closedWindow) {
            }

            @Override
            public void windowAdded(final DockingWindow addedToWindow, final DockingWindow addedWindow) {
                if (OLDMODE) {
                    if (addedToWindow instanceof TabWindow) {
                        final TabWindow tabWindow = (TabWindow) addedToWindow;
                        if (tabWindow.getChildWindowCount() == 2) {
                            for (int i = 0; i < 2; i++) {
                                final DockingWindow childWindow = tabWindow.getChildWindow(i);
                                if (childWindow instanceof View) {
                                    final View singleChildView = (View) childWindow;
                                    singleChildView.getViewProperties().getViewTitleBarProperties().setVisible(false);
                                    System.out.println(singleChildView.getTitle()
                                            + ": (singleChildView as view, windowAdded()) title bar NOT visible now");
                                }
                            }
                        }
                        if (addedWindow instanceof View) {
                            final View view = (View) addedWindow;
                            view.getViewProperties().getViewTitleBarProperties().setVisible(false);
                            System.out.println(view.getTitle() + ": (addedWindow as view) title bar NOT visible now");
                        }
                    }
                } else {
                    SwingUtilities.invokeLater(fixit);
                }
            }

            @Override
            public void viewFocusChanged(final View arg0, final View arg1) {
                // TODO Auto-generated method stub

            }
        });
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
        run.addActionListener(new ActionListener() {
            final ScriptEngineManager factory = new ScriptEngineManager();

            @Override
            public void actionPerformed(final ActionEvent e) {
                final String text = matrixEaterScriptTextArea.getText();
                final ScriptEngine engine = factory.getEngineByName("JavaScript");
                final ModelPanel modelPanel = currentModelPanel();
                if (modelPanel != null) {
                    engine.put("modelPanel", modelPanel);
                    engine.put("model", modelPanel.getModel());
                    engine.put("world", MainPanel.this);
                    try {
                        engine.eval(text);
                    } catch (final ScriptException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(MainPanel.this, e1.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(MainPanel.this, "Must open a file!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        hackerPanel.add(run, BorderLayout.NORTH);
        hackerView = new View("Matrix Eater Script", null, hackerPanel);
        creatorPanel = new CreatorModelingPanel(newType -> {
            actionTypeGroup.maybeSetButtonType(newType);
            MainPanel.this.changeActivity(newType);
        }, prefs, actionTypeGroup, activeViewportWatcher, animatedRenderEnvironment);

        creatorView = new View("Modeling", null, creatorPanel);
        animationControllerView = new View("Animation Controller", null, new JPanel());

        final TabWindow startupTabWindow = createMainLayout();
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
        selectionItemTypeGroup.addToolbarButtonListener(newType -> {
            animationModeState = newType == SelectionItemTypes.ANIMATE;
            // we need to refresh the state of stuff AFTER the ModelPanels, this
            // is a pretty signficant design flaw, so we're just going to
            // post to the EDT to get behind them (they're called
            // on the same notifier as this method)
            SwingUtilities.invokeLater(this::refreshAnimationModeState);

            if (newType == SelectionItemTypes.TPOSE) {

                final Object[] settings = {"Move Linked", "Move Single"};
                final Object dialogResult = JOptionPane.showInputDialog(null, "Choose settings:", "T-Pose Settings",
                        JOptionPane.PLAIN_MESSAGE, null, settings, settings[0]);
                final boolean moveLinked = dialogResult == settings[0];
                ModelEditorManager.MOVE_LINKED = moveLinked;
            }
            repaint();
        });

        actionTypeGroup.addToolbarButtonListener(newType -> {
            if (newType != null) {
                changeActivity(newType);
            }
        });
        actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);
        viewportTransferHandler = new ViewportTransferHandler();
        coordDisplayListener = MainPanel.this::setMouseCoordDisplay;
        openAnimationViewerAction = new OpenViewAction(rootWindow, "Animation Preview", new OpenViewGetter() {
            @Override
            public View getView() {
                return previewView;
            }
        });
        openAnimationControllerAction = new OpenViewAction(rootWindow, "Animation Controller", new OpenViewGetter() {
            @Override
            public View getView() {
                return animationControllerView;
            }
        });
        openModelingTabAction = new OpenViewAction(rootWindow, "Modeling", new OpenViewGetter() {
            @Override
            public View getView() {
                return creatorView;
            }
        });
        openPerspectiveAction = new OpenViewAction(rootWindow, "Perspective", new OpenViewGetter() {
            @Override
            public View getView() {
                return perspectiveView;
            }
        });
        openOutlinerAction = new OpenViewAction(rootWindow, "Outliner", new OpenViewGetter() {
            @Override
            public View getView() {
                return viewportControllerWindowView;
            }
        });
        openSideAction = new OpenViewAction(rootWindow, "Side", new OpenViewGetter() {
            @Override
            public View getView() {
                return leftView;
            }
        });
        openTimeSliderAction = new OpenViewAction(rootWindow, "Footer", new OpenViewGetter() {
            @Override
            public View getView() {
                return timeSliderView;
            }
        });
        openFrontAction = new OpenViewAction(rootWindow, "Front", new OpenViewGetter() {
            @Override
            public View getView() {
                return frontView;
            }
        });
        openBottomAction = new OpenViewAction(rootWindow, "Bottom", new OpenViewGetter() {
            @Override
            public View getView() {
                return bottomView;
            }
        });
        openToolsAction = new OpenViewAction(rootWindow, "Tools", new OpenViewGetter() {
            @Override
            public View getView() {
                return toolView;
            }
        });
        openModelDataContentsViewAction = new OpenViewAction(rootWindow, "Model", new OpenViewGetter() {
            @Override
            public View getView() {
                return modelDataView;
            }
        });
        hackerViewAction = new OpenViewAction(rootWindow, "Matrix Eater Script", new OpenViewGetter() {
            @Override
            public View getView() {
                return hackerView;
            }
        });
    }

    TabWindow createMainLayout() {
        final TabWindow leftHandTabWindow = new TabWindow(
                new DockingWindow[]{viewportControllerWindowView, toolView});
        leftHandTabWindow.setSelectedTab(0);
//		leftHandTabWindow.getWindowProperties().setCloseEnabled(false);
        final SplitWindow editingTab = new SplitWindow(false, 0.875f,
                new SplitWindow(true, 0.2f, leftHandTabWindow,
                        new SplitWindow(true, 0.8f,
                                new SplitWindow(false, new SplitWindow(true, frontView, bottomView),
                                        new SplitWindow(true, leftView, perspectiveView)),
                                creatorView)),
                timeSliderView);
        editingTab.getWindowProperties().setCloseEnabled(false);
        editingTab.getWindowProperties().setTitleProvider(arg0 -> "Edit");
        ImageIcon imageIcon;
        imageIcon = new ImageIcon(MainFrame.MAIN_PROGRAM_ICON.getScaledInstance(16, 16, Image.SCALE_FAST));

        final View mpqBrowserView = createMPQBrowser(imageIcon);

        final UnitEditorTree unitEditorTree = createUnitEditorTree();
        final TabWindow tabWindow = new TabWindow(new DockingWindow[]{
                new View("Unit Browser", imageIcon, new JScrollPane(unitEditorTree)), mpqBrowserView});
        tabWindow.setSelectedTab(0);
        final SplitWindow viewingTab = new SplitWindow(true, 0.8f,
                new SplitWindow(true, 0.8f, previewView, animationControllerView), tabWindow);
        viewingTab.getWindowProperties().setTitleProvider(arg0 -> "View");
        viewingTab.getWindowProperties().setCloseEnabled(false);

        final SplitWindow modelTab = new SplitWindow(true, 0.2f, modelDataView, modelComponentView);
        modelTab.getWindowProperties().setTitleProvider(arg0 -> "Model");

        final TabWindow startupTabWindow = new TabWindow(new DockingWindow[]{viewingTab, editingTab, modelTab});
        traverseAndFix(startupTabWindow);
        return startupTabWindow;
    }

    private View createMPQBrowser(final ImageIcon imageIcon) {
        final MPQBrowser mpqBrowser = new MPQBrowser(MpqCodebase.get(), filepath -> {
            if (filepath.toLowerCase().endsWith(".mdx")) {
                FileUtils.loadFile(this, MpqCodebase.get().getFile(filepath), true);
            } else if (filepath.toLowerCase().endsWith(".blp")) {
                FileUtils.loadBLPPathAsModel(this, filepath);
            } else if (filepath.toLowerCase().endsWith(".png")) {
                FileUtils.loadBLPPathAsModel(this, filepath);
            } else if (filepath.toLowerCase().endsWith(".dds")) {
                FileUtils.loadBLPPathAsModel(this, filepath, null, 1000);
            }
        }, path -> {
            final int modIndex = Math.max(path.lastIndexOf(".w3mod/"), path.lastIndexOf(".w3mod\\"));
            String finalPath;
            if (modIndex == -1) {
                finalPath = path;
            } else {
                finalPath = path.substring(modIndex + ".w3mod/".length());
            }
            final ModelPanel modelPanel = currentModelPanel();
            if (modelPanel != null) {
                if (modelPanel.getModel().getFormatVersion() > 800) {
                    finalPath = finalPath.replace("\\", "/"); // Reforged prefers forward slash
                }
                modelPanel.getModel().add(new Bitmap(finalPath));
                modelStructureChangeListener.texturesChanged();
            }
        });
        final View view = new View("Data Browser", imageIcon, mpqBrowser);
        view.getWindowProperties().setCloseEnabled(true);
        return view;
    }

    private View createMPQBrowser() {
        return createMPQBrowser(
                new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)));
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

    private UnitEditorTree createUnitEditorTree() {
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
    private final CoordDisplayListener coordDisplayListener;
    protected ModelEditorActionType actionType;
    JMenu teamColorMenu;
    CreatorModelingPanel creatorPanel;

    public void refreshAnimationModeState() {
        if (animationModeState) {
            if ((currentModelPanel() != null) && (currentModelPanel().getModel() != null)) {
                if (currentModelPanel().getModel().getAnimsSize() > 0) {
                    final Animation anim = currentModelPanel().getModel().getAnim(0);
                    animatedRenderEnvironment.setBounds(anim.getStart(), anim.getEnd());
                }
                currentModelPanel().getEditorRenderModel().refreshFromEditor(animatedRenderEnvironment, IDENTITY,
                        IDENTITY, IDENTITY, currentModelPanel().getPerspArea().getViewport());
                currentModelPanel().getEditorRenderModel().updateNodes(true, false);
                timeSliderPanel.setNodeSelectionManager(
                        currentModelPanel().getModelEditorManager().getNodeAnimationSelectionManager());
            }
            if ((actionTypeGroup.getActiveButtonType() == actionTypeGroup.getToolbarButtonTypes()[3])
                    || (actionTypeGroup.getActiveButtonType() == actionTypeGroup.getToolbarButtonTypes()[4])) {
                actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);
            }
        }
        animatedRenderEnvironment.setStaticViewMode(!animationModeState);
        if (!animationModeState) {
            if ((currentModelPanel() != null) && (currentModelPanel().getModel() != null)) {
                currentModelPanel().getEditorRenderModel().refreshFromEditor(animatedRenderEnvironment, IDENTITY,
                        IDENTITY, IDENTITY, currentModelPanel().getPerspArea().getViewport());
                currentModelPanel().getEditorRenderModel().updateNodes(true, false); // update to 0 position
            }
        }
        final List<ToolbarButtonGroup<ToolbarActionButtonType>.ToolbarButtonAction> buttons = actionTypeGroup
                .getButtons();
        final int numberOfButtons = buttons.size();
        for (int i = 3; i < numberOfButtons; i++) {
            buttons.get(i).getButton().setVisible(!animationModeState);
        }
        snapButton.setVisible(!animationModeState);
        timeSliderPanel.setDrawing(animationModeState);
        setKeyframe.setVisible(animationModeState);
        setTimeBounds.setVisible(animationModeState);
        timeSliderPanel.setKeyframeModeActive(animationModeState);
        if (animationModeState) {
            animationModeButton.setColors(prefs.getActiveColor1(), prefs.getActiveColor2());
        } else {
            animationModeButton.resetColors();
        }
        timeSliderPanel.repaint();
        creatorPanel.setAnimationModeState(animationModeState);
    }

    void reloadGeosetManagers(final ModelPanel display) {
        geoControl.repaint();
        display.getModelViewManagingTree().reloadFromModelView();
        geoControl.setViewportView(display.getModelViewManagingTree());
        reloadComponentBrowser(display);
        display.getPerspArea().reloadTextures();// .mpanel.perspArea.reloadTextures();//addGeosets(newGeosets);
        display.getAnimationViewer().reload();
        display.getAnimationController().reload();
        creatorPanel.reloadAnimationList();

        display.getEditorRenderModel().refreshFromEditor(animatedRenderEnvironment, IDENTITY, IDENTITY, IDENTITY,
                display.getPerspArea().getViewport());
    }

    void reloadComponentBrowser(final ModelPanel display) {
        geoControlModelData.repaint();
        display.getModelComponentBrowserTree().reloadFromModelView();
        geoControlModelData.setViewportView(display.getModelComponentBrowserTree());
    }

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

    private void linkActions(final JComponent root) {
        root.getActionMap().put("Undo", undoAction);
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Z"),
                "Undo");

        root.getActionMap().put("Redo", redoAction);
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Y"),
                "Redo");

        root.getActionMap().put("Delete", deleteAction());
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("DELETE"), "Delete");

        root.getActionMap().put("CloneSelection", cloneAction());

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
                        final ModelPanel modelPanel = currentModelPanel();
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

        root.getActionMap().put("shiftSelect", new AbstractAction("shiftSelect") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                // if (prefs.getSelectionType() == 0) {
                // for (int b = 0; b < 3; b++) {
                // buttons.get(b).resetColors();
                // }
                // addButton.setColors(prefs.getActiveColor1(),
                // prefs.getActiveColor2());
                // prefs.setSelectionType(1);
                // cheatShift = true;
                // }
                if (selectionModeGroup.getActiveButtonType() == SelectionMode.SELECT) {
                    selectionModeGroup.setToolbarButtonType(SelectionMode.ADD);
                    cheatShift = true;
                }
            }
        });
        root.getActionMap().put("altSelect", new AbstractAction("altSelect") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // if (prefs.getSelectionType() == 0) {
                // for (int b = 0; b < 3; b++) {
                // buttons.get(b).resetColors();
                // }
                // deselectButton.setColors(prefs.getActiveColor1(),
                // prefs.getActiveColor2());
                // prefs.setSelectionType(2);
                // cheatAlt = true;
                // }
                if (selectionModeGroup.getActiveButtonType() == SelectionMode.SELECT) {
                    selectionModeGroup.setToolbarButtonType(SelectionMode.DESELECT);
                    cheatAlt = true;
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        // .put(KeyStroke.getKeyStroke("control pressed CONTROL"), "shiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt pressed ALT"),
                "altSelect");

        root.getActionMap().put("unShiftSelect", new AbstractAction("unShiftSelect") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = getFocusedComponent();
                if (focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if ((selectionModeGroup.getActiveButtonType() == SelectionMode.ADD) && cheatShift) {
                    selectionModeGroup.setToolbarButtonType(SelectionMode.SELECT);
                    cheatShift = false;
                }
            }
        });
        root.getActionMap().put("unAltSelect", new AbstractAction("unAltSelect") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if ((selectionModeGroup.getActiveButtonType() == SelectionMode.DESELECT) && cheatAlt) {
                    selectionModeGroup.setToolbarButtonType(SelectionMode.SELECT);
                    cheatAlt = false;
                }
            }
        });
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released SHIFT"),
                "unShiftSelect");
        // root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released
        // CONTROL"),
        // "unShiftSelect");
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released ALT"),
                "unAltSelect");

        root.getActionMap().put("Select All", selectAllAction());
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control A"),
                "Select All");

        root.getActionMap().put("Invert Selection", invertSelectAction());
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control I"),
                "Invert Selection");

        root.getActionMap().put("Expand Selection", expandSelectionAction());
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control E"),
                "Expand Selection");

        root.getActionMap().put("RigAction", rigAction());
        root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control W"),
                "RigAction");
    }

    private void updateUIFromProgramPreferences() {
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
                    final ModelPanel modelPanel = currentModelPanel();
                    if (modelPanel != null) {
                        modelPanel.getAnimationViewer().reloadAllTextures();
                        modelPanel.getPerspArea().reloadAllTextures();

                        reloadComponentBrowser(modelPanel);
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
                newModel();
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
            } else if (e.getSource() == clearRecent) {
                clearResent();
            } else if (e.getSource() == nullmodelButton) {
                FileUtils.nullModelFile(this);
                refreshController();
            } else if ((e.getSource() == save) && (currentMDL() != null) && (currentMDL().getFile() != null)) {
                onClickSave();
            } else if (e.getSource() == saveAs) {
                onClickSaveAs();
            } else if (e.getSource() == contextCloseAll) {
                this.closeAll();
            } else if (e.getSource() == contextCloseOthers) {
                this.closeOthers(currentModelPanel);
            } else if (e.getSource() == showVertexModifyControls) {
                showVertexModifyControls();
            } else if (e.getSource() == textureModels) {
                prefs.setTextureModels(textureModels.isSelected());
            } else if (e.getSource() == showNormals) {
                prefs.setShowNormals(showNormals.isSelected());
            } else if (e.getSource() == editUVs) {
                editUVs();
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
                final AnimationFrame aFrame = new AnimationFrame(currentModelPanel(), timeSliderPanel::revalidateKeyframeDisplay);
                aFrame.setVisible(true);
            } else if (e.getSource() == linearizeAnimations) {
                linearizeAnimations();
            } else if (e.getSource() == duplicateSelection) {
                duplicateSelection();
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

        final ModelView disp = currentModelPanel().getModelViewManager();
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

    private void duplicateSelection() {
        // final int x = JOptionPane.showConfirmDialog(this,
        // "This is an irreversible process that will split selected
        // vertices into many copies of themself, one for each face, so
        // you can wrap textures and normals in a different
        // way.\n\nContinue?",
        // "Warning"/* : Divide Vertices" */,
        // JOptionPane.OK_CANCEL_OPTION);
        // if (x == JOptionPane.OK_OPTION) {
        final ModelPanel currentModelPanel = currentModelPanel();
        if (currentModelPanel != null) {
            currentModelPanel.getUndoManager().pushAction(currentModelPanel.getModelEditorManager()
                    .getModelEditor().cloneSelectedComponents(namePicker));
        }
        // }
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
        final ModelPanel modelPanel = currentModelPanel();
        final int oldIndex = modelPanels.indexOf(modelPanel);
        if (modelPanel != null) {
            if (modelPanel.close(this)) {
                modelPanels.remove(modelPanel);
                windowMenu.remove(modelPanel.getMenuItem());
                if (modelPanels.size() > 0) {
                    final int newIndex = Math.min(modelPanels.size() - 1, oldIndex);
                    setCurrentModel(modelPanels.get(newIndex));
                } else {
                    // TODO remove from notifiers to fix leaks
                    setCurrentModel(null);
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

    private void clearResent() {
        final int dialogResult = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the Recent history?", "Confirm Clear",
                JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            SaveProfile.get().clearRecent();
            updateRecent();
        }
    }

    private void showVertexModifyControls() {
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

    private void editUVs() {
        final ModelPanel disp = currentModelPanel();
        if (disp.getEditUVPanel() == null) {
            final UVPanel panel = new UVPanel(disp, prefs, modelStructureChangeListener);
            disp.setEditUVPanel(panel);

            panel.initViewport();
            final FloatingWindow floatingWindow = rootWindow.createFloatingWindow(
                    new Point(getX() + (getWidth() / 2), getY() + (getHeight() / 2)), panel.getSize(),
                    panel.getView());
            panel.init();
            floatingWindow.getTopLevelAncestor().setVisible(true);
            panel.packFrame();
        } else if (!disp.getEditUVPanel().frameVisible()) {
            final FloatingWindow floatingWindow = rootWindow.createFloatingWindow(
                    new Point(getX() + (getWidth() / 2), getY() + (getHeight() / 2)),
                    disp.getEditUVPanel().getSize(), disp.getEditUVPanel().getView());
            floatingWindow.getTopLevelAncestor().setVisible(true);
        }
    }


    private void dataSourcesChanged() {
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

    private boolean onClickSaveAs() {
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
                    currentModelPanel().getMenuItem().setName(currentFile.getName().split("\\.")[0]);
                    currentModelPanel().getMenuItem().setToolTipText(currentFile.getPath());
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

    void newModel() {
        final JPanel newModelPanel = new JPanel();
        newModelPanel.setLayout(new MigLayout());
        newModelPanel.add(new JLabel("Model Name: "), "cell 0 0");
        final JTextField newModelNameField = new JTextField("MrNew", 25);
        newModelPanel.add(newModelNameField, "cell 1 0");
        final JRadioButton createEmptyButton = new JRadioButton("Create Empty", true);
        newModelPanel.add(createEmptyButton, "cell 0 1");
        final JRadioButton createPlaneButton = new JRadioButton("Create Plane");
        newModelPanel.add(createPlaneButton, "cell 0 2");
        final JRadioButton createBoxButton = new JRadioButton("Create Box");
        newModelPanel.add(createBoxButton, "cell 0 3");
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(createBoxButton);
        buttonGroup.add(createPlaneButton);
        buttonGroup.add(createEmptyButton);

        final int userDialogResult = JOptionPane.showConfirmDialog(this, newModelPanel, "New Model",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        // TODO Duplicated code!
        if (userDialogResult == JOptionPane.OK_OPTION) {
            final EditableModel mdl = new EditableModel(newModelNameField.getText());
            if (createBoxButton.isSelected()) {
                final SpinnerNumberModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
                final JSpinner spinner = new JSpinner(sModel);
                final int userChoice = JOptionPane.showConfirmDialog(this, spinner, "Box: Choose Segments",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (userChoice != JOptionPane.OK_OPTION) {
                    return;
                }
                ModelUtils.createBox(mdl, new Vertex(64, 64, 128), new Vertex(-64, -64, 0),
                        ((Number) spinner.getValue()).intValue());
            } else if (createPlaneButton.isSelected()) {
                final SpinnerNumberModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
                final JSpinner spinner = new JSpinner(sModel);
                final int userChoice = JOptionPane.showConfirmDialog(this, spinner, "Plane: Choose Segments",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (userChoice != JOptionPane.OK_OPTION) {
                    return;
                }
                ModelUtils.createGroundPlane(mdl, new Vertex(64, 64, 0), new Vertex(-64, -64, 0),
                        ((Number) spinner.getValue()).intValue());
            }
            final ModelPanel temp = createModelPanel(mdl, GlobalIcons.MDL_ICON, false);
            loadModel(true, true, temp);
        }

    }

    ModelPanel createModelPanel(EditableModel model, ImageIcon icon, boolean specialBLPModel) {
        return new ModelPanel(this, model, prefs, MainPanel.this, selectionItemTypeGroup,
                selectionModeGroup, modelStructureChangeListener, coordDisplayListener, viewportTransferHandler,
                activeViewportWatcher, icon, specialBLPModel, textureExporter);
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

    private String convertPathToMDX(String filepath) {
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

    public ModelPanel currentModelPanel() {
        return currentModelPanel;
    }

    /**
     * Returns the MDLDisplay associated with a given MDL, or null if one cannot be
     * found.
     *
     * @param model
     */
    public ModelPanel displayFor(final EditableModel model) {
        ModelPanel output = null;
        ModelView tempDisplay;
        for (final ModelPanel modelPanel : modelPanels) {
            tempDisplay = modelPanel.getModelViewManager();
            if (tempDisplay.getModel() == model) {
                output = modelPanel;
                break;
            }
        }
        return output;
    }

    public void loadModel(final boolean temporary, final boolean selectNewTab, final ModelPanel temp) {
        if (temporary) {
            temp.getModelViewManager().getModel().setTemp(true);
        }
        final ModelPanel modelPanel = temp;
        final JMenuItem menuItem = new JMenuItem(temp.getModel().getName());
        menuItem.setIcon(temp.getIcon());
        windowMenu.add(menuItem);
        menuItem.addActionListener(e -> setCurrentModel(modelPanel));
        temp.setJMenuItem(menuItem);
        temp.getModelViewManager().addStateListener(new RepaintingModelStateListener(MainPanel.this));
        temp.changeActivity(currentActivity);

        if (geoControl == null) {
            geoControl = new JScrollPane(temp.getModelViewManagingTree());
            viewportControllerWindowView.setComponent(geoControl);
            viewportControllerWindowView.repaint();
            geoControlModelData = new JScrollPane(temp.getModelComponentBrowserTree());
            modelDataView.setComponent(geoControlModelData);
            modelComponentView.setComponent(temp.getComponentsPanel());
            modelDataView.repaint();
        }
        addTabForView(temp, selectNewTab);
        modelPanels.add(temp);

        // tabbedPane.addTab(f.getName().split("\\.")[0], icon, temp, f.getPath());
        // if (selectNewTab) {
        // tabbedPane.setSelectedComponent(temp);
        // }
        if (temporary) {
            temp.getModelViewManager().getModel().setFileRef(null);
        }
        // }
        // }).start();
        toolsMenu.setEnabled(true);

        if (selectNewTab && (prefs.getQuickBrowse() != null) && prefs.getQuickBrowse()) {
            for (int i = (modelPanels.size() - 2); i >= 0; i--) {
                final ModelPanel openModelPanel = modelPanels.get(i);
                if (openModelPanel.getUndoManager().isRedoListEmpty()
                        && openModelPanel.getUndoManager().isUndoListEmpty()) {
                    if (openModelPanel.close(this)) {
                        modelPanels.remove(openModelPanel);
                        windowMenu.remove(openModelPanel.getMenuItem());
                    }
                }
            }
        }
    }

    public void addTabForView(final ModelPanel view, final boolean selectNewTab) {
        if (selectNewTab) {
            view.getMenuItem().doClick();
        }
    }

    public void setCurrentModel(final ModelPanel modelContextManager) {
        currentModelPanel = modelContextManager;
        if (currentModelPanel == null) {
            final JPanel jPanel = new JPanel();
            jPanel.add(new JLabel("..."));
            viewportControllerWindowView.setComponent(jPanel);
            geoControl = null;
            frontView.setComponent(new JPanel());
            bottomView.setComponent(new JPanel());
            leftView.setComponent(new JPanel());
            perspectiveView.setComponent(new JPanel());
            previewView.setComponent(new JPanel());
            animationControllerView.setComponent(new JPanel());
            refreshAnimationModeState();
            timeSliderPanel.setUndoManager(null, animatedRenderEnvironment);
            timeSliderPanel.setModelView(null);
            creatorPanel.setModelEditorManager(null);
            creatorPanel.setCurrentModel(null);
            creatorPanel.setUndoManager(null);
            modelComponentView.setComponent(new JPanel());
            geoControlModelData = null;
        } else {
            geoControl.setViewportView(currentModelPanel.getModelViewManagingTree());
            geoControl.repaint();

            frontView.setComponent(modelContextManager.getFrontArea());
            bottomView.setComponent(modelContextManager.getBotArea());
            leftView.setComponent(modelContextManager.getSideArea());
            perspectiveView.setComponent(modelContextManager.getPerspArea());
            previewView.setComponent(modelContextManager.getAnimationViewer());
            animationControllerView.setComponent(modelContextManager.getAnimationController());
            refreshAnimationModeState();
            timeSliderPanel.setUndoManager(currentModelPanel.getUndoManager(), animatedRenderEnvironment);
            timeSliderPanel.setModelView(currentModelPanel.getModelViewManager());
            creatorPanel.setModelEditorManager(currentModelPanel.getModelEditorManager());
            creatorPanel.setCurrentModel(currentModelPanel.getModelViewManager());
            creatorPanel.setUndoManager(currentModelPanel.getUndoManager());

            geoControlModelData.setViewportView(currentModelPanel.getModelComponentBrowserTree());

            modelComponentView.setComponent(currentModelPanel.getComponentsPanel());
            geoControlModelData.repaint();
            currentModelPanel.getModelComponentBrowserTree().reloadFromModelView();
        }
        activeViewportWatcher.viewportChanged(null);
        timeSliderPanel.revalidateKeyframeDisplay();
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

    public boolean closeAll() {
        boolean success = true;
        final Iterator<ModelPanel> iterator = modelPanels.iterator();
        boolean closedCurrentPanel = false;
        ModelPanel lastUnclosedModelPanel = null;
        while (iterator.hasNext()) {
            final ModelPanel panel = iterator.next();
            if (success = panel.close(this)) {
                windowMenu.remove(panel.getMenuItem());
                iterator.remove();
                if (panel == currentModelPanel) {
                    closedCurrentPanel = true;
                }
            } else {
                lastUnclosedModelPanel = panel;
                break;
            }
        }
        if (closedCurrentPanel) {
            setCurrentModel(lastUnclosedModelPanel);
        }
        return success;
    }

    public boolean closeOthers(final ModelPanel panelToKeepOpen) {
        boolean success = true;
        final Iterator<ModelPanel> iterator = modelPanels.iterator();
        boolean closedCurrentPanel = false;
        ModelPanel lastUnclosedModelPanel = null;
        while (iterator.hasNext()) {
            final ModelPanel panel = iterator.next();
            if (panel == panelToKeepOpen) {
                lastUnclosedModelPanel = panel;
                continue;
            }
            if (success = panel.close(this)) {
                windowMenu.remove(panel.getMenuItem());
                iterator.remove();
                if (panel == currentModelPanel) {
                    closedCurrentPanel = true;
                }
            } else {
                lastUnclosedModelPanel = panel;
                break;
            }
        }
        if (closedCurrentPanel) {
            setCurrentModel(lastUnclosedModelPanel);
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

    private Component getFocusedComponent() {
        final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        final Component focusedComponent = kfm.getFocusOwner();
        return focusedComponent;
    }

    private boolean focusedComponentNeedsTyping(final Component focusedComponent) {
        return (focusedComponent instanceof JTextArea) || (focusedComponent instanceof JTextField);
    }
}
