package com.hiveworkshop.rms.ui.util.lwjglcanvas;

public class GearRenderer extends RenderThing {


    private GLXGears gears;

    public GearRenderer() {

    }

    public void init() {
        gears = new GLXGears();
    }

    public void render(int width, int height) {

        if (gears != null) {
            gears.setSize(width, height);
            gears.render();
            gears.animate();
        }
    }
}
