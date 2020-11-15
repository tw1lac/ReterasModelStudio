package com.hiveworkshop.wc3.mdl;

import java.util.List;

import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.animation.AddKeyframeAction;
import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;

public interface AnimatedNode extends TimelineContainer {

	AddKeyframeAction createTranslationKeyframe(final RenderModel renderModel, final AnimFlag translationFlag,
                                                       final ModelStructureChangeListener structureChangeListener);

	AddKeyframeAction createRotationKeyframe(final RenderModel renderModel, final AnimFlag rotationTimeline,
			final ModelStructureChangeListener structureChangeListener);

	AddKeyframeAction createScalingKeyframe(final RenderModel renderModel, final AnimFlag scalingTimeline,
			final ModelStructureChangeListener structureChangeListener);

	void updateTranslationKeyframe(final RenderModel renderModel, final double newDeltaX, final double newDeltaY,
			final double newDeltaZ, final Vector3f savedLocalTranslation);

	void updateRotationKeyframe(final RenderModel renderModel, final double centerX, final double centerY,
			final double centerZ, final double radians, final byte firstXYZ, final byte secondXYZ,
			final Quaternion savedLocalRotation);

	void updateScalingKeyframe(final RenderModel renderModel, final double scaleX, final double scaleY,
			final double scaleZ, final Vector3f savedLocalScaling);

	void updateLocalRotationKeyframe(final int trackTime, final Integer trackGlobalSeq,
			final Quaternion localRotation);

	void updateLocalRotationKeyframeInverse(final int trackTime, final Integer trackGlobalSeq,
			final Quaternion localRotation);

	void updateLocalTranslationKeyframe(final int trackTime, final Integer trackGlobalSeq,
			final double newDeltaX, final double newDeltaY, final double newDeltaZ);

	void updateLocalScalingKeyframe(final int trackTime, final Integer trackGlobalSeq,
			final Vector3f localScaling);

	boolean hasFlag(IdObject.NodeFlags flag);

	AnimatedNode getParent();

	Vertex getPivotPoint();

	List<? extends AnimatedNode> getChildrenNodes();

	String getName();

	float getRenderVisibility(AnimatedRenderEnvironment animatedRenderEnvironment);

	Vertex getRenderTranslation(AnimatedRenderEnvironment animatedRenderEnvironment);

	QuaternionRotation getRenderRotation(AnimatedRenderEnvironment animatedRenderEnvironment);

	Vertex getRenderScale(AnimatedRenderEnvironment animatedRenderEnvironment);
}
