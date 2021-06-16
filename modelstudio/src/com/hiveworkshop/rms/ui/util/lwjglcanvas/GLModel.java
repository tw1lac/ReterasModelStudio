package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec4;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class GLModel {

	FloatBuffer color;

	int positionVBO;
	int normalVBO;
	int textureCoordVBO;

	int vertexCount;

	public GLModel(EditableModel model, RenderModel renderModel, float[] color) {
//		this.color = memAllocFloat(4);
		this.color = FloatBuffer.allocate(4);
		this.color.put(color).flip();

		positionVBO = makeModelBuffers(model, renderModel);
	}

	public GLModel(EditableModel model, RenderModel renderModel) {
		this.color = memAllocFloat(4);
		this.color.put(new float[] {0.8f, 0.1f, 0.0f, 1.0f}).flip();
		positionVBO = makeModelBuffers(model, renderModel);
	}

	public int makeModelBuffers(EditableModel model, RenderModel renderModel) {
		positionVBO = glGenBuffers();
		normalVBO = glGenBuffers();
		textureCoordVBO = glGenBuffers();
		vertexCount = new Builder()
				.renderModel(model, renderModel)
				.updateVBO(positionVBO, normalVBO, textureCoordVBO);
		return positionVBO;
	}

	void bind(int positions, int normals) {
		glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
		glVertexAttribPointer(positions, 3, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, normalVBO);
		glVertexAttribPointer(normals, 3, GL_FLOAT, false, 0, 0);
	}

	void bind(int positions, int normals, int textureCoodrs) {
		glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
		glVertexAttribPointer(positions, 3, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, normalVBO);
		glVertexAttribPointer(normals, 3, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, textureCoordVBO);
		glVertexAttribPointer(textureCoodrs, 2, GL_FLOAT, false, 0, 0);
	}

	private static class Builder {
		private final Vec3[] verts = new Vec3[] {new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(0, 0, 0)};
		private final Vec3[] norms = new Vec3[] {new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vec3(0, 0, 1)};
		private final Vec2[] tVs = new Vec2[] {new Vec2(0, 0), new Vec2(0, 0), new Vec2(0, 0)};
		private int vertexCount;
		private FloatBuffer positions;
		private FloatBuffer normals;
		private FloatBuffer textCoordsBuffer;
		private final Vec4 normalSumHeap = new Vec4(0, 0, 1, 0);

		Builder renderModel(EditableModel model, RenderModel renderModel) {
			int size = 0;
			for (final Geoset geo : model.getGeosets()) {
//					size += geo.getVertices().size();
				size += geo.getTriangles().size() * 3;
			}

			positions = memAllocFloat(size * 3 * 8);
			normals = memAllocFloat(size * 3 * 8);
			textCoordsBuffer = memAllocFloat(size * 2 * 8);

			for (final Geoset geo : model.getGeosets()) {
				processMesh(geo, renderModel);
			}
			return this;
		}

		private void processMesh(Geoset geo, RenderModel renderModel) {

			for (final Triangle tri : geo.getTriangles()) {
				int trisCount = 0;
				for (final GeosetVertex vertex : tri.getVerts()) {
					Mat4 skinBonesMatrixSumHeap = ModelUtils.processBones(renderModel, vertex, geo);

					verts[trisCount].set(vertex).transform(skinBonesMatrixSumHeap);


					if (vertex.getNormal() != null) {
						norms[trisCount].set(vertex.getNormal()).transform(skinBonesMatrixSumHeap).normalize();
					} else {
						norms[trisCount].set(0, 0, 1);
					}

//						if (vertex.getNormal() != null) {
//							normalSumHeap.set(vertex.getNormal(), 0).transform(skinBonesMatrixSumHeap).normalize();
//							norms[trisCount].set(normalSumHeap);
//						} else {
//							norms[trisCount].set(0, 0, 1);
//						}


					if (vertex.getTverts() != null && vertex.getTverts().get(0) != null) {
						tVs[trisCount].set(vertex.getTverts().get(0));
					} else {
						tVs[trisCount].set(0, 0);
					}

					trisCount++;
				}
				addVertex(verts[0], norms[0], tVs[0]);
				addVertex(verts[1], norms[1], tVs[1]);
				addVertex(verts[2], norms[2], tVs[2]);
			}
		}

		private void addVertex(Vec3 vertex, Vec3 normal, Vec2 tV) {
			positions.put(vertexCount * 3 + 0, vertex.x);
			positions.put(vertexCount * 3 + 1, vertex.y);
			positions.put(vertexCount * 3 + 2, vertex.z);

			normals.put(vertexCount * 3 + 0, normal.x);
			normals.put(vertexCount * 3 + 1, normal.y);
			normals.put(vertexCount * 3 + 2, normal.z);

			textCoordsBuffer.put(vertexCount * 2 + 0, tV.x);
			textCoordsBuffer.put(vertexCount * 2 + 1, tV.y);

			vertexCount++;
		}

		int updateVBO(int positionVBO, int normalVBO, int textureCoordVBO) {
			// VBO for vertex data
			positions.limit(vertexCount * 3);

			glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
			glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

			// VBO for normals data
			normals.limit(vertexCount * 3);

			glBindBuffer(GL_ARRAY_BUFFER, normalVBO);
			glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

			// VBO for textureCord data
			textCoordsBuffer.limit(vertexCount * 2);

			glBindBuffer(GL_ARRAY_BUFFER, textureCoordVBO);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);


			memFree(positions);
			memFree(normals);
			memFree(textCoordsBuffer);

			positions = null;
			normals = null;
			textCoordsBuffer = null;

			return vertexCount;
		}
	}

	public void cleanup() {

		glDeleteBuffers(positionVBO);
		glDeleteBuffers(normalVBO);
		glDeleteBuffers(textureCoordVBO);
	}
}
