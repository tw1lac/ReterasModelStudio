package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexBuffers {
	private int capacity = 0;
	private FloatBuffer positions;
	private FloatBuffer texcoords;
	private FloatBuffer normals;
	private FloatBuffer colors;

	void ensureCapacity(int vertices) {
		if (capacity < vertices) {
			capacity = vertices;
			positions = ByteBuffer.allocateDirect(vertices * 3 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			texcoords = ByteBuffer.allocateDirect(vertices * 2 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			normals = ByteBuffer.allocateDirect(vertices * 3 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			colors = ByteBuffer.allocateDirect(vertices * 4 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
		}
	}

	void setPosition(int index, float x, float y, float z) {
		int offset = index * 3;

		positions.put(offset + 0, x);
		positions.put(offset + 1, y);
		positions.put(offset + 2, z);
	}

	void setPosition(int index, float[] position) {
		setPosition(index, position[0], position[1], position[2]);
	}

	void setPosition(int index, Vec3 position) {
		setPosition(index, position.x, position.y, position.z);
	}

	void setTexcoord(int index, float x, float y) {
		int offset = index * 2;

		texcoords.put(offset + 0, x);
		texcoords.put(offset + 1, y);
	}

	void setTexcoord(int index, float[] texcoord) {
		setTexcoord(index, texcoord[0], texcoord[1]);
	}

	void setTexcoord(int index, Vec2 texcoord) {
		setTexcoord(index, texcoord.x, texcoord.y);
	}

	void setNormal(int index, float x, float y, float z) {
		int offset = index * 3;

		normals.put(offset + 0, x);
		normals.put(offset + 1, y);
		normals.put(offset + 2, z);
	}

	void setNormal(int index, float[] normal) {
		setNormal(index, normal[0], normal[1], normal[2]);
	}

	void setNormal(int index, Vec3 normal) {
		setNormal(index, normal.x, normal.y, normal.z);
	}

	void setColorRGB(int index, float r, float g, float b) {
		int offset = index * 3;

		colors.put(offset + 0, r);
		colors.put(offset + 1, g);
		colors.put(offset + 2, b);
	}

	void setColorRGB(int index, Vec3 color) {
		setColorRGB(index, color.x, color.y, color.z);
	}

	void setColorRGB(int index, float[] color) {
		setColorRGB(index, color[0], color[1], color[2]);
	}

	void setColorRGBA(int index, float r, float g, float b, float a) {
		int offset = index * 4;

		colors.put(offset + 0, r);
		colors.put(offset + 1, g);
		colors.put(offset + 2, b);
		colors.put(offset + 3, a);
	}

	void setColorRGBA(int index, float[] color) {
		setColorRGBA(index, color[0], color[1], color[2], color[3]);
	}

	void setColorRGBA(int index, Vec4 color) {
		setColorRGBA(index, color.x, color.y, color.z, color.w);
	}

	FloatBuffer getPositions() {
		return positions.asReadOnlyBuffer();
	}

	FloatBuffer getTexcoords() {
		return texcoords.asReadOnlyBuffer();
	}

	FloatBuffer getNormals() {
		return normals.asReadOnlyBuffer();
	}

	FloatBuffer getColors() {
		return colors.asReadOnlyBuffer();
	}
}
