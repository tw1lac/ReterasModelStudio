package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.*;

public class BufferTracker {
	public int vaoId;
	public int positionVBO = -1;
	public int normalVBO = -1;
	public int triangleVBO = -1;
	public int textureCoordVBO = -1;

	BufferTracker() {
//		System.out.println("generating vaoID");
		vaoId = glGenVertexArrays();
//		System.out.println("done generating vaoID: " + vaoId);

		positionVBO = glGenBuffers();
		normalVBO = glGenBuffers();
		triangleVBO = glGenBuffers();
		textureCoordVBO = glGenBuffers();
	}

	public void recreateBuffers() {
		positionVBO = glGenBuffers();
		normalVBO = glGenBuffers();
		triangleVBO = glGenBuffers();
		textureCoordVBO = glGenBuffers();
	}

	//	public void destroy(){
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		if(positionVBO != -1){
//			glDeleteBuffers(positionVBO);
//			positionVBO = -1;
//		}
//		if(normalVBO != -1){
//			glDeleteBuffers(normalVBO);
//			normalVBO = -1;
//		}
//		if(triangleVBO != -1){
//			glDeleteBuffers(triangleVBO);
//			triangleVBO = -1;
//		}
//		if(textureCoordVBO != -1){
//			glDeleteBuffers(textureCoordVBO);
//			textureCoordVBO = -1;
//		}
//	}
	public void destroy() {
		if (vaoId != -1) {
			glDisableVertexAttribArray(0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			if (positionVBO != -1) {
				glDeleteBuffers(positionVBO);
				positionVBO = -1;
			}
			if (normalVBO != -1) {
				glDeleteBuffers(normalVBO);
				normalVBO = -1;
			}
			if (triangleVBO != -1) {
				glDeleteBuffers(triangleVBO);
				triangleVBO = -1;
			}
			if (textureCoordVBO != -1) {
				glDeleteBuffers(textureCoordVBO);
				textureCoordVBO = -1;
			}
			glBindVertexArray(0);
			glDeleteVertexArrays(vaoId);
			vaoId = -1;
		}
	}
}
