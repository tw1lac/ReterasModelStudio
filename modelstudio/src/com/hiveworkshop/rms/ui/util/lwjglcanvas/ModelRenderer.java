package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.ExtLog;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ModelRenderer extends RenderThing {
    Vec3 Y_AXIS = new Vec3(0, 1, 0);
    Vec3 Z_AXIS = new Vec3(0, 0, 1);

    Vec3 MOVE_X = new Vec3(10, 0, 0);
    Vec3 MOVE_Y = new Vec3(0, 10, 0);
    Vec3 MOVE_Z = new Vec3(0, 0, 10);

    //    Vec3 statCamPos = new Vec3(0f, -70f, -200f);
    Vec3 statCamPos = new Vec3(0f, 0f, -200f);
    Vec3 dynCamPos = new Vec3(0, 0, 0);

    EditableModel model;
    RenderModel renderModel;
    TimeEnvironmentImpl renderEnv;
    boolean animate;
    private GLXMoldel glxMoldel;

    private boolean ortho = false;
    private Vec3 cameraPos = new Vec3(0, 0, 0);
    private Quat inverseCameraRotation = new Quat();
    private Quat inverseCameraRotationYSpin = new Quat();
    private Quat inverseCameraRotationZSpin = new Quat();
    private double m_zoom = 1;
    private float xAngle;
    private float yAngle;
    private Point cameraPanStartPoint;
    private Point cameraSpinStartPoint;
    private Point actStart;

    ExtLog currentExt = new ExtLog(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);
    ExtLog modelExtent = new ExtLog(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);

    public ModelRenderer(EditableModel model, RenderModel renderModel, TimeEnvironmentImpl renderEnv) {
        this.model = model;
        this.renderModel = renderModel;
        this.renderEnv = renderEnv;
        setCurrentExtent();
        loadDefaultCameraFor();
    }

    void loadDefaultCameraFor() {
        double boundsRadius = getBoundsRadius();

//		m_zoom = 128 / (boundsRadius * 1.3) * getWidth()/200;
        m_zoom = 128 / (boundsRadius * 1.3);
        cameraPos.y -= boundsRadius / 4;
        yAngle += 35;

        calculateCameraRotation();
    }

    public ModelRenderer setAnimate(boolean animate) {
        this.animate = animate;
        return this;
    }

    public void init() {
        glxMoldel = new GLXMoldel(model, renderModel);
        glxMoldel.setZoom(m_zoom);
//        glxMoldel.setCameraPos(cameraPos);
//        System.out.println("ugg-zoom");
        glxMoldel.setCameraPos(getAdjustedCameraPos());
//        System.out.println("ugg-cam");
    }

    private Vec3 getAdjustedCameraPos() {
        System.out.println("ugg-dynPos");
        return dynCamPos.set(cameraPos).scale((float) m_zoom).add(statCamPos);
//        return Vec3.getScaled(cameraPos, (float) m_zoom).sub(statCamPos);
    }

    public void render(int width, int height) {
        if (animate || renderEnv.isLive()) {
            renderEnv.updateAnimationTime();
            renderModel.updateNodes(false, false);
        }

        int scale = 1;
        if (glxMoldel != null) {
            glxMoldel.setSize(width * scale, height * scale);
            glxMoldel.render();
            glxMoldel.animate();
        }
    }

    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_X) {
            glxMoldel.angleX = (glxMoldel.angleX + 90) % 360;
            System.out.println("angleX: " + glxMoldel.angleX);

        } else if (e.getKeyCode() == KeyEvent.VK_Y) {
            glxMoldel.angleY = (glxMoldel.angleY + 90) % 360;
            System.out.println("angleY: " + glxMoldel.angleY);

        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
            glxMoldel.angleZ = (glxMoldel.angleZ + 90) % 360;
            System.out.println("angleZ: " + glxMoldel.angleZ);
        }

        if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
            // Top view
            System.out.println("VK_NUMPAD7, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
            Vec3 maxExt = resetZoom();
//            setViewportCamera(0, (int) -(maxExt.length() * .54), 0, 90, 0, 0);
            setViewportCamera(0, (int) -(maxExt.length() / 6), 0, 90, 0, 0);
        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
            // Front view
            System.out.println("VK_NUMPAD1, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
            Vec3 maxExt = resetZoom();
            setViewportCamera(0, (int) -(maxExt.length() / 6), 0, 0, 0, 0);
        }
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
            // Side view
            System.out.println("VK_NUMPAD3, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
            Vec3 maxExt = resetZoom();
            setViewportCamera(0, (int) -(maxExt.length() / 6), 0, 0, 0, 90);
        }
        if (e.getKeyCode() == KeyEvent.VK_O) {
            // Orto Mode
            ortho = !ortho;
            System.out.println("VK_O, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_PLUS) {
            m_zoom *= 1.15;
            glxMoldel.setZoom(m_zoom);
            System.out.println("VK_PLUS, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS) {
            m_zoom /= 1.15;
            glxMoldel.setZoom(m_zoom);
            System.out.println("VK_MINUS, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            cameraPos.sub(MOVE_X);
            glxMoldel.setCameraPos(getAdjustedCameraPos());
            System.out.println("VK_LEFT, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            cameraPos.add(MOVE_X);
            glxMoldel.setCameraPos(getAdjustedCameraPos());
            System.out.println("VK_RIGHT, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            cameraPos.sub(MOVE_Y);
            glxMoldel.setCameraPos(getAdjustedCameraPos());
            System.out.println("VK_UP, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            cameraPos.add(MOVE_Y);
            glxMoldel.setCameraPos(getAdjustedCameraPos());
            System.out.println("VK_DOWN, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            cameraPos.sub(MOVE_Z);
            glxMoldel.setCameraPos(getAdjustedCameraPos());
            System.out.println("VK_PAGE_UP, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
        if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            cameraPos.add(MOVE_Z);
            glxMoldel.setCameraPos(getAdjustedCameraPos());
            System.out.println("VK_PAGE_DOWN, pos: " + dynCamPos + ", zoom: " + m_zoom + ", (" + cameraPos + ")");
        }
    }

    private void setViewportCamera(int x, int y, int z, int rX, int rY, int rZ) {
        cameraPanStartPoint = null;
        cameraSpinStartPoint = null;
        actStart = null;

        xAngle = rX;
        yAngle = rY;

        calculateCameraRotation();

//        Vec3 vertexHeap = new Vec3(-x, y, -z);
        cameraPos.set(x, y, z);

        glxMoldel.setCameraPos(getAdjustedCameraPos());
        glxMoldel.setAngleX(270 + rX);
        glxMoldel.setAngleY(rY);
        glxMoldel.setAngleZ(270 + rZ);
    }

    public Vec3 resetZoom() {
        Vec3 maxExt = currentExt.getMaximumExtent();
        m_zoom = 128 / (maxExt.length());
        glxMoldel.setZoom(m_zoom);
        return maxExt;
    }

    private void calculateCameraRotation() {
        inverseCameraRotationYSpin.setFromAxisAngle(Y_AXIS, (float) Math.toRadians(yAngle)).invertRotation();
        inverseCameraRotationZSpin.setFromAxisAngle(Z_AXIS, (float) Math.toRadians(xAngle)).invertRotation();

        inverseCameraRotation.set(inverseCameraRotationYSpin).mul(inverseCameraRotationZSpin).invertRotation();
    }

    private void setCurrentExtent() {
        modelExtent.setMinMax(model.getExtents());
        if ((renderEnv.getCurrentAnimation() != null) && renderEnv.getCurrentAnimation().getExtents() != null) {
            currentExt = renderEnv.getCurrentAnimation().getExtents();
        }
        if (currentExt.getMaximumExtent().distance(new Vec3(0, 0, 0)) < 1) {
            currentExt = modelExtent;
        }
    }


    private double getBoundsRadius() {
        ExtLog defaultAnimationExtent = new ExtLog(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);
        if ((renderEnv.getCurrentAnimation() != null) && renderEnv.getCurrentAnimation().getExtents() != null) {
            defaultAnimationExtent.setMinMax(renderEnv.getCurrentAnimation().getExtents());
        }
        ExtLog someExtent = new ExtLog(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);
        someExtent.setMinMax(defaultAnimationExtent);
        someExtent.setMinMax(modelExtent);

        double boundsRadius = 64;
        if (someExtent.hasBoundsRadius() && (someExtent.getBoundsRadius() > 1)) {
            final double extBoundRadius = someExtent.getBoundsRadius();
            if (extBoundRadius > boundsRadius) {
                boundsRadius = extBoundRadius;
            }
        }
        if ((someExtent.getMaximumExtent() != null) && (someExtent.getMaximumExtent() != null)) {
            final double minMaxBoundRadius = someExtent.getMaximumExtent().distance(someExtent.getMinimumExtent()) / 2;
            if (minMaxBoundRadius > boundsRadius) {
                boundsRadius = minMaxBoundRadius;
            }
        }
        if ((boundsRadius > 10000) || (boundsRadius < 0.1)) {
            boundsRadius = 64;
        }

        return boundsRadius;
    }
}
