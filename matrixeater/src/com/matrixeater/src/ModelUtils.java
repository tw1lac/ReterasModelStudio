package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.FaceCreationException;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;
import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.Viewport;
import com.hiveworkshop.wc3.mdl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ModelUtils {

    static GeosetVertex addGeosetAndTVerticies(Geoset newGeoset, int vX, int vY, int vZ, int tX, int tY) {
        final GeosetVertex vertex = new GeosetVertex(vX, (double) vY / 2, vZ, new Normal(0, 0, 1));
        final TVertex tVert = new TVertex(tX, tY);
        vertex.addTVertex(tVert);
        newGeoset.add(vertex);
        vertex.setGeoset(newGeoset);
        return vertex;
    }

    static AbstractAction getCreateFaceShortcut(final MainPanel mainPanel) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component focusedComponent = mainPanel.getFocusedComponent();
                if (mainPanel.focusedComponentNeedsTyping(focusedComponent)) {
                    return;
                }
                if (!mainPanel.animationModeState) {
                    try {
                        final ModelPanel modelPanel = mainPanel.currentModelPanel;
                        if (modelPanel != null) {
                            final Viewport viewport = mainPanel.activeViewportWatcher.getViewport();
                            final Vertex facingVector = viewport == null ? new Vertex(0, 0, 1)
                                    : viewport.getFacingVector();
                            final UndoAction createFaceFromSelection = modelPanel.getModelEditorManager()
                                    .getModelEditor().createFaceFromSelection(facingVector);
                            modelPanel.getUndoManager().pushAction(createFaceFromSelection);
                        }
                    } catch (final FaceCreationException exc) {
                        JOptionPane.showMessageDialog(mainPanel, exc.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (final Exception exc) {
                        ExceptionPopup.display(exc);
                    }
                }
            }
        };
    }
}
