package com.hiveworkshop.rms.editor.render3d;

import com.hiveworkshop.rms.editor.model.ParticleEmitter2;
import com.hiveworkshop.rms.ui.application.viewer.AnimatedRenderEnvironment;
import com.hiveworkshop.rms.util.MathUtils;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec4;

public class RenderParticle2 extends EmittedObject<RenderParticleEmitter2View> {
	private static final Vec3 locationHeap = new Vec3();
	private static final Vec4 location4Heap = new Vec4();
	private final RenderParticleEmitter2 emitter;
	private boolean head;
	private final Vec3 location;
	private final Vec3 velocity;
	private float gravity;
	private final Vec3 nodeScale;

	private RenderNode node;

	public RenderParticle2(final RenderParticleEmitter2 emitter) {
		this.emitter = emitter;
		emitterView = null;
		health = 0;
		head = true;
		location = new Vec3();
		velocity = new Vec3();
		gravity = 0;
		nodeScale = new Vec3();

		vertices = new float[12];
		verticesV = new Vec3[4];
		lta = 0;
		lba = 0;
		rta = 0;
		rba = 0;
		rgb = 0;
	}

	@Override
	public void reset(final RenderParticleEmitter2View emitterView, final boolean isHead) {
		double width = emitterView.getLength();
		double length = emitterView.getWidth();
		double latitude = emitterView.getLatitude();
		final double variation = emitterView.getVariation();
		final double speed = emitterView.getSpeed();
		final double gravity = emitterView.getGravity();

		final ParticleEmitter2 modelObject = emitter.modelObject;
		final RenderNode node = emitterView.instance.getRenderNode(modelObject);
		final Vec3 pivotPoint = modelObject.getPivotPoint();
		final Vec3 scale = node.getWorldScale();
		width *= 0.5;
		length *= 0.5;
		latitude = Math.toRadians(latitude);

		this.emitterView = emitterView;
		this.node = node;
		health = (float) modelObject.getLifeSpan();
		head = isHead;
		this.gravity = (float) (gravity * scale.z);

		nodeScale.set(scale);

		// Local location

		float randomWidth = MathUtils.randomInRange(-width, width);
		float randomLength = MathUtils.randomInRange(-length, length);
		location.set(pivotPoint).add(new Vec3(randomWidth, randomLength, 0));

		// World location
		if (!modelObject.getModelSpace()) {
			Vec4 vec4loc = new Vec4(location, 1);
			vec4loc.transform(node.getWorldMatrix());
			location.set(vec4loc);
		}

		// Location rotation
		Vec4 vec4perpZ = new Vec4(0, 0, 1, (float) (Math.PI / 2));
		Quat rotationZHeap = new Quat().setFromAxisAngle(vec4perpZ);
		Vec4 vec4randomX = new Vec4(1, 0, 0, MathUtils.randomInRange(-latitude, latitude));
		Quat rotationYHeap = new Quat().setFromAxisAngle(vec4randomX);
		rotationYHeap.mul(rotationZHeap);

		// If this is not a line emitter, emit in a sphere rather than a circle
		if (!modelObject.getLineEmitter()) {
			Vec4 vec4randomY = new Vec4(0, 1, 0, MathUtils.randomInRange(-latitude, latitude));
			Quat rotationXHeap = new Quat().setFromAxisAngle(vec4randomY);
			rotationYHeap.mul(rotationXHeap);
		}

		// World rotation
		if (!modelObject.getModelSpace()) {
			rotationYHeap.mul(node.getWorldRotation());
		}

		// Apply the rotation
		Vec4 vec4Z = new Vec4(0, 0, 1, 1);
		vec4Z.transform(rotationYHeap);
		velocity.set(vec4Z);

		// Apply speed
		velocity.scale((float) speed + MathUtils.randomInRange(-variation, variation));

		// Apply the parent's scale
		velocity.multiply(scale);
	}

