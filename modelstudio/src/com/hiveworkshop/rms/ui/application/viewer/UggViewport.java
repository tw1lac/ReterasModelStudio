package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Bitmap;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Layer;
import com.hiveworkshop.rms.editor.model.ParticleEmitter2;
import com.hiveworkshop.rms.editor.render3d.InternalInstance;
import com.hiveworkshop.rms.editor.render3d.InternalResource;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.BetterAWTGLCanvas;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec4;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class UggViewport extends BetterAWTGLCanvas {
	public UggViewport(final ModelView modelView, final ProgramPreferences programPreferences, final boolean loadDefaultCamera) throws LWJGLException {
		super();
	}


	public void paintNormal(Vec4 vertexSumHeap, Vec4 normalSumHeap) {
		GL11.glVertex3f(vertexSumHeap.y, vertexSumHeap.z, vertexSumHeap.x);

//		Vec3 nSA = normalSumHeap.getVec3().scale((float)(6 / m_zoom));
		Vec3 nSA = normalSumHeap.getVec3().scale((float)(6 / 2)); //TODO Remove
		Vec3 vSA = vertexSumHeap.getVec3().add(nSA);

		GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
		GL11.glVertex3f(vSA.y, vSA.z, vSA.x);
	}

	public void paintVert(Layer layer, GeosetVertex vertex, Vec4 vertexSumHeap) {
		int coordId = layer.getCoordId();
		if (coordId >= vertex.getTverts().size()) {
			coordId = vertex.getTverts().size() - 1;
		}
		GL11.glTexCoord2f(vertex.getTverts().get(coordId).x, vertex.getTverts().get(coordId).y);
		GL11.glVertex3f(vertexSumHeap.y, vertexSumHeap.z, vertexSumHeap.x);
	}

	public void bindParticleTexture(final ParticleEmitter2 particle2, final Bitmap tex, final Integer texture) {
//		if (texture != null) {
//			bindTexture(tex, texture);
//		} else if (textureMap.size() > 0) {
//			bindTexture(tex, 0);
//		}
//		switch (particle2.getFilterMode()) {
//			case BLEND -> setBlendWOAlpha(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//			case ADDITIVE, ALPHAKEY -> setBlendWOAlpha(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//			case MODULATE -> setBlendWOAlpha(GL11.GL_ZERO, GL11.GL_SRC_COLOR);
//			case MODULATE2X -> setBlendWOAlpha(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
//		}
//		if (particle2.getUnshaded()) {
//			GL11.glDisable(GL_LIGHTING);
//		} else {
//			glEnable(GL_LIGHTING);
//		}
	}

	private void normalizeHeap(Vec4 heap) {
		if (heap.length() > 0) {
			heap.normalize();
		} else {
			heap.set(0, 1, 0, 0);
		}
	}

	private void enableGlThings(int... thing){
		for(int t: thing){
			glEnable(t);
		}
	}

	private void disableGlThings(int... thing){
		for(int t: thing){
			glDisable(t);
		}
	}

	private final class Particle2TextureInstance implements InternalResource, InternalInstance {
		private final Bitmap bitmap;
		private final ParticleEmitter2 particle;
		private boolean loaded = false;

		public Particle2TextureInstance(final Bitmap bitmap, final ParticleEmitter2 particle) {
			this.bitmap = bitmap;
			this.particle = particle;
		}

		@Override
		public void setTransformation(final Vec3 worldLocation, final Quat rotation, final Vec3 worldScale) {
		}

		@Override
		public void setSequence(final int index) {
		}

		@Override
		public void show() {
		}

		@Override
		public void setPaused(final boolean paused) {
		}

		@Override
		public void move(final Vec3 deltaPosition) {
		}

		@Override
		public void hide() {
		}

		@Override
		public void bind() {
//			if (!loaded) {
//				loadToTexMap(bitmap);
//				loaded = true;
//			}
//			final Integer texture = textureMap.get(bitmap);
//			bindParticleTexture(particle, bitmap, texture);
		}

		@Override
		public InternalInstance addInstance() {
			return this;
		}

	}
}
