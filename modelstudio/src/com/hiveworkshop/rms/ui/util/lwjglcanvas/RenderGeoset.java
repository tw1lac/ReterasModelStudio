package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Matrix;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.util.BiMap;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.*;

public class RenderGeoset {
	Geoset geoset;
	RenderModel renderModel;
	//	BiMap<Integer, RenderVert> vertexMap = new BiMap<>();
	BiMap<Integer, GeosetVertex> vertexMap = new BiMap<>();
	BiMap<GeosetVertex, RenderVert> renderVertexMap = new BiMap<>();
	boolean isHD = false;
	private FloatBuffer vertexBuffer;
	private IntBuffer triangleBuffer;
	private FloatBuffer normalBuffer;
	private FloatBuffer texCoordsBuffer;

	Map<Matrix, Mat4> transformMapSD = new HashMap<>();
	Map<GeosetVertex.SkinBone[], Mat4> transformMapHD = new HashMap<>();

	public RenderGeoset(Geoset geoset, RenderModel renderModel) {
		this.geoset = geoset;
		this.renderModel = renderModel;
		rebuildVertexMap();
	}

	private void checkHD() {
		isHD = ModelUtils.isTangentAndSkinSupported(geoset.getParentModel())
				&& (geoset.getVertex(0).getSkinBoneBones() != null);
	}

	private void rebuildVertexMap() {
		vertexMap.clear();
		checkHD();
		for (GeosetVertex vertex : geoset.getVertices()) {
			renderVertexMap.computeIfAbsent(vertex, v -> new RenderVert(vertex));
//			vertexMap.put(vertexMap.size(), renderVertexMap.get(vertex));
			vertexMap.put(vertexMap.size(), vertex);
		}
		renderVertexMap.removeIfKeyNotIn(geoset.getVertices());

	}

	//	public void addToBuffers(int vertBuffer, int normBuffer, int texBuffer, int triBuffer){
	public void addToBuffers(BufferTracker bufferTracker, int vertBuffer, int normBuffer, int texBuffer, int triBuffer) {
		try {
//			bufferTracker.recreateBuffers();
			int verts = vertexMap.size();
			int tris = geoset.getTriangles().size();
			intiBuffers(verts, tris);

			for (int i = 0; i < verts; i++) {
				RenderVert renderVert = renderVertexMap.get(vertexMap.get(i));
				renderVert.update(getTransform(renderVert.vertex));
//			vertexBuffer.put(i*3, renderVert.getRenderPos().toFloatArray());
				vertexBuffer.put(i * 3 + 0, renderVert.getRenderPos().x);
				vertexBuffer.put(i * 3 + 1, renderVert.getRenderPos().y);
				vertexBuffer.put(i * 3 + 2, renderVert.getRenderPos().z);

//			normalBuffer.put(i*3, renderVert.getRenderNorm().toFloatArray());
				normalBuffer.put(i * 3 + 0, renderVert.getRenderNorm().x);
				normalBuffer.put(i * 3 + 1, renderVert.getRenderNorm().y);
				normalBuffer.put(i * 3 + 2, renderVert.getRenderNorm().z);
//			texCoordsBuffer.put(i*2, renderVert.getTVert().toFloatArray());
				texCoordsBuffer.put(i * 2 + 0, renderVert.getTVert().x);
				texCoordsBuffer.put(i * 2 + 1, renderVert.getTVert().y);
			}

			int ugg = 0;
			for (Triangle triangle : geoset.getTriangles()) {
				for (GeosetVertex vertex : triangle.getVerts()) {
					triangleBuffer.put(ugg++, vertexMap.getByValue(vertex));
				}
			}

//			System.out.println("binding buffers");
			vertexBuffer.limit(verts * 3);
			glBindBuffer(GL_ARRAY_BUFFER, bufferTracker.positionVBO);
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(vertBuffer, 3, GL_FLOAT, false, 0, 0);
//			memFree(vertexBuffer);

			normalBuffer.limit(verts * 3);
			glBindBuffer(GL_ARRAY_BUFFER, bufferTracker.normalVBO);
			glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(normBuffer, 3, GL_FLOAT, false, 0, 0);
//			memFree(normalBuffer);

			texCoordsBuffer.limit(verts * 2);
			glBindBuffer(GL_ARRAY_BUFFER, bufferTracker.textureCoordVBO);
			glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(texBuffer, 2, GL_FLOAT, false, 0, 0);
//			memFree(texCoordsBuffer);

			triangleBuffer.limit(tris * 3);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferTracker.triangleVBO);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, triangleBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(triBuffer, 3, GL_INT, false, 0, 0);
//			memFree(triangleBuffer);
		} finally {

			freeBuffers();
		}
	}

	public void render() {

	}

	public int getVertCount() {
		return vertexMap.size();
	}

	private void freeBuffers() {
//		System.out.println("freeing buffers");
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		memFree(vertexBuffer);
		memFree(normalBuffer);
		memFree(texCoordsBuffer);
		memFree(triangleBuffer);

//		System.out.println("buffers freed");
	}

	private void intiBuffers(int verts, int tris) {
//		System.out.println("init buffers");
		vertexBuffer = memAllocFloat(verts * 3 * 8);
		normalBuffer = memAllocFloat(verts * 3 * 8);
		texCoordsBuffer = memAllocFloat(verts * 2 * 8);
		triangleBuffer = memAllocInt(tris * 3 * 8);
	}

	Mat4 getTransform(GeosetVertex vertex) {
		if (renderModel.getAnimatedRenderEnvironment().isLive() || true) {
			if (isHD) {
				GeosetVertex.SkinBone[] skinBones = vertex.getSkinBones();
				return transformMapHD.computeIfAbsent(skinBones, k -> ModelUtils.processHdBones(renderModel, skinBones));
			} else {
				Matrix matrix = vertex.getMatrix();
				return transformMapSD.computeIfAbsent(matrix, k -> ModelUtils.processSdBones(renderModel, matrix.getBones()));
			}
		}
		return null;
	}

	static class RenderVert {
		GeosetVertex vertex;
		Vec3 renderPos = new Vec3();
		Vec3 renderNorm = new Vec3(0, 0, 1);
		Vec2 tVert = new Vec2(0, 0);

		public RenderVert(GeosetVertex vertex) {
			this.vertex = vertex;
			renderPos.set(vertex);
			if (vertex.getNormal() != null) {
				renderNorm.set(vertex.getNormal());
			}
			if (vertex.getTverts() != null && vertex.getTverts().get(0) != null) {
				tVert.set(vertex.getTVertex(0));
			}
		}

		public RenderVert update(Mat4 mat4) {
			renderPos.set(vertex);
			if (vertex.getNormal() != null) {
				renderNorm.set(vertex.getNormal()).normalize();
			}
			if (mat4 != null) {
				renderPos.transform(mat4);
				if (vertex.getNormal() != null) {
					renderNorm.transform(mat4).normalize();
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