	@Override
	public void update() {
		final ParticleEmitter2 modelObject = emitter.modelObject;
		final float dt = AnimatedRenderEnvironment.FRAMES_PER_UPDATE * 0.001f;
		final Vec3 worldLocation = locationHeap;
		final Vec4 worldLocation4f = location4Heap;

		health -= dt;

		velocity.z -= gravity * dt;

		location.add(Vec3.getScaled(velocity, dt));

		worldLocation.set(location);
		worldLocation4f.set(location, 1);

		final float lifeFactor = (float) ((modelObject.getLifeSpan() - health) / modelObject.getLifeSpan());
		final float timeMiddle = (float) modelObject.getTime();
		float factor;
		final int firstColor;
		final Vec3 interval;

		if (lifeFactor < timeMiddle) {
			factor = lifeFactor / timeMiddle;

			firstColor = 0;

			if (head) {
				interval = modelObject.getHeadUVAnim();
			} else {
				interval = modelObject.getTailUVAnim();
			}
		} else {
			factor = (lifeFactor - timeMiddle) / (1 - timeMiddle);

			firstColor = 1;

			if (head) {
				interval = modelObject.getHeadDecayUVAnim();
			} else {
				interval = modelObject.getTailDecayUVAnim();
			}
		}

		factor = Math.min(factor, 1);

		final float start = interval.x;
		final float end = interval.y;
		final float repeat = interval.z;
		final Vec3 scaling = modelObject.getParticleScaling();
		final Vec3[] colors = modelObject.getSegmentColors();
		final float scale = MathUtils.lerp(scaling.getCoord((byte) firstColor), scaling.getCoord((byte) (firstColor + 1)), factor);
		final float left;
		final float top;
		final float right;
		final float bottom;
		final RenderModel instance = emitterView.instance;

		// If this is a team colored emitter, get the team color tile from the atlas
		// Otherwise do normal texture atlas handling.
		if (modelObject.isTeamColored()) {
			// except that Matrix Eater has no such atlas and we are simply copying from Ghostwolf
			left = 0;
			top = 0;
		} else {
			final int columns = modelObject.getCols();
			float index = 0;
			final float spriteCount = end - start;
			if ((spriteCount > 0) && ((columns > 1) || (modelObject.getRows() > 1))) {
				// Repeating speeds up the sprite animation, which makes it effectively run N times in its interval.
				// E.g. if repeat is 4, the sprite animation will be seen 4 times, and thus also run 4 times as fast
				index = (float) (start + (Math.floor(spriteCount * repeat * factor) % spriteCount));
			}

			left = index % columns;
			top = (int) (index / columns);
		}
		right = left + 1;
		bottom = top + 1;

//		final Vec3 firstColorVertexME = colors[firstColor];
//		final Vec3 secondColorVertexME = colors[firstColor + 1];
//		color1Heap.set(firstColorVertexME.x, firstColorVertexME.y, firstColorVertexME.z, modelObject.getAlpha().getCoord((byte) firstColor));
//		color2Heap.set(secondColorVertexME.x, secondColorVertexME.y, secondColorVertexME.z, modelObject.getAlpha().getCoord((byte) (firstColor + 1)));
//		color1Heap.lerp(color2Heap, factor, colorHeap);
		Vec4 color1Heap = new Vec4(colors[firstColor], modelObject.getAlpha().getCoord((byte) firstColor));
		Vec4 color2Heap = new Vec4(colors[firstColor + 1], modelObject.getAlpha().getCoord((byte) (firstColor + 1)));
		Vec4 colorHeap = Vec4.getLerped(color1Heap, color2Heap, factor);

		final int a = ((int) colorHeap.w) & 0xFF;

		lta = MathUtils.uint8ToUint24((byte) right, (byte) bottom, (byte) a);
		lba = MathUtils.uint8ToUint24((byte) left, (byte) bottom, (byte) a);
		rta = MathUtils.uint8ToUint24((byte) right, (byte) top, (byte) a);
		rba = MathUtils.uint8ToUint24((byte) left, (byte) top, (byte) a);
		rgb = MathUtils.uint8ToUint24((byte) ((int) (colorHeap.z * 255) & 0xFF), (byte) ((int) (colorHeap.y * 255) & 0xFF), (byte) ((int) (colorHeap.x * 255) & 0xFF));

		final Vec4[] vectors;

		// Choose between a default rectangle or a billboarded one
		if (modelObject.getXYQuad()) {
			vectors = instance.getSpacialVectors();
		} else {
			vectors = instance.getBillboardVectors();
		}

//		final float[] vertices = this.vertices;

		Vec3 scaleV = Vec3.getScaled(this.nodeScale, scale);

		if (head) {
			// If this is a model space emitter, the particle location is in local space, so
			// convert it now to world space.
			if (modelObject.getModelSpace()) {
				worldLocation4f.transform(node.getWorldMatrix());
			}

			Vec3 p = worldLocation4f.getVec3();
//			final float px = worldLocation4f.x;
//			final float py = worldLocation4f.y;
//			final float pz = worldLocation4f.z;

//			final Vec4 pv1 = vectors[0];
//			final Vec4 pv2 = vectors[1];
//			final Vec4 pv3 = vectors[2];
//			final Vec4 pv4 = vectors[3];
			final Vec3 pv1 = vectors[0].getVec3();
			final Vec3 pv2 = vectors[1].getVec3();
			final Vec3 pv3 = vectors[2].getVec3();
			final Vec3 pv4 = vectors[3].getVec3();

//			final Vec3[] verticesV = new Vec3[4];
			verticesV[0] = Vec3.getSum(p, Vec3.getProd(pv1, scaleV));
			verticesV[1] = Vec3.getSum(p, Vec3.getProd(pv2, scaleV));
			verticesV[2] = Vec3.getSum(p, Vec3.getProd(pv3, scaleV));
			verticesV[3] = Vec3.getSum(p, Vec3.getProd(pv4, scaleV));

			vertices[0] = verticesV[0].x;
			vertices[1] = verticesV[0].y;
			vertices[2] = verticesV[0].z;
			vertices[3] = verticesV[1].x;
			vertices[4] = verticesV[1].y;
			vertices[5] = verticesV[1].z;
			vertices[6] = verticesV[2].x;
			vertices[7] = verticesV[2].y;
			vertices[8] = verticesV[2].z;
			vertices[9] = verticesV[3].x;
			vertices[10] = verticesV[3].y;
			vertices[11] = verticesV[3].z;

//			vertices[0] = p.x + (pv1.x * scaleV.x);
//			vertices[1] = p.y + (pv1.y * scaleV.y);
//			vertices[2] = p.z + (pv1.z * scaleV.z);
//			vertices[3] = p.x + (pv2.x * scaleV.x);
//			vertices[4] = p.y + (pv2.y * scaleV.y);
//			vertices[5] = p.z + (pv2.z * scaleV.z);
//			vertices[6] = p.x + (pv3.x * scaleV.x);
//			vertices[7] = p.y + (pv3.y * scaleV.y);
//			vertices[8] = p.z + (pv3.z * scaleV.z);
//			vertices[9] = p.x + (pv4.x * scaleV.x);
//			vertices[10] = p.y + (pv4.y * scaleV.y);
//			vertices[11] = p.z + (pv4.z * scaleV.z);

//			vertices[0] = px + (pv1.x * scalex);
//			vertices[1] = py + (pv1.y * scaley);
//			vertices[2] = pz + (pv1.z * scalez);
//			vertices[3] = px + (pv2.x * scalex);
//			vertices[4] = py + (pv2.y * scaley);
//			vertices[5] = pz + (pv2.z * scalez);
//			vertices[6] = px + (pv3.x * scalex);
//			vertices[7] = py + (pv3.y * scaley);
//			vertices[8] = pz + (pv3.z * scalez);
//			vertices[9] = px + (pv4.x * scalex);
//			vertices[10] = py + (pv4.y * scaley);
//			vertices[11] = pz + (pv4.z * scalez);
		} else {
			final double tailLength = modelObject.getTailLength();
			Vec3 offsetV = Vec3.getScaled(velocity, (float)tailLength);

			// The start and end of the tail
			Vec4 startHeap = new Vec4(worldLocation4f.getVec3().sub(offsetV), 1);
			Vec4 endHeap = new Vec4(worldLocation4f.getVec3(), 1);
//			startHeap.set((float) (worldLocation4f.x - offsetx), (float) (worldLocation4f.y - offsety), (float) (worldLocation4f.z - offsetz), 1);
//			endHeap.set(worldLocation4f.x, worldLocation4f.y, worldLocation4f.z, 1);

			// If this is a model space emitter, the start and end are in local space, so
			// convert them to world space.
			if (modelObject.getModelSpace()) {
				startHeap.transform(node.getWorldMatrix());
				endHeap.transform(node.getWorldMatrix());
			}

			Vec3 startV = startHeap.getVec3();
			Vec3 endV = endHeap.getVec3();

			// Get the normal to the tail in camera space
			// This allows to build a 2D rectangle around the 3D tail
			Vec3 tailHeap = Vec3.getDiff(endV, startV);
//			tailHeap.set(endx - startx, endy - starty, endz - startz);
			if (tailHeap.lengthSquared() > 0) {
				tailHeap.normalize();
			}
			Vec3 normalHeap = instance.getBillboardVectors()[6].getVec3();
			normalHeap.cross(tailHeap);
			if (normalHeap.lengthSquared() > 0) {
				normalHeap.normalize();
			}

			Vec3 normal = Vec3.getProd(normalHeap, scaleV);

			verticesV[0] = Vec3.getDiff(startV, normal);
			verticesV[1] = Vec3.getSum(endV, normal);
			verticesV[2] = Vec3.getDiff(endV, normal);
			verticesV[3] = Vec3.getSum(scaleV, normal);

			vertices[0] = verticesV[0].x;
			vertices[1] = verticesV[0].y;
			vertices[2] = verticesV[0].z;

			vertices[3] = verticesV[1].x;
			vertices[4] = verticesV[1].y;
			vertices[5] = verticesV[1].z;

			vertices[6] = verticesV[2].x;
			vertices[7] = verticesV[2].y;
			vertices[8] = verticesV[2].z;

			vertices[9] = verticesV[3].x;
			vertices[10] = verticesV[3].y;
			vertices[11] = verticesV[3].z;

//			vertices[0] = startx - normalX;
//			vertices[1] = starty - normalY;
//			vertices[2] = startz - normalZ;
//
//			vertices[6] = endx + normalX;
//			vertices[7] = endy + normalY;
//			vertices[8] = endz + normalZ;
//
//			vertices[3] = endx - normalX;
//			vertices[4] = endy - normalY;
//			vertices[5] = endz - normalZ;
//
//			vertices[9] = startx + normalX;
//			vertices[10] = starty + normalY;
//			vertices[11] = startz + normalZ;
		}
	}
}
