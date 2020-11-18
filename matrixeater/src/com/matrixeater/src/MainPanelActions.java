//package com.matrixeater.src;
//
//import com.hiveworkshop.wc3.gui.BLPHandler;
//import com.hiveworkshop.wc3.gui.ExceptionPopup;
//import com.hiveworkshop.wc3.gui.GlobalIcons;
//import com.hiveworkshop.wc3.gui.ProgramPreferences;
//import com.hiveworkshop.wc3.gui.datachooser.DataSourceDescriptor;
//import com.hiveworkshop.wc3.gui.modeledit.*;
//import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
//import com.hiveworkshop.wc3.gui.modeledit.newstuff.ModelEditorManager;
//import com.hiveworkshop.wc3.gui.modeledit.newstuff.listener.ClonedNodeNamePicker;
//import com.hiveworkshop.wc3.gui.modeledit.util.TextureExporter;
//import com.hiveworkshop.wc3.jworldedit.models.BetterUnitEditorModelSelector;
//import com.hiveworkshop.wc3.jworldedit.objects.DoodadTabTreeBrowserBuilder;
//import com.hiveworkshop.wc3.jworldedit.objects.UnitEditorTree;
//import com.hiveworkshop.wc3.mdl.*;
//import com.hiveworkshop.wc3.mdl.v2.ModelView;
//import com.hiveworkshop.wc3.mdl.v2.ModelViewStateListener;
//import com.hiveworkshop.wc3.mdx.MdxModel;
//import com.hiveworkshop.wc3.mdx.MdxUtils;
//import com.hiveworkshop.wc3.mpq.MpqCodebase;
//import com.hiveworkshop.wc3.resources.WEString;
//import com.hiveworkshop.wc3.units.GameObject;
//import com.hiveworkshop.wc3.units.ModelOptionPane;
//import com.hiveworkshop.wc3.units.UnitOptionPane;
//import com.hiveworkshop.wc3.units.fields.UnitFields;
//import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
//import com.hiveworkshop.wc3.units.objectdata.War3ID;
//import com.hiveworkshop.wc3.user.SaveProfile;
//import com.hiveworkshop.wc3.util.ModelUtils;
//import com.matrixeater.imp.AnimationTransfer;
//import com.owens.oobjloader.builder.Build;
//import com.owens.oobjloader.parser.Parse;
//import de.wc3data.stream.BlizzardDataInputStream;
//import de.wc3data.stream.BlizzardDataOutputStream;
//import net.infonode.docking.FloatingWindow;
//import net.infonode.docking.SplitWindow;
//import net.infonode.docking.View;
//import net.miginfocom.swing.MigLayout;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.DefaultStyledDocument;
//import javax.swing.text.rtf.RTFEditorKit;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.TreePath;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//public class MainPanelActions {
//    private final MainPanel mainPanel;
//
//    public MainPanelActions(MainPanel mainPanel) {
//        this.mainPanel = mainPanel;
//    }
//
//    final AbstractAction undoAction = createUndoAction();
//    final AbstractAction redoAction = createRedoAction();
//    final ClonedNodeNamePicker namePicker = createClonedNodeNamePicker();
//
//    final AbstractAction cloneAction = createCloneAction();
//    final AbstractAction deleteAction = createDeleteAction();
//    AbstractAction cutAction = createCutAction();
//    AbstractAction copyAction = createCopyAction();
//    AbstractAction pasteAction = createPasteAction();
//    final AbstractAction selectAllAction = createSelectAllAction();
//    final AbstractAction invertSelectAction = createInvertSelectAction();
//    final AbstractAction rigAction = createRigAction();
//    final AbstractAction expandSelectionAction = createExpandSelectionAction();
//    final AbstractAction snapNormalsAction = createSnapNormalsAction();
//    final AbstractAction snapVerticesAction = createsnapVerticesAction();
//    final AbstractAction recalcNormalsAction = createRecalcNormalsAction();
//    final AbstractAction recalcExtentsAction = createRecalcExtentsAction();
//    final AbstractAction flipAllUVsUAction = createFlipAllUVsUAction();
//    final AbstractAction flipAllUVsVAction = createFlipAllUVsVAction();
//    final AbstractAction inverseAllUVsAction = createInverseAllUVsAction();
//    final AbstractAction mirrorXAction = createMirrorXAction();
//    final AbstractAction mirrorYAction = createMirrorYAction();
//    final AbstractAction mirrorZAction = createMirrorZAction();
//    final AbstractAction insideOutAction = createInsideOutAction();
//    final AbstractAction insideOutNormalsAction = createInsideOutNormalsAction();
//    final AbstractAction viewMatricesAction = createViewMatricesAction();
//    final AbstractAction openAnimationViewerAction = createOpenAnimationViewerAction();
//    final AbstractAction openAnimationControllerAction = createOpenAnimationControllerAction();
//    final AbstractAction openModelingTabAction = createOpenModelingTabAction();
//    final AbstractAction openPerspectiveAction = createOpenPerspectiveAction();
//    final AbstractAction openOutlinerAction = createOpenOutlinerAction();
//    final AbstractAction openSideAction = createOpenSideAction();
//    final AbstractAction openTimeSliderAction = createOpenTimeSliderAction();
//    final AbstractAction openFrontAction = createOpenFrontAction();
//    final AbstractAction openBottomAction = createOpenBottomAction();
//    final AbstractAction openToolsAction = createOpenToolsAction();
//    final AbstractAction openModelDataContentsViewAction = createOpenModelDataContentsViewAction();
//    final AbstractAction hackerViewAction = createHackerViewAction();
//    final AbstractAction openPreferencesAction = createOpenPreferencesAction();
//    final AbstractAction openMPQViewerAction = createOpenMPQViewerAction();
//    final AbstractAction openUnitViewerAction = createOpenUnitViewerAction();
//    final AbstractAction openDoodadViewerAction = createOpenDoodadViewerAction();
//    final AbstractAction openHiveViewerAction = createOpenHiveViewerAction();
//
//
//    private UndoActionImplementation createUndoAction(){
//        return new UndoActionImplementation("Undo", mainPanel);
//    }
//    private RedoActionImplementation createRedoAction() {
//        return new RedoActionImplementation("Redo", mainPanel);
//    }
//    private ClonedNodeNamePickerImplementation createClonedNodeNamePicker(){
//        return new ClonedNodeNamePickerImplementation(mainPanel);
//    }
//
//    private AbstractAction createCloneAction(){
//        return new AbstractAction("CloneSelection") {
//        @Override
//        public void actionPerformed(final ActionEvent e) {
//            final ModelPanel mpanel = mainPanel.currentModelPanel();
//            if (mpanel != null) {
//                try {
//                    mpanel.getUndoManager().pushAction(
//                            mpanel.getModelEditorManager().getModelEditor().cloneSelectedComponents(namePicker));
//                } catch (final Exception exc) {
//                    ExceptionPopup.display(exc);
//                }
//            }
//            mainPanel.refreshUndo();
//            mainPanel.repaintSelfAndChildren(mpanel);
//        }
//        };
//    }
//
//    private AbstractAction createDeleteAction() {
//        return new AbstractAction("Delete") {
//                @Override
//                public void actionPerformed(final ActionEvent e) {
//                    final ModelPanel mpanel = mainPanel.currentModelPanel();
//                    if (mpanel != null) {
//                        if (mainPanel.animationModeState) {
//                            mainPanel.timeSliderPanel.deleteSelectedKeyframes();
//                        } else {
//                            mpanel.getUndoManager()
//                                    .pushAction(mpanel.getModelEditorManager().getModelEditor().deleteSelectedComponents());
//                        }
//                    }
//                    mainPanel.repaintSelfAndChildren(mpanel);
//                }
//            };
//    }
//    private AbstractAction createCutAction() {
//        return new AbstractAction("Cut") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = mainPanel.currentModelPanel();
//                if (mpanel != null) {
//                    try {
//                        mpanel.getModelEditorManager();// cut
//                        // something
//                        // to
//                        // clipboard
//                    } catch (final Exception exc) {
//                        ExceptionPopup.display(exc);
//                    }
//                }
//                mainPanel.refreshUndo();
//                mainPanel.repaintSelfAndChildren(mpanel);
//            }
//        };
//    }
//
//    private AbstractAction createCopyAction() {
//        return new AbstractAction("Copy") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    try {
//                        mpanel.getModelEditorManager();// copy
//                        // something
//                        // to
//                        // clipboard
//                    } catch (final Exception exc) {
//                        ExceptionPopup.display(exc);
//                    }
//                }
//                refreshUndo();
//                repaintSelfAndChildren(mpanel);
//            }
//        };
//    }
//
//    private AbstractAction createPasteAction() {
//        AbstractAction pasteAction = new AbstractAction("Paste") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    try {
//                        mpanel.getModelEditorManager();// paste
//                        // something
//                        // from
//                        // clipboard
//                    } catch (final Exception exc) {
//                        ExceptionPopup.display(exc);
//                    }
//                }
//                refreshUndo();
//                repaintSelfAndChildren(mpanel);
//            }
//        };
//    }
//
//    private AbstractAction createSelectAllAction() {
//        final AbstractAction selectAllAction = new AbstractAction("Select All") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().selectAll());
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createInvertSelectAction() {
//        final AbstractAction invertSelectAction = new AbstractAction("Invert Selection") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().invertSelection());
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createRigAction() {
//        final AbstractAction rigAction = new AbstractAction("Rig") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    boolean valid = false;
//                    for (final Vertex v : mpanel.getModelEditorManager().getSelectionView().getSelectedVertices()) {
//                        final int index = mpanel.getModel().getPivots().indexOf(v);
//                        if (index != -1) {
//                            if (index < mpanel.getModel().getIdObjects().size()) {
//                                final IdObject node = mpanel.getModel().getIdObject(index);
//                                if ((node instanceof Bone) && !(node instanceof Helper)) {
//                                    valid = true;
//                                }
//                            }
//                        }
//                    }
//                    if (valid) {
//                        mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().rig());
//                    } else {
//                        System.err.println("NOT RIGGING, NOT VALID");
//                    }
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createExpandSelectionAction() {
//        final AbstractAction expandSelectionAction = new AbstractAction("Expand Selection") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().expandSelection());
//                }
//                repaint();
//            }
//        };
//    }
//    private AbstractAction createSnapNormalsAction() {
//        final AbstractAction snapNormalsAction = new AbstractAction("Snap Normals") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().snapNormals());
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createsnapVerticesAction() {
//        final AbstractAction snapVerticesAction = new AbstractAction("Snap Vertices") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    mpanel.getUndoManager()
//                            .pushAction(mpanel.getModelEditorManager().getModelEditor().snapSelectedVertices());
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createRecalcNormalsAction() {
//        final AbstractAction recalcNormalsAction = new AbstractAction("RecalculateNormals") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().recalcNormals());
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createRecalcExtentsAction() {
//        final AbstractAction recalcExtentsAction = new AbstractAction("RecalculateExtents") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    final JPanel messagePanel = new JPanel(new MigLayout());
//                    messagePanel.add(new JLabel("This will calculate the extents of all model components. Proceed?"),
//                            "wrap");
//                    messagePanel.add(new JLabel("(It may destroy existing extents)"), "wrap");
//                    final JRadioButton considerAllBtn = new JRadioButton("Consider all geosets for calculation");
//                    final JRadioButton considerCurrentBtn = new JRadioButton(
//                            "Consider current editable geosets for calculation");
//                    final ButtonGroup buttonGroup = new ButtonGroup();
//                    buttonGroup.add(considerAllBtn);
//                    buttonGroup.add(considerCurrentBtn);
//                    considerAllBtn.setSelected(true);
//                    messagePanel.add(considerAllBtn, "wrap");
//                    messagePanel.add(considerCurrentBtn, "wrap");
//                    final int userChoice = JOptionPane.showConfirmDialog(MainPanel.this, messagePanel, "Message",
//                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//                    if (userChoice == JOptionPane.YES_OPTION) {
//                        mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor()
//                                .recalcExtents(considerCurrentBtn.isSelected()));
//                    }
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createFlipAllUVsUAction() {
//        final AbstractAction flipAllUVsUAction = new AbstractAction("Flip All UVs U") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                for (final Geoset geo : currentMDL().getGeosets()) {
//                    for (final UVLayer layer : geo.getUVLayers()) {
//                        for (int i = 0; i < layer.numTVerteces(); i++) {
//                            final TVertex tvert = layer.getTVertex(i);
//                            tvert.y = 1.0 - tvert.y;
//                        }
//                    }
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createFlipAllUVsVAction() {
//        final AbstractAction flipAllUVsVAction = new AbstractAction("Flip All UVs V") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                // TODO this should be an action
//                for (final Geoset geo : currentMDL().getGeosets()) {
//                    for (final UVLayer layer : geo.getUVLayers()) {
//                        for (int i = 0; i < layer.numTVerteces(); i++) {
//                            final TVertex tvert = layer.getTVertex(i);
//                            tvert.y = 1.0 - tvert.y;
//                        }
//                    }
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createInverseAllUVsAction() {
//        final AbstractAction inverseAllUVsAction = new AbstractAction("Swap UVs U for V") {
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
//
//    private AbstractAction createMirrorXAction() {
//        final AbstractAction mirrorXAction = new AbstractAction("Mirror X") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    final Vertex selectionCenter = mpanel.getModelEditorManager().getModelEditor().getSelectionCenter();
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().mirror((byte) 0,
//                            mirrorFlip.isSelected(), selectionCenter.x, selectionCenter.y, selectionCenter.z));
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createMirrorYAction() {
//        final AbstractAction mirrorYAction = new AbstractAction("Mirror Y") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ModelPanel mpanel = currentModelPanel();
//                if (mpanel != null) {
//                    final Vertex selectionCenter = mpanel.getModelEditorManager().getModelEditor().getSelectionCenter();
//
//                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().mirror((byte) 1,
//                            mirrorFlip.isSelected(), selectionCenter.x, selectionCenter.y, selectionCenter.z));
//                }
//                repaint();
//            }
//        };
//    }
//
//    private AbstractAction createMirrorZAction() {
//        final AbstractAction mirrorZAction = new AbstractAction("Mirror Z") {
//        @Override
//        public void actionPerformed(final ActionEvent e) {
//            final ModelPanel mpanel = currentModelPanel();
//            if (mpanel != null) {
//                final Vertex selectionCenter = mpanel.getModelEditorManager().getModelEditor().getSelectionCenter();
//
//                mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().mirror((byte) 2,
//                        mirrorFlip.isSelected(), selectionCenter.x, selectionCenter.y, selectionCenter.z));
//            }
//            repaint();
//        }
//    };
//    }
//
//    private AbstractAction createInsideOutAction() {
//        final AbstractAction insideOutAction = new AbstractAction("Inside Out") {
//        @Override
//        public void actionPerformed(final ActionEvent e) {
//            final ModelPanel mpanel = currentModelPanel();
//            if (mpanel != null) {
//                mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().flipSelectedFaces());
//            }
//            repaint();
//        }
//    };
//    }
//
//    private AbstractAction createInsideOutNormalsAction() {
//        final AbstractAction insideOutNormalsAction = new AbstractAction("Inside Out Normals") {
//        @Override
//        public void actionPerformed(final ActionEvent e) {
//            final ModelPanel mpanel = currentModelPanel();
//            if (mpanel != null) {
//                mpanel.getUndoManager()
//                        .pushAction(mpanel.getModelEditorManager().getModelEditor().flipSelectedNormals());
//            }
//            repaint();
//        }
//    };
//    }
//
//    private AbstractAction createViewMatricesAction() {
//        final AbstractAction viewMatricesAction = new AbstractAction("View Matrices") {
//        @Override
//        public void actionPerformed(final ActionEvent e) {
//            final ModelPanel mpanel = currentModelPanel();
//            if (mpanel != null) {
//                mpanel.viewMatrices();
//            }
//            repaint();
//        }
//    };
//    }
//
//    private AbstractAction createOpenAnimationViewerAction() {
//        final AbstractAction openAnimationViewerAction = new MainPanel.OpenViewAction("Animation Preview", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return previewView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenAnimationControllerAction() {
//        final AbstractAction openAnimationControllerAction = new MainPanel.OpenViewAction("Animation Controller", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return animationControllerView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenModelingTabAction() {
//        final AbstractAction openModelingTabAction = new MainPanel.OpenViewAction("Modeling", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return creatorView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenPerspectiveAction() {
//        final AbstractAction openPerspectiveAction = new MainPanel.OpenViewAction("Perspective", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return perspectiveView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenOutlinerAction() {
//        final AbstractAction openOutlinerAction = new MainPanel.OpenViewAction("Outliner", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return viewportControllerWindowView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenSideAction() {
//        final AbstractAction openSideAction = new MainPanel.OpenViewAction("Side", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return leftView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenTimeSliderAction() {
//        final AbstractAction openTimeSliderAction = new MainPanel.OpenViewAction("Footer", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return timeSliderView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenFrontAction() {
//        final AbstractAction openFrontAction = new MainPanel.OpenViewAction("Front", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return frontView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenBottomAction() {
//        final AbstractAction openBottomAction = new MainPanel.OpenViewAction("Bottom", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return bottomView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenToolsAction() {
//        final AbstractAction openToolsAction = new MainPanel.OpenViewAction("Tools", new MainPanel.OpenViewGetter() {
//        @Override
//        public View getView() {
//            return toolView;
//        }
//    });
//    }
//
//    private AbstractAction createOpenModelDataContentsViewAction() {
//        final AbstractAction openModelDataContentsViewAction = new MainPanel.OpenViewAction("Model", new MainPanel.OpenViewGetter() {
//            @Override
//            public View getView() {
//                return modelDataView;
//            }
//        });
//    }
//
//    private AbstractAction createHackerViewAction() {
//        final AbstractAction hackerViewAction = new MainPanel.OpenViewAction("Matrix Eater Script", new mainPanel.OpenViewGetter() {
//            @Override
//            public View getView() {
//                return hackerView;
//            }
//        });
//    }
//
//
//    private AbstractAction createOpenPreferencesAction() {
//        final AbstractAction openPreferencesAction = new AbstractAction("Open Preferences") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final ProgramPreferences programPreferences = new ProgramPreferences();
//                programPreferences.loadFrom(mainPanel.prefs);
//                final List<DataSourceDescriptor> priorDataSources = SaveProfile.get().getDataSources();
//                final ProgramPreferencesPanel programPreferencesPanel = new ProgramPreferencesPanel(programPreferences,
//                        priorDataSources);
//                // final JFrame frame = new JFrame("Preferences");
//                // frame.setIconImage(MainFrame.frame.getIconImage());
//                // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                // frame.setContentPane(programPreferencesPanel);
//                // frame.pack();
//                // frame.setLocationRelativeTo(MainPanel.this);
//                // frame.setVisible(true);
//
//                final int ret = JOptionPane.showConfirmDialog(mainPanel, programPreferencesPanel, "Preferences",
//                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//                if (ret == JOptionPane.OK_OPTION) {
//                    mainPanel.prefs.loadFrom(programPreferences);
//                    final List<DataSourceDescriptor> dataSources = programPreferencesPanel.getDataSources();
//                    final boolean changedDataSources = (dataSources != null) && !dataSources.equals(priorDataSources);
//                    if (changedDataSources) {
//                        SaveProfile.get().setDataSources(dataSources);
//                    }
//                    SaveProfile.save();
//                    if (changedDataSources) {
//                        mainPanel.dataSourcesChanged();
//                    }
//                    mainPanel.updateUIFromProgramPreferences();
//                }
//            }
//        };
//    }
//
//
//    private AbstractAction createOpenMPQViewerAction() {
//        final AbstractAction openMPQViewerAction = new AbstractAction("Open MPQ Browser") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final View view = mainPanel.createMPQBrowser();
//                mainPanel.rootWindow.setWindow(new SplitWindow(true, 0.75f, mainPanel.rootWindow.getWindow(), view));
//            }
//        };
//    }
//
//    private AbstractAction createOpenUnitViewerAction() {
//        final AbstractAction openUnitViewerAction = new AbstractAction("Open Unit Browser") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final UnitEditorTree unitEditorTree = mainPanel.createUnitEditorTree();
//                mainPanel.rootWindow.setWindow(new SplitWindow(true, 0.75f, mainPanel.rootWindow.getWindow(),
//                        new View("Unit Browser",
//                                new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)),
//                                new JScrollPane(unitEditorTree))));
//            }
//        };
//    }
//
//    private AbstractAction createOpenDoodadViewerAction() {
//        final AbstractAction openDoodadViewerAction = new AbstractAction("Open Doodad Browser") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final UnitEditorTree unitEditorTree = new UnitEditorTree(mainPanel.getDoodadData(), new DoodadTabTreeBrowserBuilder(),
//                        mainPanel.getUnitEditorSettings(), MutableObjectData.WorldEditorDataType.DOODADS);
//                unitEditorTree.selectFirstUnit();
//                // final FloatingWindow floatingWindow =
//                // rootWindow.createFloatingWindow(rootWindow.getLocation(),
//                // mpqBrowser.getPreferredSize(),
//                // new View("MPQ Browser",
//                // new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16,
//                // Image.SCALE_FAST)),
//                // mpqBrowser));
//                // floatingWindow.getTopLevelAncestor().setVisible(true);
//                unitEditorTree.addMouseListener(new MouseAdapter() {
//                    @Override
//                    public void mouseClicked(final MouseEvent e) {
//                        try {
//                            if (e.getClickCount() >= 2) {
//                                final TreePath currentUnitTreePath = unitEditorTree.getSelectionPath();
//                                if (currentUnitTreePath != null) {
//                                    final DefaultMutableTreeNode o = (DefaultMutableTreeNode) currentUnitTreePath
//                                            .getLastPathComponent();
//                                    if (o.getUserObject() instanceof MutableObjectData.MutableGameObject) {
//                                        final MutableObjectData.MutableGameObject obj = (MutableObjectData.MutableGameObject) o.getUserObject();
//                                        final int numberOfVariations = obj.getFieldAsInteger(War3ID.fromString("dvar"), 0);
//                                        if (numberOfVariations > 1) {
//                                            for (int i = 0; i < numberOfVariations; i++) {
//                                                final String path = mainPanel.convertPathToMDX(
//                                                        obj.getFieldAsString(War3ID.fromString("dfil"), 0) + i + ".mdl");
//                                                final String portrait = ModelUtils.getPortrait(path);
//                                                final ImageIcon icon = new ImageIcon(com.hiveworkshop.wc3.util.IconUtils
//                                                        .getIcon(obj, MutableObjectData.WorldEditorDataType.DOODADS)
//                                                        .getScaledInstance(16, 16, Image.SCALE_DEFAULT));
//
//                                                System.out.println(path);
//                                                mainPanel.loadStreamMdx(MpqCodebase.get().getResourceAsStream(path), true, i == 0,
//                                                        icon);
//                                                if (mainPanel.prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
//                                                    mainPanel.loadStreamMdx(MpqCodebase.get().getResourceAsStream(portrait), true,
//                                                            false, icon);
//                                                }
//                                            }
//                                        } else {
//                                            final String path = mainPanel.convertPathToMDX(
//                                                    obj.getFieldAsString(War3ID.fromString("dfil"), 0));
//                                            final String portrait = ModelUtils.getPortrait(path);
//                                            final ImageIcon icon = new ImageIcon(com.hiveworkshop.wc3.util.IconUtils
//                                                    .getIcon(obj, MutableObjectData.WorldEditorDataType.DOODADS)
//                                                    .getScaledInstance(16, 16, Image.SCALE_DEFAULT));
//                                            System.out.println(path);
//                                            mainPanel.loadStreamMdx(MpqCodebase.get().getResourceAsStream(path), true, true, icon);
//                                            if (mainPanel.prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
//                                                mainPanel.loadStreamMdx(MpqCodebase.get().getResourceAsStream(portrait), true, false,
//                                                        icon);
//                                            }
//                                        }
//                                        mainPanel.toolsMenu.getAccessibleContext().setAccessibleDescription(
//                                                "Allows the user to control which parts of the model are displayed for editing.");
//                                        mainPanel.toolsMenu.setEnabled(true);
//                                    }
//                                }
//                            }
//                        } catch (final Exception exc) {
//                            exc.printStackTrace();
//                            ExceptionPopup.display(exc);
//                        }
//                    }
//                });
//                mainPanel.rootWindow.setWindow(new SplitWindow(true, 0.75f, mainPanel.rootWindow.getWindow(),
//                        new View("Doodad Browser",
//                                new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)),
//                                new JScrollPane(unitEditorTree))));
//            }
//        };
//    }
//
//    private AbstractAction createOpenHiveViewerAction() {
//        final AbstractAction openHiveViewerAction = new AbstractAction("Open Hive Browser") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                final JPanel panel = new JPanel();
//                panel.setLayout(new BorderLayout());
//                panel.add(BorderLayout.BEFORE_FIRST_LINE, new JLabel(POWERED_BY_HIVE));
//                // final JPanel resourceFilters = new JPanel();
//                // resourceFilters.setBorder(BorderFactory.createTitledBorder("Resource
//                // Filters"));
//                // panel.add(BorderLayout.BEFORE_LINE_BEGINS, resourceFilters);
//                // resourceFilters.add(new JLabel("Resource Type"));
//                // resourceFilters.add(new JComboBox<>(new String[] { "Any" }));
//                final JList<String> view = new JList<>(
//                        new String[]{"Bongo Bongo (Phantom Shadow Beast)", "Other Model", "Other Model"});
//                view.setCellRenderer(new DefaultListCellRenderer() {
//                    @Override
//                    public Component getListCellRendererComponent(final javax.swing.JList<?> list, final Object value,
//                                                                  final int index, final boolean isSelected, final boolean cellHasFocus) {
//                        final Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index,
//                                isSelected, cellHasFocus);
//                        final ImageIcon icon = new ImageIcon(MainPanel.class.getResource("ImageBin/deleteme.png"));
//                        setIcon(new ImageIcon(icon.getImage().getScaledInstance(48, 32, Image.SCALE_DEFAULT)));
//                        return listCellRendererComponent;
//                    }
//                });
//                panel.add(BorderLayout.BEFORE_LINE_BEGINS, new JScrollPane(view));
//
//                final JPanel tags = new JPanel();
//                tags.setBorder(BorderFactory.createTitledBorder("Tags"));
//                tags.setLayout(new GridLayout(30, 1));
//                tags.add(new JCheckBox("Results must include all selected tags"));
//                tags.add(new JSeparator());
//                tags.add(new JLabel("Types (Models)"));
//                tags.add(new JSeparator());
//                tags.add(new JCheckBox("Building"));
//                tags.add(new JCheckBox("Doodad"));
//                tags.add(new JCheckBox("Item"));
//                tags.add(new JCheckBox("User Interface"));
//                panel.add(BorderLayout.CENTER, tags);
//                // final FloatingWindow floatingWindow =
//                // rootWindow.createFloatingWindow(rootWindow.getLocation(),
//                // mpqBrowser.getPreferredSize(),
//                // new View("MPQ Browser",
//                // new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16,
//                // Image.SCALE_FAST)),
//                // mpqBrowser));
//                // floatingWindow.getTopLevelAncestor().setVisible(true);
//                mainPanel.rootWindow.setWindow(new SplitWindow(true, 0.75f, mainPanel.rootWindow.getWindow(), new View("Hive Browser",
//                        new ImageIcon(MainFrame.frame.getIconImage().getScaledInstance(16, 16, Image.SCALE_FAST)), panel)));
//            }
//        };
//    }
//
//
//    private void createTeamColorMenuItems() {
//        for (int i = 0; i < 25; i++) {
//            final String colorNumber = String.format("%2s", i).replace(' ', '0');
//            try {
//                final String colorName = WEString.getString("WESTRING_UNITCOLOR_" + colorNumber);
//                final JMenuItem menuItem = new JMenuItem(colorName, new ImageIcon(BLPHandler.get()
//                        .getGameTex("ReplaceableTextures\\TeamColor\\TeamColor" + colorNumber + ".blp")));
//                teamColorMenu.add(menuItem);
//                final int teamColorValueNumber = i;
//                menuItem.addActionListener(e -> {
//                    Material.teamColor = teamColorValueNumber;
//                    final ModelPanel modelPanel = currentModelPanel();
//                    if (modelPanel != null) {
//                        modelPanel.getAnimationViewer().reloadAllTextures();
//                        modelPanel.getPerspArea().reloadAllTextures();
//
//                        reloadComponentBrowser(modelPanel);
//                    }
//                    profile.getPreferences().setTeamColor(teamColorValueNumber);
//                });
//            } catch (final Exception ex) {
//                // load failed
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void actionPerformed(final ActionEvent e) {
//        // Open, off of the file menu:
//        refreshUndo();
//        try {
//            if (e.getSource() == newModel) {
//                newModel();
//            } else if (e.getSource() == open) {
//                onClickOpen();
//            } else if (e.getSource() == close) {
//                final ModelPanel modelPanel = currentModelPanel();
//                final int oldIndex = modelPanels.indexOf(modelPanel);
//                if (modelPanel != null) {
//                    if (modelPanel.close(this)) {
//                        modelPanels.remove(modelPanel);
//                        windowMenu.remove(modelPanel.getMenuItem());
//                        if (modelPanels.size() > 0) {
//                            final int newIndex = Math.min(modelPanels.size() - 1, oldIndex);
//                            setCurrentModel(modelPanels.get(newIndex));
//                        } else {
//                            // TODO remove from notifiers to fix leaks
//                            setCurrentModel(null);
//                        }
//                    }
//                }
//            } else if (e.getSource() == fetchUnit) {
//                final GameObject unitFetched = fetchUnit();
//                if (unitFetched != null) {
//                    final String filepath = convertPathToMDX(unitFetched.getField("file"));
//                    if (filepath != null) {
//                        loadStreamMdx(MpqCodebase.get().getResourceAsStream(filepath), true, true,
//                                unitFetched.getScaledIcon(0.25f));
//                        final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
//                                + filepath.substring(filepath.lastIndexOf('.'));
//                        if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
//                            loadStreamMdx(MpqCodebase.get().getResourceAsStream(portrait), true, false,
//                                    unitFetched.getScaledIcon(0.25f));
//                        }
//                        toolsMenu.getAccessibleContext().setAccessibleDescription(
//                                "Allows the user to control which parts of the model are displayed for editing.");
//                        toolsMenu.setEnabled(true);
//                    }
//                }
//            } else if (e.getSource() == fetchModel) {
//                final ModelOptionPane.ModelElement model = fetchModel();
//                if (model != null) {
//                    final String filepath = convertPathToMDX(model.getFilepath());
//                    if (filepath != null) {
//
//                        final ImageIcon icon = model.hasCachedIconPath() ? new ImageIcon(BLPHandler.get()
//                                .getGameTex(model.getCachedIconPath()).getScaledInstance(16, 16, Image.SCALE_FAST))
//                                : MDLIcon;
//                        loadStreamMdx(MpqCodebase.get().getResourceAsStream(filepath), true, true, icon);
//                        final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
//                                + filepath.substring(filepath.lastIndexOf('.'));
//                        if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
//                            loadStreamMdx(MpqCodebase.get().getResourceAsStream(portrait), true, false, icon);
//                        }
//                        toolsMenu.getAccessibleContext().setAccessibleDescription(
//                                "Allows the user to control which parts of the model are displayed for editing.");
//                        toolsMenu.setEnabled(true);
//                    }
//                }
//            } else if (e.getSource() == fetchObject) {
//                final MutableObjectData.MutableGameObject objectFetched = fetchObject();
//                if (objectFetched != null) {
//                    final String filepath = convertPathToMDX(objectFetched.getFieldAsString(UnitFields.MODEL_FILE, 0));
//                    if (filepath != null) {
//                        loadStreamMdx(MpqCodebase.get().getResourceAsStream(filepath), true, true,
//                                new ImageIcon(BLPHandler.get()
//                                        .getGameTex(objectFetched.getFieldAsString(UnitFields.INTERFACE_ICON, 0))
//                                        .getScaledInstance(16, 16, Image.SCALE_FAST)));
//                        final String portrait = filepath.substring(0, filepath.lastIndexOf('.')) + "_portrait"
//                                + filepath.substring(filepath.lastIndexOf('.'));
//                        if (prefs.isLoadPortraits() && MpqCodebase.get().has(portrait)) {
//                            loadStreamMdx(MpqCodebase.get().getResourceAsStream(portrait), true, false,
//                                    new ImageIcon(BLPHandler.get()
//                                            .getGameTex(objectFetched.getFieldAsString(UnitFields.INTERFACE_ICON, 0))
//                                            .getScaledInstance(16, 16, Image.SCALE_FAST)));
//                        }
//                        toolsMenu.getAccessibleContext().setAccessibleDescription(
//                                "Allows the user to control which parts of the model are displayed for editing.");
//                        toolsMenu.setEnabled(true);
//                    }
//                }
//            } else if (e.getSource() == importButton) {
//                fc.setDialogTitle("Import");
//                final EditableModel current = currentMDL();
//                if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                    fc.setCurrentDirectory(current.getFile().getParentFile());
//                } else if (profile.getPath() != null) {
//                    fc.setCurrentDirectory(new File(profile.getPath()));
//                }
//                final int returnValue = fc.showOpenDialog(this);
//
//                if (returnValue == JFileChooser.APPROVE_OPTION) {
//                    currentFile = fc.getSelectedFile();
//                    profile.setPath(currentFile.getParent());
//                    toolsMenu.getAccessibleContext().setAccessibleDescription(
//                            "Allows the user to control which parts of the model are displayed for editing.");
//                    toolsMenu.setEnabled(true);
//                    importFile(currentFile);
//                }
//
//                fc.setSelectedFile(null);
//
//                // //Special thanks to the JWSFileChooserDemo from oracle's Java
//                // tutorials, from which many ideas were borrowed for the
//                // following
//                // FileOpenService fos = null;
//                // FileContents fileContents = null;
//                //
//                // try
//                // {
//                // fos =
//                // (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
//                // }
//                // catch (UnavailableServiceException exc )
//                // {
//                //
//                // }
//                //
//                // if( fos != null )
//                // {
//                // try
//                // {
//                // fileContents = fos.openFileDialog(null, null);
//                // }
//                // catch (Exception exc )
//                // {
//                // JOptionPane.showMessageDialog(this,"Opening command failed:
//                // "+exc.getLocalizedMessage());
//                // }
//                // }
//                //
//                // if( fileContents != null)
//                // {
//                // try
//                // {
//                // fileContents.getName();
//                // }
//                // catch (IOException exc)
//                // {
//                // JOptionPane.showMessageDialog(this,"Problem opening file:
//                // "+exc.getLocalizedMessage());
//                // }
//                // }
//                refreshController();
//            } else if (e.getSource() == importUnit) {
//                final GameObject fetchUnitResult = fetchUnit();
//                if (fetchUnitResult == null) {
//                    return;
//                }
//                final String filepath = convertPathToMDX(fetchUnitResult.getField("file"));
//                final EditableModel current = currentMDL();
//                if (filepath != null) {
//                    final File animationSource = MpqCodebase.get().getFile(filepath);
//                    importFile(animationSource);
//                }
//                refreshController();
//            } else if (e.getSource() == importGameModel) {
//                final ModelOptionPane.ModelElement fetchModelResult = fetchModel();
//                if (fetchModelResult == null) {
//                    return;
//                }
//                final String filepath = convertPathToMDX(fetchModelResult.getFilepath());
//                final EditableModel current = currentMDL();
//                if (filepath != null) {
//                    final File animationSource = MpqCodebase.get().getFile(filepath);
//                    importFile(animationSource);
//                }
//                refreshController();
//            } else if (e.getSource() == importGameObject) {
//                final MutableObjectData.MutableGameObject fetchObjectResult = fetchObject();
//                if (fetchObjectResult == null) {
//                    return;
//                }
//                final String filepath = convertPathToMDX(fetchObjectResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
//                final EditableModel current = currentMDL();
//                if (filepath != null) {
//                    final File animationSource = MpqCodebase.get().getFile(filepath);
//                    importFile(animationSource);
//                }
//                refreshController();
//            } else if (e.getSource() == importFromWorkspace) {
//                final List<EditableModel> optionNames = new ArrayList<>();
//                for (final ModelPanel modelPanel : modelPanels) {
//                    final EditableModel model = modelPanel.getModel();
//                    optionNames.add(model);
//                }
//                final EditableModel choice = (EditableModel) JOptionPane.showInputDialog(this,
//                        "Choose a workspace item to import data from:", "Import from Workspace",
//                        JOptionPane.OK_CANCEL_OPTION, null, optionNames.toArray(), optionNames.get(0));
//                if (choice != null) {
//                    importFile(EditableModel.deepClone(choice, choice.getHeaderName()));
//                }
//                refreshController();
//            } else if (e.getSource() == importButtonS) {
//                final JFrame frame = new JFrame("Animation Transferer");
//                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                frame.setContentPane(new AnimationTransfer(frame));
//                frame.setIconImage(com.matrixeater.src.MainPanel.AnimIcon.getImage());
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//            } else if (e.getSource() == mergeGeoset) {
//                fc.setDialogTitle("Merge Single Geoset (Oinker-based)");
//                final EditableModel current = currentMDL();
//                if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                    fc.setCurrentDirectory(current.getFile().getParentFile());
//                } else if (profile.getPath() != null) {
//                    fc.setCurrentDirectory(new File(profile.getPath()));
//                }
//                final int returnValue = fc.showOpenDialog(this);
//
//                if (returnValue == JFileChooser.APPROVE_OPTION) {
//                    currentFile = fc.getSelectedFile();
//                    final EditableModel geoSource = EditableModel.read(currentFile);
//                    profile.setPath(currentFile.getParent());
//                    boolean going = true;
//                    Geoset host = null;
//                    while (going) {
//                        final String s = JOptionPane.showInputDialog(this,
//                                "Geoset into which to Import: (1 to " + current.getGeosetsSize() + ")");
//                        try {
//                            final int x = Integer.parseInt(s);
//                            if ((x >= 1) && (x <= current.getGeosetsSize())) {
//                                host = current.getGeoset(x - 1);
//                                going = false;
//                            }
//                        } catch (final NumberFormatException ignored) {
//
//                        }
//                    }
//                    Geoset newGeoset = null;
//                    going = true;
//                    while (going) {
//                        final String s = JOptionPane.showInputDialog(this,
//                                "Geoset to Import: (1 to " + geoSource.getGeosetsSize() + ")");
//                        try {
//                            final int x = Integer.parseInt(s);
//                            if (x <= geoSource.getGeosetsSize()) {
//                                newGeoset = geoSource.getGeoset(x - 1);
//                                going = false;
//                            }
//                        } catch (final NumberFormatException ignored) {
//
//                        }
//                    }
//                    newGeoset.updateToObjects(current);
//                    System.out.println("putting " + newGeoset.numUVLayers() + " into a nice " + host.numUVLayers());
//                    for (int i = 0; i < newGeoset.numVerteces(); i++) {
//                        final GeosetVertex ver = newGeoset.getVertex(i);
//                        host.add(ver);
//                        ver.setGeoset(host);// geoset = host;
//                        // for( int z = 0; z < host.n.numUVLayers(); z++ )
//                        // {
//                        // host.getUVLayer(z).addTVertex(newGeoset.getVertex(i).getTVertex(z));
//                        // }
//                    }
//                    for (int i = 0; i < newGeoset.numTriangles(); i++) {
//                        final Triangle tri = newGeoset.getTriangle(i);
//                        host.add(tri);
//                        tri.setGeoRef(host);
//                    }
//                }
//
//                fc.setSelectedFile(null);
//            } else if (e.getSource() == clearRecent) {
//                final int dialogResult = JOptionPane.showConfirmDialog(this,
//                        "Are you sure you want to clear the Recent history?", "Confirm Clear",
//                        JOptionPane.YES_NO_OPTION);
//                if (dialogResult == JOptionPane.YES_OPTION) {
//                    SaveProfile.get().clearRecent();
//                    updateRecent();
//                }
//            } else if (e.getSource() == nullmodelButton) {
//                nullmodelFile();
//                refreshController();
//            } else if ((e.getSource() == save) && (currentMDL() != null) && (currentMDL().getFile() != null)) {
//                onClickSave();
//            } else if (e.getSource() == saveAs) {
//                if (!onClickSaveAs()) {
//                }
//                // } else if (e.getSource() == contextClose) {
//                // if (((ModelPanel) tabbedPane.getComponentAt(contextClickedTab)).close()) {//
//                // this);
//                // tabbedPane.remove(contextClickedTab);
//                // }
//            } else if (e.getSource() == contextCloseAll) {
//                this.closeAll();
//            } else if (e.getSource() == contextCloseOthers) {
//                this.closeOthers(currentModelPanel);
//            } else if (e.getSource() == showVertexModifyControls) {
//                final boolean selected = showVertexModifyControls.isSelected();
//                prefs.setShowVertexModifierControls(selected);
//                // SaveProfile.get().setShowViewportButtons(selected);
//                for (final ModelPanel panel : modelPanels) {
//                    panel.getFrontArea().setControlsVisible(selected);
//                    panel.getBotArea().setControlsVisible(selected);
//                    panel.getSideArea().setControlsVisible(selected);
//                    final UVPanel uvPanel = panel.getEditUVPanel();
//                    if (uvPanel != null) {
//                        uvPanel.setControlsVisible(selected);
//                    }
//                }
//            } else if (e.getSource() == textureModels) {
//                prefs.setTextureModels(textureModels.isSelected());
//            } else if (e.getSource() == showNormals) {
//                prefs.setShowNormals(showNormals.isSelected());
//            } else if (e.getSource() == editUVs) {
//                final ModelPanel disp = currentModelPanel();
//                if (disp.getEditUVPanel() == null) {
//                    final UVPanel panel = new UVPanel(disp, prefs, modelStructureChangeListener);
//                    disp.setEditUVPanel(panel);
//
//                    panel.initViewport();
//                    final FloatingWindow floatingWindow = rootWindow.createFloatingWindow(
//                            new Point(getX() + (getWidth() / 2), getY() + (getHeight() / 2)), panel.getSize(),
//                            panel.getView());
//                    panel.init();
//                    floatingWindow.getTopLevelAncestor().setVisible(true);
//                    panel.packFrame();
//                } else if (!disp.getEditUVPanel().frameVisible()) {
//                    final FloatingWindow floatingWindow = rootWindow.createFloatingWindow(
//                            new Point(getX() + (getWidth() / 2), getY() + (getHeight() / 2)),
//                            disp.getEditUVPanel().getSize(), disp.getEditUVPanel().getView());
//                    floatingWindow.getTopLevelAncestor().setVisible(true);
//                }
//            } else if (e.getSource() == exportTextures) {
//                final DefaultListModel<Material> materials = new DefaultListModel<>();
//                for (int i = 0; i < currentMDL().getMaterials().size(); i++) {
//                    final Material mat = currentMDL().getMaterials().get(i);
//                    materials.addElement(mat);
//                }
//                for (final ParticleEmitter2 emitter2 : currentMDL().sortedIdObjects(ParticleEmitter2.class)) {
//                    final Material dummyMaterial = new Material(
//                            new Layer("Blend", currentMDL().getTexture(emitter2.getTextureID())));
//                }
//
//                final JList<Material> materialsList = new JList<>(materials);
//                materialsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//                materialsList.setCellRenderer(new MaterialListRenderer(currentMDL()));
//                JOptionPane.showMessageDialog(this, new JScrollPane(materialsList));
//
//                if (exportTextureDialog.getCurrentDirectory() == null) {
//                    final EditableModel current = currentMDL();
//                    if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                        fc.setCurrentDirectory(current.getFile().getParentFile());
//                    } else if (profile.getPath() != null) {
//                        fc.setCurrentDirectory(new File(profile.getPath()));
//                    }
//                }
//                if (exportTextureDialog.getCurrentDirectory() == null) {
//                    exportTextureDialog.setSelectedFile(new File(exportTextureDialog.getCurrentDirectory()
//                            + File.separator + materialsList.getSelectedValue().getName()));
//                }
//
//                final int x = exportTextureDialog.showSaveDialog(this);
//                if (x == JFileChooser.APPROVE_OPTION) {
//                    final File file = exportTextureDialog.getSelectedFile();
//                    if (file != null) {
//                        try {
//                            if (file.getName().lastIndexOf('.') >= 0) {
//                                BufferedImage bufferedImage = materialsList.getSelectedValue()
//                                        .getBufferedImage(currentMDL().getWrappedDataSource());
//                                String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1)
//                                        .toUpperCase();
//                                if (fileExtension.equals("BMP") || fileExtension.equals("JPG")
//                                        || fileExtension.equals("JPEG")) {
//                                    JOptionPane.showMessageDialog(this,
//                                            "Warning: Alpha channel was converted to black. Some data will be lost\nif you convert this texture back to Warcraft BLP.");
//                                    bufferedImage = BLPHandler.removeAlphaChannel(bufferedImage);
//                                }
//                                if (fileExtension.equals("BLP")) {
//                                    fileExtension = "blp";
//                                }
//                                final boolean write = ImageIO.write(bufferedImage, fileExtension, file);
//                                if (!write) {
//                                    JOptionPane.showMessageDialog(this, "File type unknown or unavailable");
//                                }
//                            } else {
//                                JOptionPane.showMessageDialog(this, "No file type was specified");
//                            }
//                        } catch (final Exception e1) {
//                            ExceptionPopup.display(e1);
//                            e1.printStackTrace();
//                        }
//                    } else {
//                        JOptionPane.showMessageDialog(this, "No output file was specified");
//                    }
//                }
//            } else if (e.getSource() == scaleAnimations) {
//                // if( disp.animpanel == null )
//                // {
//                // AnimationPanel panel = new UVPanel(disp);
//                // disp.setUVPanel(panel);
//                // panel.showFrame();
//                // }
//                // else if(!disp.uvpanel.frameVisible() )
//                // {
//                // disp.uvpanel.showFrame();
//                // }
//                final AnimationFrame aFrame = new AnimationFrame(currentModelPanel(), timeSliderPanel::revalidateKeyframeDisplay);
//                aFrame.setVisible(true);
//            } else if (e.getSource() == linearizeAnimations) {
//                final int x = JOptionPane.showConfirmDialog(this,
//                        "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify animations?",
//                        "Warning: Linearize Animations", JOptionPane.OK_CANCEL_OPTION);
//                if (x == JOptionPane.OK_OPTION) {
//                    final List<AnimFlag> allAnimFlags = currentMDL().getAllAnimFlags();
//                    for (final AnimFlag flag : allAnimFlags) {
//                        flag.linearize();
//                    }
//                }
//            } else if (e.getSource() == duplicateSelection) {
//                // final int x = JOptionPane.showConfirmDialog(this,
//                // "This is an irreversible process that will split selected
//                // vertices into many copies of themself, one for each face, so
//                // you can wrap textures and normals in a different
//                // way.\n\nContinue?",
//                // "Warning"/* : Divide Vertices" */,
//                // JOptionPane.OK_CANCEL_OPTION);
//                // if (x == JOptionPane.OK_OPTION) {
//                final ModelPanel currentModelPanel = currentModelPanel();
//                if (currentModelPanel != null) {
//                    currentModelPanel.getUndoManager().pushAction(currentModelPanel.getModelEditorManager()
//                            .getModelEditor().cloneSelectedComponents(namePicker));
//                }
//                // }
//            } else if (e.getSource() == simplifyKeyframes) {
//                final int x = JOptionPane.showConfirmDialog(this,
//                        "This is an irreversible process that will lose some of your model data,\nin exchange for making it a smaller storage size.\n\nContinue and simplify keyframes?",
//                        "Warning: Simplify Keyframes", JOptionPane.OK_CANCEL_OPTION);
//                if (x == JOptionPane.OK_OPTION) {
//                    simplifyKeyframes();
//                }
//            } else if (e.getSource() == riseFallBirth) {
//                final ModelView disp = currentModelPanel().getModelViewManager();
//                final EditableModel model = disp.getModel();
//                final Animation lastAnim = model.getAnim(model.getAnimsSize() - 1);
//
//                final Animation oldBirth = model.findAnimByName("birth");
//                final Animation oldDeath = model.findAnimByName("death");
//
//                Animation birth = new Animation("Birth", lastAnim.getEnd() + 300, lastAnim.getEnd() + 2300);
//                Animation death = new Animation("Death", birth.getEnd() + 300, birth.getEnd() + 2300);
//                final Animation stand = model.findAnimByName("stand");
//
//                final int confirmed = JOptionPane.showConfirmDialog(this,
//                        "This will permanently alter model. Are you sure?", "Confirmation",
//                        JOptionPane.OK_CANCEL_OPTION);
//                if (confirmed != JOptionPane.OK_OPTION) {
//                    return;
//                }
//
//                boolean wipeoutOldBirth = false;
//                if (oldBirth != null) {
//                    final String[] choices = { "Ignore", "Delete", "Overwrite" };
//                    final Object x = JOptionPane.showInputDialog(this,
//                            "Existing birth detected. What should be done with it?", "Question",
//                            JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
//                    if (x == choices[1]) {
//                        wipeoutOldBirth = true;
//                    } else if (x == choices[2]) {
//                        birth = oldBirth;
//                    } else {
//                        return;
//                    }
//                }
//                boolean wipeoutOldDeath = false;
//                if (oldDeath != null) {
//                    final String[] choices = { "Ignore", "Delete", "Overwrite" };
//                    final Object x = JOptionPane.showInputDialog(this,
//                            "Existing death detected. What should be done with it?", "Question",
//                            JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
//                    if (x == choices[1]) {
//                        wipeoutOldDeath = true;
//                    } else if (x == choices[2]) {
//                        death = oldDeath;
//                    } else {
//                        return;
//                    }
//                }
//                if (wipeoutOldBirth) {
//                    model.remove(oldBirth);
//                }
//                if (wipeoutOldDeath) {
//                    model.remove(oldDeath);
//                }
//
//                final List<IdObject> roots = new ArrayList<>();
//                for (final IdObject obj : model.getIdObjects()) {
//                    if (obj.getParent() == null) {
//                        roots.add(obj);
//                    }
//                }
//                for (final AnimFlag af : model.getAllAnimFlags()) {
//                    af.deleteAnim(birth);
//                    af.deleteAnim(death);
//                }
//                for (final IdObject obj : roots) {
//                    if (obj instanceof Bone) {
//                        final Bone b = (Bone) obj;
//                        AnimFlag trans = null;
//                        boolean globalSeq = false;
//                        for (final AnimFlag af : b.getAnimFlags()) {
//                            if (af.getTypeId() == AnimFlag.TRANSLATION) {
//                                if (af.hasGlobalSeq()) {
//                                    globalSeq = true;
//                                } else {
//                                    trans = af;
//                                }
//                            }
//                        }
//                        if (globalSeq) {
//                            continue;
//                        }
//                        if (trans == null) {
//                            final ArrayList<Integer> times = new ArrayList<>();
//                            final ArrayList<Integer> values = new ArrayList<>();
//                            trans = new AnimFlag("Translation", times, values);
//                            trans.addTag("Linear");
//                            b.getAnimFlags().add(trans);
//                        }
//                        trans.addEntry(birth.getStart(), new Vertex(0, 0, -300));
//                        trans.addEntry(birth.getEnd(), new Vertex(0, 0, 0));
//                        trans.addEntry(death.getStart(), new Vertex(0, 0, 0));
//                        trans.addEntry(death.getEnd(), new Vertex(0, 0, -300));
//                    }
//                }
//
//                // visibility
//                for (final VisibilitySource source : model.getAllVisibilitySources()) {
//                    final AnimFlag dummy = new AnimFlag("dummy");
//                    final AnimFlag af = source.getVisibilityFlag();
//                    dummy.copyFrom(af);
//                    af.deleteAnim(birth);
//                    af.deleteAnim(death);
//                    af.copyFrom(dummy, stand.getStart(), stand.getEnd(), birth.getStart(), birth.getEnd());
//                    af.copyFrom(dummy, stand.getStart(), stand.getEnd(), death.getStart(), death.getEnd());
//                    af.setEntry(death.getEnd(), 0);
//                }
//
//                if (!birth.getTags().contains("NonLooping")) {
//                    birth.addTag("NonLooping");
//                }
//                if (!death.getTags().contains("NonLooping")) {
//                    death.addTag("NonLooping");
//                }
//
//                if (!model.contains(birth)) {
//                    model.add(birth);
//                }
//                if (!model.contains(death)) {
//                    model.add(death);
//                }
//
//                JOptionPane.showMessageDialog(this, "Done!");
//            } else if (e.getSource() == animFromFile) {
//                fc.setDialogTitle("Animation Source");
//                final EditableModel current = currentMDL();
//                if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                    fc.setCurrentDirectory(current.getFile().getParentFile());
//                } else if (profile.getPath() != null) {
//                    fc.setCurrentDirectory(new File(profile.getPath()));
//                }
//                final int returnValue = fc.showOpenDialog(this);
//
//                if (returnValue == JFileChooser.APPROVE_OPTION) {
//                    currentFile = fc.getSelectedFile();
//                    profile.setPath(currentFile.getParent());
//                    final EditableModel animationSourceModel = EditableModel.read(currentFile);
//                    addSingleAnimation(current, animationSourceModel);
//                }
//
//                fc.setSelectedFile(null);
//
//                refreshController();
//            } else if (e.getSource() == animFromUnit) {
//                fc.setDialogTitle("Animation Source");
//                final GameObject fetchResult = fetchUnit();
//                if (fetchResult == null) {
//                    return;
//                }
//                final String filepath = convertPathToMDX(fetchResult.getField("file"));
//                final EditableModel current = currentMDL();
//                if (filepath != null) {
//                    final EditableModel animationSource = EditableModel.read(MpqCodebase.get().getFile(filepath));
//                    addSingleAnimation(current, animationSource);
//                }
//            } else if (e.getSource() == animFromModel) {
//                fc.setDialogTitle("Animation Source");
//                final ModelOptionPane.ModelElement fetchResult = fetchModel();
//                if (fetchResult == null) {
//                    return;
//                }
//                final String filepath = convertPathToMDX(fetchResult.getFilepath());
//                final EditableModel current = currentMDL();
//                if (filepath != null) {
//                    final EditableModel animationSource = EditableModel.read(MpqCodebase.get().getFile(filepath));
//                    addSingleAnimation(current, animationSource);
//                }
//            } else if (e.getSource() == animFromObject) {
//                fc.setDialogTitle("Animation Source");
//                final MutableObjectData.MutableGameObject fetchResult = fetchObject();
//                if (fetchResult == null) {
//                    return;
//                }
//                final String filepath = convertPathToMDX(fetchResult.getFieldAsString(UnitFields.MODEL_FILE, 0));
//                final EditableModel current = currentMDL();
//                if (filepath != null) {
//                    final EditableModel animationSource = EditableModel.read(MpqCodebase.get().getFile(filepath));
//                    addSingleAnimation(current, animationSource);
//                }
//            } else if (e.getSource() == creditsButton) {
//                final DefaultStyledDocument panel = new DefaultStyledDocument();
//                final JTextPane epane = new JTextPane();
//                epane.setForeground(Color.BLACK);
//                epane.setBackground(Color.WHITE);
//                final RTFEditorKit rtfk = new RTFEditorKit();
//                try {
//                    rtfk.read(MainPanel.class.getResourceAsStream("credits.rtf"), panel, 0);
//                } catch (final BadLocationException | IOException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//                epane.setDocument(panel);
//                final JFrame frame = new JFrame("About");
//                frame.setContentPane(new JScrollPane(epane));
//                frame.setSize(650, 500);
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//                // JOptionPane.showMessageDialog(this,new JScrollPane(epane));
//            } else if (e.getSource() == changelogButton) {
//                final DefaultStyledDocument panel = new DefaultStyledDocument();
//                final JTextPane epane = new JTextPane();
//                epane.setForeground(Color.BLACK);
//                epane.setBackground(Color.WHITE);
//                final RTFEditorKit rtfk = new RTFEditorKit();
//                try {
//                    rtfk.read(MainPanel.class.getResourceAsStream("changelist.rtf"), panel, 0);
//                } catch (final BadLocationException | IOException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//                epane.setDocument(panel);
//                final JFrame frame = new JFrame("Changelog");
//                frame.setContentPane(new JScrollPane(epane));
//                frame.setSize(650, 500);
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//                // JOptionPane.showMessageDialog(this,new JScrollPane(epane));
//            }
//            // for( int i = 0; i < geoItems.size(); i++ )
//            // {
//            // JCheckBoxMenuItem geoItem = (JCheckBoxMenuItem)geoItems.get(i);
//            // if( e.getSource() == geoItem )
//            // {
//            // frontArea.setGeosetVisible(i,geoItem.isSelected());
//            // frontArea.setGeosetHighlight(i,false);
//            // }
//            // repaint();
//            // }
//        } catch (
//
//                final Exception exc) {
//            ExceptionPopup.display(exc);
//        }
//    }
//
//    private void dataSourcesChanged() {
//        for (final ModelPanel modelPanel : mainPanel.modelPanels) {
//            final PerspDisplayPanel pdp = modelPanel.getPerspArea();
//            pdp.reloadAllTextures();
//            modelPanel.getAnimationViewer().reloadAllTextures();
//        }
//        mainPanel.directoryChangeNotifier.dataSourcesChanged();
//    }
//
//    private void simplifyKeyframes() {
//        final EditableModel currentMDL = currentMDL();
//        currentMDL.simplifyKeyframes();
//    }
//
//    private boolean onClickSaveAs() {
//        final EditableModel current = currentMDL();
//        return onClickSaveAs(current);
//    }
//
//    private boolean onClickSaveAs(final EditableModel current) {
//        try {
//            mainPanel.fc.setDialogTitle("Save as");
//            if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                mainPanel.fc.setCurrentDirectory(current.getFile().getParentFile());
//                mainPanel.fc.setSelectedFile(current.getFile());
//            } else if (mainPanel.profile.getPath() != null) {
//                mainPanel.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
//            }
//            final int returnValue = mainPanel.fc.showSaveDialog(mainPanel);
//            File temp = mainPanel.fc.getSelectedFile();
//            if (returnValue == JFileChooser.APPROVE_OPTION) {
//                if (temp != null) {
//                    final FileFilter ff = mainPanel.fc.getFileFilter();
//                    final String ext = ff.accept(new File("junk.mdl")) ? ".mdl" : ".mdx";
//                    if (ff.accept(new File("junk.obj"))) {
//                        throw new UnsupportedOperationException("OBJ saving has not been coded yet.");
//                    }
//                    final String name = temp.getName();
//                    if (name.lastIndexOf('.') != -1) {
//                        if (!name.substring(name.lastIndexOf('.')).equals(ext)) {
//                            temp = new File(
//                                    temp.getAbsolutePath().substring(0, temp.getAbsolutePath().lastIndexOf('.')) + ext);
//                        }
//                    } else {
//                        temp = new File(temp.getAbsolutePath() + ext);
//                    }
//                    mainPanel.currentFile = temp;
//                    if (temp.exists()) {
//                        final Object[] options = { "Overwrite", "Cancel" };
//                        final int n = JOptionPane.showOptionDialog(MainFrame.frame, "Selected file already exists.",
//                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
//                                options[1]);
//                        if (n == 1) {
//                            mainPanel.fc.setSelectedFile(null);
//                            return false;
//                        }
//                    }
//                    mainPanel.profile.setPath(mainPanel.currentFile.getParent());
//                    if (ext.equals(".mdl")) {
//                        currentMDL().printTo(mainPanel.currentFile);
//                    } else {
//                        final MdxModel model = new MdxModel(currentMDL());
//                        try (BlizzardDataOutputStream writer = new BlizzardDataOutputStream(mainPanel.currentFile)) {
//                            model.save(writer);
//                        } catch (final IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                    currentMDL().setFileRef(mainPanel.currentFile);
//                    // currentMDLDisp().resetBeenSaved();
//                    // TODO reset been saved
//                    currentModelPanel().getMenuItem().setName(mainPanel.currentFile.getName().split("\\.")[0]);
//                    currentModelPanel().getMenuItem().setToolTipText(mainPanel.currentFile.getPath());
//                } else {
//                    JOptionPane.showMessageDialog(mainPanel,
//                            "You tried to save, but you somehow didn't select a file.\nThat is bad.");
//                }
//            }
//            mainPanel.fc.setSelectedFile(null);
//            return true;
//        } catch (final Exception exc) {
//            ExceptionPopup.display(exc);
//        }
//        refreshController();
//        return false;
//    }
//
//    private void onClickSave() {
//        try {
//            if (currentMDL() != null) {
//                currentMDL().saveFile();
//                mainPanel.profile.setPath(currentMDL().getFile().getParent());
//                // currentMDLDisp().resetBeenSaved();
//                // TODO reset been saved
//            }
//        } catch (final Exception exc) {
//            ExceptionPopup.display(exc);
//        }
//        refreshController();
//    }
//
//    private void onClickOpen() {
//        mainPanel.fc.setDialogTitle("Open");
//        final EditableModel current = currentMDL();
//        if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//            mainPanel.fc.setCurrentDirectory(current.getFile().getParentFile());
//        } else if (mainPanel.profile.getPath() != null) {
//            mainPanel.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
//        }
//
//        final int returnValue = mainPanel.fc.showOpenDialog(mainPanel);
//
//        if (returnValue == JFileChooser.APPROVE_OPTION) {
//            openFile(mainPanel.fc.getSelectedFile());
//        }
//
//        mainPanel.fc.setSelectedFile(null);
//
//        // //Special thanks to the JWSFileChooserDemo from oracle's Java
//        // tutorials, from which many ideas were borrowed for the following
//        // FileOpenService fos = null;
//        // FileContents fileContents = null;
//        //
//        // try
//        // {
//        // fos =
//        // (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
//        // }
//        // catch (UnavailableServiceException exc )
//        // {
//        //
//        // }
//        //
//        // if( fos != null )
//        // {
//        // try
//        // {
//        // fileContents = fos.openFileDialog(null, null);
//        // }
//        // catch (Exception exc )
//        // {
//        // JOptionPane.showMessageDialog(this,"Opening command failed:
//        // "+exc.getLocalizedMessage());
//        // }
//        // }
//        //
//        // if( fileContents != null)
//        // {
//        // try
//        // {
//        // fileContents.getName();
//        // }
//        // catch (IOException exc)
//        // {
//        // JOptionPane.showMessageDialog(this,"Problem opening file:
//        // "+exc.getLocalizedMessage());
//        // }
//        // }
//    }
//
//    private void newModel() {
//        final JPanel newModelPanel = new JPanel();
//        newModelPanel.setLayout(new MigLayout());
//        newModelPanel.add(new JLabel("Model Name: "), "cell 0 0");
//        final JTextField newModelNameField = new JTextField("MrNew", 25);
//        newModelPanel.add(newModelNameField, "cell 1 0");
//        final JRadioButton createEmptyButton = new JRadioButton("Create Empty", true);
//        newModelPanel.add(createEmptyButton, "cell 0 1");
//        final JRadioButton createPlaneButton = new JRadioButton("Create Plane");
//        newModelPanel.add(createPlaneButton, "cell 0 2");
//        final JRadioButton createBoxButton = new JRadioButton("Create Box");
//        newModelPanel.add(createBoxButton, "cell 0 3");
//        final ButtonGroup buttonGroup = new ButtonGroup();
//        buttonGroup.add(createBoxButton);
//        buttonGroup.add(createPlaneButton);
//        buttonGroup.add(createEmptyButton);
//
//        final int userDialogResult = JOptionPane.showConfirmDialog(this, newModelPanel, "New Model",
//                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//        if (userDialogResult == JOptionPane.OK_OPTION) {
//            final EditableModel mdl = new EditableModel(newModelNameField.getText());
//            if (createBoxButton.isSelected()) {
//                final SpinnerNumberModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
//                final JSpinner spinner = new JSpinner(sModel);
//                final int userChoice = JOptionPane.showConfirmDialog(this, spinner, "Box: Choose Segments",
//                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//                if (userChoice != JOptionPane.OK_OPTION) {
//                    return;
//                }
//                ModelUtils.createBox(mdl, new Vertex(64, 64, 128), new Vertex(-64, -64, 0),
//                        ((Number) spinner.getValue()).intValue());
//            } else if (createPlaneButton.isSelected()) {
//                final SpinnerNumberModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
//                final JSpinner spinner = new JSpinner(sModel);
//                final int userChoice = JOptionPane.showConfirmDialog(this, spinner, "Plane: Choose Segments",
//                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//                if (userChoice != JOptionPane.OK_OPTION) {
//                    return;
//                }
//                ModelUtils.createGroundPlane(mdl, new Vertex(64, 64, 0), new Vertex(-64, -64, 0),
//                        ((Number) spinner.getValue()).intValue());
//            }
//            final ModelPanel temp = new ModelPanel(this, mdl, prefs, MainPanel.this, selectionItemTypeGroup,
//                    selectionModeGroup, modelStructureChangeListener, coordDisplayListener, viewportTransferHandler,
//                    activeViewportWatcher, GlobalIcons.MDL_ICON, false, textureExporter);
//            loadModel(true, true, temp);
//        }
//
//    }
//
//    private GameObject fetchUnit() {
//        final GameObject choice = UnitOptionPane.show(this);
//        if (choice != null) {
//
//        } else {
//            return null;
//        }
//
//        String filepath = choice.getField("file");
//
//        try {
//            filepath = convertPathToMDX(filepath);
//            // modelDisp = new MDLDisplay(toLoad, null);
//        } catch (final Exception exc) {
//            exc.printStackTrace();
//            // bad model!
//            JOptionPane.showMessageDialog(MainFrame.frame, "The chosen model could not be used.", "Program Error",
//                    JOptionPane.ERROR_MESSAGE);
//            return null;
//        }
//        return choice;
//    }
//
//    private String convertPathToMDX(String filepath) {
//        if (filepath.endsWith(".mdl")) {
//            filepath = filepath.replace(".mdl", ".mdx");
//        } else if (!filepath.endsWith(".mdx")) {
//            filepath = filepath.concat(".mdx");
//        }
//        return filepath;
//    }
//
//    private ModelOptionPane.ModelElement fetchModel() {
//        final ModelOptionPane.ModelElement model = ModelOptionPane.showAndLogIcon(this);
//        if (model == null) {
//            return null;
//        }
//        String filepath = model.getFilepath();
//        if (filepath != null) {
//
//        } else {
//            return null;
//        }
//        try {
//            filepath = convertPathToMDX(filepath);
//        } catch (final Exception exc) {
//            exc.printStackTrace();
//            // bad model!
//            JOptionPane.showMessageDialog(MainFrame.frame, "The chosen model could not be used.", "Program Error",
//                    JOptionPane.ERROR_MESSAGE);
//            return null;
//        }
//        return model;
//    }
//
//    private MutableObjectData.MutableGameObject fetchObject() {
//        final BetterUnitEditorModelSelector selector = new BetterUnitEditorModelSelector(getUnitData(),
//                getUnitEditorSettings());
//        final int x = JOptionPane.showConfirmDialog(this, selector, "Object Editor - Select Unit",
//                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//        final MutableObjectData.MutableGameObject choice = selector.getSelection();
//        if ((choice == null) || (x != JOptionPane.OK_OPTION)) {
//            return null;
//        }
//
//        String filepath = choice.getFieldAsString(UnitFields.MODEL_FILE, 0);
//
//        try {
//            filepath = convertPathToMDX(filepath);
//        } catch (final Exception exc) {
//            exc.printStackTrace();
//            // bad model!
//            JOptionPane.showMessageDialog(MainFrame.frame, "The chosen model could not be used.", "Program Error",
//                    JOptionPane.ERROR_MESSAGE);
//            return null;
//        }
//        return choice;
//    }
//
//    private void addSingleAnimation(final EditableModel current, final EditableModel animationSourceModel) {
//        Animation choice = null;
//        choice = (Animation) JOptionPane.showInputDialog(this, "Choose an animation!", "Add Animation",
//                JOptionPane.QUESTION_MESSAGE, null, animationSourceModel.getAnims().toArray(),
//                animationSourceModel.getAnims().get(0));
//        if (choice == null) {
//            JOptionPane.showMessageDialog(this, "Bad choice. No animation added.");
//            return;
//        }
//        final Animation visibilitySource = (Animation) JOptionPane.showInputDialog(this,
//                "Which animation from THIS model to copy visiblity from?", "Add Animation",
//                JOptionPane.QUESTION_MESSAGE, null, current.getAnims().toArray(), current.getAnims().get(0));
//        if (visibilitySource == null) {
//            JOptionPane.showMessageDialog(this, "No visibility will be copied.");
//        }
//        final List<Animation> animationsAdded = current.addAnimationsFrom(animationSourceModel,
//                Collections.singletonList(choice));
//        for (final Animation anim : animationsAdded) {
//            current.copyVisibility(visibilitySource, anim);
//        }
//        JOptionPane.showMessageDialog(this, "Added " + animationSourceModel.getName() + "'s " + choice.getName()
//                + " with " + visibilitySource.getName() + "'s visibility  OK!");
//        modelStructureChangeListener.animationsAdded(animationsAdded);
//    }
//
//    private interface OpenViewGetter {
//        View getView();
//    }
//
//    private final class OpenViewAction extends AbstractAction {
//        private final MainPanel.OpenViewGetter openViewGetter;
//
//        private OpenViewAction(final String name, final MainPanel.OpenViewGetter openViewGetter) {
//            super(name);
//            this.openViewGetter = openViewGetter;
//        }
//
//        @Override
//        public void actionPerformed(final ActionEvent e) {
//            final View view = openViewGetter.getView();
//            if ((view.getTopLevelAncestor() == null) || !view.getTopLevelAncestor().isVisible()) {
//                final FloatingWindow createFloatingWindow = rootWindow.createFloatingWindow(rootWindow.getLocation(),
//                        new Dimension(640, 480), view);
//                createFloatingWindow.getTopLevelAncestor().setVisible(true);
//            }
//        }
//    }
//
//    private interface ModelReference {
//        EditableModel getModel();
//    }
//
//    private final class ModelStructureChangeListenerImplementation implements ModelStructureChangeListener {
//        private final MainPanel.ModelReference modelReference;
//
//        public ModelStructureChangeListenerImplementation(final MainPanel.ModelReference modelReference) {
//            this.modelReference = modelReference;
//        }
//
//        public ModelStructureChangeListenerImplementation(final EditableModel model) {
//            this.modelReference = () -> model;
//        }
//
//        @Override
//        public void nodesRemoved(final List<IdObject> nodes) {
//            // Tell program to set visibility after import
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                // display.setBeenSaved(false); // we edited the model
//                // TODO notify been saved system, wherever that moves to
//                for (final IdObject geoset : nodes) {
//                    display.getModelViewManager().makeIdObjectNotVisible(geoset);
//                }
//                reloadGeosetManagers(display);
//            }
//        }
//
//        @Override
//        public void nodesAdded(final List<IdObject> nodes) {
//            // Tell program to set visibility after import
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                // display.setBeenSaved(false); // we edited the model
//                // TODO notify been saved system, wherever that moves to
//                for (final IdObject geoset : nodes) {
//                    display.getModelViewManager().makeIdObjectVisible(geoset);
//                }
//                reloadGeosetManagers(display);
//                display.getEditorRenderModel().refreshFromEditor(animatedRenderEnvironment, IDENTITY, IDENTITY,
//                        IDENTITY, display.getPerspArea().getViewport());
//                display.getAnimationViewer().reload();
//            }
//        }
//
//        @Override
//        public void geosetsRemoved(final List<Geoset> geosets) {
//            // Tell program to set visibility after import
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                // display.setBeenSaved(false); // we edited the model
//                // TODO notify been saved system, wherever that moves to
//                for (final Geoset geoset : geosets) {
//                    display.getModelViewManager().makeGeosetNotEditable(geoset);
//                    display.getModelViewManager().makeGeosetNotVisible(geoset);
//                }
//                reloadGeosetManagers(display);
//            }
//        }
//
//        @Override
//        public void geosetsAdded(final List<Geoset> geosets) {
//            // Tell program to set visibility after import
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                // display.setBeenSaved(false); // we edited the model
//                // TODO notify been saved system, wherever that moves to
//                for (final Geoset geoset : geosets) {
//                    display.getModelViewManager().makeGeosetEditable(geoset);
//                    // display.getModelViewManager().makeGeosetVisible(geoset);
//                }
//                reloadGeosetManagers(display);
//            }
//        }
//
//        @Override
//        public void camerasAdded(final List<Camera> cameras) {
//            // Tell program to set visibility after import
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                // display.setBeenSaved(false); // we edited the model
//                // TODO notify been saved system, wherever that moves to
//                for (final Camera camera : cameras) {
//                    display.getModelViewManager().makeCameraVisible(camera);
//                    // display.getModelViewManager().makeGeosetVisible(geoset);
//                }
//                reloadGeosetManagers(display);
//            }
//        }
//
//        @Override
//        public void camerasRemoved(final List<Camera> cameras) {
//            // Tell program to set visibility after import
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                // display.setBeenSaved(false); // we edited the model
//                // TODO notify been saved system, wherever that moves to
//                for (final Camera camera : cameras) {
//                    display.getModelViewManager().makeCameraNotVisible(camera);
//                    // display.getModelViewManager().makeGeosetVisible(geoset);
//                }
//                reloadGeosetManagers(display);
//            }
//        }
//
//        @Override
//        public void timelineAdded(final TimelineContainer node, final AnimFlag timeline) {
//
//        }
//
//        @Override
//        public void keyframeAdded(final TimelineContainer node, final AnimFlag timeline, final int trackTime) {
//            timeSliderPanel.revalidateKeyframeDisplay();
//        }
//
//        @Override
//        public void timelineRemoved(final TimelineContainer node, final AnimFlag timeline) {
//
//        }
//
//        @Override
//        public void keyframeRemoved(final TimelineContainer node, final AnimFlag timeline, final int trackTime) {
//            timeSliderPanel.revalidateKeyframeDisplay();
//        }
//
//        @Override
//        public void animationsAdded(final List<Animation> animation) {
//            currentModelPanel().getAnimationViewer().reload();
//            currentModelPanel().getAnimationController().reload();
//            creatorPanel.reloadAnimationList();
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                reloadComponentBrowser(display);
//            }
//        }
//
//        @Override
//        public void animationsRemoved(final List<Animation> animation) {
//            currentModelPanel().getAnimationViewer().reload();
//            currentModelPanel().getAnimationController().reload();
//            creatorPanel.reloadAnimationList();
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                reloadComponentBrowser(display);
//            }
//        }
//
//        @Override
//        public void texturesChanged() {
//            final ModelPanel modelPanel = currentModelPanel();
//            if (modelPanel != null) {
//                modelPanel.getAnimationViewer().reloadAllTextures();
//                modelPanel.getPerspArea().reloadAllTextures();
//            }
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                reloadComponentBrowser(display);
//            }
//        }
//
//        @Override
//        public void headerChanged() {
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                reloadComponentBrowser(display);
//            }
//        }
//
//        @Override
//        public void animationParamsChanged(final Animation animation) {
//            currentModelPanel().getAnimationViewer().reload();
//            currentModelPanel().getAnimationController().reload();
//            creatorPanel.reloadAnimationList();
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                reloadComponentBrowser(display);
//            }
//        }
//
//        @Override
//        public void globalSequenceLengthChanged(final int index, final Integer newLength) {
//            currentModelPanel().getAnimationViewer().reload();
//            currentModelPanel().getAnimationController().reload();
//            creatorPanel.reloadAnimationList();
//            final ModelPanel display = displayFor(modelReference.getModel());
//            if (display != null) {
//                reloadComponentBrowser(display);
//            }
//        }
//    }
//
//    private static final class RepaintingModelStateListener implements ModelViewStateListener {
//        private final JComponent component;
//
//        public RepaintingModelStateListener(final JComponent component) {
//            this.component = component;
//        }
//
//        @Override
//        public void idObjectVisible(final IdObject bone) {
//            component.repaint();
//        }
//
//        @Override
//        public void idObjectNotVisible(final IdObject bone) {
//            component.repaint();
//        }
//
//        @Override
//        public void highlightGeoset(final Geoset geoset) {
//            component.repaint();
//        }
//
//        @Override
//        public void geosetVisible(final Geoset geoset) {
//            component.repaint();
//        }
//
//        @Override
//        public void geosetNotVisible(final Geoset geoset) {
//            component.repaint();
//        }
//
//        @Override
//        public void geosetNotEditable(final Geoset geoset) {
//            component.repaint();
//        }
//
//        @Override
//        public void geosetEditable(final Geoset geoset) {
//            component.repaint();
//        }
//
//        @Override
//        public void cameraVisible(final Camera camera) {
//            component.repaint();
//        }
//
//        @Override
//        public void cameraNotVisible(final Camera camera) {
//            component.repaint();
//        }
//
//        @Override
//        public void unhighlightGeoset(final Geoset geoset) {
//            component.repaint();
//        }
//
//        @Override
//        public void highlightNode(final IdObject node) {
//            component.repaint();
//        }
//
//        @Override
//        public void unhighlightNode(final IdObject node) {
//            component.repaint();
//        }
//    }
//
//    static class RecentItem extends JMenuItem {
//        public RecentItem(final String what) {
//            super(what);
//        }
//
//        String filepath;
//    }
//
//    public void updateRecent() {
//        final List<String> recent = SaveProfile.get().getRecent();
//        for (final MainPanel.RecentItem recentItem : recentItems) {
//            recentMenu.remove(recentItem);
//        }
//        recentItems.clear();
//        for (int i = 0; i < recent.size(); i++) {
//            final String fp = recent.get(recent.size() - i - 1);
//            if ((recentItems.size() <= i) || (recentItems.get(i).filepath != fp)) {
//                // String[] bits = recent.get(i).split("/");
//
//                final MainPanel.RecentItem item = new MainPanel.RecentItem(new File(fp).getName());
//                item.filepath = fp;
//                recentItems.add(item);
//                item.addActionListener(e -> {
//
//                    currentFile = new File(item.filepath);
//                    profile.setPath(currentFile.getParent());
//                    // frontArea.clearGeosets();
//                    // sideArea.clearGeosets();
//                    // botArea.clearGeosets();
//                    toolsMenu.getAccessibleContext().setAccessibleDescription(
//                            "Allows the user to control which parts of the model are displayed for editing.");
//                    toolsMenu.setEnabled(true);
//                    SaveProfile.get().addRecent(currentFile.getPath());
//                    updateRecent();
//                    loadFile(currentFile);
//                });
//                recentMenu.add(item, recentMenu.getItemCount() - 2);
//            }
//        }
//    }
//
//    public EditableModel currentMDL() {
//        if (currentModelPanel != null) {
//            return currentModelPanel.getModel();
//        } else {
//            return null;
//        }
//    }
//
//    public ModelEditorManager currentMDLDisp() {
//        if (currentModelPanel != null) {
//            return currentModelPanel.getModelEditorManager();
//        } else {
//            return null;
//        }
//    }
//
//    public ModelPanel currentModelPanel() {
//        return currentModelPanel;
//    }
//
//    /**
//     * Returns the MDLDisplay associated with a given MDL, or null if one cannot be
//     * found.
//     *
//     * @param model
//     */
//    public ModelPanel displayFor(final EditableModel model) {
//        ModelPanel output = null;
//        ModelView tempDisplay;
//        for (final ModelPanel modelPanel : modelPanels) {
//            tempDisplay = modelPanel.getModelViewManager();
//            if (tempDisplay.getModel() == model) {
//                output = modelPanel;
//                break;
//            }
//        }
//        return output;
//    }
//
//    public void loadFile(final File f, final boolean temporary, final boolean selectNewTab, final ImageIcon icon) {
//        if (f.getPath().toLowerCase().endsWith("blp")) {
//            loadBLPPathAsModel(f.getName(), f.getParentFile());
//            return;
//        }
//        if (f.getPath().toLowerCase().endsWith("png")) {
//            loadBLPPathAsModel(f.getName(), f.getParentFile());
//            return;
//        }
//        ModelPanel temp = null;
//        if (f.getPath().toLowerCase().endsWith("mdx")) {
//            try (BlizzardDataInputStream in = new BlizzardDataInputStream(new FileInputStream(f))) {
//                final EditableModel model = new EditableModel(MdxUtils.loadModel(in));
//                model.setFileRef(f);
//                temp = new ModelPanel(this, model, prefs, MainPanel.this, selectionItemTypeGroup, selectionModeGroup,
//                        modelStructureChangeListener, coordDisplayListener, viewportTransferHandler,
//                        activeViewportWatcher, icon, false, textureExporter);
//            } catch (final IOException e) {
//                e.printStackTrace();
//                ExceptionPopup.display(e);
//                throw new RuntimeException("Reading mdx failed");
//            }
//        } else if (f.getPath().toLowerCase().endsWith("obj")) {
//            // final Build builder = new Build();
//            // final MDLOBJBuilderInterface builder = new
//            // MDLOBJBuilderInterface();
//            final Build builder = new Build();
//            try {
//                final Parse obj = new Parse(builder, f.getPath());
//                temp = new ModelPanel(this, builder.createMDL(), prefs, MainPanel.this, selectionItemTypeGroup,
//                        selectionModeGroup, modelStructureChangeListener, coordDisplayListener, viewportTransferHandler,
//                        activeViewportWatcher, icon, false, textureExporter);
//            } catch (final IOException e) {
//                ExceptionPopup.display(e);
//                e.printStackTrace();
//            }
//        } else {
//            temp = new ModelPanel(this, EditableModel.read(f), prefs, MainPanel.this, selectionItemTypeGroup,
//                    selectionModeGroup, modelStructureChangeListener, coordDisplayListener, viewportTransferHandler,
//                    activeViewportWatcher, icon, false, textureExporter);
//            temp.setFile(f);
//        }
//        loadModel(temporary, selectNewTab, temp);
//    }
//
//    public void loadStreamMdx(final InputStream f, final boolean temporary, final boolean selectNewTab,
//                              final ImageIcon icon) {
//        ModelPanel temp = null;
//        try (BlizzardDataInputStream in = new BlizzardDataInputStream(f)) {
//            final EditableModel model = new EditableModel(MdxUtils.loadModel(in));
//            model.setFileRef(null);
//            temp = new ModelPanel(this, model, prefs, MainPanel.this, selectionItemTypeGroup, selectionModeGroup,
//                    modelStructureChangeListener, coordDisplayListener, viewportTransferHandler, activeViewportWatcher,
//                    icon, false, textureExporter);
//        } catch (final IOException e) {
//            e.printStackTrace();
//            ExceptionPopup.display(e);
//            throw new RuntimeException("Reading mdx failed");
//        }
//        loadModel(temporary, selectNewTab, temp);
//    }
//
//    public void loadBLPPathAsModel(final String filepath) {
//        loadBLPPathAsModel(filepath, null);
//    }
//
//    public void loadBLPPathAsModel(final String filepath, final File workingDirectory) {
//        loadBLPPathAsModel(filepath, workingDirectory, 800);
//    }
//
//    public void loadBLPPathAsModel(final String filepath, final File workingDirectory, final int version) {
//        final EditableModel blankTextureModel = new EditableModel(filepath.substring(filepath.lastIndexOf('\\') + 1));
//        blankTextureModel.setFormatVersion(version);
//        if (workingDirectory != null) {
//            blankTextureModel.setFileRef(new File(workingDirectory.getPath() + "/" + filepath + ".mdl"));
//        }
//        final Geoset newGeoset = new Geoset();
//        final Layer layer = new Layer("Blend", new Bitmap(filepath));
//        layer.add("Unshaded");
//        final Material material = new Material(layer);
//        newGeoset.setMaterial(material);
//        final BufferedImage bufferedImage = material.getBufferedImage(blankTextureModel.getWrappedDataSource());
//        final int textureWidth = bufferedImage.getWidth();
//        final int textureHeight = bufferedImage.getHeight();
//        final float aspectRatio = textureWidth / (float) textureHeight;
//
//        final int displayWidth = (int) (aspectRatio > 1 ? 128 : 128 * aspectRatio);
//        final int displayHeight = (int) (aspectRatio < 1 ? 128 : 128 / aspectRatio);
//
//        final int groundOffset = aspectRatio > 1 ? (128 - displayHeight) / 2 : 0;
//        final GeosetVertex upperLeft = new GeosetVertex(0, displayWidth / 2, displayHeight + groundOffset,
//                new Normal(0, 0, 1));
//        final TVertex upperLeftTVert = new TVertex(1, 0);
//        upperLeft.addTVertex(upperLeftTVert);
//        newGeoset.add(upperLeft);
//        upperLeft.setGeoset(newGeoset);
//
//        final GeosetVertex upperRight = new GeosetVertex(0, -displayWidth / 2, displayHeight + groundOffset,
//                new Normal(0, 0, 1));
//        newGeoset.add(upperRight);
//        final TVertex upperRightTVert = new TVertex(0, 0);
//        upperRight.addTVertex(upperRightTVert);
//        upperRight.setGeoset(newGeoset);
//
//        final GeosetVertex lowerLeft = new GeosetVertex(0, displayWidth / 2, groundOffset, new Normal(0, 0, 1));
//        newGeoset.add(lowerLeft);
//        final TVertex lowerLeftTVert = new TVertex(1, 1);
//        lowerLeft.addTVertex(lowerLeftTVert);
//        lowerLeft.setGeoset(newGeoset);
//
//        final GeosetVertex lowerRight = new GeosetVertex(0, -displayWidth / 2, groundOffset, new Normal(0, 0, 1));
//        newGeoset.add(lowerRight);
//        final TVertex lowerRightTVert = new TVertex(0, 1);
//        lowerRight.addTVertex(lowerRightTVert);
//        lowerRight.setGeoset(newGeoset);
//
//        newGeoset.add(new Triangle(upperLeft, upperRight, lowerLeft));
//        newGeoset.add(new Triangle(upperRight, lowerRight, lowerLeft));
//        blankTextureModel.add(newGeoset);
//        blankTextureModel.add(new Animation("Stand", 0, 1000));
//        blankTextureModel.doSavePreps();
//
//        loadModel(workingDirectory == null, true,
//                new ModelPanel(MainPanel.this, blankTextureModel, prefs, MainPanel.this, selectionItemTypeGroup,
//                        selectionModeGroup, modelStructureChangeListener, coordDisplayListener, viewportTransferHandler,
//                        activeViewportWatcher, GlobalIcons.ORANGE_ICON, true, textureExporter));
//    }
//
//    public void loadModel(final boolean temporary, final boolean selectNewTab, final ModelPanel temp) {
//        if (temporary) {
//            temp.getModelViewManager().getModel().setTemp(true);
//        }
//        final ModelPanel modelPanel = temp;
//        // temp.getRootWindow().addMouseListener(new MouseAdapter() {
//        // @Override
//        // public void mouseEntered(final MouseEvent e) {
//        // currentModelPanel = ModelPanel;
//        // geoControl.setViewportView(currentModelPanel.getModelViewManagingTree());
//        // geoControl.repaint();
//        // }
//        // });
//        final JMenuItem menuItem = new JMenuItem(temp.getModel().getName());
//        menuItem.setIcon(temp.getIcon());
//        windowMenu.add(menuItem);
//        menuItem.addActionListener(e -> setCurrentModel(modelPanel));
//        temp.setJMenuItem(menuItem);
//        temp.getModelViewManager().addStateListener(new MainPanel.RepaintingModelStateListener(MainPanel.this));
//        temp.changeActivity(currentActivity);
//
//        if (geoControl == null) {
//            geoControl = new JScrollPane(temp.getModelViewManagingTree());
//            viewportControllerWindowView.setComponent(geoControl);
//            viewportControllerWindowView.repaint();
//            geoControlModelData = new JScrollPane(temp.getModelComponentBrowserTree());
//            modelDataView.setComponent(geoControlModelData);
//            modelComponentView.setComponent(temp.getComponentsPanel());
//            modelDataView.repaint();
//        }
//        addTabForView(temp, selectNewTab);
//        modelPanels.add(temp);
//
//        // tabbedPane.addTab(f.getName().split("\\.")[0], icon, temp, f.getPath());
//        // if (selectNewTab) {
//        // tabbedPane.setSelectedComponent(temp);
//        // }
//        if (temporary) {
//            temp.getModelViewManager().getModel().setFileRef(null);
//        }
//        // }
//        // }).start();
//        toolsMenu.setEnabled(true);
//
//        if (selectNewTab && (prefs.getQuickBrowse() != null) && prefs.getQuickBrowse()) {
//            for (int i = (modelPanels.size() - 2); i >= 0; i--) {
//                final ModelPanel openModelPanel = modelPanels.get(i);
//                if (openModelPanel.getUndoManager().isRedoListEmpty()
//                        && openModelPanel.getUndoManager().isUndoListEmpty()) {
//                    if (openModelPanel.close(this)) {
//                        modelPanels.remove(openModelPanel);
//                        windowMenu.remove(openModelPanel.getMenuItem());
//                    }
//                }
//            }
//        }
//    }
//
//    public void addTabForView(final ModelPanel view, final boolean selectNewTab) {
//        // modelTabStringViewMap.addView(view);
//        // final DockingWindow previousWindow = modelTabWindow.getWindow();
//        // final TabWindow tabWindow = previousWindow instanceof TabWindow ? (TabWindow)
//        // previousWindow : new
//        // TabWindow();
//        // DockingWindow selectedWindow = null;
//        // if (previousWindow == tabWindow) {
//        // selectedWindow = tabWindow.getSelectedWindow();
//        // }
//        // if (previousWindow != null && tabWindow != previousWindow) {
//        // tabWindow.addTab(previousWindow);
//        // }
//        // tabWindow.addTab(view);
//        // if (selectedWindow != null) {
//        // tabWindow.setSelectedTab(tabWindow.getChildWindowIndex(selectNewTab ? view :
//        // selectedWindow));
//        // }
//        // modelTabWindow.setWindow(tabWindow);
//        if (selectNewTab) {
//            view.getMenuItem().doClick();
//        }
//    }
//
//    public void setCurrentModel(final ModelPanel modelContextManager) {
//        currentModelPanel = modelContextManager;
//        if (currentModelPanel == null) {
//            final JPanel jPanel = new JPanel();
//            jPanel.add(new JLabel("..."));
//            viewportControllerWindowView.setComponent(jPanel);
//            geoControl = null;
//            frontView.setComponent(new JPanel());
//            bottomView.setComponent(new JPanel());
//            leftView.setComponent(new JPanel());
//            perspectiveView.setComponent(new JPanel());
//            previewView.setComponent(new JPanel());
//            animationControllerView.setComponent(new JPanel());
//            refreshAnimationModeState();
//            timeSliderPanel.setUndoManager(null, animatedRenderEnvironment);
//            timeSliderPanel.setModelView(null);
//            creatorPanel.setModelEditorManager(null);
//            creatorPanel.setCurrentModel(null);
//            creatorPanel.setUndoManager(null);
//            modelComponentView.setComponent(new JPanel());
//            geoControlModelData = null;
//        } else {
//            geoControl.setViewportView(currentModelPanel.getModelViewManagingTree());
//            geoControl.repaint();
//
//            frontView.setComponent(modelContextManager.getFrontArea());
//            bottomView.setComponent(modelContextManager.getBotArea());
//            leftView.setComponent(modelContextManager.getSideArea());
//            perspectiveView.setComponent(modelContextManager.getPerspArea());
//            previewView.setComponent(modelContextManager.getAnimationViewer());
//            animationControllerView.setComponent(modelContextManager.getAnimationController());
//            refreshAnimationModeState();
//            timeSliderPanel.setUndoManager(currentModelPanel.getUndoManager(), animatedRenderEnvironment);
//            timeSliderPanel.setModelView(currentModelPanel.getModelViewManager());
//            creatorPanel.setModelEditorManager(currentModelPanel.getModelEditorManager());
//            creatorPanel.setCurrentModel(currentModelPanel.getModelViewManager());
//            creatorPanel.setUndoManager(currentModelPanel.getUndoManager());
//
//            geoControlModelData.setViewportView(currentModelPanel.getModelComponentBrowserTree());
//
//            modelComponentView.setComponent(currentModelPanel.getComponentsPanel());
//            geoControlModelData.repaint();
//            currentModelPanel.getModelComponentBrowserTree().reloadFromModelView();
//        }
//        activeViewportWatcher.viewportChanged(null);
//        timeSliderPanel.revalidateKeyframeDisplay();
//    }
//
//    public void loadFile(final File f, final boolean temporary) {
//        loadFile(f, temporary, true, mainPanel.MDLIcon);
//    }
//
//    public void loadFile(final File f) {
//        loadFile(f, false);
//    }
//
//    public void openFile(final File f) {
//        mainPanel.currentFile = f;
//        mainPanel.profile.setPath(mainPanel.currentFile.getParent());
//        // frontArea.clearGeosets();
//        // sideArea.clearGeosets();
//        // botArea.clearGeosets();
//        mainPanel.toolsMenu.getAccessibleContext().setAccessibleDescription(
//                "Allows the user to control which parts of the model are displayed for editing.");
//        mainPanel.toolsMenu.setEnabled(true);
//        SaveProfile.get().addRecent(mainPanel.currentFile.getPath());
//        updateRecent();
//        loadFile(mainPanel.currentFile);
//    }
//
//    public void importFile(final File f) {
//        final EditableModel currentModel = currentMDL();
//        if (currentModel != null) {
//            importFile(EditableModel.read(f));
//        }
//    }
//
//    public void importFile(final EditableModel model) {
//        final EditableModel currentModel = currentMDL();
//        if (currentModel != null) {
//            mainPanel.importPanel = new ImportPanel(currentModel, model);
//            mainPanel.importPanel.setCallback(new MainPanel.ModelStructureChangeListenerImplementation(new MainPanel.ModelReference() {
//                private final EditableModel model = currentMDL();
//
//                @Override
//                public EditableModel getModel() {
//                    return model;
//                }
//            }));
//
//        }
//    }
//
//    public String incName(final String name) {
//        String output = name;
//
//        int depth = 1;
//        boolean continueLoop = true;
//        while (continueLoop) {
//            char c = '0';
//            try {
//                c = output.charAt(output.length() - depth);
//            } catch (final IndexOutOfBoundsException e) {
//                // c remains '0'
//                continueLoop = false;
//            }
//            for (char n = '0'; (n < '9') && continueLoop; n++) {
//                // JOptionPane.showMessageDialog(null,"checking "+c+" against
//                // "+n);
//                if (c == n) {
//                    char x = c;
//                    x++;
//                    output = output.substring(0, output.length() - depth) + x
//                            + output.substring((output.length() - depth) + 1);
//                    continueLoop = false;
//                }
//            }
//            if (c == '9') {
//                output = output.substring(0, output.length() - depth) + 0
//                        + output.substring((output.length() - depth) + 1);
//            } else if (continueLoop) {
//                output = output.substring(0, (output.length() - depth) + 1) + 1
//                        + output.substring((output.length() - depth) + 1);
//                continueLoop = false;
//            }
//            depth++;
//        }
//        if (output == null) {
//            output = "name error";
//        } else if (output.equals(name)) {
//            output = output + "_edit";
//        }
//
//        return output;
//    }
//
//    public void nullmodelFile() {
//        final EditableModel currentMDL = currentMDL();
//        if (currentMDL != null) {
//            final EditableModel newModel = new EditableModel();
//            newModel.copyHeaders(currentMDL);
//            if (newModel.getFileRef() == null) {
//                newModel.setFileRef(
//                        new File(System.getProperty("java.io.tmpdir") + "MatrixEaterExtract/matrixeater_anonymousMDL",
//                                "" + (int) (Math.random() * Integer.MAX_VALUE) + ".mdl"));
//            }
//            while (newModel.getFile().exists()) {
//                newModel.setFileRef(
//                        new File(currentMDL.getFile().getParent() + "/" + incName(newModel.getName()) + ".mdl"));
//            }
//            mainPanel.importPanel = new ImportPanel(newModel, EditableModel.deepClone(currentMDL, "CurrentModel"));
//
//            final Thread watcher = new Thread(() -> {
//                while (mainPanel.importPanel.getParentFrame().isVisible()
//                        && (!mainPanel.importPanel.importStarted() || mainPanel.importPanel.importEnded())) {
//                    try {
//                        Thread.sleep(1);
//                    } catch (final Exception e) {
//                        ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
//                    }
//                }
//                // if( !importPanel.getParentFrame().isVisible() &&
//                // !importPanel.importEnded() )
//                // JOptionPane.showMessageDialog(null,"bad voodoo
//                // "+importPanel.importSuccessful());
//                // else
//                // JOptionPane.showMessageDialog(null,"good voodoo
//                // "+importPanel.importSuccessful());
//                // if( importPanel.importSuccessful() )
//                // {
//                // newModel.saveFile();
//                // loadFile(newModel.getFile());
//                // }
//
//                if (mainPanel.importPanel.importStarted()) {
//                    while (!mainPanel.importPanel.importEnded()) {
//                        try {
//                            Thread.sleep(1);
//                        } catch (final Exception e) {
//                            ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
//                        }
//                    }
//
//                    if (mainPanel.importPanel.importSuccessful()) {
//                        newModel.saveFile();
//                        loadFile(newModel.getFile());
//                    }
//                }
//            });
//            watcher.start();
//        }
//    }
//
//    public void parseTriangles(final String input, final Geoset g) {
//        // Loading triangles to a geoset requires verteces to be loaded first
//        final String[] s = input.split(",");
//        s[0] = s[0].substring(4);
//        final int s_size = countContainsString(input, ",");
//        s[s_size - 1] = s[s_size - 1].substring(0, s[s_size - 1].length() - 2);
//        for (int t = 0; t < (s_size - 1); t += 3)// s[t+3].equals("")||
//        {
//            for (int i = 0; i < 3; i++) {
//                s[t + i] = s[t + i].substring(1);
//            }
//            try {
//                g.addTriangle(new Triangle(Integer.parseInt(s[t]), Integer.parseInt(s[t + 1]),
//                        Integer.parseInt(s[t + 2]), g));
//            } catch (final NumberFormatException e) {
//                JOptionPane.showMessageDialog(this, "Error: Unable to interpret information in Triangles: " + s[t]
//                        + ", " + s[t + 1] + ", or " + s[t + 2]);
//            }
//        }
//        // try
//        // {
//        // g.addTriangle(new Triangle(g.getVertex( Integer.parseInt(s[t])
//        // ),g.getVertex( Integer.parseInt(s[t+1])),g.getVertex(
//        // Integer.parseInt(s[t+2])),g) );
//        // }
//        // catch (NumberFormatException e)
//        // {
//        // JOptionPane.showMessageDialog(this,"Error: Unable to interpret
//        // information in Triangles.");
//        // }
//    }
//
//    public boolean doesContainString(final String a, final String b)// see if a
//    // contains
//    // b
//    {
//        final int l = a.length();
//        for (int i = 0; i < l; i++) {
//            if (a.startsWith(b, i)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public int countContainsString(final String a, final String b)// see if a
//    // contains
//    // b
//    {
//        final int l = a.length();
//        int x = 0;
//        for (int i = 0; i < l; i++) {
//            if (a.startsWith(b, i)) {
//                x++;
//            }
//        }
//        return x;
//    }
//
//    @Override
//    public void refreshUndo() {
//        mainPanel.undo.setEnabled(undo.funcEnabled());
//        mainPanel.redo.setEnabled(redo.funcEnabled());
//    }
//
//    public void refreshController() {
//        if (mainPanel.geoControl != null) {
//            mainPanel.geoControl.repaint();
//        }
//        if (mainPanel.geoControlModelData != null) {
//            mainPanel.geoControlModelData.repaint();
//        }
//    }
//
//    // @Override
//    // public void mouseEntered(final MouseEvent e) {
//    // refreshUndo();
//    // }
//
//    // @Override
//    // public void mouseExited(final MouseEvent e) {
//    // refreshUndo();
//    // }
//
//    // @Override
//    // public void mousePressed(final MouseEvent e) {
//    // refreshUndo();
//    // }
//
//    // @Override
//    // public void mouseReleased(final MouseEvent e) {
//    // refreshUndo();
//    //
//    // }
//
//    // @Override
//    // public void mouseClicked(final MouseEvent e) {
//    // if (e.getSource() == tabbedPane && e.getButton() == MouseEvent.BUTTON3) {
//    // for (int i = 0; i < tabbedPane.getTabCount(); i++) {
//    // if (tabbedPane.getBoundsAt(i).contains(e.getX(), e.getY())) {
//    // contextClickedTab = i;
//    // contextMenu.show(tabbedPane, e.getX(), e.getY());
//    // }
//    // }
//    // }
//    // }
//
//    // @Override
//    // public void stateChanged(final ChangeEvent e) {
//    // if (((ModelPanel) tabbedPane.getSelectedComponent()) != null) {
//    // geoControl.setMDLDisplay(((ModelPanel)
//    // tabbedPane.getSelectedComponent()).getModelViewManagingTree());
//    // } else {
//    // geoControl.setMDLDisplay(null);
//    // }
//    // }
//
//    public void setMouseCoordDisplay(final byte dim1, final byte dim2, final double value1, final double value2) {
//        for (JTextField jTextField : mainPanel.mouseCoordDisplay) {
//            jTextField.setText("");
//        }
//        mainPanel.mouseCoordDisplay[dim1].setText((float) value1 + "");
//        mainPanel.mouseCoordDisplay[dim2].setText((float) value2 + "");
//    }
//
//    private class UndoMenuItem extends JMenuItem {
//        public UndoMenuItem(final String text) {
//            super(text);
//        }
//
//        @Override
//        public String getText() {
//            if (funcEnabled()) {
//                return "Undo " + currentModelPanel().getUndoManager().getUndoText();// +"
//                // Ctrl+Z";
//            } else {
//                return "Can't undo";// +" Ctrl+Z";
//            }
//        }
//
//        public boolean funcEnabled() {
//            try {
//                return !currentModelPanel().getUndoManager().isUndoListEmpty();
//            } catch (final NullPointerException e) {
//                return false;
//            }
//        }
//    }
//
//    private class RedoMenuItem extends JMenuItem {
//        public RedoMenuItem(final String text) {
//            super(text);
//        }
//
//        @Override
//        public String getText() {
//            if (funcEnabled()) {
//                return "Redo " + currentModelPanel().getUndoManager().getRedoText();// +"
//                // Ctrl+Y";
//            } else {
//                return "Can't redo";// +" Ctrl+Y";
//            }
//        }
//
//        public boolean funcEnabled() {
//            try {
//                return !currentModelPanel().getUndoManager().isRedoListEmpty();
//            } catch (final NullPointerException e) {
//                return false;
//            }
//        }
//    }
//
//    public boolean closeAll() {
//        boolean success = true;
//        final Iterator<ModelPanel> iterator = mainPanel.modelPanels.iterator();
//        boolean closedCurrentPanel = false;
//        ModelPanel lastUnclosedModelPanel = null;
//        while (iterator.hasNext()) {
//            final ModelPanel panel = iterator.next();
//            if (success = panel.close(mainPanel)) {
//                mainPanel.windowMenu.remove(panel.getMenuItem());
//                iterator.remove();
//                if (panel == mainPanel.currentModelPanel) {
//                    closedCurrentPanel = true;
//                }
//            } else {
//                lastUnclosedModelPanel = panel;
//                break;
//            }
//        }
//        if (closedCurrentPanel) {
//            setCurrentModel(lastUnclosedModelPanel);
//        }
//        return success;
//    }
//
//    public boolean closeOthers(final ModelPanel panelToKeepOpen) {
//        boolean success = true;
//        final Iterator<ModelPanel> iterator = mainPanel.modelPanels.iterator();
//        boolean closedCurrentPanel = false;
//        ModelPanel lastUnclosedModelPanel = null;
//        while (iterator.hasNext()) {
//            final ModelPanel panel = iterator.next();
//            if (panel == panelToKeepOpen) {
//                lastUnclosedModelPanel = panel;
//                continue;
//            }
//            if (success = panel.close(mainPanel)) {
//                mainPanel.windowMenu.remove(panel.getMenuItem());
//                iterator.remove();
//                if (panel == mainPanel.currentModelPanel) {
//                    closedCurrentPanel = true;
//                }
//            } else {
//                lastUnclosedModelPanel = panel;
//                break;
//            }
//        }
//        if (closedCurrentPanel) {
//            setCurrentModel(lastUnclosedModelPanel);
//        }
//        return success;
//    }
//
//    protected void repaintSelfAndChildren(final ModelPanel mpanel) {
//        repaint();
//        geoControl.repaint();
//        geoControlModelData.repaint();
//        mpanel.repaintSelfAndRelatedChildren();
//    }
//
//    private final MainPanel.TextureExporterImpl textureExporter = new MainPanel.TextureExporterImpl();
//
//    public final class TextureExporterImpl implements TextureExporter {
//        public JFileChooser getFileChooser() {
//            return mainPanel.exportTextureDialog;
//        }
//
//        @Override
//        public void showOpenDialog(final String suggestedName, final TextureExporterClickListener fileHandler,
//                                   final Component parent) {
//            if (mainPanel.exportTextureDialog.getCurrentDirectory() == null) {
//                final EditableModel current = currentMDL();
//                if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                    mainPanel.fc.setCurrentDirectory(current.getFile().getParentFile());
//                } else if (mainPanel.profile.getPath() != null) {
//                    mainPanel.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
//                }
//            }
//            if (mainPanel.exportTextureDialog.getCurrentDirectory() == null) {
//                mainPanel.exportTextureDialog.setSelectedFile(
//                        new File(mainPanel.exportTextureDialog.getCurrentDirectory() + File.separator + suggestedName));
//            }
//            final int showOpenDialog = mainPanel.exportTextureDialog.showOpenDialog(parent);
//            if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
//                final File file = mainPanel.exportTextureDialog.getSelectedFile();
//                if (file != null) {
//                    fileHandler.onClickOK(file, mainPanel.exportTextureDialog.getFileFilter());
//                } else {
//                    JOptionPane.showMessageDialog(parent, "No import file was specified");
//                }
//            }
//        }
//
//        @Override
//        public void exportTexture(final String suggestedName, final TextureExporterClickListener fileHandler,
//                                  final Component parent) {
//
//            if (mainPanel.exportTextureDialog.getCurrentDirectory() == null) {
//                final EditableModel current = currentMDL();
//                if ((current != null) && !current.isTemp() && (current.getFile() != null)) {
//                    mainPanel.fc.setCurrentDirectory(current.getFile().getParentFile());
//                } else if (mainPanel.profile.getPath() != null) {
//                    mainPanel.fc.setCurrentDirectory(new File(mainPanel.profile.getPath()));
//                }
//            }
//            if (mainPanel.exportTextureDialog.getCurrentDirectory() == null) {
//                mainPanel.exportTextureDialog.setSelectedFile(
//                        new File(mainPanel.exportTextureDialog.getCurrentDirectory() + File.separator + suggestedName));
//            }
//
//            final int x = mainPanel.exportTextureDialog.showSaveDialog(parent);
//            if (x == JFileChooser.APPROVE_OPTION) {
//                final File file = mainPanel.exportTextureDialog.getSelectedFile();
//                if (file != null) {
//                    try {
//                        if (file.getName().lastIndexOf('.') >= 0) {
//                            fileHandler.onClickOK(file, mainPanel.exportTextureDialog.getFileFilter());
//                        } else {
//                            JOptionPane.showMessageDialog(parent, "No file type was specified");
//                        }
//                    } catch (final Exception e2) {
//                        ExceptionPopup.display(e2);
//                        e2.printStackTrace();
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(parent, "No output file was specified");
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void save(final EditableModel model) {
//        if (model.getFile() != null) {
//            model.saveFile();
//        } else {
//            onClickSaveAs(model);
//        }
//    }
//
//    private Component getFocusedComponent() {
//        final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        final Component focusedComponent = kfm.getFocusOwner();
//        return focusedComponent;
//    }
//
//    private boolean focusedComponentNeedsTyping(final Component focusedComponent) {
//        return (focusedComponent instanceof JTextArea) || (focusedComponent instanceof JTextField);
//    }
//
//}
