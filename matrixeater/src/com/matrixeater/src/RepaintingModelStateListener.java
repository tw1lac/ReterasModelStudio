package com.matrixeater.src;

import com.hiveworkshop.wc3.mdl.Camera;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.IdObject;
import com.hiveworkshop.wc3.mdl.v2.ModelViewStateListener;

import javax.swing.*;

public class RepaintingModelStateListener implements ModelViewStateListener {
    private final JComponent component;

    public RepaintingModelStateListener(final JComponent component) {
        this.component = component;
    }

    @Override
    public void idObjectVisible(final IdObject bone) {
        component.repaint();
    }

    @Override
    public void idObjectNotVisible(final IdObject bone) {
        component.repaint();
    }

    @Override
    public void highlightGeoset(final Geoset geoset) {
        component.repaint();
    }

    @Override
    public void geosetVisible(final Geoset geoset) {
        component.repaint();
    }

    @Override
    public void geosetNotVisible(final Geoset geoset) {
        component.repaint();
    }

    @Override
    public void geosetNotEditable(final Geoset geoset) {
        component.repaint();
    }

    @Override
    public void geosetEditable(final Geoset geoset) {
        component.repaint();
    }

    @Override
    public void cameraVisible(final Camera camera) {
        component.repaint();
    }

    @Override
    public void cameraNotVisible(final Camera camera) {
        component.repaint();
    }

    @Override
    public void unhighlightGeoset(final Geoset geoset) {
        component.repaint();
    }

    @Override
    public void highlightNode(final IdObject node) {
        component.repaint();
    }

    @Override
    public void unhighlightNode(final IdObject node) {
        component.repaint();
    }

}
