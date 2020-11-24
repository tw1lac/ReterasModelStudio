package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.animedit.TimeEnvironmentImpl;
import com.hiveworkshop.wc3.gui.animedit.TimeSliderPanel;
import com.hiveworkshop.wc3.gui.modeledit.ModeButton;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.creator.CreatorModelingPanel;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.listener.ClonedNodeNamePicker;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarActionButtonType;
import com.hiveworkshop.wc3.gui.modeledit.toolbar.ToolbarButtonGroup;
import com.hiveworkshop.wc3.mdl.Animation;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

import javax.swing.*;
import java.util.List;

public class ModelPanelUgg {
    JScrollPane geoControl;
    JScrollPane geoControlModelData;

    public ModelPanelUgg(){
        geoControl = null;
        geoControlModelData = null;
    }

    /**
     * Returns the MDLDisplay associated with a given MDL, or null if one cannot be
     * found.
     */
    public static ModelPanel displayFor(List<ModelPanel> modelPanels, final EditableModel model) {
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

    public void loadModel(MainPanel mainPanel, final boolean temporary, final boolean selectNewTab, final ModelPanel temp) {
        if (temporary) {
            temp.getModelViewManager().getModel().setTemp(true);
        }

        final ModelPanel modelPanel = temp;
        final JMenuItem menuItem = new JMenuItem(temp.getModel().getName());
        menuItem.setIcon(temp.getIcon());
        mainPanel.menuBar.windowMenu.add(menuItem);
        menuItem.addActionListener(e -> setCurrentModel(mainPanel, modelPanel));
        temp.setJMenuItem(menuItem);
        temp.getModelViewManager().addStateListener(new RepaintingModelStateListener(mainPanel));
        temp.changeActivity(mainPanel.currentActivity);

        if (geoControl == null) {
            geoControl = new JScrollPane(temp.getModelViewManagingTree());
            mainPanel.mainLayoutUgg.editTab.viewportControllerWindowView.setComponent(geoControl);
            mainPanel.mainLayoutUgg.editTab.viewportControllerWindowView.repaint();
            geoControlModelData = new JScrollPane(temp.getModelComponentBrowserTree());
            mainPanel.mainLayoutUgg.modelDataView.setComponent(geoControlModelData);
            mainPanel.mainLayoutUgg.modelComponentView.setComponent(temp.getComponentsPanel());
            mainPanel.mainLayoutUgg.modelDataView.repaint();
        }
        addTabForView(temp, selectNewTab);
        mainPanel.modelPanels.add(temp);

        // tabbedPane.addTab(f.getName().split("\\.")[0], icon, temp, f.getPath());
        // if (selectNewTab) {
        // tabbedPane.setSelectedComponent(temp);
        // }
        if (temporary) {
            temp.getModelViewManager().getModel().setFileRef(null);
        }
        // }
        // }).start();
        mainPanel.menuBar.toolsMenu.setEnabled(true);

        if (selectNewTab && (mainPanel.prefs.getQuickBrowse() != null) && mainPanel.prefs.getQuickBrowse()) {
            for (int i = (mainPanel.modelPanels.size() - 2); i >= 0; i--) {
                final ModelPanel openModelPanel = mainPanel.modelPanels.get(i);
                if (openModelPanel.getUndoManager().isRedoListEmpty()
                        && openModelPanel.getUndoManager().isUndoListEmpty()) {
                    if (openModelPanel.close(mainPanel)) {
                        mainPanel.modelPanels.remove(openModelPanel);
                        mainPanel.menuBar.windowMenu.remove(openModelPanel.getMenuItem());
                    }
                }
            }
        }
    }

    public static void addTabForView(final ModelPanel view, final boolean selectNewTab) {
        if (selectNewTab) {
            view.getMenuItem().doClick();
        }
    }

    public void setCurrentModel(MainPanel mainPanel, final ModelPanel modelContextManager) {
        mainPanel.currentModelPanel = modelContextManager;
        if (mainPanel.currentModelPanel == null) {
            final JPanel jPanel = new JPanel();
            jPanel.add(new JLabel("..."));
            mainPanel.mainLayoutUgg.editTab.viewportControllerWindowView.setComponent(jPanel);
            geoControl = null;

            mainPanel.mainLayoutUgg.editTab.frontView.setComponent(new JPanel());
            mainPanel.mainLayoutUgg.editTab.bottomView.setComponent(new JPanel());
            mainPanel.mainLayoutUgg.editTab.leftView.setComponent(new JPanel());
            mainPanel.mainLayoutUgg.editTab.perspectiveView.setComponent(new JPanel());

            mainPanel.mainLayoutUgg.previewView.setComponent(new JPanel());
            mainPanel.mainLayoutUgg.animationControllerView.setComponent(new JPanel());

            refreshAnimationModeState(mainPanel.actionTypeGroup,
                    mainPanel.animatedRenderEnvironment,
                    mainPanel.mainLayoutUgg.editTab.animationModeButton,
                    mainPanel.animationModeState,
                    mainPanel.mainLayoutUgg.editTab.creatorPanel,
                    mainPanel.currentModelPanel,
                    mainPanel.prefs,
                    mainPanel.mainLayoutUgg.editTab.setKeyframe,
                    mainPanel.mainLayoutUgg.editTab.setTimeBounds,
                    mainPanel.snapButton,
                    mainPanel.mainLayoutUgg.editTab.timeSliderPanel);

            mainPanel.mainLayoutUgg.editTab.timeSliderPanel.setUndoManager(null, mainPanel.animatedRenderEnvironment);
            mainPanel.mainLayoutUgg.editTab.timeSliderPanel.setModelView(null);
            mainPanel.mainLayoutUgg.editTab.creatorPanel.setModelEditorManager(null);
            mainPanel.mainLayoutUgg.editTab.creatorPanel.setCurrentModel(null);
            mainPanel.mainLayoutUgg.editTab.creatorPanel.setUndoManager(null);
            mainPanel.mainLayoutUgg.modelComponentView.setComponent(new JPanel());
            geoControlModelData = null;
        } else {
            geoControl.setViewportView(mainPanel.currentModelPanel.getModelViewManagingTree());
            geoControl.repaint();

            mainPanel.mainLayoutUgg.editTab.frontView.setComponent(modelContextManager.getFrontArea());
            mainPanel.mainLayoutUgg.editTab.bottomView.setComponent(modelContextManager.getBotArea());
            mainPanel.mainLayoutUgg.editTab.leftView.setComponent(modelContextManager.getSideArea());
            mainPanel.mainLayoutUgg.editTab.perspectiveView.setComponent(modelContextManager.getPerspArea());

            mainPanel.mainLayoutUgg.previewView.setComponent(modelContextManager.getAnimationViewer());
            mainPanel.mainLayoutUgg.animationControllerView.setComponent(modelContextManager.getAnimationController());

            refreshAnimationModeState(mainPanel.actionTypeGroup,
                    mainPanel.animatedRenderEnvironment,
                    mainPanel.mainLayoutUgg.editTab.animationModeButton,
                    mainPanel.animationModeState,
                    mainPanel.mainLayoutUgg.editTab.creatorPanel,
                    mainPanel.currentModelPanel,
                    mainPanel.prefs,
                    mainPanel.mainLayoutUgg.editTab.setKeyframe,
                    mainPanel.mainLayoutUgg.editTab.setTimeBounds,
                    mainPanel.snapButton,
                    mainPanel.mainLayoutUgg.editTab.timeSliderPanel);

            mainPanel.mainLayoutUgg.editTab.timeSliderPanel.setUndoManager(mainPanel.currentModelPanel.getUndoManager(), mainPanel.animatedRenderEnvironment);
            mainPanel.mainLayoutUgg.editTab.timeSliderPanel.setModelView(mainPanel.currentModelPanel.getModelViewManager());
            mainPanel.mainLayoutUgg.editTab.creatorPanel.setModelEditorManager(mainPanel.currentModelPanel.getModelEditorManager());
            mainPanel.mainLayoutUgg.editTab.creatorPanel.setCurrentModel(mainPanel.currentModelPanel.getModelViewManager());
            mainPanel.mainLayoutUgg.editTab.creatorPanel.setUndoManager(mainPanel.currentModelPanel.getUndoManager());
            mainPanel.mainLayoutUgg.modelComponentView.setComponent(mainPanel.currentModelPanel.getComponentsPanel());

            geoControlModelData.setViewportView(mainPanel.currentModelPanel.getModelComponentBrowserTree());

            geoControlModelData.repaint();
            mainPanel.currentModelPanel.getModelComponentBrowserTree().reloadFromModelView();
        }
        mainPanel.activeViewportWatcher.viewportChanged(null);
        mainPanel.mainLayoutUgg.editTab.timeSliderPanel.revalidateKeyframeDisplay();
    }

    static void duplicateSelection(ClonedNodeNamePicker namePicker, ModelPanel currentModelPanel1) {
        // final int x = JOptionPane.showConfirmDialog(this,
        // "This is an irreversible process that will split selected
        // vertices into many copies of themself, one for each face, so
        // you can wrap textures and normals in a different
        // way.\n\nContinue?",
        // "Warning"/* : Divide Vertices" */,
        // JOptionPane.OK_CANCEL_OPTION);
        // if (x == JOptionPane.OK_OPTION) {
        final ModelPanel currentModelPanel = currentModelPanel1;
        if (currentModelPanel != null) {
            currentModelPanel.getUndoManager().pushAction(currentModelPanel.getModelEditorManager()
                    .getModelEditor().cloneSelectedComponents(namePicker));
        }
        // }
    }

    public static void refreshAnimationModeState(ToolbarButtonGroup<ToolbarActionButtonType> actionTypeGroup, TimeEnvironmentImpl animatedRenderEnvironment, ModeButton animationModeButton, boolean animationModeState, CreatorModelingPanel creatorPanel, ModelPanel currentModelPanel, ProgramPreferences prefs, JButton setKeyframe, JButton setTimeBounds, JButton snapButton, TimeSliderPanel timeSliderPanel) {
        if (animationModeState) {
            if ((currentModelPanel != null) && (currentModelPanel.getModel() != null)) {
                if (currentModelPanel.getModel().getAnimsSize() > 0) {
                    final Animation anim = currentModelPanel.getModel().getAnim(0);
                    animatedRenderEnvironment.setBounds(anim.getStart(), anim.getEnd());
                }
                refreshAndUpdateNodes(animatedRenderEnvironment, currentModelPanel);

                timeSliderPanel.setNodeSelectionManager(
                        currentModelPanel.getModelEditorManager().getNodeAnimationSelectionManager());
            }
            if ((actionTypeGroup.getActiveButtonType() == actionTypeGroup.getToolbarButtonTypes()[3])
                    || (actionTypeGroup.getActiveButtonType() == actionTypeGroup.getToolbarButtonTypes()[4])) {
                actionTypeGroup.setToolbarButtonType(actionTypeGroup.getToolbarButtonTypes()[0]);
            }
        }
        animatedRenderEnvironment.setStaticViewMode(!animationModeState);
        if (!animationModeState) {
            if ((currentModelPanel != null) && (currentModelPanel.getModel() != null)) {
                refreshAndUpdateNodes(animatedRenderEnvironment, currentModelPanel);
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

    private static void refreshAndUpdateNodes(TimeEnvironmentImpl animatedRenderEnvironment, ModelPanel currentModelPanel) {
        currentModelPanel.getEditorRenderModel().refreshFromEditor(animatedRenderEnvironment, MainPanel.IDENTITY,
                MainPanel.IDENTITY, MainPanel.IDENTITY, currentModelPanel.getPerspArea().getViewport());
        currentModelPanel.getEditorRenderModel().updateNodes(true, false); // update to 0 position
    }

    void reloadGeosetManagers(MainPanel mainPanel, final ModelPanel display) {
        geoControl.repaint();
        display.getModelViewManagingTree().reloadFromModelView();
        geoControl.setViewportView(display.getModelViewManagingTree());
        reloadComponentBrowser(geoControlModelData, display);
        display.getPerspArea().reloadTextures();// .mpanel.perspArea.reloadTextures();//addGeosets(newGeosets);
        display.getAnimationViewer().reload();
        display.getAnimationController().reload();
        mainPanel.mainLayoutUgg.editTab.creatorPanel.reloadAnimationList();

        display.getEditorRenderModel().refreshFromEditor(mainPanel.animatedRenderEnvironment, MainPanel.IDENTITY, MainPanel.IDENTITY, MainPanel.IDENTITY,
                display.getPerspArea().getViewport());
    }

    static void reloadComponentBrowser(JScrollPane geoControlModelData, final ModelPanel display) {
        geoControlModelData.repaint();
        display.getModelComponentBrowserTree().reloadFromModelView();
        geoControlModelData.setViewportView(display.getModelComponentBrowserTree());
    }

    public void refreshController() {
        if (geoControl != null) {
            geoControl.repaint();
        }
        if (geoControlModelData != null) {
            geoControlModelData.repaint();
        }
    }

}
