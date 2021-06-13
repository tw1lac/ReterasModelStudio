package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Vec4;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4x3d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.joml.Math.PI;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * Modern OpenGL port of <a href="https://www.opengl.org/archives/resources/code/samples/glut_examples/mesademos/gears.c">gears.c</a>.
 */
public class GLXMoldel {
	private final int program;
	private final int positions;
	private final int normals;
	private final int u_NORMAL;
	private final int u_MVP;
	private final int u_LIGHT;
	private final int u_COLOR;
	private final Matrix4d
			P = new Matrix4d(),
			MVP = new Matrix4d();

	// ---------------------
	private final Matrix4x3d
			V = new Matrix4x3d(),
			M = new Matrix4x3d(),
			MV = new Matrix4x3d();
	private final Matrix3d normal = new Matrix3d();
	private final Vector3d light = new Vector3d();
	private final FloatBuffer vec3f = BufferUtils.createFloatBuffer(3);
	private final FloatBuffer mat3f = BufferUtils.createFloatBuffer(3 * 3);
	private final FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);
	public double angleX = 270;

	// ---------------------
	public double angleY = 0;
	public double angleZ = 270;
	EditableModel model;
	RenderModel renderModel;
	private GLModel GLModel;
	private long count;
	private double startTime;
	private double distance = 40.0f;
	private double angle;


	public GLXMoldel(EditableModel model, RenderModel renderModel) {
		System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
		System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
		System.err.println("GL_VERSION: " + glGetString(GL_VERSION));

		this.model = model;
		this.renderModel = renderModel;

		GLCapabilities caps = GL.getCapabilities();
		if (!caps.OpenGL20) {
			throw new IllegalStateException("This demo requires OpenGL 2.0 or higher.");
		}

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		P.setFrustum(-1.0, 1.0, -1.0, 1.0, 5.0, 100.0);

		try {
			ByteBuffer vs = IOUtil.ioResourceToByteBuffer("gears.vert", 4096);
			ByteBuffer fs = IOUtil.ioResourceToByteBuffer("gears.frag", 4096);

			int version;
			if (caps.OpenGL33) {
				version = 330;
			} else if (caps.OpenGL21) {
				version = 120;
			} else {
				version = 110;
			}

			program = compileShaders(version, vs, fs);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		u_MVP = glGetUniformLocation(program, "u_MVP");
		u_NORMAL = glGetUniformLocation(program, "u_NORMAL");
		u_LIGHT = glGetUniformLocation(program, "u_LIGHT");
		u_COLOR = glGetUniformLocation(program, "u_COLOR");

		positions = glGetAttribLocation(program, "in_Position");
		normals = glGetAttribLocation(program, "in_Normal");

		if (caps.OpenGL30) {
			int vao = glGenVertexArrays();
			glBindVertexArray(vao); // bind and forget
		}
		glEnableVertexAttribArray(positions);
		glEnableVertexAttribArray(normals);

//		gear1 = new Gear(1.0, 4.0, 1.0, 20, 0.7, new float[] {0.8f, 0.1f, 0.0f, 1.0f});
//		gear2 = new Gear(0.5, 2.0, 2.0, 10, 0.7, new float[] {0.0f, 0.8f, 0.2f, 1.0f});
//		gear3 = new Gear(1.3, 2.0, 0.5, 10, 0.7, new float[] {0.2f, 0.2f, 1.0f, 1.0f});

		GLModel = new GLModel(model, renderModel, new float[] {0.8f, 0.1f, 0.0f, 1.0f});

		startTime = System.currentTimeMillis() / 1000.0;
	}

	private static void printShaderInfoLog(int obj) {
		int infologLength = glGetShaderi(obj, GL_INFO_LOG_LENGTH);
		if (infologLength > 0) {
			glGetShaderInfoLog(obj);
			System.out.format("%s\n", glGetShaderInfoLog(obj));
		}
	}

	private static void printProgramInfoLog(int obj) {
		int infologLength = glGetProgrami(obj, GL_INFO_LOG_LENGTH);
		if (infologLength > 0) {
			glGetProgramInfoLog(obj);
			System.out.format("%s\n", glGetProgramInfoLog(obj));
		}
	}

	private static void compileShader(int version, int shader, ByteBuffer code) {
		try (MemoryStack stack = stackPush()) {
			ByteBuffer header = stack.ASCII("#version " + version + "\n#line 0\n", false);

			glShaderSource(
					shader,
					stack.pointers(header, code),
					stack.ints(header.remaining(), code.remaining())
			);

			glCompileShader(shader);
			printShaderInfoLog(shader);

			if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new IllegalStateException("Failed to compile shader.");
			}
		}
	}

	private static int compileShaders(int version, ByteBuffer vs, ByteBuffer fs) {
		int v = glCreateShader(GL_VERTEX_SHADER);
		int f = glCreateShader(GL_FRAGMENT_SHADER);

		compileShader(version, v, vs);
		compileShader(version, f, fs);

		int p = glCreateProgram();
		glAttachShader(p, v);
		glAttachShader(p, f);
		glLinkProgram(p);
		printProgramInfoLog(p);

		if (glGetProgrami(p, GL_LINK_STATUS) != GL_TRUE) {
			throw new IllegalStateException("Failed to link program.");
		}

		glUseProgram(p);
		return p;
	}

	public void animate() {
		angle += 2.0;
	}

	public void setSize(int width, int height) {
		float h = height / (float) width;

		glViewport(0, 0, width, height);
		if (h < 1.0f) {
			int i = 1;
			P.setFrustum(-i / h, i / h, -i, i, 5.0, 6000.0);
		} else {
			P.setFrustum(-1.0, 1.0, -1, 1, 5.0, 6000.0);
		}
	}

	public void render() {
		GLModel.makeModelBuffers(model, renderModel);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// VIEW
//		V.translation(0.0, 0.0, -distance)
//				.rotateX(20.0f * PI / 180)
//				.rotateY(30.0f * PI / 180);
		//V.rotateZ(0.0f * PI / 180);

		// LIGHT
		glUniform3fv(u_LIGHT, V.transformDirection(light.set(5.0, 5.0, 10.0)).normalize().get(vec3f));

		// GEAR 1
		double angle2 = 180;
		M.scale(1);
		M.translation(-3.0, -2.0, -500.0);

		M.rotateX(angleX * PI / 180);
		M.rotateY(angleY * PI / 180);
		M.rotateZ(angleZ * PI / 180);
		drawModel(GLModel);

		count++;

		double theTime = System.currentTimeMillis() / 1000.0;
		if (theTime >= startTime + 1.0) {
			System.out.format("%d fps\n", count);
			startTime = theTime;
			count = 0;
		}
	}

	private void drawModel(GLModel GLModel) {
		glUniformMatrix3fv(u_NORMAL, false, V.mul(M, MV).normal(normal).get(mat3f));
		glUniformMatrix4fv(u_MVP, false, P.mul(MV, MVP).get(mat4f));
		glUniform4fv(u_COLOR, GLModel.color);

		GLModel.bind(positions, normals);
		glDrawArrays(GL_TRIANGLES, 0, GLModel.vertexCount);
	}

	private static class GLModel {

		final FloatBuffer color;

		int positionVBO;
		int normalVBO;

		int vertexCount;

		private GLModel(EditableModel model, RenderModel renderModel, float[] color) {
			this.color = BufferUtils.createFloatBuffer(4);
			this.color.put(color).flip();

			positionVBO = makeModelBuffers(model, renderModel);
		}

		private int makeModelBuffers(EditableModel model, RenderModel renderModel) {
			positionVBO = glGenBuffers();
			normalVBO = glGenBuffers();

			vertexCount = new Builder()
					.renderModel(model, renderModel)
					.updateVBO(positionVBO, normalVBO);
			return positionVBO;
		}

		void bind(int positions, int normals) {
			glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
			glVertexAttribPointer(positions, 3, GL_FLOAT, false, 0, 0);

			glBindBuffer(GL_ARRAY_BUFFER, normalVBO);
			glVertexAttribPointer(normals, 3, GL_FLOAT, false, 0, 0);
		}

		private static class Builder {
			private final double[] quads = new double[4 * 3];
			private final double[] tris = new double[3 * 3];
			private int vertexCount;
			private FloatBuffer positions;
			private FloatBuffer normals;
			private double
					normalX,
					normalY,
					normalZ;
			private int quadCount;
			private int trisCount;

			Builder renderModel(EditableModel model, RenderModel renderModel) {
				int size = 0;
				for (final Geoset geo : model.getGeosets()) {
					size += geo.getVertices().size();
				}

				positions = memAllocFloat(size * 3 * 8);
				normals = memAllocFloat(size * 3 * 8);

				normal3f(0.0, 0.0, 1.0);

				for (final Geoset geo : model.getGeosets()) {
					processMesh(geo, isHD(geo, model.getFormatVersion()), renderModel);
				}
				return this;
			}

			private boolean isHD(Geoset geo, int formatVersion) {
				return (ModelUtils.isTangentAndSkinSupported(formatVersion)) && (geo.getVertices().size() > 0) && (geo.getVertex(0).getSkinBones() != null);
			}

			private void processMesh(Geoset geo, boolean isHd, RenderModel renderModel) {

				for (final Triangle tri : geo.getTriangles()) {
					trisCount = 0;
					for (final GeosetVertex vertex : tri.getVerts()) {
						Mat4 skinBonesMatrixSumHeap;
						if (isHd) {
							skinBonesMatrixSumHeap = ModelUtils.processHdBones(renderModel, vertex.getSkinBones());
						} else {
							skinBonesMatrixSumHeap = ModelUtils.processSdBones(renderModel, vertex.getBones());
						}
						Vec4 vertexSumHeap = Vec4.getTransformed(new Vec4(vertex, 1), skinBonesMatrixSumHeap);


						if (vertex.getNormal() != null) {
							Vec4 normalSumHeap = Vec4.getTransformed(new Vec4(vertex.getNormal(), 0), skinBonesMatrixSumHeap);
							normalSumHeap.normalize();
							normal3f(normalSumHeap.x, normalSumHeap.y, normalSumHeap.z);
						}
						vertex3f(vertexSumHeap.x, vertexSumHeap.y, vertexSumHeap.z);
					}
				}
			}

			private void normal3f(double x, double y, double z) {
				normalX = x;
				normalY = y;
				normalZ = z;
			}


			private void vertex3f(double x, double y, double z) {
//				quads[quadCount * 3] = x;
//				quads[quadCount * 3 + 1] = y;
//				quads[quadCount * 3 + 2] = z;
//
//				if (++quadCount == 4) {
//					addVertex(quads[0], quads[1], quads[2]);
//					addVertex(quads[3], quads[4], quads[5]);
//					addVertex(quads[6], quads[7], quads[8]);
//
//					addVertex(quads[6], quads[7], quads[8]);
//					addVertex(quads[3], quads[4], quads[5]);
//					addVertex(quads[9], quads[10], quads[11]);
//
//					System.arraycopy(quads, 2 * 3, quads, 0, 2 * 3);
//					quadCount = 2;
//				}

				tris[trisCount * 3] = x;
				tris[trisCount * 3 + 1] = y;
				tris[trisCount * 3 + 2] = z;

				if (++trisCount == 3) {
					addVertex(tris[0], tris[1], tris[2]);
					addVertex(tris[3], tris[4], tris[5]);
					addVertex(tris[6], tris[7], tris[8]);

					System.arraycopy(tris, 3, tris, 0, 3);
					trisCount = 1;
				}
			}

			private void addVertex(double x, double y, double z) {
				positions.put(vertexCount * 3, (float) x);
				positions.put(vertexCount * 3 + 1, (float) y);
				positions.put(vertexCount * 3 + 2, (float) z);

				normals.put(vertexCount * 3, (float) normalX);
				normals.put(vertexCount * 3 + 1, (float) normalY);
				normals.put(vertexCount * 3 + 2, (float) normalZ);

				vertexCount++;
			}

			int updateVBO(int positionVBO, int normalVBO) {
				// VBO for vertex data
				positions.limit(vertexCount * 3);

				glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
				glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

				// VBO for normals data
				normals.limit(vertexCount * 3);

				glBindBuffer(GL_ARRAY_BUFFER, normalVBO);
				glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

				memFree(positions);
				memFree(normals);

				positions = null;
				normals = null;

				return vertexCount;
			}
		}
	}

}