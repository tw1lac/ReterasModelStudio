package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv;

import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;

public interface TVertexModelElementRenderer {
	void renderFace(Color borderColor, Color color, Vec3 a, Vec3 b, Vec3 c);

	void renderVertex(Color color, Vec3 vertex);
}
