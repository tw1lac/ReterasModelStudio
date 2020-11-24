package com.hiveworkshop.wc3.mdl;

import com.hiveworkshop.wc3.gui.animedit.TimeEnvironmentImpl;
import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.animation.AddKeyframeAction;
import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import com.hiveworkshop.wc3.mdl.render3d.RenderNode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.Map;

public abstract class AbstractAnimatedNode implements AnimatedNode {
	private static final Vector4f translationHeap = new Vector4f();
	private static final Matrix4f matrixHeap = new Matrix4f();
	private static final Quaternion rotationHeap = new Quaternion();
	private static final Quaternion rotationDeltaHeap = new Quaternion();
	private static final Vector4f axisAngleHeap = new Vector4f();

	private static final Vector3f IDENTITY = new Vector3f(0, 0, 0);

	@Override
	public AddKeyframeAction createTranslationKeyframe(final RenderModel renderModel, final AnimFlag translationFlag,
			final ModelStructureChangeListener structureChangeListener) {
		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final int animationTime = renderModel.getAnimatedRenderEnvironment().getAnimationTime();
		int trackTime = renderModel.getAnimatedRenderEnvironment().getCurrentAnimation().getStart() + animationTime;
		final Integer globalSeq = ((TimeEnvironmentImpl) renderModel.getAnimatedRenderEnvironment()).getGlobalSeq();
		if (globalSeq != null) {
			trackTime = renderModel.getAnimatedRenderEnvironment().getGlobalSeqTime(globalSeq);
		}
		final int floorIndex = translationFlag.floorIndex(trackTime);
		final RenderNode renderNode = renderModel.getRenderNode(this);

		if ((floorIndex != -1) && (translationFlag.getTimes().size() > 0)
				&& (translationFlag.getTimes().get(floorIndex) == trackTime)) {
			return null;
		} else {
			final Vector3f localLocation = renderNode.getLocalLocation();
			final int insertIndex = floorIndex + 1;
			translationFlag.getTimes().add(insertIndex, trackTime);
			final Vertex keyframeValue = new Vertex(localLocation.x, localLocation.y, localLocation.z);
			translationFlag.getValues().add(insertIndex, keyframeValue);
			if (translationFlag.tans()) {
				final Vertex inTan = new Vertex(localLocation.x, localLocation.y, localLocation.z);
				translationFlag.getInTans().add(insertIndex, inTan);
				final Vertex outTan = new Vertex(localLocation.x, localLocation.y, localLocation.z);
				translationFlag.getOutTans().add(insertIndex, outTan);
				structureChangeListener.keyframeAdded(this, translationFlag, trackTime);
				return new AddKeyframeAction(this, translationFlag, trackTime, keyframeValue, inTan, outTan,
						structureChangeListener);
			} else {
				structureChangeListener.keyframeAdded(this, translationFlag, trackTime);
				return new AddKeyframeAction(this, translationFlag, trackTime, keyframeValue, structureChangeListener);
			}
		}
	}

	@Override
	public AddKeyframeAction createRotationKeyframe(final RenderModel renderModel, final AnimFlag rotationTimeline,
			final ModelStructureChangeListener structureChangeListener) {
		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final int animationTime = renderModel.getAnimatedRenderEnvironment().getAnimationTime();
		int trackTime = renderModel.getAnimatedRenderEnvironment().getCurrentAnimation().getStart() + animationTime;
		final Integer globalSeq = ((TimeEnvironmentImpl) renderModel.getAnimatedRenderEnvironment()).getGlobalSeq();
		if (globalSeq != null) {
			trackTime = renderModel.getAnimatedRenderEnvironment().getGlobalSeqTime(globalSeq);
		}
		final int floorIndex = rotationTimeline.floorIndex(trackTime);
		final RenderNode renderNode = renderModel.getRenderNode(this);

		if ((floorIndex != -1) && (rotationTimeline.getTimes().size() > 0)
				&& (rotationTimeline.getTimes().get(floorIndex) == trackTime)) {
			return null;
		} else {
			final Quaternion localRotation = renderNode.getLocalRotation();
			final int insertIndex = floorIndex + 1;
			rotationTimeline.getTimes().add(insertIndex, trackTime);
			final QuaternionRotation keyframeValue = new QuaternionRotation(localRotation.x, localRotation.y,
					localRotation.z, localRotation.w);
			rotationTimeline.getValues().add(insertIndex, keyframeValue);
			if (rotationTimeline.tans()) {
				final QuaternionRotation inTan = new QuaternionRotation(localRotation.x, localRotation.y,
						localRotation.z, localRotation.w);
				rotationTimeline.getInTans().add(insertIndex, inTan);
				final QuaternionRotation outTan = new QuaternionRotation(localRotation.x, localRotation.y,
						localRotation.z, localRotation.w);
				rotationTimeline.getOutTans().add(insertIndex, outTan);
				structureChangeListener.keyframeAdded(this, rotationTimeline, trackTime);
				return new AddKeyframeAction(this, rotationTimeline, trackTime, keyframeValue, inTan, outTan,
						structureChangeListener);
			} else {
				structureChangeListener.keyframeAdded(this, rotationTimeline, trackTime);
				return new AddKeyframeAction(this, rotationTimeline, trackTime, keyframeValue, structureChangeListener);
			}
		}
	}

