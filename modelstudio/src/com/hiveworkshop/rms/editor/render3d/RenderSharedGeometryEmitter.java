package com.hiveworkshop.rms.editor.render3d;

import com.hiveworkshop.rms.util.Vec3;

import java.nio.ByteBuffer;

public abstract class RenderSharedGeometryEmitter<MODEL_OBJECT extends EmitterIdObject, EMITTER_VIEW extends EmitterView>
		extends RenderSharedEmitter<MODEL_OBJECT, EMITTER_VIEW> {
	private static final int MAX_POWER_OF_TWO = 1 << 30;
	private final int elementsPerEmit;
	private float[] data;
	private RenderData[] renderData;
	private final ByteBuffer buffer;
	protected InternalResource internalResource;

	public RenderSharedGeometryEmitter(final MODEL_OBJECT model_object, final int elementsPerEmit,
			final InternalResource internalResource) {
		super(model_object);
		this.elementsPerEmit = elementsPerEmit;
		this.internalResource = internalResource;
		this.data = new float[0];
		this.renderData = new RenderData[0];
		this.buffer = ByteBuffer.allocate(0);
	}

	@Override
	public void updateData() {
		final int sizeNeeded = alive * elementsPerEmit;

		if (data.length < sizeNeeded) {
			data = new float[powerOfTwo(sizeNeeded)];
			renderData = new RenderData[powerOfTwo(sizeNeeded)/5];

			// GL15.glBindBuffer();
			// glBufferData
		}

		for (int i = 0, offset = 0; i < alive; i += 1, offset += 30) {
			final EmittedObject<EMITTER_VIEW> object = objects.get(i);
			final Vec3[] verticesV = object.verticesV;
			final float lta = object.lta;
			final float lba = object.lba;
			final float rta = object.rta;
			final float rba = object.rba;
			final float rgb = object.rgb;

			renderData[(offset/5)+0] = new RenderData(verticesV[0], lta, rgb);
			renderData[(offset/5)+1] = new RenderData(verticesV[1], lba, rgb);
			renderData[(offset/5)+2] = new RenderData(verticesV[2], rba, rgb);
			renderData[(offset/5)+3] = new RenderData(verticesV[0], lta, rgb);
			renderData[(offset/5)+4] = new RenderData(verticesV[2], rba, rgb);
			renderData[(offset/5)+5] = new RenderData(verticesV[3], rta, rgb);

//			data[offset + 0] = verticesV[0].x;
//			data[offset + 1] = verticesV[0].y;
//			data[offset + 2] = verticesV[0].z;
//			data[offset + 3] = lta;
//			data[offset + 4] = rgb;
//
//			data[offset + 5] = verticesV[1].x;
//			data[offset + 6] = verticesV[1].y;
//			data[offset + 7] = verticesV[1].z;
//			data[offset + 8] = lba;
//			data[offset + 9] = rgb;
//
//			data[offset + 10] = verticesV[2].x;
//			data[offset + 11] = verticesV[2].y;
//			data[offset + 12] = verticesV[2].z;
//			data[offset + 13] = rba;
//			data[offset + 14] = rgb;
//
//			data[offset + 15] = verticesV[0].x;
//			data[offset + 16] = verticesV[0].y;
//			data[offset + 17] = verticesV[0].z;
//			data[offset + 18] = lta;
//			data[offset + 19] = rgb;
//
//			data[offset + 20] = verticesV[2].x;
//			data[offset + 21] = verticesV[2].y;
//			data[offset + 22] = verticesV[2].z;
//			data[offset + 23] = rba;
//			data[offset + 24] = rgb;
//
//			data[offset + 25] = verticesV[3].x;
//			data[offset + 26] = verticesV[3].y;
//			data[offset + 27] = verticesV[3].z;
//			data[offset + 28] = rta;
//			data[offset + 29] = rgb;
		}
	}

	@Override
	public void render(final RenderModel modelView, final ParticleEmitterShader shader) {
		if ((internalResource != null) && (alive > 0)) {
			shader.renderParticles(modelObject.getBlendSrc(), modelObject.getBlendDst(), modelObject.getRows(),
					modelObject.getCols(), internalResource, renderData, modelObject.isRibbonEmitter(), alive * 6);
		}
	}

	/**
	 * Returns a power of two size for the given target capacity.
	 */
	private static int powerOfTwo(final int capacity) {
		int numElements = capacity - 1;
		numElements |= numElements >>> 1;
		numElements |= numElements >>> 2;
		numElements |= numElements >>> 4;
		numElements |= numElements >>> 8;
		numElements |= numElements >>> 16;
		return (numElements < 0) ? 1 : (numElements >= MAX_POWER_OF_TWO) ? MAX_POWER_OF_TWO : numElements + 1;
	}

	static class RenderData {
		public Vec3 v;
		public float uv;
		public float color;

		RenderData(Vec3 v, float uv, float color){
			this.v = v;
			this.uv = uv;
			this.color = color;
		}

		@Override
		public String toString() {
			return "RenderData{" + "v=" + v + ", uv=" + uv + ", color=" + color + '}';
		}
	}
}
