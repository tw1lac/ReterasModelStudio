package com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.renderparts;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
//
//import java.util.HashMap;
//import java.util.Map;

public class RenderGeoset {
	public final Geoset geoset;
	private final ModelView modelView;
	private final RenderModel renderModel;
	boolean isHD;
//	Map<Matrix, Mat4> transformMapSD = new HashMap<>();
//	Map<GeosetVertex.SkinBone[], Mat4> transformMapHD = new HashMap<>();

	// Rather than generating a new matrix every time getTransform is called, create it once and pass it.
	// This is fine since this matrix is immediately used to transform the vertex and is not needed afterwards.
	Mat4 transformHeap = new Mat4();

	public RenderGeoset(Geoset geoset, RenderModel renderModel, ModelView modelView) {
		this.geoset = geoset;
		this.renderModel = renderModel;
		this.modelView = modelView;
		this.isHD = ModelUtils.isTangentAndSkinSupported(geoset.getParentModel())
				&& (geoset.getVertex(0).getSkinBoneBones() != null);
	}

	public RenderGeoset updateTransforms(boolean forceAnimated){
//		transformMapSD.clear();
//		transformMapHD.clear();
		for(GeosetVertex geosetVertex : geoset.getVertices()){
			geosetVertex.renderVert.update(getTransform(geosetVertex, forceAnimated));
		}
		return this;
	}

	private Mat4 getTransform(GeosetVertex vertex, boolean forceAnimated) {
		if (renderModel.getTimeEnvironment().isLive() || forceAnimated) {
			if (isHD) {
				return ModelUtils.processHdBones(renderModel, vertex.getSkinBones(), transformHeap);
			} else {
				return ModelUtils.processSdBones(renderModel, vertex.getMatrix().getBones(), transformHeap);
			}

			// This code uses caching, which should make it much faster...but it's not.
			// It's actually pretty much the same speed as not caching.
			// This is because Map.put/computeIfAbsent are ridiculously slow.
			// Is it a Java thing? is the complex key type forcing some slow hashing? I don't know.
//			if (isHD) {
//				GeosetVertex.SkinBone[] skinBones = vertex.getSkinBones();
//				return transformMapHD.computeIfAbsent(skinBones, k -> ModelUtils.processHdBones(renderModel, skinBones));
//			} else {
//				Matrix matrix = vertex.getMatrix();
//				return transformMapSD.computeIfAbsent(matrix, k -> ModelUtils.processSdBones(renderModel, matrix.getBones()));
//			}
		}
		return null;
	}

	public static class RenderVert {
		GeosetVertex vertex;
		Vec3 renderPos = new Vec3();
		Vec3 renderNorm = new Vec3(0, 0, 1);
		Vec2 tVert = new Vec2(0, 0);

		public RenderVert(GeosetVertex vertex) {
			this.vertex = vertex;
		}

		public RenderVert update(Mat4 mat4) {
			renderPos.set(vertex);
			if (vertex.getNormal() != null) {
				renderNorm.set(vertex.getNormal());
			}
			if (mat4 != null) {
				renderPos.transform(mat4);
				if (vertex.getNormal() != null) {
//					mat4.printMatrix();
					renderNorm.transform(0, mat4).normalize();
				}
			}
			if (vertex.getTverts() != null && vertex.getTverts().get(0) != null) {
				tVert.set(vertex.getTVertex(0));
			}
			return this;
		}

		public GeosetVertex getVertex() {
			return vertex;
		}

		public Vec3 getRenderPos() {
			return renderPos;
		}

		public Vec3 getRenderNorm() {
			return renderNorm;
		}

		public Vec2 getTVert() {
			return tVert;
		}
	}
}
