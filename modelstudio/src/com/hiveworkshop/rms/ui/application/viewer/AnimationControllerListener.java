package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.lwjglcanvas.LWJGLCanvas;
import com.hiveworkshop.rms.ui.util.lwjglcanvas.ModelRenderer;
import com.hiveworkshop.rms.util.Quat;
import org.lwjgl.glfw.GLFWErrorCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.glfw.GLFW.glfwInit;

public class AnimationControllerListener extends JPanel {
	//	private final PerspectiveViewport perspectiveViewport;
	TimeEnvironmentImpl renderEnv;
	private ModelHandler modelHandler;
	static LWJGLCanvas canvas;

	public AnimationControllerListener(ModelHandler modelHandler, ProgramPreferences programPreferences, boolean doDefaultCamera) {
		this.modelHandler = modelHandler;
		try {
			renderEnv = modelHandler.getPreviewTimeEnv();
			modelHandler.getModelView().setVetoOverrideParticles(true);

			canvas = getLwjglCanvas();

//			perspectiveViewport = new PerspectiveViewport(modelHandler.getModelView(), modelHandler.getPreviewRenderModel(), programPreferences, modelHandler.getPreviewTimeEnv(), doDefaultCamera);
//			perspectiveViewport.setMinimumSize(new Dimension(200, 200));

			renderEnv.setAnimationTime(0);
			renderEnv.setLive(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		setLayout(new BorderLayout());
//		add(perspectiveViewport, BorderLayout.CENTER);
		add(canvas, BorderLayout.CENTER);
	}


	private LWJGLCanvas getLwjglCanvas() {
		GLFWErrorCallback.createPrint().set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize glfw");
		}

		ModelRenderer gearRenderer = new ModelRenderer(modelHandler.getModel(), modelHandler.getPreviewRenderModel(), modelHandler.getPreviewTimeEnv());

		modelHandler.getPreviewRenderModel().refreshFromEditor(new Quat(0, 0, 0, 1), new Quat(0, 0, 0, 1), new Quat(0, 0, 0, 1), null);

		LWJGLCanvas canvas = new LWJGLCanvas();
		canvas.setRenderThing(gearRenderer);
		canvas.setSize(640, 480);
		return canvas;
	}

//	public void setModel(ModelView modelView) {
//		this.modelView = modelView;
//		perspectiveViewport.setModel(modelView);
//		reload();
//	}

	public void setTitle(String title) {
		setBorder(BorderFactory.createTitledBorder(title));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		perspectiveViewport.paint(perspectiveViewport.getGraphics());
	}

	public void reloadAllTextures() {
//		perspectiveViewport.reloadAllTextures();
	}

	public void reload() {
//		perspectiveViewport.reloadTextures();
	}

	public void setAnimation(Animation animation) {
		renderEnv.setAnimation(animation);
	}

	public void setAnimationTime(int time) {
		renderEnv.setAnimationTime(time);
	}

	public void playAnimation() {
		renderEnv.setRelativeAnimationTime(0);
		renderEnv.setLive(true);
	}

	public void setLoop(AnimationControllerListener.LoopType loopType) {
		renderEnv.setLoopType(loopType);
	}

	public void setSpeed(float speed) {
		renderEnv.setAnimationSpeed(speed);
	}

	public Animation getCurrentAnimation() {
		return renderEnv.getCurrentAnimation();
	}

	public void setLevelOfDetail(int levelOfDetail) {
//		perspectiveViewport.setLevelOfDetail(levelOfDetail);
	}

	public BufferedImage getBufferedImage() {
//		return perspectiveViewport.getBufferedImage();
		return new BufferedImage(30, 30, 1);
	}

	public enum LoopType {
		DEFAULT_LOOP, ALWAYS_LOOP, NEVER_LOOP
	}
}
