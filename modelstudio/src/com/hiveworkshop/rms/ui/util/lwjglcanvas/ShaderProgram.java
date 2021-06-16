package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ShaderProgram {

	private final int programId;
	int openGLVersion = 330;
	private int vertexShaderId;
	private int fragmentShaderId;
	Map<String, Integer> uniforms = new HashMap<>();

	public ShaderProgram() throws Exception {
		programId = glCreateProgram();
		if (programId == 0) {
			throw new Exception("Could not create Shader");
		}
	}

	public ShaderProgram createShader(String path) {
		if (path.endsWith(".vert")) {
			return createVertShader(path);
		} else if (path.endsWith(".frag")) {
			return createFragShader(path);
		}
		return this;
	}

	public int getProgramId() {
		return programId;
	}

	public ShaderProgram createVertShader(String path) {
		try {
			ByteBuffer buffer = IOUtil.ioResourceToByteBuffer(path, 4096);
			vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
			compileShader(openGLVersion, vertexShaderId, buffer);
			glAttachShader(programId, vertexShaderId);
		} catch (IOException e) {
			System.out.println("failed create vertex shader from file: \"" + path + "\"");
			e.printStackTrace();
		}
		return this;
	}

	public ShaderProgram createFragShader(String path) {
		try {
			ByteBuffer buffer = IOUtil.ioResourceToByteBuffer(path, 4096);
			fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
			compileShader(openGLVersion, fragmentShaderId, buffer);
			glAttachShader(programId, fragmentShaderId);
		} catch (IOException e) {
			System.out.println("failed create fragment shader from file: \"" + path + "\"");
			e.printStackTrace();
		}
		return this;
	}

	private static void compileShader(int version, int shaderId, ByteBuffer code) {
		try (MemoryStack stack = stackPush()) {
			ByteBuffer header = stack.ASCII("#version " + version + "\n#line 0\n", false);

			glShaderSource(
					shaderId,
					stack.pointers(header, code),
					stack.ints(header.remaining(), code.remaining())
			);

			glCompileShader(shaderId);
			printShaderInfoLog(shaderId);

			if (glGetShaderi(shaderId, GL_COMPILE_STATUS) != GL_TRUE) {
				throw new IllegalStateException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
			}
		}
	}

	public void setUniform(String uniformName, int value) {
		glUniform1i(uniforms.get(uniformName), value);
	}

	public void createUniform(String uniformName) {
		int uniformLocation = glGetUniformLocation(programId, uniformName);
		if (uniformLocation < 0) {
			new Exception("Could not find uniform:" + uniformName).printStackTrace();
			uniformLocation = 0;
		}
		uniforms.put(uniformName, uniformLocation);
	}

	//	public void setUniform(String uniformName, Vector3f value) {
//		// Dump the matrix into a float buffer
//		try (MemoryStack stack = MemoryStack.stackPush()) {
//			FloatBuffer fb = stack.mallocFloat(4);
//			value.get(fb);
//			glUniform3fv(uniforms.get(uniformName), value);
//		}
//	}
	public void setUniform(String uniformName, float[] value) {
		glUniform3fv(uniforms.get(uniformName), value);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		// Dump the matrix into a float buffer
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
		}
	}

	public ShaderProgram link() throws Exception {
		glLinkProgram(programId);

		printProgramInfoLog(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			glDetachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			glDetachShader(programId, fragmentShaderId);
		}

		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
		}
		return this;
	}

	public void bind() {
		glUseProgram(programId);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		if (programId != 0) {
			glDeleteProgram(programId);
		}
	}


	private static void printShaderInfoLog(int obj) {
		int infoLogLength = glGetShaderi(obj, GL_INFO_LOG_LENGTH);
		if (infoLogLength > 0) {
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
}
