package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.mdl.*;

import java.util.List;

//interface ModelReference {
//    EditableModel getModel();
//}

public class ModelStructureChangeListenerImplementation implements ModelStructureChangeListener {

    private MainPanel.ModelReference modelReference;
    private MainPanel mainPanel;

    public ModelStructureChangeListenerImplementation(MainPanel mainPanel, MainPanel.ModelReference modelReference) {
        this.modelReference = modelReference;
        this.mainPanel = mainPanel;
    }

    public ModelStructureChangeListenerImplementation(MainPanel mainPanel, final EditableModel model) {
        this.modelReference = () -> model;
        this.mainPanel = mainPanel;
    }

    @Override
    public void nodesRemoved(final List<IdObject> nodes) {
        // Tell program to set visibility after import
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            // display.setBeenSaved(false); // we edited the model
            // TODO notify been saved system, wherever that moves to
            for (final IdObject geoset : nodes) {
                display.getModelViewManager().makeIdObjectNotVisible(geoset);
            }
            ModelPanelUgg.reloadGeosetManagers(mainPanel, display);
        }
    }

    @Override
    public void nodesAdded(final List<IdObject> nodes) {
        // Tell program to set visibility after import
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            // display.setBeenSaved(false); // we edited the model
            // TODO notify been saved system, wherever that moves to
            for (final IdObject geoset : nodes) {
                display.getModelViewManager().makeIdObjectVisible(geoset);
            }
            ModelPanelUgg.reloadGeosetManagers(mainPanel, display);
            display.getEditorRenderModel().refreshFromEditor(mainPanel.animatedRenderEnvironment, MainPanel.IDENTITY, MainPanel.IDENTITY,
                    MainPanel.IDENTITY, display.getPerspArea().getViewport());
            display.getAnimationViewer().reload();
        }
    }

    @Override
    public void geosetsRemoved(final List<Geoset> geosets) {
        // Tell program to set visibility after import
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            // display.setBeenSaved(false); // we edited the model
            // TODO notify been saved system, wherever that moves to
            for (final Geoset geoset : geosets) {
                display.getModelViewManager().makeGeosetNotEditable(geoset);
                display.getModelViewManager().makeGeosetNotVisible(geoset);
            }
            ModelPanelUgg.reloadGeosetManagers(mainPanel, display);
        }
    }

    @Override
    public void geosetsAdded(final List<Geoset> geosets) {
        // Tell program to set visibility after import
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            // display.setBeenSaved(false); // we edited the model
            // TODO notify been saved system, wherever that moves to
            for (final Geoset geoset : geosets) {
                display.getModelViewManager().makeGeosetEditable(geoset);
                // display.getModelViewManager().makeGeosetVisible(geoset);
            }
            ModelPanelUgg.reloadGeosetManagers(mainPanel, display);
        }
    }

    @Override
    public void camerasAdded(final List<Camera> cameras) {
        // Tell program to set visibility after import
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            // display.setBeenSaved(false); // we edited the model
            // TODO notify been saved system, wherever that moves to
            for (final Camera camera : cameras) {
                display.getModelViewManager().makeCameraVisible(camera);
                // display.getModelViewManager().makeGeosetVisible(geoset);
            }
            ModelPanelUgg.reloadGeosetManagers(mainPanel, display);
        }
    }

    @Override
    public void camerasRemoved(final List<Camera> cameras) {
        // Tell program to set visibility after import
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            // display.setBeenSaved(false); // we edited the model
            // TODO notify been saved system, wherever that moves to
            for (final Camera camera : cameras) {
                display.getModelViewManager().makeCameraNotVisible(camera);
                // display.getModelViewManager().makeGeosetVisible(geoset);
            }
            ModelPanelUgg.reloadGeosetManagers(mainPanel, display);
        }
    }

    @Override
    public void timelineAdded(final TimelineContainer node, final AnimFlag timeline) {

    }

    @Override
    public void keyframeAdded(final TimelineContainer node, final AnimFlag timeline, final int trackTime) {
        mainPanel.mainLayoutUgg.editTab.timeSliderPanel.revalidateKeyframeDisplay();
    }

    @Override
    public void timelineRemoved(final TimelineContainer node, final AnimFlag timeline) {

    }

    @Override
    public void keyframeRemoved(final TimelineContainer node, final AnimFlag timeline, final int trackTime) {
        mainPanel.mainLayoutUgg.editTab.timeSliderPanel.revalidateKeyframeDisplay();
    }

    @Override
    public void animationsAdded(final List<Animation> animation) {
        mainPanel.currentModelPanel.getAnimationViewer().reload();
        mainPanel.currentModelPanel.getAnimationController().reload();
        mainPanel.mainLayoutUgg.editTab.creatorPanel.reloadAnimationList();
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, display);
        }
    }

    @Override
    public void animationsRemoved(final List<Animation> animation) {
        mainPanel.currentModelPanel.getAnimationViewer().reload();
        mainPanel.currentModelPanel.getAnimationController().reload();
        mainPanel.mainLayoutUgg.editTab.creatorPanel.reloadAnimationList();
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, display);
        }
    }

    @Override
    public void texturesChanged() {
        final ModelPanel modelPanel = mainPanel.currentModelPanel;
        if (modelPanel != null) {
            modelPanel.getAnimationViewer().reloadAllTextures();
            modelPanel.getPerspArea().reloadAllTextures();
        }
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, display);
        }
    }

    @Override
    public void headerChanged() {
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, display);
        }
    }

    @Override
    public void animationParamsChanged(final Animation animation) {
        mainPanel.currentModelPanel.getAnimationViewer().reload();
        mainPanel.currentModelPanel.getAnimationController().reload();
        mainPanel.mainLayoutUgg.editTab.creatorPanel.reloadAnimationList();
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, display);
        }
    }

    @Override
    public void globalSequenceLengthChanged(final int index, final Integer newLength) {
        mainPanel.currentModelPanel.getAnimationViewer().reload();
        mainPanel.currentModelPanel.getAnimationController().reload();
        mainPanel.mainLayoutUgg.editTab.creatorPanel.reloadAnimationList();
        final ModelPanel display = ModelPanelUgg.displayFor(mainPanel.modelPanels, modelReference.getModel());
        if (display != null) {
            ModelPanelUgg.reloadComponentBrowser(mainPanel.geoControlModelData, display);
        }
    }
}