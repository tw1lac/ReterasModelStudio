package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.render3d.RenderNode;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.renderparts.RenderGeoset;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.ui.preferences.EditorColorPrefs;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class CubePainter {
	static final Vec3 xAxis = new Vec3(1, 0, 0);
	static final Vec3 xAxis_isolate = new Vec3(0, 1, 1);
	static final Vec3 yAxis = new Vec3(0, 1, 0);
	static final Vec3 yAxis_isolate = new Vec3(1, 0, 1);
	static final Vec3 zAxis = new Vec3(0, 0, 1);
	static final Vec3 zAxis_isolate = new Vec3(1, 1, 0);
	static double A90 = (Math.PI/2.0);

	public static void paintRekt(Vec3 start, Vec3 end1, Vec3 end2, Vec3 end3, CameraHandler cameraHandler) {

//		glBegin(GL11.GL_TRIANGLES);
//		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		GL11.glDepthMask(false);
		glBegin(GL11.GL_LINES);

		float frnt = 1;

		glColor4f(1f, .2f, 1f, .7f);

		GL11.glNormal3f(0, frnt, 0);
		doGlQuad(start, end1, end2, end1);

		doGlQuad(end2, end3, start, end3);

		glEnd();
	}

	public static void paintBones4(ModelView modelView, RenderModel renderModel, IdObject idObject, CameraHandler cameraHandler, BoneRenderThing2 boneRenderThing) {
		RenderNode renderNode = renderModel.getRenderNode(idObject);
		if (renderNode != null) {

			Vec3 renderPosNode = renderNode.getPivot();
			RenderNode parentNode = renderModel.getRenderNode(idObject.getParent());
			if (idObject.getParent() != null && parentNode != null) {
				boneRenderThing.transform2(renderPosNode, parentNode.getPivot());
			} else {
				boneRenderThing.transform2(renderPosNode, renderPosNode);
			}

			EditorColorPrefs colorPrefs = ProgramGlobals.getEditorColorPrefs();

////			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
//			glPolygonMode(GL_FRONT_FACE, GL_FILL);
////			glPolygonMode(GL_CULL_FACE, GL_FILL);
//			glBegin(GL_QUADS);

			float[] components;
			if (modelView.getHighlightedNode() == idObject) {
				components = colorPrefs.getColorComponents(ColorThing.NODE_HIGHLIGHTED);
			} else if (modelView.isSelected(idObject)) {
				components = colorPrefs.getColorComponents(ColorThing.NODE_SELECTED);
			} else if (modelView.isEditable(idObject)) {
				components = colorPrefs.getColorComponents(ColorThing.NODE);
			} else {
				components = colorPrefs.getColorComponents(ColorThing.NODE_UNEDITABLE);
			}
//			glColor4f(components[0], components[1], components[2], components[3]);

//			if (modelView.isSelected(idObject)) {
//				glColor4f(1f, .0f, .0f, .7f);
//			} else if (modelView.isEditable(idObject)){
//				glColor4f(.5f, .3f, .7f, .7f);
//			} else {
//				glColor4f(.4f, .3f, .7f, .4f);
//			}

			boneRenderThing.doGlGeom(components);

//			glEnd();
		}
	}

	private static void doGlQuad(Vec3 RT, Vec3 LT, Vec3 RB, Vec3 LB) {
		GL11.glVertex3f(RT.x, RT.y, RT.z);
		GL11.glVertex3f(LT.x, LT.y, LT.z);
		GL11.glVertex3f(LB.x, LB.y, LB.z);
		GL11.glVertex3f(RB.x, RB.y, RB.z);
	}

	public static void paintVertSquares2(ModelView modelView, RenderModel renderModel, Geoset geo, CameraHandler cameraHandler, VertRendererThing vertRendererThing) {
//		float v = (float) ((cameraHandler.geomX(4) - cameraHandler.geomX(0)));//*cameraHandler.getZoom());
//		glBegin(GL11.GL_TRIANGLES);

//		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glPolygonMode(GL_FRONT, GL_FILL);
//		glPolygonMode(GL_COLOR, GL_FILL);
//		glBegin(GL_QUADS);
		glBegin(GL11.GL_TRIANGLES);
		RenderGeoset renderGeoset = renderModel.getRenderGeoset(geo);
		EditorColorPrefs colorPrefs = ProgramGlobals.getEditorColorPrefs();

		if (renderGeoset != null) {
			for (GeosetVertex vertex : geo.getVertices()) {
				float[] components;
				if (modelView.isSelected(vertex)) {
					components = colorPrefs.getColorComponents(ColorThing.VERTEX_SELECTED);
//					glColor4f(1f, .0f, .0f, .7f);
//					glColor4f(components[0], components[1], components[2], components[3]);
				} else if (modelView.isEditable(vertex)) {
					components = colorPrefs.getColorComponents(ColorThing.VERTEX);
//					glColor4f(.5f, .3f, .7f, .7f);
				} else {
					components = colorPrefs.getColorComponents(ColorThing.VERTEX_UNEDITABLE);
//					glColor4f(.4f, .3f, .7f, .4f);
				}
				glColor4f(components[0], components[1], components[2], components[3]);
				vertRendererThing.transform(cameraHandler.getInverseCameraRotation(), vertex.renderVert.getRenderPos()).doGlGeom();
			}
		}
		glEnd();
	}
}
