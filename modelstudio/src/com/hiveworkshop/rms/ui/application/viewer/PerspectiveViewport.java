package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.render3d.RenderParticleEmitter2;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.animation.Sequence;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.renderparts.RenderGeoset;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.ui.preferences.EditorColorPrefs;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.BetterAWTGLCanvas;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class PerspectiveViewport extends BetterAWTGLCanvas {
	private final float[] whiteDiffuse = {1f, 1f, 1f, 1f};
	private final float[] posSun = {0.0f, 10.0f, 0.0f, 1.0f};
	private RenderModel renderModel;
	private ModelView modelView;
	private TimeEnvironmentImpl renderEnv;

	private TextureThing textureThing;

	private final CameraHandler cameraHandler;
	private Timer paintTimer;
	private boolean mouseInBounds = false;
	private boolean texLoaded = false;

	private Class<? extends Throwable> lastThrownErrorClass;
	private boolean wantReload = false;
	private boolean wantReloadAll = false;
	private int popupCount = 0;
	private final ProgramPreferences programPreferences;
	private final EditorColorPrefs colorPrefs;

	private long lastExceptionTimeMillis = 0;
	private int levelOfDetail = -1;

	private float xRatio;
	private float yRatio;
	private final MouseListenerThing mouseAdapter;
	private final VertRendererThing vertRendererThing;
	private final BoneRenderThing2 boneRenderThing;

	// Used for the different rendering functions.
	// Call ensureBuffersCapacity(vertices) to ensure there is space.
	// Effectively every vertex can contain up to:
	//   Vec3 position
	//   Vec2 uv
	//   Vec3 normal
	//   Vec4 color
	// "up to" because for example the normals rendering only uses the position and the RGB part of the color.
	private FloatBuffer positionsBuffer;
	private FloatBuffer uvsBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer colorsBuffer;

	ExtLog currentExt = new ExtLog(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);
	ExtLog modelExtent = new ExtLog(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0);

	public PerspectiveViewport() throws LWJGLException {
		super();
		this.programPreferences = ProgramGlobals.getPrefs();
		this.colorPrefs = ProgramGlobals.getEditorColorPrefs();
		cameraHandler = new CameraHandler(this);
		vertRendererThing = new VertRendererThing(cameraHandler.getPixelSize());
		boneRenderThing = new BoneRenderThing2();

		mouseAdapter = new MouseListenerThing(cameraHandler, programPreferences);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
		addKeyListener(getShortcutKeyListener());

		setBackground(ProgramGlobals.getEditorColorPrefs().getColor(ColorThing.BACKGROUND_COLOR));
		setMinimumSize(new Dimension(200, 200));


		paintTimer = new Timer(16, e -> {
			repaint();
			if (!isShowing()) {
				paintTimer.stop();
			}
		});
		paintTimer.start();
//		Timer clickTimer = new Timer(16, e -> cameraHandler.clickTimerAction());
	}

	public PerspectiveViewport setModel(ModelView modelView, RenderModel renderModel, boolean loadDefaultCamera) {
		this.renderModel = renderModel;
		if(renderModel != null){
			textureThing = new TextureThing(modelView, programPreferences);
			renderEnv = renderModel.getTimeEnvironment();

			this.modelView = modelView;
			modelExtent.setMinMax(modelView.getModel().getExtents());
			setCurrentExtent();
			if (loadDefaultCamera) {
				ViewportHelpers.findDefaultAnimation(modelView, renderEnv);
				cameraHandler.loadDefaultCameraFor(ViewportHelpers.getBoundsRadius(renderEnv, modelExtent));
			}

			this.renderModel.setCameraHandler(cameraHandler);
			this.renderModel.refreshFromEditor(getParticleTextureInstance());
		} else {
			renderEnv = null;
		}
		return this;
	}

	public Particle2TextureInstance getParticleTextureInstance() {
		return new Particle2TextureInstance(textureThing, modelView, programPreferences);
	}

	public TextureThing getTextureThing() {
		return textureThing;
	}

	public void setLevelOfDetail(int levelOfDetail) {
		this.levelOfDetail = levelOfDetail;
	}

	public void setModel(final ModelView modelView) {
		renderEnv.setAnimation(null);
		this.modelView = modelView;
//		renderModel = new RenderModel(modelView.getModel(), modelView, renderEnv);
		renderModel = new RenderModel(modelView.getModel(), modelView);
		renderEnv = renderModel.getTimeEnvironment();
		renderModel.setCameraHandler(cameraHandler);
		modelExtent.setDefault().setMinMax(modelView.getModel().getExtents());
		setCurrentExtent();
		if (modelView.getModel().getAnims().size() > 0) {
			renderEnv.setAnimation(modelView.getModel().getAnim(0));
		}
		Vec3 maxExt = currentExt.getMaximumExtent();
		double boundsRadius = ViewportHelpers.getBoundsRadius(renderEnv, modelExtent);
		double radius;
		if (modelView.getModel().getAnims().size() < 2) {
			System.out.println("model? maxExt: " + maxExt.length() + ", boundsR: " + boundsRadius);
			radius = maxExt.length();
		} else {
			System.out.println("dodad? maxExt: " + maxExt.length() + ", boundsR: " + boundsRadius);
//			radius = boundsRadius/2;
			radius = boundsRadius;
		}
		cameraHandler.loadDefaultCameraFor(radius);

		reloadAllTextures();
	}


	public CameraHandler getCameraHandler() {
		return cameraHandler;
	}

	public MouseListenerThing getMouseListenerThing() {
		return mouseAdapter;
	}

	public PerspectiveViewport setAllowRotation(boolean allow) {
		cameraHandler.setAllowRotation(allow);
		return this;
	}

	public PerspectiveViewport setAllowToggleOrtho(boolean allow) {
		cameraHandler.setAllowToggleOrtho(allow);
		return this;
	}

	public PerspectiveViewport toggleOrtho() {
		cameraHandler.toggleOrtho();
		return this;
	}

	protected KeyAdapter getShortcutKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);

				setCurrentExtent();
