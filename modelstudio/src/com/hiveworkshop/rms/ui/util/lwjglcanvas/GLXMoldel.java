package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import com.hiveworkshop.rms.editor.model.Bitmap;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.util.Vec3;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4x3d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.joml.Math.PI;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

/**
 * Modern OpenGL port of <a href="https://www.opengl.org/archives/resources/code/samples/glut_examples/mesademos/gears.c">gears.c</a>.
 */
public class GLXMoldel {
	ShaderProgram shaderProgram;

	private final int positions;
	private final int normals;
	private final int textureCoords;
	private final int tris;
	//	private final int u_TEX_COORDS;
	private final int u_NORMAL;
	private final int u_MVP;
	private final int u_LIGHT;
	private final int u_COLOR;

	private final Matrix4d P = new Matrix4d();
	private final Matrix4d MVP = new Matrix4d();

	// ---------------------
	private final Matrix4x3d V = new Matrix4x3d();
	private final Matrix4x3d M = new Matrix4x3d();
	private final Matrix4x3d MV = new Matrix4x3d();

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

	//	private Map<RenderGeoset, BufferTracker> geosetBufferMap = new HashMap<>();
	private Map<Geoset, BufferTracker> geosetBufferMap = new HashMap<>();
	private Map<Geoset, RenderGeoset> geosetRenderMap = new HashMap<>();

	int vaoId;

	Vec3 cameraPos = new Vec3(0, 0, 0);

	private double zoom = 1;
	TextureThing2 textureThing2;

	public GLXMoldel(EditableModel model, RenderModel renderModel) {
		System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
		System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
		System.err.println("GL_VERSION: " + glGetString(GL_VERSION));

		this.model = model;
		this.renderModel = renderModel;
		this.textureThing2 = new TextureThing2(model);

		GLCapabilities caps = GL.getCapabilities();
		if (!caps.OpenGL20) {
			throw new IllegalStateException("This demo requires OpenGL 2.0 or higher.");
		}

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		P.setFrustum(-1.0, 1.0, -1.0, 1.0, 5.0, 100.0);

		int program;
		try {
			shaderProgram = new ShaderProgram()
					.createVertShader("vertex_test2.vert")
					.createFragShader("vertex_test2.frag")
					.link();

			program = shaderProgram.getProgramId();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}

		u_MVP = glGetUniformLocation(program, "u_MVP");
		u_NORMAL = glGetUniformLocation(program, "u_NORMAL");
		u_LIGHT = glGetUniformLocation(program, "u_LIGHT");
		u_COLOR = glGetUniformLocation(program, "u_COLOR");
//		u_TEX_COORDS = glGetUniformLocation(program, "u_TEX_COORDS");

		positions = glGetAttribLocation(program, "in_Position");
		normals = glGetAttribLocation(program, "in_Normal");
		textureCoords = glGetAttribLocation(program, "in_TVerts");
		tris = glGetAttribLocation(program, "in_Tris");

		if (caps.OpenGL30) {
			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId); // bind and forget
		}

//		enableAttributes();

//		gear1 = new Gear(1.0, 4.0, 1.0, 20, 0.7, new float[] {0.8f, 0.1f, 0.0f, 1.0f});
//		gear2 = new Gear(0.5, 2.0, 2.0, 10, 0.7, new float[] {0.0f, 0.8f, 0.2f, 1.0f});
//		gear3 = new Gear(1.3, 2.0, 0.5, 10, 0.7, new float[] {0.2f, 0.2f, 1.0f, 1.0f});

//		GLModel = new GLModel(model, renderModel, new float[] {0.8f, 0.1f, 0.0f, 1.0f});


		textureThing2.createTextureMap();
		shaderProgram.createUniform("texture_sampler");
		shaderProgram.createUniform("color");
		shaderProgram.createUniform("useColor");
//		GLModel = new GLModel(model, renderModel);

