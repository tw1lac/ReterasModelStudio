package com.hiveworkshop.rms.ui.application.edit.uv.panel;

import com.hiveworkshop.rms.editor.model.Bitmap;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.ExtLog;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.viewer.ObjectRenderers.CubePainter;
import com.hiveworkshop.rms.ui.application.viewer.ObjectRenderers.GridPainter2;
import com.hiveworkshop.rms.ui.application.viewer.TextureThing;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class ImageBufferFiller {

	private RenderModel renderModel;

	private TextureThing textureThing;

	private boolean texLoaded = false;

	private Class<? extends Throwable> lastThrownErrorClass;
	private boolean wantReload = false;
	private boolean wantReloadAll = false;
	private int popupCount = 0;
	private ProgramPreferences programPreferences;

	private long lastExceptionTimeMillis = 0;
	private float xRatio;
	private float yRatio;
	private GridPainter2 gridPainter;

	private UVRenderer geosetRenderer;
	private Bitmap currTexture;

	ExtLog currentExt = new ExtLog(Vec3.ZERO, Vec3.ZERO, 0);
	ExtLog modelExtent = new ExtLog(Vec3.ZERO, Vec3.ZERO, 0);

	float[] backgroundColor = new float[4];
	Color bgColor;

	public ImageBufferFiller(){
		this.programPreferences = ProgramGlobals.getPrefs();
		gridPainter = new GridPainter2();

//		geosetRenderer = new UVRenderer(cameraManager, programPreferences);
		geosetRenderer = new UVRenderer(null, programPreferences);

		bgColor = ProgramGlobals.getEditorColorPrefs().getColor(ColorThing.BACKGROUND_COLOR);
	}

	private void drawBackgroundImage(){
		glShadeModel(GL11.GL_FLAT);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glColor4f(1, 1, 1, 1);

		if (currTexture != null) {
			glEnable(GL11.GL_TEXTURE_2D);
			textureThing.bindTexture(currTexture, 0);
		} else {
			glDisable(GL11.GL_TEXTURE_2D);
		}
		glBegin(GL_TRIANGLES);

		GL11.glNormal3f(0, 0, 1);

//		glColor4f(1, 0, 0, .5f);
		addTextureCoord(0, 0);
		addTextureCoord(0, 1);
		addTextureCoord(1, 0);

//		glColor4f(0, 0, 1, .5f);

		addTextureCoord(0, 1);
		addTextureCoord(1, 0);
		addTextureCoord(1, 1);
		glEnd();

	}

	private void addTextureCoord(float x, float y){
		GL11.glTexCoord2f(x, y);
		GL11.glVertex3f(x, y, 0);
	}



	private void addLamp(float lightValue, float x, float y, float z, int glLight) {
		FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
		lightColor.put(lightValue).put(lightValue).put(lightValue).put(1f).flip();
		FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
		lightPos.put(x).put(y).put(z).put(1f).flip();
		glLight(glLight, GL_DIFFUSE, lightColor);
		glLight(glLight, GL_POSITION, lightPos);
	}

	public void setModel(ModelView modelView, RenderModel renderModel, boolean loadDefaultCamera) {
		if (renderModel != null) {
			EditableModel model = modelView.getModel();
			TextureThing textureThing = new TextureThing(programPreferences);
			geosetRenderer.updateModel(renderModel, modelView, textureThing);
//			cameraHandler.loadDefaultCameraFor();
		}
	}


	public TextureThing getTextureThing() {
		return textureThing;
	}

	public void initGL() {
		try {
			if ((programPreferences == null) || programPreferences.textureModels()) {
				forceReloadTextures();
			}
		} catch (final Throwable e) {
			JOptionPane.showMessageDialog(null, "initGL failed because of this exact reason:\n"
					+ e.getClass().getSimpleName() + ": " + e.getMessage());
			throw new RuntimeException(e);
		}
		// JAVA 9+ or maybe WIN 10 allow ridiculous virtual pixes, this combination of
		// old library code and java std library code give me a metric for the ridiculous ratio:
		xRatio = (float) (Display.getDisplayMode().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		yRatio = (float) (Display.getDisplayMode().getHeight() / Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		// These ratios will be wrong and users will see corrupted visuals
		// (bad scale, only fits part of window, etc)
		// if they are using Windows 10 differing UI scale per monitor.
		// I don't think I have an API to query that information yet, though.
	}


	public void paintGL(ImageViewerCanvas imageViewerCanvas, boolean autoRepainting) {
		if ((System.currentTimeMillis() - lastExceptionTimeMillis) < 5000) {
			System.out.println("not rendering :O");
			if ((System.currentTimeMillis() - lastExceptionTimeMillis) < 100) {
				System.err.println("AnimatedPerspectiveViewport omitting frames due to avoid Exception log spam");
			}
			return;
		}
		reloadIfNeeded();
		try {
			glViewport(0, 0, (int) (imageViewerCanvas.getWidth() * xRatio), (int) (imageViewerCanvas.getHeight() * yRatio));
			enableGlThings(GL_DEPTH_TEST, GL_COLOR_MATERIAL, GL_LIGHTING, GL_LIGHT0, GL_LIGHT1, GL_NORMALIZE);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDepthMask(true);
			if ((programPreferences != null) && (programPreferences.getPerspectiveBackgroundColor() != null)) {
				programPreferences.getPerspectiveBackgroundColor().getColorComponents(backgroundColor);
				float[] colorComponents = ProgramGlobals.getEditorColorPrefs().getColorComponents(ColorThing.BACKGROUND_COLOR);
				glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], autoRepainting ? 1.0f : 0.0f);
			} else {
				glClearColor(.3f, .3f, .3f, autoRepainting ? 1.0f : 0.0f);
			}
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//			glMatrixMode(GL_MODELVIEW);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();

			imageViewerCanvas.getCameraManager().setUpCamera();

			FloatBuffer ambientColor = BufferUtils.createFloatBuffer(4);
			ambientColor.put(0.6f).put(0.6f).put(0.6f).put(1f).flip();
			glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientColor);
//			addLamp(0.8f, 40.0f, 200.0f, 80.0f, GL_LIGHT0);
//			addLamp(0.2f, -100.0f, 200.5f, 0.5f, GL_LIGHT1);
			addLamp(0.8f, 80.0f, 40.0f, 200.0f, GL_LIGHT0);
			addLamp(0.2f, 0.5f, -100.0f, 200.5f, GL_LIGHT1);


			GL11.glDisable(GL_LIGHTING);
			drawBackgroundImage();
			if (programPreferences != null && programPreferences.showPerspectiveGrid()) {
				gridPainter.fillGridBuffer(null);
			}

			geosetRenderer.doRender();



			if (imageViewerCanvas.getMouseListenerThing().isSelecting()) {
				CubePainter.paintRekt(imageViewerCanvas.getMouseListenerThing().getStartPGeo(), imageViewerCanvas.getMouseListenerThing().getEndPGeo1(), imageViewerCanvas.getMouseListenerThing().getEndPGeo2(), imageViewerCanvas.getMouseListenerThing().getEndPGeo3(), imageViewerCanvas.getCameraManager());
			}
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);

		} catch (final Throwable e) {
			lastExceptionTimeMillis = System.currentTimeMillis();
			if ((lastThrownErrorClass == null) || (lastThrownErrorClass != e.getClass())) {
				lastThrownErrorClass = e.getClass();
				popupCount++;
				if (popupCount < 10) {
					ExceptionPopup.display(e);
				}
			}
			System.out.println("Failed to render");
			throw new RuntimeException(e);
		}
	}

	private void reloadIfNeeded() {
		if (wantReloadAll) {
			wantReloadAll = false;
			wantReload = false;// If we just reloaded all, no need to reload some.
			try {
				initGL();// Re-overwrite textures
			} catch (final Exception e) {
				e.printStackTrace();
				ExceptionPopup.display("Error loading textures:", e);
			}
		} else if (wantReload) {
			wantReload = false;
			try {
				forceReloadTextures();
			} catch (final Exception e) {
				e.printStackTrace();
				ExceptionPopup.display("Error loading new texture:", e);
			}
		} else if (!texLoaded && ((programPreferences == null) || programPreferences.textureModels())) {
			forceReloadTextures();
		}
	}
	protected void forceReloadTextures() {
		texLoaded = true;
		if (textureThing != null) {
			textureThing.reMakeTextureMap(renderModel.getModel());
		}
	}


	public boolean renderTextures() {
		return texLoaded && ((programPreferences == null) || programPreferences.textureModels());
	}

	public void reloadTextures() {
		wantReload = true;
	}

	public void reloadAllTextures() {
		wantReloadAll = true;
	}

	private void enableGlThings(int... thing) {
		for (int t : thing) {
			glEnable(t);
		}
	}

	private void disableGlThings(int... thing) {
		for (int t : thing) {
			glDisable(t);
		}
	}
}
