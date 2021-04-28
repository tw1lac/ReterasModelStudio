package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.lwjglcanvas.LWJGLCanvas;
import com.hiveworkshop.rms.ui.util.lwjglcanvas.ModelRenderer;
import com.hiveworkshop.rms.util.Quat;
import org.lwjgl.glfw.GLFWErrorCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.glfw.GLFW.glfwInit;

public class ControlledAnimationViewer extends JPanel implements AnimationControllerListener {
	private ModelView modelView;
	//	private final AnimatedPerspectiveViewport perspectiveViewport;
	ComPerspRenderEnv renderEnv;
	static LWJGLCanvas canvas;
	RenderModel renderModel;
	ModelRenderer gearRenderer;
	int animationTime = 0;

	public ControlledAnimationViewer(final ModelView modelView, final ProgramPreferences programPreferences, final boolean doDefaultCamera) {
		if (canvas != null) {
			canvas.destroy();
		}
		this.modelView = modelView;
		try {
			renderEnv = new ComPerspRenderEnv();
			modelView.setVetoOverrideParticles(true);
			renderModel = new RenderModel(modelView.getModel(), modelView);
//			perspectiveViewport = new AnimatedPerspectiveViewport(modelView, renderModel, programPreferences, renderEnv, doDefaultCamera);
//			perspectiveViewport.setMinimumSize(new Dimension(200, 200));
			renderEnv.setAnimationTime(0);
			renderEnv.setLive(true);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		canvas = getLwjglCanvas();
		setLayout(new BorderLayout());
//		add(getMainPanel(), BorderLayout.CENTER);
		add(canvas, BorderLayout.CENTER);
	}

	public void setModel(final ModelView modelView) {
		this.modelView = modelView;
//		perspectiveViewport.setModel(modelView);
		reload();
	}

	public void setTitle(final String title) {
		setBorder(BorderFactory.createTitledBorder(title));
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// perspectiveViewport.repaint();
//		perspectiveViewport.paint(perspectiveViewport.getGraphics());
	}

	public void reloadAllTextures() {
		//perspectiveViewport.reloadAllTextures();
	}

	public void reload() {
		//perspectiveViewport.reloadTextures();
	}

	private LWJGLCanvas getLwjglCanvas() {
		GLFWErrorCallback.createPrint().set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize glfw");
		}

		gearRenderer = new ModelRenderer(modelView.getModel(), renderModel, renderEnv);

		LWJGLCanvas canvas = new LWJGLCanvas();
		canvas.setRenderThing(gearRenderer);
		canvas.setSize(640, 480);
		return canvas;
	}

	public void setAnimationTime(int time) {
//		perspectiveViewport.setAnimationTime(time);
		renderEnv.setAnimationTime(time);
	}

	@Override
	public void setAnimation(final Animation animation) {
//		perspectiveViewport.setAnimation(animation);
		renderEnv.setAnimation(animation);
		if (gearRenderer != null) {
			gearRenderer.setAnimate(false);
		}

		renderModel.refreshFromEditor(renderEnv, new Quat(0, 0, 0, 1), new Quat(0, 0, 0, 1), new Quat(0, 0, 0, 1), null);
	}

	@Override
	public void playAnimation() {
//		perspectiveViewport.setAnimationTime(0);
		renderEnv.setLive(true);
		if (gearRenderer != null) {
			gearRenderer.setAnimate(true);
		}
//		animationTime += 100;
//		if(renderEnv.getCurrentAnimation()!= null){
//			animationTime = (100 + animationTime)%renderEnv.getCurrentAnimation().length();
//			System.out.println("animationTime: "+ animationTime);
//		}
		renderEnv.setAnimationTime(animationTime);


//		renderModel.updateNodes(false, false);

	}

	@Override
	public void setLoop(final LoopType loopType) {
//		perspectiveViewport.setLoopType(loopType);
		renderEnv.setLoopType(loopType);
	}

	@Override
	public void setSpeed(final float speed) {
//		perspectiveViewport.setAnimationSpeed(speed);
		renderEnv.setAnimationSpeed(speed);
	}

	public Animation getCurrentAnimation() {
//		return perspectiveViewport.getCurrentAnimation();
		return renderEnv.getCurrentAnimation();
	}

//	public void setSpawnParticles(final boolean b) {
//		perspectiveViewport.setSpawnParticles(b);
//	}

	@Override
	public void setLevelOfDetail(final int levelOfDetail) {
		//perspectiveViewport.setLevelOfDetail(levelOfDetail);
	}

	//	public BufferedImage getBufferedImage() {
//		return perspectiveViewport.getBufferedImage();
//	}
	public BufferedImage getBufferedImage() {
		return new BufferedImage(30, 30, 1);
	}
}