	@Override
	public AddKeyframeAction createScalingKeyframe(final RenderModel renderModel, final AnimFlag scalingTimeline,
			final ModelStructureChangeListener structureChangeListener) {
		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final int animationTime = renderModel.getAnimatedRenderEnvironment().getAnimationTime();
		int trackTime = renderModel.getAnimatedRenderEnvironment().getCurrentAnimation().getStart() + animationTime;
		final Integer globalSeq = ((TimeEnvironmentImpl) renderModel.getAnimatedRenderEnvironment()).getGlobalSeq();
		if (globalSeq != null) {
			trackTime = renderModel.getAnimatedRenderEnvironment().getGlobalSeqTime(globalSeq);
		}
		final int floorIndex = scalingTimeline.floorIndex(trackTime);
		final RenderNode renderNode = renderModel.getRenderNode(this);

		if ((floorIndex != -1) && (scalingTimeline.getTimes().size() > 0)
				&& (scalingTimeline.getTimes().get(floorIndex) == trackTime)) {
			return null;
		} else {
			final Vector3f localScale = renderNode.getLocalScale();
			final int insertIndex = floorIndex + 1;
			scalingTimeline.getTimes().add(insertIndex, trackTime);
			final Vertex keyframeValue = new Vertex(localScale.x, localScale.y, localScale.z);
			scalingTimeline.getValues().add(insertIndex, keyframeValue);
			if (scalingTimeline.tans()) {
				final Vertex inTan = new Vertex(localScale.x, localScale.y, localScale.z);
				scalingTimeline.getInTans().add(insertIndex, inTan);
				final Vertex outTan = new Vertex(localScale.x, localScale.y, localScale.z);
				scalingTimeline.getOutTans().add(insertIndex, outTan);
				structureChangeListener.keyframeAdded(this, scalingTimeline, trackTime);
				return new AddKeyframeAction(this, scalingTimeline, trackTime, keyframeValue, inTan, outTan,
						structureChangeListener);
			} else {
				structureChangeListener.keyframeAdded(this, scalingTimeline, trackTime);
				return new AddKeyframeAction(this, scalingTimeline, trackTime, keyframeValue, structureChangeListener);
			}
		}
	}

