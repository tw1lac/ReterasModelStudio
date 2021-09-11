package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;

import static org.lwjgl.opengl.GL11.*;

public class SkeletonRenderer {
	private static Vec3[] corners = {
		new Vec3(), new Vec3(), new Vec3(), new Vec3(),
		new Vec3(), new Vec3(), new Vec3(), new Vec3(),
		new Vec3(), new Vec3(), new Vec3(), new Vec3()
	};
	private static Vec3[] normals = { new Vec3(), new Vec3(), new Vec3(), new Vec3(), new Vec3(), new Vec3() };
	private static float startSize = 0.4f;
	private static float middleSize = 1f;
	private static float endSize = 0.2f;
	private static float middle = 0.3f;

	private static Quat rotHeap = new Quat();
	private static Quat rotHeap2 = new Quat();

	public static void renderSkeleton(VertexBuffers renderBuffers, ModelView modelView, RenderModel renderModel, CameraHandler cameraHandler) {
		var idObjects = modelView.getVisibleIdObjects();
		int renderedObjects = 0;

		// Objects * 10 quads * 2 triangles * 3 vertices.
		renderBuffers.ensureCapacity(idObjects.size() * 60);

		for (var idObject : idObjects) {
			var renderNode = renderModel.getRenderNode(idObject);
			if (renderNode != null) {
				int offset = renderedObjects * 60;
				var renderPos = renderNode.getPivot();
				var renderRot = renderNode.getWorldRotation();
				float length = 0.0f;

				var parentNode = renderModel.getRenderNode(idObject.getParent());
				if (parentNode != null) {
					length = renderPos.distance(parentNode.getPivot());
				}

				var color = getIdObjectColor(modelView, idObject);
				float middleLength = length * middle;

				// Start cap.
				corners[0].set(-startSize, startSize, 0);
				corners[1].set(-startSize, -startSize, 0);
				corners[2].set(startSize, -startSize, 0);
				corners[3].set(startSize, startSize, 0);

				// Middle cap.
				corners[4].set(-middleSize, middleSize, middleLength);
				corners[5].set(-middleSize, -middleSize, middleLength);
				corners[6].set(middleSize, -middleSize, middleLength);
				corners[7].set(middleSize, middleSize, middleLength);

				// End cap.
				corners[8].set(-endSize, endSize, length);
				corners[9].set(-endSize, -endSize, length);
				corners[10].set(endSize, -endSize, length);
				corners[11].set(endSize, endSize, length);

				rotHeap2.setFromAxisAngle2(0, 0, 1, (float) Math.PI / 2);
				for (int i = 0; i < 12; i++) {
					rotHeap.set(renderRot).mul(rotHeap2);

					corners[i].rotate(Vec3.ZERO, parentNode.getWorldRotation()).add(renderPos);
				}

				normals[0].set(Vec3.X_AXIS);
				normals[1].set(Vec3.NEGATIVE_X_AXIS);
				normals[2].set(Vec3.Y_AXIS);
				normals[3].set(Vec3.NEGATIVE_Y_AXIS);
				normals[4].set(Vec3.Z_AXIS);
				normals[5].set(Vec3.NEGATIVE_Z_AXIS);

				for (int i = 0; i < 6; i++) {
					normals[i].rotate(Vec3.ZERO, renderRot);
				}

				// Start cap.
				renderQuad(renderBuffers, offset + 0, corners[3], corners[2], corners[1], corners[0], normals[5], color);

				// Start sides.
				renderQuad(renderBuffers, offset + 6, corners[0], corners[1], corners[5], corners[4], normals[0], color);
				renderQuad(renderBuffers, offset + 12, corners[1], corners[2], corners[6], corners[5], normals[3], color);
				renderQuad(renderBuffers, offset + 18, corners[2], corners[3], corners[7], corners[6], normals[1], color);
				renderQuad(renderBuffers, offset + 24, corners[3], corners[0], corners[4], corners[7], normals[2], color);

				// End sides.
				renderQuad(renderBuffers, offset + 30, corners[4], corners[5], corners[9], corners[8], normals[0], color);
				renderQuad(renderBuffers, offset + 36, corners[5], corners[6], corners[10], corners[9], normals[3], color);
				renderQuad(renderBuffers, offset + 42, corners[6], corners[7], corners[11], corners[10], normals[1], color);
				renderQuad(renderBuffers, offset + 48, corners[7], corners[4], corners[8], corners[11], normals[2], color);

				// End cap.
				renderQuad(renderBuffers, offset + 54, corners[8], corners[9], corners[10], corners[11], normals[4], color);

				renderedObjects++;
			}
		}

		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glVertexPointer(3, 0, renderBuffers.getPositions());
		glNormalPointer(0, renderBuffers.getNormals());
		glColorPointer(4, 0, renderBuffers.getColors());

		glDrawArrays(GL_TRIANGLES, 0, renderedObjects * 60);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}

	private static void renderQuad(VertexBuffers renderBuffers, int offset, Vec3 v0, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 normal, float[] color) {
		renderBuffers.setPosition(offset + 0, v0);
		renderBuffers.setPosition(offset + 1, v1);
		renderBuffers.setPosition(offset + 2, v2);

		renderBuffers.setPosition(offset + 3, v0);
		renderBuffers.setPosition(offset + 4, v2);
		renderBuffers.setPosition(offset + 5, v3);

		for (int i = 0; i < 6; i++) {
			renderBuffers.setNormal(offset + i, normal);
			renderBuffers.setColorRGBA(offset + i, color);
		}
	}

	private static float[] getIdObjectColor(ModelView modelView, IdObject idObject) {
		var colorPrefs = ProgramGlobals.getEditorColorPrefs();

		if (modelView.getHighlightedNode() == idObject) {
			return colorPrefs.getColorComponents(ColorThing.NODE_HIGHLIGHTED);
		} else if (modelView.isSelected(idObject)) {
			return colorPrefs.getColorComponents(ColorThing.NODE_SELECTED);
		} else if (modelView.isEditable(idObject)) {
			return colorPrefs.getColorComponents(ColorThing.NODE);
		} else {
			return colorPrefs.getColorComponents(ColorThing.NODE_UNEDITABLE);
		}
	}
}
