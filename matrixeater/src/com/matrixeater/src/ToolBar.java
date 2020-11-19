package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.activity.ModelEditorMultiManipulatorActivity;
import com.hiveworkshop.wc3.gui.modeledit.activity.ModelEditorViewportActivity;
import com.hiveworkshop.wc3.gui.modeledit.activity.UndoActionListener;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.ModelEditorManager;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.ModelEditorActionType;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.builder.model.*;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionMode;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarActionButtonType;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarButtonGroup;
import com.hiveworkshop.wc3.gui.modeledit.viewport.ViewportIconUtils;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.NoSuchElementException;

public class ToolBar {
    public static JToolBar createJToolBar(final MainPanel mainPanel) {
        mainPanel.toolbar = new JToolBar(JToolBar.HORIZONTAL);
        mainPanel.toolbar.setFloatable(false);

        addToolbarIcon(mainPanel.toolbar, "New", "new.png", () -> NewModelPanel.newModel(mainPanel));

        addToolbarIcon(mainPanel.toolbar, "Open", "open.png", () -> FileUtils.onClickOpen(mainPanel));

        addToolbarIcon(mainPanel.toolbar, "Save", "save.png", () -> FileUtils.onClickSave(mainPanel));

        mainPanel.toolbar.addSeparator();

        addToolbarIcon(mainPanel.toolbar, "Undo", "undo.png", mainPanel.undoAction);
        addToolbarIcon(mainPanel.toolbar, "Redo", "redo.png", mainPanel.redoAction);

        mainPanel.toolbar.addSeparator();
        mainPanel.selectionModeGroup = new ToolbarButtonGroup<>(mainPanel.toolbar, SelectionMode.values());
        mainPanel.toolbar.addSeparator();
        mainPanel.selectionItemTypeGroup = new ToolbarButtonGroup<>(mainPanel.toolbar, SelectionItemTypes.values());
        mainPanel.toolbar.addSeparator();

        //TODO stuff that should be created using functions
        ToolbarActionButtonType selectAndMoveDescriptor = new ToolbarActionButtonType(
                ViewportIconUtils.loadImageIcon("icons/actions/move2.png"), "Select and Move") {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView, final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.TRANSLATION;
                return new ModelEditorMultiManipulatorActivity(
                        new MoverWidgetManipulatorBuilder(modelEditorManager.getModelEditor(),
                                modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView),
                        undoActionListener, modelEditorManager.getSelectionView());
            }
        };
        ToolbarActionButtonType selectAndRotateDescriptor = new ToolbarActionButtonType(
                ViewportIconUtils.loadImageIcon("icons/actions/rotate.png"), "Select and Rotate") {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView, final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.ROTATION;
                return new ModelEditorMultiManipulatorActivity(
                        new RotatorWidgetManipulatorBuilder(modelEditorManager.getModelEditor(),
                                modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView),
                        undoActionListener, modelEditorManager.getSelectionView());
            }
        };
        ToolbarActionButtonType selectAndScaleDescriptor = new ToolbarActionButtonType(
                ViewportIconUtils.loadImageIcon("icons/actions/scale.png"), "Select and Scale") {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView, final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.SCALING;
                return new ModelEditorMultiManipulatorActivity(
                        new ScaleWidgetManipulatorBuilder(modelEditorManager.getModelEditor(),
                                modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView),
                        undoActionListener, modelEditorManager.getSelectionView());
            }
        };
        ToolbarActionButtonType selectAndExtrudeDescriptor = new ToolbarActionButtonType(
                ViewportIconUtils.loadImageIcon("icons/actions/extrude.png"), "Select and Extrude") {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView, final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.TRANSLATION;
                return new ModelEditorMultiManipulatorActivity(
                        new ExtrudeWidgetManipulatorBuilder(modelEditorManager.getModelEditor(),
                                modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView),
                        undoActionListener, modelEditorManager.getSelectionView());
            }
        };
        ToolbarActionButtonType selectAndExtendDescriptor = new ToolbarActionButtonType(
                ViewportIconUtils.loadImageIcon("icons/actions/extend.png"), "Select and Extend") {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView, final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.TRANSLATION;
                return new ModelEditorMultiManipulatorActivity(
                        new ExtendWidgetManipulatorBuilder(modelEditorManager.getModelEditor(),
                                modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView),
                        undoActionListener, modelEditorManager.getSelectionView());
            }
        };
        mainPanel.actionTypeGroup = new ToolbarButtonGroup<>(mainPanel.toolbar,
                new ToolbarActionButtonType[]{selectAndMoveDescriptor, selectAndRotateDescriptor,
                        selectAndScaleDescriptor, selectAndExtrudeDescriptor, selectAndExtendDescriptor,});
        mainPanel.currentActivity = mainPanel.actionTypeGroup.getActiveButtonType();
        mainPanel.toolbar.addSeparator();
        mainPanel.snapButton = mainPanel.toolbar.add(new AbstractAction("Snap", ViewportIconUtils.loadImageIcon("icons/actions/snap.png")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    final ModelPanel currentModelPanel = ModelPanelUgg.currentModelPanel(mainPanel.currentModelPanel);
                    if (currentModelPanel != null) {
                        currentModelPanel.getUndoManager().pushAction(
                                currentModelPanel.getModelEditorManager().getModelEditor().snapSelectedVertices());
                    }
                } catch (final NoSuchElementException exc) {
                    JOptionPane.showMessageDialog(mainPanel, "Nothing to undo!");
                } catch (final Exception exc) {
                    ExceptionPopup.display(exc);
                }
            }
        });

        return mainPanel.toolbar;
    }

    static void addToolbarIcon(JToolBar toolbar, String hooverText, String icon, AbstractAction action) {
        JButton button = new JButton(ViewportIconUtils.loadImageIcon("icons/actions/" + icon));
        button.setToolTipText(hooverText);
        button.addActionListener(action);
        toolbar.add(button);
    }

    static void addToolbarIcon(JToolBar toolbar, String hooverText, String icon, Runnable function) {
        AbstractAction action = new AbstractAction(hooverText) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    function.run();
                } catch (final Exception exc) {
                    exc.printStackTrace();
                    ExceptionPopup.display(exc);
                }
            }
        };

        JButton button = new JButton(ViewportIconUtils.loadImageIcon("icons/actions/" + icon));
        button.setToolTipText(hooverText);
        button.addActionListener(action);
        toolbar.add(button);
    }
}