		startTime = System.currentTimeMillis() / 1000.0;
	}

	private void enableAttributes() {
		glEnableVertexAttribArray(positions);
		glEnableVertexAttribArray(normals);
		glEnableVertexAttribArray(textureCoords);
		glEnableVertexAttribArray(tris);
	}

	private void disableAttributes() {
		glDisableVertexAttribArray(positions);
		glDisableVertexAttribArray(normals);
		glDisableVertexAttribArray(textureCoords);
		glDisableVertexAttribArray(tris);
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		shaderProgram.bind();
//		GLModel.makeModelBuffers(model, renderModel);

		// LIGHT
		glUniform3fv(u_LIGHT, V.transformDirection(light.set(5.0, 5.0, 10.0)).normalize().get(vec3f));


		M.translation(cameraPos.x, cameraPos.y, cameraPos.z);
		M.scale(zoom);

		M.rotateX(angleX * PI / 180);
		M.rotateY(angleY * PI / 180);
		M.rotateZ(angleZ * PI / 180);


		textureThing2.bindTexture(model.getTexture(0));

//		drawModel(GLModel);

		shaderProgram.setUniform("texture_sampler", 0);
		for (Geoset geoset : model.getGeosets()) {

//			System.out.println("drawing geoset: " + geoset.getName());
			RenderGeoset renderGeoset = geosetRenderMap.computeIfAbsent(geoset, k -> new RenderGeoset(geoset, renderModel));
//			System.out.println("renderGeoset");
			BufferTracker bufferTracker = geosetBufferMap.computeIfAbsent(geoset, k -> new BufferTracker());
//			System.out.println("bufferTracker fetched");

////			System.out.println("binding things");


			Bitmap bitmap = geoset.getMaterial().firstLayer().getTextureBitmap();
//			System.out.println("texture: " + bitmap.getName());
//			glBindVertexArray(vaoId);
			glBindVertexArray(bufferTracker.vaoId);
			enableAttributes();


			FloatBuffer color = memAllocFloat(4);
			color.put(new float[] {0.8f, 0.1f, 0.0f, 1.0f}).flip();
			glUniformMatrix3fv(u_NORMAL, false, V.mul(M, MV).normal(normal).get(mat3f));
			glUniformMatrix4fv(u_MVP, false, P.mul(MV, MVP).get(mat4f));
			glUniform4fv(u_COLOR, color);


			float[] c = {1, 0, 0};

			shaderProgram.setUniform("color", c);
			shaderProgram.setUniform("useColor", 0);

			textureThing2.bindTexture(bitmap);
			renderGeoset.addToBuffers(bufferTracker, positions, normals, textureCoords, tris);
//			glDrawArrays(GL_TRIANGLES, 0, renderGeoset.getVertCount());
//			System.out.println("drawing geoset elements");
//			glDrawArrays(GL_TRIANGLES, 0, renderGeoset.getVertCount());
			glDrawElements(GL_TRIANGLES, renderGeoset.getVertCount(), GL_UNSIGNED_INT, 0);
//			System.out.println("drawn geoset elements");
			disableAttributes();
//			glDisableVertexAttribArray(positions);
//			glDisableVertexAttribArray(normals);
//			glDisableVertexAttribArray(textureCoords);
//			glDisableVertexAttribArray(tris);
			glBindVertexArray(0);
//			glBindTexture(GL_TEXTURE_2D, 0);
//			System.out.println("bound vao 0");
		}

		count++;

		double theTime = System.currentTimeMillis() / 1000.0;
		if (theTime >= startTime + 1.0) {
//			System.out.format("%d fps\n", count);
			startTime = theTime;
			count = 0;
		}
//
//		glDisableVertexAttribArray(positions);
//		glDisableVertexAttribArray(normals);
//		glDisableVertexAttribArray(textureCoords);

//
//		glBindVertexArray(0);
//		glBindTexture(GL_TEXTURE_2D, 0);

		shaderProgram.unbind();
	}

	// this should probably be draw geoset...
	private void drawModel(GLModel GLModel) {
		glUniformMatrix3fv(u_NORMAL, false, V.mul(M, MV).normal(normal).get(mat3f));
		glUniformMatrix4fv(u_MVP, false, P.mul(MV, MVP).get(mat4f));
		glUniform4fv(u_COLOR, GLModel.color);

		GLModel.bind(positions, normals, textureCoords);
		glDrawArrays(GL_TRIANGLES, 0, GLModel.vertexCount);

//		glDrawElements(GL_TRIANGLES, GLModel.vertexCount, GL_UNSIGNED_INT, 0);
	}

	public void cleanup() {
		if (shaderProgram != null) {
			shaderProgram.cleanup();
		}

		glDisableVertexAttribArray(0);

		// Delete the VBO
		for (BufferTracker bufferTracker : geosetBufferMap.values()) {
			bufferTracker.destroy();
		}
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GLModel.cleanup();

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		shaderProgram.cleanup();
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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

	public GLXMoldel setZoom(double zoom) {
		this.zoom = zoom;
		System.out.println("zoom: " + zoom);
		return this;
	}

	public GLXMoldel setAngleX(double angleX) {
		this.angleX = angleX;
		return this;
	}

	public GLXMoldel setAngleY(double angleY) {
		this.angleY = angleY;
		return this;
	}

	public GLXMoldel setAngleZ(double angleZ) {
		this.angleZ = angleZ;
		return this;
	}

	public GLXMoldel setCameraPos(Vec3 cameraPos) {
		System.out.println("cameraPos: " + cameraPos);
		this.cameraPos.set(cameraPos);
		return this;
	}

	private int getVersion(GLCapabilities caps) {
		if (caps.OpenGL33) {
			System.out.println("version: " + 330);
			return 330;
		} else if (caps.OpenGL21) {
			System.out.println("version: " + 120);
			return 120;
		} else {
			System.out.println("version: " + 110);
			return 110;
		}
	}

//	static class BufferTracker{
//		int vaoId;
//		int positionVBO = -1;
//		int normalVBO = -1;
//		int triangleVBO = -1;
//		int textureCoordVBO = -1;
//
//		BufferTracker(){
//			vaoId = glGenVertexArrays();
//		}
//
//		private void destroy(){
//			if(vaoId != -1){
//				glDisableVertexAttribArray(0);
//				glBindBuffer(GL_ARRAY_BUFFER, 0);
//				if(positionVBO != -1){
//					glDeleteBuffers(positionVBO);
//					positionVBO = -1;
//				}
//				if(normalVBO != -1){
//					glDeleteBuffers(normalVBO);
//					normalVBO = -1;
//				}
//				if(triangleVBO != -1){
//					glDeleteBuffers(triangleVBO);
//					triangleVBO = -1;
//				}
//				if(textureCoordVBO != -1){
//					glDeleteBuffers(textureCoordVBO);
//					textureCoordVBO = -1;
//				}
//				glBindVertexArray(0);
//				glDeleteVertexArrays(vaoId);
//				vaoId = -1;
//			}
//		}
//	}
}