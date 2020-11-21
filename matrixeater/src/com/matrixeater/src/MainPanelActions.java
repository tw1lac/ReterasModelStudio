package com.matrixeater.src;


import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.animedit.TimeBoundChangeListener;
import com.hiveworkshop.wc3.gui.animedit.TimeBoundChooserPanel;
import com.hiveworkshop.wc3.gui.animedit.TimeSliderTimeListener;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.ModelEditorManager;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionMode;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarActionButtonType;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarButtonListener;
import com.hiveworkshop.wc3.mdl.*;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainPanelActions {

    public MainPanelActions(MainPanel mainPanel) {
    }


    static AbstractAction cloneActionUgg(final MainPanel mainPanel) {
        return new AbstractAction("CloneSelection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                cloneAction(mainPanel);
            }
        };
    }

    static void cloneAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            try {
                mpanel.getUndoManager().pushAction(
                        mpanel.getModelEditorManager().getModelEditor().cloneSelectedComponents(mainPanel.namePicker));
            } catch (final Exception exc) {
                ExceptionPopup.display(exc);
            }
        }
        mainPanel.menuBar.refreshUndo();
        mainPanel.repaintSelfAndChildren(mpanel);
    }

    static AbstractAction deleteActionUgg(final MainPanel mainPanel) {
        return new AbstractAction("Delete") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                deleteAction(mainPanel);
            }
        };
    }

    static void deleteAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            if (mainPanel.animationModeState) {
                mainPanel.mainLayoutUgg.editTab.timeSliderPanel.deleteSelectedKeyframes();
            } else {
                mpanel.getUndoManager()
                        .pushAction(mpanel.getModelEditorManager().getModelEditor().deleteSelectedComponents());
            }
        }
        mainPanel.repaintSelfAndChildren(mpanel);
    }

    static AbstractAction selectAllAction(final MainPanel mainPanel) {
        return new AbstractAction("Select All") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = mainPanel.currentModelPanel;
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().selectAll());
                }
                mainPanel.repaint();
            }
        };
    }

    static AbstractAction invertSelectAction(final MainPanel mainPanel) {
        return new AbstractAction("Invert Selection") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = mainPanel.currentModelPanel;
                if (mpanel != null) {
                    mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().invertSelection());
                }
                mainPanel.repaint();
            }
        };
    }

    static AbstractAction rigAction(final MainPanel mainPanel) {
        return new AbstractAction("Rig") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ModelPanel mpanel = mainPanel.currentModelPanel;
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
                mainPanel.repaint();
            }
        };
    }

