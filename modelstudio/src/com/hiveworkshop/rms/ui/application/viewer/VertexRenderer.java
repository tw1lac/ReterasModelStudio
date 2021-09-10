package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.ui.preferences.EditorColorPrefs;
import com.hiveworkshop.rms.util.Vec3;

import static org.lwjgl.opengl.GL11.*;

public class VertexRenderer {
	private static VertexBuffers renderBuffers = new VertexBuffers();
	private static Vec3[] corners = {
		new Vec3(0, 1, 1),
		new Vec3(0, -1, 1),
		new Vec3(0, -1, -1),
		new Vec3(0, 1, -1),
	};
	private static Vec3[] computedCorners = {
			new Vec3(),
			new Vec3(),
			new Vec3(),
			new Vec3()
	};
	private static float rectSize = 0.125f ;

	public static void renderVertices(ModelView modelView, Geoset geo, CameraHandler cameraHandler) {
		var vertices = geo.getVertices();
		var inverseRotation = cameraHandler.getActualInverseCameraRotation();
		int renderedVertices = 0;

		renderBuffers.ensureCapacity(vertices.size() * 6);

		glPolygonMode(GL_FRONT, GL_FILL);
		glDisable(GL_DEPTH_TEST);

		for (GeosetVertex vertex : vertices) {
			int offset = renderedVertices * 6;
			float[] color = getVertexColor(modelView, vertex);

			for (int i = 0; i < 4; i++) {
				computedCorners[i].set(corners[i]).transform(inverseRotation).scale(rectSize).add(vertex.renderVert.getRenderPos());
			}

			renderBuffers.setPosition(offset + 0, computedCorners[0]);
			renderBuffers.setPosition(offset + 1, computedCorners[1]);
			renderBuffers.setPosition(offset + 2, computedCorners[2]);

			renderBuffers.setPosition(offset + 3, computedCorners[0]);
			renderBuffers.setPosition(offset + 4, computedCorners[2]);
			renderBuffers.setPosition(offset + 5, computedCorners[3]);

			for (int i = 0; i < 6; i++) {
				renderBuffers.setColorRGBA(offset + i, color);
			}

			renderedVertices++;
		}

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glVertexPointer(3, 0, renderBuffers.getPositions());
		glColorPointer(4, 0, renderBuffers.getColors());

		glDrawArrays(GL_TRIANGLES, 0, renderedVertices * 6);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}

	public static float[] getVertexColor(ModelView modelView, GeosetVertex vertex) {
		EditorColorPrefs colorPrefs = ProgramGlobals.getEditorColorPrefs();

		if (modelView.isSelected(vertex)) {
			return colorPrefs.getColorComponents(ColorThing.VERTEX_SELECTED);
		} else if (modelView.isEditable(vertex)) {
			return colorPrefs.getColorComponents(ColorThing.VERTEX);
		} else {
			return colorPrefs.getColorComponents(ColorThing.VERTEX_UNEDITABLE);
		}
	}
}
