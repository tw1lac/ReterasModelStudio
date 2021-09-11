package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.util.Vec3;

import static org.lwjgl.opengl.GL11.*;

public class VertexRenderer {
	private static Vec3[] corners = {
		new Vec3(0, 1, 1),
		new Vec3(0, -1, 1),
		new Vec3(0, -1, -1),
		new Vec3(0, 1, -1),
	};
	private static Vec3[] computedCorners = { new Vec3(), new Vec3(), new Vec3(), new Vec3() };
	private static Vec3[] worldCorners = { new Vec3(), new Vec3(), new Vec3(), new Vec3() };
	private static float rectSize = 0.125f ;

	public static void renderVertices(VertexBuffers renderBuffers, ModelView modelView, Geoset geo, CameraHandler cameraHandler) {
		var vertices = geo.getVertices();
		var inverseRotation = cameraHandler.getActualInverseCameraRotation();
		int renderedVertices = 0;

		for (int i = 0; i < 4; i++) {
			computedCorners[i].set(corners[i]).transform(inverseRotation).scale(rectSize);
		}

		// Vertices * 2 triangles * 3 vertices.
		renderBuffers.ensureCapacity(vertices.size() * 6);

		glPolygonMode(GL_FRONT, GL_FILL);
		glDisable(GL_DEPTH_TEST);

		for (GeosetVertex vertex : vertices) {
			for (int i = 0; i < 4; i++) {
				worldCorners[i].set(computedCorners[i]).add(vertex.renderVert.getRenderPos());
			}

			renderQuad(renderBuffers, renderedVertices * 6, worldCorners, getVertexColor(modelView, vertex));

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

	private static void renderQuad(VertexBuffers renderBuffers, int offset, Vec3[] v, float[] color) {
		renderBuffers.setPosition(offset + 0, v[0]);
		renderBuffers.setPosition(offset + 1, v[1]);
		renderBuffers.setPosition(offset + 2, v[2]);

		renderBuffers.setPosition(offset + 3, v[0]);
		renderBuffers.setPosition(offset + 4, v[2]);
		renderBuffers.setPosition(offset + 5, v[3]);

		for (int i = 0; i < 6; i++) {
			renderBuffers.setColorRGBA(offset + i, color);
		}
	}

	private static float[] getVertexColor(ModelView modelView, GeosetVertex vertex) {
		var colorPrefs = ProgramGlobals.getEditorColorPrefs();

		if (modelView.isSelected(vertex)) {
			return colorPrefs.getColorComponents(ColorThing.VERTEX_SELECTED);
		} else if (modelView.isEditable(vertex)) {
			return colorPrefs.getColorComponents(ColorThing.VERTEX);
		} else {
			return colorPrefs.getColorComponents(ColorThing.VERTEX_UNEDITABLE);
		}
	}
}