//    static AbstractAction expandSelectionActionugg(final MainPanel mainPanel) {
//        return new AbstractAction("Expand Selection") {
//            @Override
//            public void actionPerformed(final ActionEvent e) {
//                expandSelectionAction(mainPanel);
//            }
//        };
//    }

    static void expandSelectionAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().expandSelection());
        }
        mainPanel.repaint();
    }

    static void snapNormalsAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().snapNormals());
        }
        mainPanel.repaint();
    }

    static void snapVerticesAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager()
                    .pushAction(mpanel.getModelEditorManager().getModelEditor().snapSelectedVertices());
        }
        mainPanel.repaint();
    }

    static void recalculateNormalsAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().recalcNormals());
        }
        mainPanel.repaint();
    }

    static void recalculateExtentsAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
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
            final int userChoice = JOptionPane.showConfirmDialog(mainPanel, messagePanel, "Message",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (userChoice == JOptionPane.YES_OPTION) {
                mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor()
                        .recalcExtents(considerCurrentBtn.isSelected()));
            }
        }
        mainPanel.repaint();
    }

    static void flipAllUVsAxisAction(MainPanel mainPanel, String s) {
        for (final Geoset geo : mainPanel.currentMDL().getGeosets()) {
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
        mainPanel.repaint();
    }

    static void mirrorAxisAction(MainPanel mainPanel, String s) {
        byte i = 0;
        if(s.endsWith("Y")){
            i = 1;
        }else if (s.endsWith("Z")){
            i = 2;
        }
        byte finalI = i;
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            final Vertex selectionCenter = mpanel.getModelEditorManager().getModelEditor().getSelectionCenter();
            mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().mirror(finalI,
                    mainPanel.mirrorFlip.isSelected(), selectionCenter.x, selectionCenter.y, selectionCenter.z));
        }
        mainPanel.repaint();
    }

    static void insideOutAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager().pushAction(mpanel.getModelEditorManager().getModelEditor().flipSelectedFaces());
        }
        mainPanel.repaint();
    }

    static void insideOutNormalsAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager()
                    .pushAction(mpanel.getModelEditorManager().getModelEditor().flipSelectedNormals());
        }
        mainPanel.repaint();
    }

    static void viewMatricesAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.viewMatrices();
        }
        mainPanel.repaint();
    }

    static DockingWindowListener createDockingWindowListener(final MainPanel mainPanel) {
        return new DockingWindowListener() {
            @Override
            public void windowUndocking(final DockingWindow arg0) {
            }

            @Override
            public void windowUndocked(final DockingWindow dockingWindow) {
                SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> {
                    if (dockingWindow instanceof View) {
                        final Component component = ((View) dockingWindow).getComponent();
                        if (component instanceof JComponent) {
                            mainPanel.linkActions(((JComponent) component).getRootPane());
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
        };
    }

    static DockingWindowListener createDockingWindowListener(Runnable fixit) {
        return new DockingWindowListener() {

            @Override
            public void windowUndocking(final DockingWindow removedWindow) {
                if (MainPanel.OLDMODE) {
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
            public void windowUndocked(final DockingWindow arg0) {}

            @Override
            public void windowShown(final DockingWindow arg0) {}

            @Override
            public void windowRestoring(final DockingWindow arg0) {}

            @Override
            public void windowRestored(final DockingWindow arg0) {}

            @Override
            public void windowRemoved(final DockingWindow removedFromWindow, final DockingWindow removedWindow) {
                if (MainPanel.OLDMODE) {
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
            public void windowMinimizing(final DockingWindow arg0) {}

            @Override
            public void windowMinimized(final DockingWindow arg0) {}

            @Override
            public void windowMaximizing(final DockingWindow arg0) {}

            @Override
            public void windowMaximized(final DockingWindow arg0) {}

            @Override
            public void windowHidden(final DockingWindow arg0) {}

            @Override
            public void windowDocking(final DockingWindow arg0) {}

            @Override
            public void windowDocked(final DockingWindow arg0) {}

            @Override
            public void windowClosing(final DockingWindow closingWindow) {
                if (MainPanel.OLDMODE) {
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
                if (MainPanel.OLDMODE) {
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
        };
    }

    static TimeSliderTimeListener createTimeSliderTimeListener(MainPanel mainPanel) {
        return currentTime -> {
            mainPanel.animatedRenderEnvironment.setCurrentTime(currentTime - mainPanel.animatedRenderEnvironment.getStart());
            if (mainPanel.currentModelPanel != null) {
                mainPanel.currentModelPanel.getEditorRenderModel().updateNodes(true, false);
                mainPanel.currentModelPanel.repaintSelfAndRelatedChildren();
            }
        };
    }


    static void createKeyframeAction(MainPanel mainPanel) {
        final ModelPanel mpanel = mainPanel.currentModelPanel;
        if (mpanel != null) {
            mpanel.getUndoManager()
                    .pushAction(mpanel.getModelEditorManager().getModelEditor().createKeyframe(mainPanel.actionType));
        }
        mainPanel.repaintSelfAndChildren(mpanel);
    }

    static void timeBoundChooserPanel(MainPanel mainPanel) {
        final TimeBoundChooserPanel timeBoundChooserPanel = new TimeBoundChooserPanel(
                mainPanel.currentModelPanel == null ? null : mainPanel.currentModelPanel.getModelViewManager(),
                mainPanel.modelStructureChangeListener);
        final int confirmDialogResult = JOptionPane.showConfirmDialog(mainPanel, timeBoundChooserPanel,
                "Set Time Bounds", JOptionPane.OK_CANCEL_OPTION);
        if (confirmDialogResult == JOptionPane.OK_OPTION) {
            timeBoundChooserPanel.applyTo(mainPanel.animatedRenderEnvironment);
            if (mainPanel.currentModelPanel != null) {
                mainPanel.currentModelPanel.getEditorRenderModel().refreshFromEditor(mainPanel.animatedRenderEnvironment,
                        MainPanel.IDENTITY, MainPanel.IDENTITY, MainPanel.IDENTITY, mainPanel.currentModelPanel.getPerspArea().getViewport());
                mainPanel.currentModelPanel.getEditorRenderModel().updateNodes(true, false);
            }
        }
    }

    static TimeBoundChangeListener animatedRenderEnvironmentChangeListener(MainPanel mainPanel) {
        return (start, end) -> {
            final Integer globalSeq = mainPanel.animatedRenderEnvironment.getGlobalSeq();
            if (globalSeq != null) {
                mainPanel.mainLayoutUgg.editTab.creatorPanel.setChosenGlobalSeq(globalSeq);
            } else {
                final ModelPanel modelPanel = mainPanel.currentModelPanel;
                if (modelPanel != null) {
                    boolean foundAnim = false;
                    for (final Animation animation : modelPanel.getModel().getAnims()) {
                        if ((animation.getStart() == start) && (animation.getEnd() == end)) {
                            mainPanel.mainLayoutUgg.editTab.creatorPanel.setChosenAnimation(animation);
                            foundAnim = true;
                            break;
                        }
                    }
                    if (!foundAnim) {
                        mainPanel.mainLayoutUgg.editTab.creatorPanel.setChosenAnimation(null);
                    }
                }

            }
        };
    }

    static ToolbarButtonListener<SelectionItemTypes> createSelectionItemTypesButtonListener(MainPanel mainPanel) {
        // we need to refresh the state of stuff AFTER the ModelPanels, this is a pretty signficant design flaw,
        // so we're just going to post to the EDT to get behind them (they're called on the same notifier as this method)
        return newType -> {
            mainPanel.animationModeState = newType == SelectionItemTypes.ANIMATE;
            SwingUtilities.invokeLater(() -> ModelPanelUgg.refreshAnimationModeState(
                    mainPanel.actionTypeGroup,
                    mainPanel.animatedRenderEnvironment,
                    mainPanel.mainLayoutUgg.editTab.animationModeButton,
                    mainPanel.animationModeState,
                    mainPanel.mainLayoutUgg.editTab.creatorPanel,
                    mainPanel.currentModelPanel,
                    mainPanel.prefs,
                    mainPanel.mainLayoutUgg.editTab.setKeyframe,
                    mainPanel.mainLayoutUgg.editTab.setTimeBounds,
                    mainPanel.snapButton,
                    mainPanel.mainLayoutUgg.editTab.timeSliderPanel));

            if (newType == SelectionItemTypes.TPOSE) {

                final Object[] settings = {"Move Linked", "Move Single"};
                final Object dialogResult = JOptionPane.showInputDialog(null, "Choose settings:", "T-Pose Settings",
                        JOptionPane.PLAIN_MESSAGE, null, settings, settings[0]);
                ModelEditorManager.MOVE_LINKED = dialogResult == settings[0];
            }
            mainPanel.repaint();
        };
    }

    static ToolbarButtonListener<ToolbarActionButtonType> createActionTypeGroupButtonListener(MainPanel mainPanel) {
        return newType -> {
            if (newType != null) {
                mainPanel.changeActivity(newType);
            }
        };
    }


    static void getAltSelectAction(MainPanel mainPanel, boolean b, SelectionMode select, boolean b2) {
        if (b) {
            mainPanel.selectionModeGroup.setToolbarButtonType(select);
            mainPanel.cheatAlt = b2;
        }
    }

    static void getShiftSelectAction(MainPanel mainPanel, boolean b, SelectionMode add, boolean b2) {
        final Component focusedComponent = mainPanel.getFocusedComponent();
        if (mainPanel.focusedComponentNeedsTyping(focusedComponent)) {
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
        if (b) {
            mainPanel.selectionModeGroup.setToolbarButtonType(add);
            mainPanel.cheatShift = b2;
        }
    }
}
