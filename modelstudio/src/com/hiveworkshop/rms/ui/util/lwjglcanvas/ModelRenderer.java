package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;

import java.awt.event.KeyEvent;

public class ModelRenderer extends RenderThing {


    EditableModel model;
    RenderModel renderModel;
    TimeEnvironmentImpl renderEnv;
    boolean animate;
    private GLXMoldel glxMoldel;

    public ModelRenderer(EditableModel model, RenderModel renderModel, TimeEnvironmentImpl renderEnv) {
        this.model = model;
        this.renderModel = renderModel;
        this.renderEnv = renderEnv;
    }

    public ModelRenderer setAnimate(boolean animate) {
        this.animate = animate;
        return this;
    }

    public void init() {
        glxMoldel = new GLXMoldel(model, renderModel);
    }

    public void render(int width, int height) {
        if (animate) {
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

    }
}
