package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import java.awt.event.KeyEvent;

public class GearRenderer extends RenderThing {


    private GLXGears gears;

    boolean animate;

    public GearRenderer() {
    }

    public GearRenderer setAnimate(boolean animate) {
        this.animate = animate;
        return this;
    }

    public void init() {
        gears = new GLXGears();
    }

    public void render(int width, int height) {
        int scale = 1;
        if (gears != null) {
            gears.setSize(width * scale, height * scale);
            gears.render();
            gears.animate();
        }
    }

	public void onKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_X) {
//            gears.angleX = (gears.angleX + 90)%360;
//            System.out.println("angleX: " + gears.angleX);

		} else if (e.getKeyCode() == KeyEvent.VK_Y) {
//            gears.angleY = (gears.angleY + 90)%360;
//            System.out.println("angleY: " + gears.angleY);

		} else if (e.getKeyCode() == KeyEvent.VK_Z) {
//            gears.angleZ = (gears.angleZ + 90)%360;
//            System.out.println("angleZ: " + gears.angleZ);
		}

	}

	public void cleanup() {

	}
}
