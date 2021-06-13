package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import java.awt.event.KeyEvent;

public abstract class RenderThing {
    public abstract void init();

    public abstract void render(int x, int y);

    public void onKeyPressed(KeyEvent e) {
    }
}
