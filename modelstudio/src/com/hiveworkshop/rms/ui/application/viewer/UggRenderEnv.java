package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;

//public class UggRenderEnv implements AnimatedRenderEnvironment {
public class UggRenderEnv extends TimeEnvironmentImpl {
	private int animationTime;
	private Animation animation;
	private long lastUpdateMillis = System.currentTimeMillis();
	private AnimationControllerListener.LoopType loopType = AnimationControllerListener.LoopType.DEFAULT_LOOP;
	private boolean looping = true;
	float animSpeed = 1f;
	boolean isLive = false;

	public UggRenderEnv() {
		super(0, 1);
		animationTime = 0;
		animation = null;
	}

	@Override
	public int getGlobalSeqTime(final int globalSeqLength) {
		if (globalSeqLength == 0) {
			return 0;
		}
		return (int) (lastUpdateMillis % globalSeqLength);
	}

	@Override
	public int getAnimationTime() {
		return animationTime;
	}

	public int setAnimationTime(int newTime) {
		animationTime = newTime;
		return animationTime;
	}

	public int stepAnimationTime(int timeStep) {
		animationTime = animationTime + timeStep;
		return animationTime;
	}

	@Override
	public Animation getCurrentAnimation() {
		return animation;
	}

	public Animation setAnimation(Animation animation) {
		this.animation = animation;
		updateLastMillis();
		if (loopType == AnimationControllerListener.LoopType.DEFAULT_LOOP) {
			looping = animation != null && !animation.isNonLooping();
		}
		return this.animation;
	}

	public void updateLastMillis(){
		lastUpdateMillis = System.currentTimeMillis();
	}


	public void setLoopType(final AnimationControllerListener.LoopType loopType) {
		this.loopType = loopType;
		switch (loopType) {
			case ALWAYS_LOOP -> looping = true;
			case DEFAULT_LOOP -> looping = animation != null && !animation.isNonLooping();
			case NEVER_LOOP -> looping = false;
		}
	}

	public void setAlwaysLooping() {
		this.loopType = AnimationControllerListener.LoopType.ALWAYS_LOOP;
		looping = true;
	}

	public void setDefaultLooping() {
		this.loopType = AnimationControllerListener.LoopType.DEFAULT_LOOP;
		looping = looping = animation != null && !animation.isNonLooping();
	}

	public void setNeverLooping() {
		this.loopType = AnimationControllerListener.LoopType.NEVER_LOOP;
		looping = false;
	}

	public void setAnimationSpeed(final float speed) {
		animSpeed = speed;
	}

	public void updateAnimationTime() {
		long timeSkip = System.currentTimeMillis() - lastUpdateMillis;
		updateLastMillis();
		if ((animation != null) && (animation.length() > 0)) {
//			System.out.println("animationTime: " + animationTime);
			if (looping) {
				animationTime = (int) ((animationTime + (long) (timeSkip * animSpeed)) % animation.length());
			} else {
				animationTime = Math.min(animation.length(), (int) (animationTime + (timeSkip * animSpeed)));
			}
		}
	}
}