	@Override
	public void updateTranslationKeyframe(final RenderModel renderModel, final double newDeltaX, final double newDeltaY,
			final double newDeltaZ, final Vector3f savedLocalTranslation) {
		// Note to future author: the reason for saved local translation is that
		// we would like to be able to undo the action of moving the animation data

		// TODO global seqs, needs separate check on AnimRendEnv, and also we must  make AnimFlag.find seek on globalSeqId
		// TODO fix cast, meta knowledge: NodeAnimationModelEditor will only be
		// constructed from a TimeEnvironmentImpl render environment, and never from the anim previewerimpl
		final TimeEnvironmentImpl timeEnvironmentImpl = (TimeEnvironmentImpl) renderModel
				.getAnimatedRenderEnvironment();
		final AnimFlag translationFlag = AnimFlag.find(getAnimFlags(), "Translation",
				timeEnvironmentImpl.getGlobalSeq());
		if (translationFlag == null) {
			return;
		}
		final int animationTime = renderModel.getAnimatedRenderEnvironment().getAnimationTime();
		int trackTime = renderModel.getAnimatedRenderEnvironment().getCurrentAnimation().getStart() + animationTime;
		final Integer globalSeq = timeEnvironmentImpl.getGlobalSeq();
		if (globalSeq != null) {
			trackTime = timeEnvironmentImpl.getGlobalSeqTime(globalSeq);
		}
		final int floorIndex = translationFlag.floorIndex(trackTime);
		final RenderNode renderNode = renderModel.getRenderNode(this);
		final AnimatedNode parent = getParent();
		if (parent != null) {
			final RenderNode parentRenderNode = renderModel.getRenderNode(parent);
			Matrix4f.invert(parentRenderNode.getWorldMatrix(), matrixHeap);

			setHeap(translationHeap, 0, 0, 0);
			translationHeap.w = 1;

			Matrix4f.transform(parentRenderNode.getWorldMatrix(), translationHeap, translationHeap);

			setTranslationHeapValues((float) (translationHeap.x + newDeltaX), (float) (translationHeap.y + newDeltaY), (float) (translationHeap.z + newDeltaZ));
			translationHeap.w = 1;

			Matrix4f.transform(matrixHeap, translationHeap, translationHeap);
		} else {
			setTranslationHeapValues((float) newDeltaX, (float) newDeltaY, (float) newDeltaZ);
			translationHeap.w = 1;
		}

		if ((floorIndex != -1) && (translationFlag.getTimes().size() > 0)
				&& (translationFlag.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final Vertex oldTranslationValue = (Vertex) translationFlag.getValues().get(floorIndex);
			addToOldOutTanValues(oldTranslationValue);

			if (savedLocalTranslation != null) {
				addToSavedTranslationHeap(savedLocalTranslation);
			}

			if (translationFlag.tans()) {
				final Vertex oldInTan = (Vertex) translationFlag.getInTans().get(floorIndex);
				addToOldOutTanValues(oldInTan);

				final Vertex oldOutTan = (Vertex) translationFlag.getOutTans().get(floorIndex);
				addToOldOutTanValues(oldOutTan);
			}
		}

	}

	@Override
	public void updateRotationKeyframe(final RenderModel renderModel, final double centerX, final double centerY,
			final double centerZ, final double radians, final byte firstXYZ, final byte secondXYZ,
			final Quaternion savedLocalRotation) {
		// Note to future author: the reason for saved local rotation is that
		// we would like to be able to undo the action of rotating the animation data

		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		// TODO fix cast, meta knowledge: NodeAnimationModelEditor will only be
		// constructed from
		// a TimeEnvironmentImpl render environment, and never from the anim previewer
		// impl
		final TimeEnvironmentImpl timeEnvironmentImpl = (TimeEnvironmentImpl) renderModel
				.getAnimatedRenderEnvironment();
		final AnimFlag rotationTimeline = AnimFlag.find(getAnimFlags(), "Rotation", timeEnvironmentImpl.getGlobalSeq());
		if (rotationTimeline == null) {
			return;
		}
		final int animationTime = renderModel.getAnimatedRenderEnvironment().getAnimationTime();
		int trackTime = renderModel.getAnimatedRenderEnvironment().getCurrentAnimation().getStart() + animationTime;
		final Integer globalSeq = timeEnvironmentImpl.getGlobalSeq();
		if (globalSeq != null) {
			trackTime = timeEnvironmentImpl.getGlobalSeqTime(globalSeq);
		}
		final int floorIndex = rotationTimeline.floorIndex(trackTime);
		final RenderNode renderNode = renderModel.getRenderNode(this);
		final byte unusedXYZ = CoordinateSystem.Util.getUnusedXYZ(firstXYZ, secondXYZ);
		final AnimatedNode parent = getParent();
		if (parent != null) {
			final RenderNode parentRenderNode = renderModel.getRenderNode(parent);
			Matrix4f.invert(parentRenderNode.getWorldMatrix(), matrixHeap);

			setHeap(axisAngleHeap, 0, 0, 0);
			axisAngleHeap.w = 1;

			Matrix4f.transform(parentRenderNode.getWorldMatrix(), axisAngleHeap, axisAngleHeap);

			switch (unusedXYZ) {
			case 0:
				addToAxisHeap(1, 0, 0);
				break;
			case 1:
				addToAxisHeap(0, -1, 0);
				break;
			case 2:
				addToAxisHeap(0, 0, -1);
				break;
			}
			axisAngleHeap.w = 1;

			Matrix4f.transform(matrixHeap, axisAngleHeap, axisAngleHeap);
		} else {
			switch (unusedXYZ) {
			case 0:
				setHeap(axisAngleHeap, 1, 0, 0);
				break;
			case 1:
				setHeap(axisAngleHeap, 0, -1, 0);
				break;
			case 2:
				setHeap(axisAngleHeap, 0, 0, -1);
				break;
			}
		}
		axisAngleHeap.w = (float) radians;
		rotationDeltaHeap.setFromAxisAngle(axisAngleHeap);

		if ((floorIndex != -1) && (rotationTimeline.getTimes().size() > 0)
				&& (rotationTimeline.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final QuaternionRotation oldTranslationValue = (QuaternionRotation) rotationTimeline.getValues()
					.get(floorIndex);
			setRotationHeapQuartValues(oldTranslationValue);
			Quaternion.mul(rotationDeltaHeap, rotationHeap, rotationHeap);

			setOuldOutTanQuart(oldTranslationValue);

			if (savedLocalRotation != null) {
				Quaternion.mul(savedLocalRotation, rotationDeltaHeap, savedLocalRotation);
			}

			if (rotationTimeline.tans()) {
				final QuaternionRotation oldInTan = (QuaternionRotation) rotationTimeline.getInTans().get(floorIndex);
				setRotationHeapQuartValues(oldInTan);
				Quaternion.mul(rotationDeltaHeap, rotationHeap, rotationHeap);
				setOuldOutTanQuart(oldInTan);

				final QuaternionRotation oldOutTan = (QuaternionRotation) rotationTimeline.getOutTans().get(floorIndex);
				setRotationHeapQuartValues(oldOutTan);
				Quaternion.mul(rotationDeltaHeap, rotationHeap, rotationHeap);
				setOuldOutTanQuart(oldOutTan);
			}
		}
	}

	@Override
	public void updateScalingKeyframe(final RenderModel renderModel, final double scaleX, final double scaleY,
			final double scaleZ, final Vector3f savedLocalScaling) {
		// Note to future author: the reason for saved local scaling is that
		// we would like to be able to undo the action of moving the animation data

		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		// TODO fix cast, meta knowledge: NodeAnimationModelEditor will only be
		// constructed from
		// a TimeEnvironmentImpl render environment, and never from the anim previewer
		// impl
		final TimeEnvironmentImpl timeEnvironmentImpl = (TimeEnvironmentImpl) renderModel
				.getAnimatedRenderEnvironment();
		final AnimFlag translationFlag = AnimFlag.find(getAnimFlags(), "Scaling", timeEnvironmentImpl.getGlobalSeq());
		if (translationFlag == null) {
			return;
		}
		final int animationTime = renderModel.getAnimatedRenderEnvironment().getAnimationTime();
		int trackTime = renderModel.getAnimatedRenderEnvironment().getCurrentAnimation().getStart() + animationTime;
		final Integer globalSeq = timeEnvironmentImpl.getGlobalSeq();
		if (globalSeq != null) {
			trackTime = timeEnvironmentImpl.getGlobalSeqTime(globalSeq);
		}
		final int floorIndex = translationFlag.floorIndex(trackTime);
		// final RenderNode renderNode = renderModel.getRenderNode(this);
		// if (parent != null) {
		// final RenderNode parentRenderNode = renderModel.getRenderNode(parent);
		// translationHeap.x = (float)scaleX *
		// parentRenderNode.getInverseWorldScale().x;
		// translationHeap.y = (float)scaleY *
		// parentRenderNode.getInverseWorldScale().y;
		// translationHeap.z = (float)scaleZ *
		// parentRenderNode.getInverseWorldScale().z;
		// translationHeap.w = 1;
		// } else {
		setTranslationHeapValues((float) scaleX, (float) scaleY, (float) scaleZ);
		// translationHeap.w = 1;
		// }

		if ((floorIndex != -1) && (translationFlag.getTimes().size() > 0)
				&& (translationFlag.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final Vertex oldTranslationValue = (Vertex) translationFlag.getValues().get(floorIndex);
			multiplyOldIntanValues(oldTranslationValue);

			if (savedLocalScaling != null) {
				multiplySavedLocalScaling(savedLocalScaling);
			}

			if (translationFlag.tans()) {
				final Vertex oldInTan = (Vertex) translationFlag.getInTans().get(floorIndex);
				multiplyOldIntanValues(oldInTan);

				final Vertex oldOutTan = (Vertex) translationFlag.getOutTans().get(floorIndex);
				multiplyOldIntanValues(oldOutTan);
			}
		}
	}

	@Override
	public void updateLocalRotationKeyframe(final int trackTime, final Integer trackGlobalSeq,
			final Quaternion localRotation) {
		// Note to future author: the reason for saved local rotation is that
		// we would like to be able to undo the action of rotating the animation data

		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final AnimFlag rotationTimeline = AnimFlag.find(getAnimFlags(), "Rotation", trackGlobalSeq);
		if (rotationTimeline == null) {
			return;
		}
		final int floorIndex = rotationTimeline.floorIndex(trackTime);

		if ((floorIndex != -1) && (rotationTimeline.getTimes().size() > 0)
				&& (rotationTimeline.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final QuaternionRotation oldTranslationValue = (QuaternionRotation) rotationTimeline.getValues()
					.get(floorIndex);
			setRotationHeapQuartValues(oldTranslationValue);
			Quaternion.mul(localRotation, rotationHeap, rotationHeap);

			setOuldOutTanQuart(oldTranslationValue);

			if (rotationTimeline.tans()) {
				final QuaternionRotation oldInTan = (QuaternionRotation) rotationTimeline.getInTans().get(floorIndex);
				setRotationHeapQuartValues(oldInTan);
				Quaternion.mul(localRotation, rotationHeap, rotationHeap);
				setOuldOutTanQuart(oldInTan);

				final QuaternionRotation oldOutTan = (QuaternionRotation) rotationTimeline.getOutTans().get(floorIndex);
				setRotationHeapQuartValues(oldOutTan);
				Quaternion.mul(localRotation, rotationHeap, rotationHeap);
				setOuldOutTanQuart(oldOutTan);
			}
		}
	}

	@Override
	public void updateLocalRotationKeyframeInverse(final int trackTime, final Integer trackGlobalSeq,
			final Quaternion localRotation) {
		// Note to future author: the reason for saved local rotation is that
		// we would like to be able to undo the action of rotating the animation data

		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final AnimFlag rotationTimeline = AnimFlag.find(getAnimFlags(), "Rotation", trackGlobalSeq);
		if (rotationTimeline == null) {
			return;
		}
		final int floorIndex = rotationTimeline.floorIndex(trackTime);

		if ((floorIndex != -1) && (rotationTimeline.getTimes().size() > 0)
				&& (rotationTimeline.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final QuaternionRotation oldTranslationValue = (QuaternionRotation) rotationTimeline.getValues()
					.get(floorIndex);
			setRotationHeapQuartValues(oldTranslationValue);
			rotationDeltaHeap.setIdentity();
			Quaternion.mulInverse(rotationDeltaHeap, localRotation, rotationDeltaHeap);
			Quaternion.mul(rotationDeltaHeap, rotationHeap, rotationHeap);

			setOuldOutTanQuart(oldTranslationValue);

			if (rotationTimeline.tans()) {
				final QuaternionRotation oldInTan = (QuaternionRotation) rotationTimeline.getInTans().get(floorIndex);
				setRotationHeapQuartValues(oldInTan);
				rotationDeltaHeap.setIdentity();
				Quaternion.mulInverse(rotationDeltaHeap, localRotation, rotationDeltaHeap);
				Quaternion.mul(rotationDeltaHeap, rotationHeap, rotationHeap);
				setOuldOutTanQuart(oldInTan);

				final QuaternionRotation oldOutTan = (QuaternionRotation) rotationTimeline.getOutTans().get(floorIndex);
				setRotationHeapQuartValues(oldOutTan);
				rotationDeltaHeap.setIdentity();
				Quaternion.mulInverse(rotationDeltaHeap, localRotation, rotationDeltaHeap);
				Quaternion.mul(rotationDeltaHeap, rotationHeap, rotationHeap);
				setOuldOutTanQuart(oldOutTan);
			}
		}
	}

	@Override
	public void updateLocalTranslationKeyframe(final int trackTime, final Integer trackGlobalSeq,
			final double newDeltaX, final double newDeltaY, final double newDeltaZ) {
		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final AnimFlag translationFlag = AnimFlag.find(getAnimFlags(), "Translation", trackGlobalSeq);
		if (translationFlag == null) {
			return;
		}
		final int floorIndex = translationFlag.floorIndex(trackTime);

		if ((floorIndex != -1) && (translationFlag.getTimes().size() > 0)
				&& (translationFlag.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final Vertex oldTranslationValue = (Vertex) translationFlag.getValues().get(floorIndex);
			addToOldOutTan(newDeltaX, newDeltaY, newDeltaZ, oldTranslationValue);

			if (translationFlag.tans()) {
				final Vertex oldInTan = (Vertex) translationFlag.getInTans().get(floorIndex);
				addToOldOutTan(newDeltaX, newDeltaY, newDeltaZ, oldInTan);

				final Vertex oldOutTan = (Vertex) translationFlag.getOutTans().get(floorIndex);
				addToOldOutTan(newDeltaX, newDeltaY, newDeltaZ, oldOutTan);
			}
		}

	}

	@Override
	public void updateLocalScalingKeyframe(final int trackTime, final Integer trackGlobalSeq,
			final Vector3f localScaling) {
		// TODO global seqs, needs separate check on AnimRendEnv, and also we must
		// make AnimFlag.find seek on globalSeqId
		final AnimFlag translationFlag = AnimFlag.find(getAnimFlags(), "Scaling", trackGlobalSeq);
		if (translationFlag == null) {
			return;
		}
		final int floorIndex = translationFlag.floorIndex(trackTime);

		if ((floorIndex != -1) && (translationFlag.getTimes().size() > 0)
				&& (translationFlag.getTimes().get(floorIndex) == trackTime)) {
			// we must change it
			final Vertex oldTranslationValue = (Vertex) translationFlag.getValues().get(floorIndex);
			multiplyOldOutTan(localScaling, oldTranslationValue);

			if (translationFlag.tans()) {
				final Vertex oldInTan = (Vertex) translationFlag.getInTans().get(floorIndex);
				multiplyOldOutTan(localScaling, oldInTan);

				final Vertex oldOutTan = (Vertex) translationFlag.getOutTans().get(floorIndex);
				multiplyOldOutTan(localScaling, oldOutTan);
			}
		}

	}

	private void addToOldOutTanValues(Vertex oldOutTan) {
		addToOldOutTan(translationHeap.x, translationHeap.y, translationHeap.z, oldOutTan);
	}

	private void setHeap(Vector4f axisAngleHeap, int x, int y, int z) {
		axisAngleHeap.x = x;
		axisAngleHeap.y = y;
		axisAngleHeap.z = z;
	}

	private void setTranslationHeapValues(float scaleX, float scaleY, float scaleZ) {
		setHeap(translationHeap, (int) scaleX, (int) scaleY, (int) scaleZ);
	}

	private void addToAxisHeap(int x, int y, int z) {
		setHeap(axisAngleHeap, (int) (axisAngleHeap.x + x), (int) (axisAngleHeap.y + y), (int) (axisAngleHeap.z + z));
	}

	private void setOuldOutTanQuart(QuaternionRotation oldOutTan) {
		oldOutTan.a = rotationHeap.x;
		oldOutTan.b = rotationHeap.y;
		oldOutTan.c = rotationHeap.z;
		oldOutTan.d = rotationHeap.w;
	}

	private void addToSavedTranslationHeap(Vector3f savedLocalTranslation) {
		savedLocalTranslation.x += translationHeap.x;
		savedLocalTranslation.y += translationHeap.y;
		savedLocalTranslation.z += translationHeap.z;
	}

	private void multiplySavedLocalScaling(Vector3f savedLocalScaling) {
		savedLocalScaling.x *= translationHeap.x;
		savedLocalScaling.y *= translationHeap.y;
		savedLocalScaling.z *= translationHeap.z;
	}

	private void multiplyOldIntanValues(Vertex oldInTan) {
		oldInTan.x *= translationHeap.x;
		oldInTan.y *= translationHeap.y;
		oldInTan.z *= translationHeap.z;
	}

	private void setRotationHeapQuartValues(QuaternionRotation oldOutTan) {
		rotationHeap.x = (float) oldOutTan.a;
		rotationHeap.y = (float) oldOutTan.b;
		rotationHeap.z = (float) oldOutTan.c;
		rotationHeap.w = (float) oldOutTan.d;
	}

	private void addToOldOutTan(double newDeltaX, double newDeltaY, double newDeltaZ, Vertex oldOutTan) {
		oldOutTan.x += newDeltaX;
		oldOutTan.y += newDeltaY;
		oldOutTan.z += newDeltaZ;
	}

	private void multiplyOldOutTan(Vector3f localScaling, Vertex oldOutTan) {
		oldOutTan.x *= localScaling.x;
		oldOutTan.y *= localScaling.y;
		oldOutTan.z *= localScaling.z;
	}

	protected abstract Map<String, AnimFlag> getAnimFlags();

}