//				System.out.println(modelExtent);
//				System.out.println(currentExt);
				double rad = ViewportHelpers.getBoundsRadius(renderEnv, modelExtent);
				if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
					// Top view
					System.out.println("VK_NUMPAD7");
//					cameraHandler.setCameraTop(currentExt.getMaximumExtent().length());
					cameraHandler.setCameraTop(rad);
				}
				if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
					// Front view
					System.out.println("VK_NUMPAD1");
//					cameraHandler.setCameraFront(currentExt.getMaximumExtent().length());
					cameraHandler.setCameraFront(rad);
				}
				if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
					// Side view
					System.out.println("VK_NUMPAD3");
//					cameraHandler.setCameraSide(currentExt.getMaximumExtent().length());
					cameraHandler.setCameraSide(rad);
				}
				if (e.getKeyCode() == KeyEvent.VK_O) {
					// Orto Mode
					cameraHandler.toggleOrtho();
					System.out.println("VK_O");
				}
				if (e.getKeyCode() == KeyEvent.VK_X) {
					if (e.isControlDown()) {
						cameraHandler.rot(-45, 0, 0);
					} else {
						cameraHandler.rot(45, 0, 0);
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_Y) {
					if (e.isControlDown()) {
						cameraHandler.rot(0, -45, 0);
					} else {
						cameraHandler.rot(0, 45, 0);
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_Q) {
					if (e.isControlDown()) {
						cameraHandler.rot(0, 0, -45);
					} else {
						cameraHandler.rot(0, 0, 45);
					}
				}
			}
		};
	}

	private void setCurrentExtent() {
		if (renderEnv != null) {
			Animation animation = renderEnv.getCurrentAnimation();
			if ((animation != null) && animation.getExtents() != null) {
				currentExt = animation.getExtents();
			}
		}
		if (currentExt.getMaximumExtent().distance(new Vec3(0, 0, 0)) < 1) {
			currentExt = modelExtent;
		}
	}

	public void translate(final double a, final double b) {
		cameraHandler.translate(a, b);
	}

	protected void forceReloadTextures() {
		texLoaded = true;
		if (textureThing != null && renderModel != null) {
			textureThing.reMakeTextureMap();
			renderModel.refreshFromEditor(getParticleTextureInstance());
		}
	}



	@Override
	protected void exceptionOccurred(final LWJGLException exception) {
		super.exceptionOccurred(exception);
		exception.printStackTrace();
	}

	@Override
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
		// These ratios will be wrong and users will see corrupted visuals (bad scale, only fits part of window,
		// etc) if they are using Windows 10 differing UI scale per monitor. I don't think I have an API
		// to query that information yet, though.
	}

	@Override
	public void paintGL() {
		paintGL(true);
	}

	public void paintGL(final boolean autoRepainting) {
		setSize(getParent().getSize());
		if ((System.currentTimeMillis() - lastExceptionTimeMillis) < 5000) {
			System.out.println("not rendering :O");
			if ((System.currentTimeMillis() - lastExceptionTimeMillis) < 100) {
				System.err.println("AnimatedPerspectiveViewport omitting frames due to avoid Exception log spam");
			}
			return;
		}
		reloadIfNeeded();
		try {
			if (renderModel != null) {
				updateRenderModel();
				glViewport(0, 0, (int) (getWidth() * xRatio), (int) (getHeight() * yRatio));
				enableGlThings(GL_DEPTH_TEST, GL_COLOR_MATERIAL, GL_LIGHTING, GL_LIGHT0, GL_LIGHT1, GL_NORMALIZE);

				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glDepthMask(true);
				if ((programPreferences != null) && (programPreferences.getPerspectiveBackgroundColor() != null)) {
					float[] colorComponents = ProgramGlobals.getEditorColorPrefs().getColorComponents(ColorThing.BACKGROUND_COLOR);
					glClearColor(colorComponents[0], colorComponents[1], colorComponents[2], autoRepainting ? 1.0f : 1.0f);
				} else {
					glClearColor(.3f, .3f, .3f, autoRepainting ? 1.0f : 1.0f);
				}
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//			glMatrixMode(GL_MODELVIEW);
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();

				cameraHandler.setUpCamera();

				FloatBuffer ambientColor = BufferUtils.createFloatBuffer(4);
				ambientColor.put(0.6f).put(0.6f).put(0.6f).put(1f).flip();
				glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientColor);
//			addLamp(0.8f, 40.0f, 200.0f, 80.0f, GL_LIGHT0);
//			addLamp(0.2f, -100.0f, 200.5f, 0.5f, GL_LIGHT1);
				addLamp(0.8f, 80.0f, 40.0f, 200.0f, GL_LIGHT0);
				addLamp(0.2f, 0.5f, -100.0f, 200.5f, GL_LIGHT1);

				if (programPreferences != null && programPreferences.showPerspectiveGrid()) {
					paintGridFloor();
				}

				int formatVersion = modelView.getModel().getFormatVersion();
				renderGeosets(modelView.getVisibleGeosets(), formatVersion, false);

				if (modelView.getHighlightedGeoset() != null) {
					drawHighlightedGeosets(formatVersion);
				}

				if ((programPreferences != null)) {
					if (programPreferences.showNormals()) {
						renderNormals(formatVersion);
					}
					if (programPreferences.show3dVerts()) {
						renderVertDots(formatVersion);
					}
					if (programPreferences.getRenderParticles()) {
						renderParticles();
					}


//				drawUglyTestLine();

				}
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
				if (autoRepainting) {
					paintAndUpdate();
				}
			}
		} catch (final Throwable e) {
			paintTimer.stop();
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

	private void updateRenderModel() {
		if (renderEnv.isLive()) {
			Sequence currentSequence = renderEnv.getCurrentSequence();
//			System.out.println("update renderEnv! at " + renderEnv.getEnvTrackTime() + " " + currentSequence + " " + currentSequence.getStart() + " - " + currentSequence.getEnd() +  " (" + currentSequence.getLength() + ") ");

			renderEnv.updateAnimationTime();
			renderModel.updateNodes(false, programPreferences.getRenderParticles());
		} else if (ProgramGlobals.getSelectionItemType() == SelectionItemTypes.ANIMATE) {
			renderModel.updateNodes(false, programPreferences.getRenderParticles());
		}
		if (modelView.isGeosetsVisible()) {
			renderModel.updateGeosets();
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

	private void paintAndUpdate() {
		try {
//			System.out.println("swapping buffers");
			swapBuffers();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		if (isShowing() && !paintTimer.isRunning()) {
			paintTimer.restart();
		} else if (!isShowing() && paintTimer.isRunning()) {
			paintTimer.stop();
		}
	}

	private void addLamp(float lightValue, float x, float y, float z, int glLight) {
		FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
		lightColor.put(lightValue).put(lightValue).put(lightValue).put(1f).flip();
		FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
		lightPos.put(x).put(y).put(z).put(1f).flip();
		glLight(glLight, GL_DIFFUSE, lightColor);
		glLight(glLight, GL_POSITION, lightPos);
	}

	private void renderParticles() {
		if (renderTextures()) {
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
			glEnable(GL11.GL_TEXTURE_2D);
		}
		GL11.glDepthMask(false);
		enableGlThings(GL_BLEND, GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);

//		renderModel.getParticleShader().use();
		for (final RenderParticleEmitter2 particle : renderModel.getRenderParticleEmitters2()) {
//			System.out.println("renderParticles");
			particle.render(renderModel, renderModel.getParticleShader());
		}
	}

	private void paintGridFloor() {
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		disableGlThings(GL_ALPHA_TEST, GL_TEXTURE_2D, GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float lineLength = 200;
		float lineSpacing = 50;
		float numberOfLines = 5;
		glColor3f(255f, 1f, 255f);
		glColor4f(.7f, .7f, .7f, .4f);
		glBegin(GL11.GL_LINES);
		GL11.glNormal3f(0, 0, 0);
		float lineSpread = (numberOfLines + 1) * lineSpacing / 2;

		glColor4f(.7f, 1f, .7f, .4f);
		for (float x = -lineSpread + lineSpacing; x < lineSpread; x += lineSpacing) {
//			GL11.glVertex3f(-lineLength, 0, x);
//			GL11.glVertex3f(lineLength, 0, x);
			GL11.glVertex3f(x, -lineLength, 0);
			GL11.glVertex3f(x, lineLength, 0);
		}

		glColor4f(1f, .7f, .7f, .4f);
		for (float y = -lineSpread + lineSpacing; y < lineSpread; y += lineSpacing) {
			GL11.glVertex3f(-lineLength, y, 0);
			GL11.glVertex3f(lineLength, y, 0);
		}
		glEnd();

		cameraMarkerPainter();
	}

	private void setStandardColors(GeosetAnim geosetAnim, float geosetAnimVisibility, TimeEnvironmentImpl timeEnvironment, Layer layer) {
		if (timeEnvironment != null && timeEnvironment.getCurrentSequence() != null) {
			float layerVisibility = layer.getRenderVisibility(timeEnvironment);
			float alphaValue = geosetAnimVisibility * layerVisibility;
			if (/* geo.getMaterial().isConstantColor() && */ geosetAnim != null) {
				Vec3 renderColor = geosetAnim.getRenderColor(timeEnvironment);
				if (renderColor != null) {
					if (layer.getFilterMode() == FilterMode.ADDITIVE) {
						GL11.glColor4f(renderColor.x * alphaValue, renderColor.y * alphaValue, renderColor.z * alphaValue, alphaValue);
					} else {
						GL11.glColor4f(renderColor.x * 1f, renderColor.y * 1f, renderColor.z * 1f, alphaValue);
					}
				} else {
					GL11.glColor4f(1f, 1f, 1f, alphaValue);
				}
			} else {
				GL11.glColor4f(1f, 1f, 1f, alphaValue);
			}
		} else {
			GL11.glColor4f(1f, 1f, 1f, 1f);
		}
	}

	private void drawHighlightedGeosets(int formatVersion) {
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if ((programPreferences != null) && (programPreferences.getHighlighTriangleColor() != null)) {
			final Color highlightTriangleColor = programPreferences.getHighlighTriangleColor();
			glColor3f(highlightTriangleColor.getRed() / 255f, highlightTriangleColor.getGreen() / 255f, highlightTriangleColor.getBlue() / 255f);
		} else {
			glColor3f(1f, 3f, 1f);
		}
		renderGeoset(modelView.getHighlightedGeoset(), true, formatVersion, true);
		renderGeoset(modelView.getHighlightedGeoset(), false, formatVersion, true);
	}


	private void renderGeosets(Iterable<Geoset> geosets, int formatVersion, boolean overriddenColors) {
		GL11.glDepthMask(true);
		glShadeModel(GL11.GL_FLAT);
//		glDisable(GL_SHADE_MODEL);
		if ((programPreferences != null) && (programPreferences.viewMode() == 0)) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		} else if ((programPreferences == null) || (programPreferences.viewMode() == 1)) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}
		if (renderTextures()) {
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
			glEnable(GL11.GL_TEXTURE_2D);
//			glEnable(GL_SHADE_MODEL);
			glShadeModel(GL_SMOOTH);
		}
		for (Geoset geo : geosets) {
			if (modelView.shouldRender(geo) || (modelView.getHighlightedGeoset() != geo && overriddenColors)) {
				renderGeoset(geo, true, formatVersion, overriddenColors);
			}
		}
		for (Geoset geo : geosets) {
			if (modelView.shouldRender(geo) || (modelView.getHighlightedGeoset() != geo && overriddenColors)) {
				renderGeoset(geo, false, formatVersion, overriddenColors);
			}
		}
	}

	private void renderGeoset(Geoset geo, boolean renderOpaque, int formatVersion, boolean overriddenColors) {
		if (!correctLoD(geo, formatVersion) || geo.getVertices().isEmpty()) return;
		GeosetAnim geosetAnim = geo.getGeosetAnim();
		float geosetAnimVisibility = 1;

		Sequence animation = renderEnv == null ? null : renderEnv.getCurrentSequence();
		if ((animation != null) && (geosetAnim != null)) {
			geosetAnimVisibility = geosetAnim.getRenderVisibility(renderEnv);
			// do not show invisible geosets
			if (geosetAnimVisibility < RenderModel.MAGIC_RENDER_SHOW_CONSTANT) {
				return;
			}
		}
		Material material = geo.getMaterial();
		for (int i = 0; i < material.getLayers().size(); i++) {
			if (ModelUtils.isShaderStringSupported(formatVersion)
					&& (material.getShaderString() != null)
					&& (material.getShaderString().length() > 0)
					&& (i > 0)) {
				break; // HD-materials is not supported
			}
			Layer layer = material.getLayers().get(i);

//			if (!overriddenColors) {
//				setStandardColors(geosetAnim, geosetAnimVisibility, renderEnv, layer);
//			}

//			FilterMode filterMode = layer.getFilterMode();
//			boolean opaqueLayer = (filterMode == FilterMode.NONE) || (filterMode == FilterMode.TRANSPARENT);
			boolean opaqueLayer = (layer.getFilterMode() == FilterMode.NONE) || (layer.getFilterMode() == FilterMode.TRANSPARENT);

			if ((renderOpaque && opaqueLayer) || (!renderOpaque && !opaqueLayer)) {
				Bitmap tex = layer.getRenderTexture(renderEnv, modelView.getModel());

				textureThing.bindLayerTexture(layer, tex, formatVersion, material);

				if (overriddenColors) {
					GL11.glDisable(GL11.GL_ALPHA_TEST);
				} else {
					setStandardColors(geosetAnim, geosetAnimVisibility, renderEnv, layer);
				}

				renderMesh2(geo, layer);
			}
		}
	}


	private void renderVertDots(int formatVersion) {
		GL11.glDepthMask(true);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
//		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glDepthMask(true);
//		GL11.glEnable(GL11.GL_BLEND);
//		GL11.glEnable(GL_SHADE_MODEL);
		GL11.glEnable(GL11.GL_CULL_FACE);
//		disableGlThings(GL_ALPHA_TEST, GL_TEXTURE_2D, GL_CULL_FACE);
		disableGlThings(GL_ALPHA_TEST, GL_TEXTURE_2D, GL_SHADE_MODEL);
//		disableGlThings(GL_ALPHA_TEST, GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

//		glColor3f(255f, 1f, 255f);
		glColor4f(.7f, .0f, .0f, .4f);
		for (final Geoset geo : modelView.getEditableGeosets()) {
			if (correctLoD(geo, formatVersion) && modelView.shouldRender(geo)) {
//				CubePainter.paintVertCubes(modelView, renderModel, geo);
//				CubePainter.paintVertCubes2(modelView, renderModel, geo, cameraHandler);
				vertRendererThing.updateSquareSize(cameraHandler.getPixelSize());
				CubePainter.paintVertSquares2(modelView, renderModel, geo, cameraHandler, vertRendererThing);
			}
		}
		for (final IdObject idObject : modelView.getVisibleIdObjects()) {
			CubePainter.paintBones4(modelView, renderModel, idObject, cameraHandler, boneRenderThing);
		}

		GL11.glEnable(GL_SHADE_MODEL);
	}

	private boolean correctLoD(Geoset geo, int formatVersion) {
		if ((ModelUtils.isLevelOfDetailSupported(formatVersion)) && (geo.getLevelOfDetailName() != null) && (geo.getLevelOfDetailName().length() > 0) && levelOfDetail > -1) {
			return geo.getLevelOfDetail() == levelOfDetail;
		}
		return true;
	}

	private void ensureBuffersCapacity(int vertices) {
		if (positionsBuffer == null || positionsBuffer.capacity() < vertices * 3) {
			positionsBuffer = ByteBuffer.allocateDirect(vertices * 3 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			uvsBuffer = ByteBuffer.allocateDirect(vertices * 2 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			normalsBuffer = ByteBuffer.allocateDirect(vertices * 3 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			colorsBuffer = ByteBuffer.allocateDirect(vertices * 4 * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
		}
	}

	private void renderMesh2(Geoset geo, Layer layer) {
		var triangles = geo.getTriangles();
		int trianglesCount = triangles.size();
		int renderedVertices = 0;

		// Grow/create the buffers if needed.
		// Triangles * 3 Vertices * 3 floats.
		ensureBuffersCapacity(trianglesCount * 3);

		for (Triangle tri : geo.getTriangles()) {
			float[] color = getTriAreaColor(tri);

			for (GeosetVertex vertex : tri.getVerts()) {
				if(!modelView.isHidden(vertex)){
					RenderGeoset.RenderVert renderVert = vertex.renderVert;
					Vec3 renderPos = renderVert.getRenderPos();
					Vec3 renderNorm = renderVert.getRenderNorm();

					int coordId = layer.getCoordId();
					var uvs = vertex.getTverts();
					if (coordId >= uvs.size()) {
						coordId = uvs.size() - 1;
					}

					Vec2 uv = uvs.get(coordId);

					int offset2 = renderedVertices * 2;
					int offset3 = renderedVertices * 3;
					int offset4 = renderedVertices * 4;

					positionsBuffer.put(offset3 + 0, renderPos.x);
					positionsBuffer.put(offset3 + 1, renderPos.y);
					positionsBuffer.put(offset3 + 2, renderPos.z);

					uvsBuffer.put(offset2 + 0, uv.x);
					uvsBuffer.put(offset2 + 1, uv.y);

					normalsBuffer.put(offset3 + 0, renderNorm.x);
					normalsBuffer.put(offset3 + 1, renderNorm.y);
					normalsBuffer.put(offset3 + 2, renderNorm.z);

					colorsBuffer.put(offset4 + 0, color[0]);
					colorsBuffer.put(offset4 + 1, color[1]);
					colorsBuffer.put(offset4 + 2, color[2]);
					colorsBuffer.put(offset4 + 3, color[3]);

					renderedVertices++;
				}
			}
		}

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glVertexPointer(3, 0, positionsBuffer);
		glTexCoordPointer(2, 0, uvsBuffer);
		glNormalPointer(0, normalsBuffer);
		glColorPointer(4, 0, colorsBuffer);

		glDrawArrays(GL_TRIANGLES, 0, renderedVertices);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}

	private float[] getTriAreaColor(Triangle triangle) {
		if (programPreferences != null && !programPreferences.textureModels() && cameraHandler.isOrtho()) {
			if (triangle.getGeoset() == modelView.getHighlightedGeoset()) {
				return colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE_HIGHLIGHTED);
			} else if (triFullySelected(triangle)) {
				return colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE_SELECTED);
			} else if (triFullyEditable(triangle)) {
				return colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE);
			} else {
				return colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE_UNEDITABLE);
			}
		}

		return whiteDiffuse;
	}

	private boolean triFullySelected(Triangle triangle){
		return modelView.isSelected(triangle.get(0)) && modelView.isSelected(triangle.get(1)) && modelView.isSelected(triangle.get(2));
	}
	private boolean triFullyEditable(Triangle triangle){
		return modelView.isEditable(triangle.get(0)) && modelView.isEditable(triangle.get(1)) && modelView.isEditable(triangle.get(2));
	}

	private void getTriLineColor(GeosetVertex vertex) {
		float[] color;
		if (vertex.getGeoset() == modelView.getHighlightedGeoset()) {
			color = colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE_HIGHLIGHTED);
		} else if (modelView.isSelected(vertex)) {
			color = colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE_SELECTED);
		} else if (modelView.isEditable(vertex)) {
			color = colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE);
		} else {
			color = colorPrefs.getColorComponents(ColorThing.TRIANGLE_LINE_UNEDITABLE);
		}

		glColor4f(color[0], color[1], color[2], color[3]);
	}


	private void renderNormals(int formatVersion) {
		glDepthMask(true);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);
		glDisable(GL_TEXTURE_2D);

		for (Geoset geo : modelView.getVisibleGeosets()) {
			RenderGeoset renderGeoset = renderModel.getRenderGeoset(geo);
			if (correctLoD(geo, formatVersion) && renderGeoset != null) {
				renderGeosetNormals(geo);
			}
		}
	}

	private void renderGeosetNormals(Geoset geo) {
		var vertices = geo.getVertices();
		int verticesCount = vertices.size();
		int renderedVertices = 0;

		ensureBuffersCapacity(verticesCount * 2);

		float factor = (float) (6 / cameraHandler.getZoom());

		for (GeosetVertex vertex : vertices) {
			Vec3 renderPos = vertex.renderVert.getRenderPos();
			Vec3 renderNorm = vertex.renderVert.getRenderNorm();
			int offset6 = renderedVertices * 6;

			positionsBuffer.put(offset6 + 0, renderPos.x);
			positionsBuffer.put(offset6 + 1, renderPos.y);
			positionsBuffer.put(offset6 + 2, renderPos.z);

			colorsBuffer.put(offset6 + 0, 1);
			colorsBuffer.put(offset6 + 1, 1);
			colorsBuffer.put(offset6 + 2, 3);

			positionsBuffer.put(offset6 + 3, renderPos.x + renderNorm.x * factor);
			positionsBuffer.put(offset6 + 4, renderPos.y + renderNorm.y * factor);
			positionsBuffer.put(offset6 + 5, renderPos.z + renderNorm.z * factor);

			colorsBuffer.put(offset6 + 3, 1);
			colorsBuffer.put(offset6 + 4, 1);
			colorsBuffer.put(offset6 + 5, 3);

			renderedVertices++;
		}

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glVertexPointer(3, 0, positionsBuffer);
		glColorPointer(3, 0, colorsBuffer);

		glDrawArrays(GL_LINES, 0, renderedVertices);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
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

	public void zoom(double amount) {
		cameraHandler.zoom(amount);
	}

	public double getZoomAmount() {
		return cameraHandler.getZoom();
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

//	public MouseAdapter getMouseAdapter() {
//		return new MouseAdapter() {
//			@Override
//			public void mouseEntered(final MouseEvent e) {
//				clickTimer.setRepeats(true);
//				clickTimer.start();
//				mouseInBounds = true;
//			}
//
//			@Override
//			public void mouseExited(final MouseEvent e) {
//				if ((cameraHandler.getCameraSpinStartPoint() == null) && (cameraHandler.getActStart() == null) && (cameraHandler.getCameraPanStartPoint() == null)) {
//					clickTimer.stop();
//				}
//				mouseInBounds = false;
//			}
//
//			@Override
//			public void mousePressed(final MouseEvent e) {
//				if (programPreferences.getThreeDCameraPanButton().isButton(e)) {
//					cameraHandler.startPan(e);
//				} else if (programPreferences.getThreeDCameraSpinButton().isButton(e)) {
//					cameraHandler.startSpinn(e);
//				} else if (e.getButton() == MouseEvent.BUTTON3) {
//					cameraHandler.startAct(e);
//				} else {
//					cameraHandler.startAct(e);
//				}
//				ccc2 = new Vec3();
////				System.out.println("camPos: " + cameraPos + ", invQ: " + inverseCameraRotation + ", invYspin: " + inverseCameraRotationYSpin + ", invZspin: " + inverseCameraRotationZSpin);
//			}
//
//			@Override
//			public void mouseReleased(final MouseEvent e) {
//				if (programPreferences.getThreeDCameraPanButton().isButton(e) && (cameraHandler.getCameraPanStartPoint() != null)) {
//					cameraHandler.finnishPan(e);
//				} else if (programPreferences.getThreeDCameraSpinButton().isButton(e) && (cameraHandler.getCameraSpinStartPoint() != null)) {
//					cameraHandler.finnishSpinn(e);
//				} else if ((e.getButton() == MouseEvent.BUTTON3) && (cameraHandler.getActStart() != null)) {
//					cameraHandler.finnishAct(e);
//				}
//				if (!mouseInBounds && (cameraHandler.getCameraSpinStartPoint() == null) && (cameraHandler.getActStart() == null) && (cameraHandler.getCameraPanStartPoint() == null)) {
//					clickTimer.stop();
//				}
//				ccc2 = null;
//				/*
//				 * if( dispMDL != null ) dispMDL.refreshUndo();
//				 */
//			}
//
//			@Override
//			public void mouseClicked(final MouseEvent e) {
//				if (e.getButton() == MouseEvent.BUTTON3) {
//				}
//			}
//
//			@Override
//			public void mouseWheelMoved(final MouseWheelEvent e) {
//				cameraHandler.doZoom3(e);
//			}
//
//			@Override
//			public void mouseDragged(MouseEvent e) {
//
//				if (ccc2 != null && cameraHandler.getActStart() != null) {
//					System.out.println("mouseDragged!");
//
//					ccc2.set(cameraHandler.getGeoPoint(e.getX(), e.getY()));
//				}
//			}
//		};
//	}

	private void cameraMarkerPainter() {
		if (mouseAdapter.isActing()) {
			CubePainter.paintRekt(mouseAdapter.getStartPGeo(), mouseAdapter.getEndPGeo1(), mouseAdapter.getEndPGeo2(), mouseAdapter.getEndPGeo3(), cameraHandler);
//			renderModel.updateGeosets();
		}
	}
}
