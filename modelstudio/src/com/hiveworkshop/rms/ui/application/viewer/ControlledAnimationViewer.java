package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import org.lwjgl.LWJGLException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ControlledAnimationViewer extends JPanel implements AnimationControllerListener {
	private ModelView mdlDisp;
	private final AnimatedPerspectiveViewport perspectiveViewport;
	UggRenderEnv uggRenderEnv;

	public ControlledAnimationViewer(final ModelView mdlDisp, final ProgramPreferences programPreferences,
			final boolean doDefaultCamera) {
		this.mdlDisp = mdlDisp;
		try {
			uggRenderEnv = new UggRenderEnv();
			perspectiveViewport = new AnimatedPerspectiveViewport(mdlDisp, programPreferences, uggRenderEnv, doDefaultCamera);
			perspectiveViewport.setMinimumSize(new Dimension(200, 200));
			perspectiveViewport.setAnimationTime(0);
			perspectiveViewport.setLive(true);
//			uggRenderEnv = (UggRenderEnv) mdlDisp.getEditorRenderModel().getAnimatedRenderEnvironment();
			System.out.println(".getAnimatedRenderEnv(): " + mdlDisp.getEditorRenderModel().getAnimatedRenderEnvironment());
			System.out.println("uggRenderEnv: " + uggRenderEnv);
		} catch (final LWJGLException e) {
			throw new RuntimeException(e);
		}
		setLayout(new BorderLayout());
		add(perspectiveViewport, BorderLayout.CENTER);
	}

	public void setModel(final ModelView modelView) {
		mdlDisp = modelView;
		perspectiveViewport.setModel(modelView);
		reload();
	}

	public void setTitle(final String title) {
		setBorder(BorderFactory.createTitledBorder(title));
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// perspectiveViewport.repaint();
		perspectiveViewport.paint(perspectiveViewport.getGraphics());
	}

	public void reloadAllTextures() {
		perspectiveViewport.reloadAllTextures();
	}

	public void reload() {
		perspectiveViewport.reloadTextures();
	}

	@Override
	public void setAnimation(final Animation animation) {
//		perspectiveViewport.setAnimation(animation);
		uggRenderEnv.setAnimation(animation);
	}

	public void setAnimationTime(int time) {
//		perspectiveViewport.setAnimationTime(time);
		uggRenderEnv.setAnimationTime(time);
	}

	@Override
	public void playAnimation() {
//		perspectiveViewport.setAnimationTime(0);
		uggRenderEnv.setAnimationTime(0);

	}

	@Override
	public void setLoop(final LoopType loopType) {
//		perspectiveViewport.setLoopType(loopType);
		uggRenderEnv.setLoopType(loopType);
	}

	@Override
	public void setSpeed(final float speed) {
//		perspectiveViewport.setAnimationSpeed(speed);
		uggRenderEnv.setAnimationSpeed(speed);
	}

	public Animation getCurrentAnimation() {
//		return perspectiveViewport.getCurrentAnimation();
		return uggRenderEnv.getCurrentAnimation();
	}

//	public void setSpawnParticles(final boolean b) {
//		perspectiveViewport.setSpawnParticles(b);
//	}

	@Override
	public void setLevelOfDetail(final int levelOfDetail) {
		perspectiveViewport.setLevelOfDetail(levelOfDetail);
	}

	public BufferedImage getBufferedImage() {
		return perspectiveViewport.getBufferedImage();
	}
}
