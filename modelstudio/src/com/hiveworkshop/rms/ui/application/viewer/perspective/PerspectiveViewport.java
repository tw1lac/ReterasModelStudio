package com.hiveworkshop.rms.ui.application.viewer.perspective;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.*;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.filesystem.sources.DataSource;
import com.hiveworkshop.rms.parsers.blp.BLPHandler;
import com.hiveworkshop.rms.parsers.blp.GPUReadyTexture;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.ui.application.edit.animation.BasicTimeBoundProvider;
import com.hiveworkshop.rms.ui.application.viewer.AnimatedRenderEnvironment;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.BetterAWTGLCanvas;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec4;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class PerspectiveViewport extends BetterAWTGLCanvas implements MouseListener, ActionListener, MouseWheelListener, RenderResourceAllocator {
	public static final boolean LOG_EXCEPTIONS = true;
	ModelView modelView;
	Vec3 cameraPos = new Vec3(0, 0, 0);
	Quat inverseCameraRotationQuat = new Quat();
	Quat inverseCameraRotationYSpin = new Quat();
	Quat inverseCameraRotationZSpin = new Quat();
	Mat4 matrixHeap = new Mat4();
	private final Vec4 axisHeap = new Vec4();
	double m_zoom = 1;
	Point lastClick;
	Point leftClickStart;
	Point actStart;
	Timer clickTimer = new Timer(16, e -> clickTimerAction());
	boolean mouseInBounds = false;
	JPopupMenu contextMenu;
	JMenuItem reAssignMatrix;
	JMenuItem cogBone;
	boolean enabled = false;

	boolean texLoaded = false;

	JCheckBox wireframe;
	HashMap<Bitmap, Integer> textureMap = new HashMap<>();

	Class<? extends Throwable> lastThrownErrorClass;
	private final ProgramPreferences programPreferences;
	private final RenderModel editorRenderModel;

	private float backgroundRed, backgroundBlue, backgroundGreen;
	Runnable repaintRunnable = new Runnable() {
		@Override
		public void run() {
			editorRenderModel.updateNodes(true, true);
		}
	};
	Timer paintTimer;

	public PerspectiveViewport(final ModelView modelView, final ProgramPreferences programPreferences, final RenderModel editorRenderModel) throws LWJGLException {
		super();
		this.programPreferences = programPreferences;
		this.editorRenderModel = editorRenderModel;
		editorRenderModel.setAllowInanimateParticles(true);
		// Dimension 1 and Dimension 2, these specify which dimensions to  display.
		// the d bytes can thus be from 0 to 2, specifying either the X, Y, or Z  dimensions
		//
		// Viewport border
		// setBorder(BorderFactory.createBevelBorder(1));
		setBackground((programPreferences == null) || (programPreferences.getPerspectiveBackgroundColor() == null) ? new Color(80, 80, 80) : programPreferences.getPerspectiveBackgroundColor());
		setMinimumSize(new Dimension(200, 200));
		// add(Box.createHorizontalStrut(200));
		// add(Box.createVerticalStrut(200));
		// setLayout( new BoxLayout(this,BoxLayout.LINE_AXIS));
		this.modelView = modelView;
		addMouseListener(this);
		addMouseWheelListener(this);

		contextMenu = new JPopupMenu();
		reAssignMatrix = new JMenuItem("Re-assign Matrix");
		reAssignMatrix.addActionListener(this);
		contextMenu.add(reAssignMatrix);

		cogBone = new JMenuItem("Auto-Center Bone(s)");
		cogBone.addActionListener(e -> cogBone());
		contextMenu.add(cogBone);

		if (programPreferences != null) {
			programPreferences.addChangeListener(() -> {
				setBackground(programPreferences.getPerspectiveBackgroundColor() == null ? new Color(80, 80, 80) : programPreferences.getPerspectiveBackgroundColor());
				loadBackgroundColors();
			});
		}
		loadBackgroundColors();
		paintTimer = new Timer(16, e -> {
			repaint();
			if (!isShowing()) {
				paintTimer.stop();
			}
		});
		paintTimer.start();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("key");
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
					System.out.println("VK_NUMPAD7");
					setViewportCamera(85, 0, 0, 0, 90);
//					topDown();
				}
				if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
					System.out.println("VK_NUMPAD1");
					setViewportCamera(0, 0, 0, 0, 0);
//					topDown();
				}
				if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
					System.out.println("VK_NUMPAD3");
					setViewportCamera(0, 0, 0, 90, 0);
//					topDown();
				}
			}
		});
	}

	private void loadBackgroundColors() {
		backgroundRed = programPreferences.getPerspectiveBackgroundColor().getRed() / 255f;
		backgroundGreen = programPreferences.getPerspectiveBackgroundColor().getGreen() / 255f;
		backgroundBlue = programPreferences.getPerspectiveBackgroundColor().getBlue() / 255f;
	}

	public void setWireframeHandler(final JCheckBox nwireframe) {
		wireframe = nwireframe;
	}

	private final float[] whiteDiffuse = { 1f, 1f, 1f, 1f };
	private final float[] posSun = { 0.0f, 10.0f, 0.0f, 1.0f };

	boolean wantReload = false;

	public void reloadTextures() {
		wantReload = true;
	}

	boolean wantReloadAll = false;
	private float xRatio;
	private float yRatio;

	public void reloadAllTextures() {
		wantReloadAll = true;
	}

	private static void setTexture(Bitmap tex, Integer texture) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, tex.isWrapWidth() ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, tex.isWrapHeight() ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
	}

	private void deleteAllTextures() {
		for (final Integer textureId : textureMap.values()) {
			GL11.glDeleteTextures(textureId);
		}
		textureMap.clear();
	}

	public static int loadTexture(final GPUReadyTexture image, final Bitmap bitmap) {
		if (image == null) {
			return -1;
		}

		final ByteBuffer buffer = image.getBuffer();
		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:

		final int textureID = GL11.glGenTextures(); // Generate texture ID
		setTexture(bitmap, textureID);

		// Setup texture scaling filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		// Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		// Return the texture ID so we can bind it later again
		return textureID;
	}

	public void addGeosets(final List<Geoset> geosets) {
		for (final Geoset geo : geosets) {// .getMDL().getGeosets()
			for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
				if (ModelUtils.isShaderStringSupported(modelView.getModel().getFormatVersion())) {
					if ((geo.getMaterial().getShaderString() != null) && (geo.getMaterial().getShaderString().length() > 0)) {
						if (i > 0) {
							break;
						}
					}
				}
				final Layer layer = geo.getMaterial().getLayers().get(i);
				if (layer.getTextureBitmap() != null) {
					loadToTexMap(layer.getTextureBitmap());
				}
				if (layer.getTextures() != null) {
					for (final Bitmap tex : layer.getTextures()) {
						loadToTexMap(tex);
					}
				}
			}
		}
	}

	public void loadToTexMap(final Bitmap bitmap) {
		if (textureMap.get(bitmap) == null) {
			String path = bitmap.getPath();
			if (!path.isEmpty() && programPreferences.getAllowLoadingNonBlpTextures() == null || !programPreferences.getAllowLoadingNonBlpTextures()) {
				path = path.replaceAll("\\.\\w+", "") + ".blp";
			}
			Integer texture = null;
			try {
				final DataSource workingDirectory = modelView.getModel().getWrappedDataSource();
				texture = loadTexture(BLPHandler.get().loadTexture2(workingDirectory, path, bitmap), bitmap);
			} catch (final Exception exc) {
				if (LOG_EXCEPTIONS) {
					exc.printStackTrace();
				}
			}
			if (texture != null) {
				textureMap.put(bitmap, texture);
			}
		}
	}

	public void setPosition(final double a, final double b) {
		cameraPos.x = (float) a;
		cameraPos.y = (float) b;
	}

	public void translate(final double a, final double b) {
		cameraPos.x += a;
		cameraPos.y += b;
	}

	public void zoom(final double amount) {
		m_zoom *= 1 + amount;
	}

	public double getZoomAmount() {
		return m_zoom;
	}

	public Point2D.Double getDisplayOffset() {
		return new Point2D.Double(cameraPos.x, cameraPos.y);
	}

	// public byte getPortFirstXYZ()
	// { return m_d1; }
	// public byte getPortSecondXYZ()
	// { return m_d2; }
	public BufferedImage getBufferedImage() {
		try {
			int height = getHeight();
			int width = getWidth();
			final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			// paintComponent(image.getGraphics(),5);
			final Pbuffer buffer = new Pbuffer(width, height, new PixelFormat(), null, null);
			buffer.makeCurrent();
			final ByteBuffer pixels = ByteBuffer.allocateDirect(width * height * 4);

			initGL();
			paintGL();

			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL_UNSIGNED_BYTE, pixels);

			final int[] array = new int[pixels.capacity() / 4];
			final int[] flippedArray = new int[pixels.capacity() / 4];

			pixels.asIntBuffer().get(array);
			for (int i = 0; i < array.length; i++) {
				final int rgba = array[i];
				final int a = rgba & 0xFF;
				array[i] = (rgba >>> 8) | (a << 24);
			}
//			for (int i = 0; i < height; i++) {
//				for (int j = 0; j < width; j++) {
//					flippedArray[(height - 1 - i) * width + j] = array[i * width + j];
//				}
//			}

			for (int i = 0; i < height; i++) {
				System.arraycopy(array, i * width, flippedArray, (height - 1 - i) * width, width);
			}
			image.getRaster().setDataElements(0, 0, width, height, flippedArray);

			buffer.releaseContext();
			return image;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	boolean initialized = false;
	private float xangle;
	private float yangle;

	public boolean renderTextures() {
		return texLoaded && ((programPreferences == null) || programPreferences.textureModels());
	}

	private final Vec4 vertexHeap = new Vec4();
	private final Vec4 appliedVertexHeap = new Vec4();
	private final Vec4 vertexSumHeap = new Vec4();
	private final Vec4 normalHeap = new Vec4();
	private final Vec4 appliedNormalHeap = new Vec4();
	private final Vec4 normalSumHeap = new Vec4();
	private final Mat4 skinBonesMatrixHeap = new Mat4();
	private final Mat4 skinBonesMatrixSumHeap = new Mat4();

	@Override
	protected void exceptionOccurred(final LWJGLException exception) {
		super.exceptionOccurred(exception);
		exception.printStackTrace();
	}

	public void forceReloadTextures() {
		texLoaded = true;
		deleteAllTextures();
		addGeosets(modelView.getModel().getGeosets());
	}

	@Override
	public void initGL() {
		try {
			if ((programPreferences == null) || programPreferences.textureModels()) {
				forceReloadTextures();
			}
		} catch (final Throwable e) {
			JOptionPane.showMessageDialog(null,
					"initGL failed because of this exact reason:\n"
							+ e.getClass().getSimpleName() + ": " + e.getMessage());
			throw new RuntimeException(e);
		}
		// JAVA 9+ or maybe WIN 10 allow ridiculous virtual pixes, this combination of old library code
		// and java std library code give me a metric for the ridiculous ratio:
		xRatio = (float) (Display.getDisplayMode().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		yRatio = (float) (Display.getDisplayMode().getHeight() / Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		// These ratios will be wrong and users will see corrupted visuals (bad scale, only fits part of window, etc)
		// if they are using Windows 10 differing UI scale per monitor. I don't think I have an API to query that
		// information yet, though.

	}

	@Override
	public void paintGL() {
		reloadIfNeeded();
		try {
			final int formatVersion = modelView.getModel().getFormatVersion();
			initContext(0, 0, 0);

			if ((programPreferences != null) && (programPreferences.viewMode() == 0)) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			} else if ((programPreferences == null) || (programPreferences.viewMode() == 1)) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}
			glViewport(0, 0, (int) (getWidth() * xRatio), (int) (getHeight() * yRatio));
			glEnable(GL_DEPTH_TEST);

			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDepthMask(true);
			glEnable(GL_COLOR_MATERIAL);
			glEnable(GL_LIGHTING);
			glEnable(GL_LIGHT0);
			glEnable(GL_LIGHT1);
			glEnable(GL_NORMALIZE);
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
			if (renderTextures()) {
				glEnable(GL11.GL_TEXTURE_2D);
			}
			GL11.glEnable(GL11.GL_BLEND);
			glClearColor(backgroundRed, backgroundGreen, backgroundBlue, 1.0f);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
//			gluPerspective(45f, (float) current_width / (float) current_height, 20.0f, 600.0f);

//			gluPerspective(45f, (float) getWidth() / (float) getHeight(), 20.0f, 600.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();

			setUpCamera();

			final FloatBuffer ambientColor = BufferUtils.createFloatBuffer(4);
			ambientColor.put(0.6f).put(0.6f).put(0.6f).put(1f).flip();
			glLightModel(GL_LIGHT_MODEL_AMBIENT, ambientColor);

			addLamp(0.8f, 40.0f, 100.0f, 80.0f, GL_LIGHT0);

			addLamp(0.2f, -100.0f, 100.5f, 0.5f, GL_LIGHT1);

			// glColor3f(1f,1f,0f);
			// glColorMaterial ( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE ) ;
			// glEnable(GL_COLOR_MATERIAL);
			glColor4f(0.5882352941176471f, 0.5882352941176471f, 1f, 0.3f);
			// glPushMatrix();
			// glTranslatef(getWidth() / 2.0f, getHeight() / 2.0f, 0.0f);
			// glRotatef(2*angle, 0f, 0f, -1.0f);
			// glRectf(-50.0f, -50.0f, 50.0f, 50.0f);


			renderGeosets(formatVersion, modelView.getVisibleGeosets(), true);

			glColor3f(1f, 1f, 1f);
			renderGeosets(formatVersion, modelView.getEditableGeosets(), false);

			GL11.glDepthMask(true);
			// System.out.println("max:
			// "+GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));
			if (modelView.getHighlightedGeoset() != null) {
				drawHighlightedGeosets(formatVersion);
			}

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			if ((programPreferences != null) && programPreferences.showNormals()) {
				drawNormals(formatVersion);
			}
			if (renderTextures()) {
				GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
				glEnable(GL11.GL_TEXTURE_2D);
			}
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			editorRenderModel.getParticleShader().use();
			for (final RenderParticleEmitter2 particle : editorRenderModel.getParticleEmitters2()) {
				particle.render(editorRenderModel, editorRenderModel.getParticleShader());
			}

			// glPopMatrix();
			paintAndUpdate();
		} catch (final Throwable e) {
//			e.printStackTrace();
			if ((lastThrownErrorClass == null) || (lastThrownErrorClass != e.getClass())) {
				lastThrownErrorClass = e.getClass();
				popupCount++;
				if (popupCount < 10) {
					ExceptionPopup.display(e);
				}
			}
			throw new RuntimeException(e);
		}
	}

	public void setUpCamera() {
		gluPerspective(45f, (float) getWidth() / (float) getHeight(), 20.0f, 600.0f);
		glTranslatef(0f, -70f, -200f);
		glRotatef(yangle, 1f, 0f, 0f);
		glRotatef(xangle, 0f, 1f, 0f);
		glTranslatef(cameraPos.y * (float) m_zoom, cameraPos.z * (float) m_zoom, cameraPos.x * (float) m_zoom);
		glScalef((float) m_zoom, (float) m_zoom, (float) m_zoom);
	}

	private void paintAndUpdate() {
		try {
			swapBuffers();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		repaintRunnable.run();
		final boolean showing = isShowing();
		final boolean running = paintTimer.isRunning();
		if (showing && !running) {
			paintTimer.restart();
		} else if (!showing && running) {
			paintTimer.stop();
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
			texLoaded = true;
		}
	}

	private void addLamp(float lightValue, float x, float y, float z, int glLight) {
		final FloatBuffer lightColor = BufferUtils.createFloatBuffer(4);
		lightColor.put(lightValue).put(lightValue).put(lightValue).put(1f).flip();
		final FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
		lightPos.put(x).put(y).put(z).put(1f).flip();
		glLight(glLight, GL_DIFFUSE, lightColor);
		glLight(glLight, GL_POSITION, lightPos);
	}

	private void drawHighlightedGeosets(int formatVersion) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if ((programPreferences != null) && (programPreferences.getHighlighTriangleColor() != null)) {
			final Color highlighTriangleColor = programPreferences.getHighlighTriangleColor();
			glColor3f(highlighTriangleColor.getRed() / 255f, highlighTriangleColor.getGreen() / 255f, highlighTriangleColor.getBlue() / 255f);
		} else {
			glColor3f(1f, 3f, 1f);
		}
		renderGeosets(modelView.getHighlightedGeoset(), true, true, true, formatVersion);
		renderGeosets(modelView.getHighlightedGeoset(), false, true, true, formatVersion);
	}

	private void drawNormals(int formatVersion) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		glBegin(GL11.GL_LINES);
		glColor3f(1f, 1f, 3f);

		renderNormals(formatVersion);
		glEnd();
	}

	private void renderNormals(int formatVersion) {
		// if( wireframe.isSelected() )
		for (final Geoset geo : modelView.getModel().getGeosets()) {// .getMDL().getGeosets()
			if ((ModelUtils.isTangentAndSkinSupported(formatVersion)) && (geo.getVertices().size() > 0) && (geo.getVertex(0).getSkinBones() != null)) {
//				renderHdNormals(geo);
				renderHdMesh(geo, null, true);
			} else {
//				renderSdNormals(geo);
				renderSdMesh(geo, null, true);
			}
		}
	}

	private void renderSdNormals(Geoset geo) {
		for (final Triangle tri : geo.getTriangles()) {
			for (final GeosetVertex vertex : tri.getVerts()) {

				setHeap(vertexHeap, vertex, 1);
				final int boneCount = vertex.getBones().size();
				if (boneCount > 0) {
					vertexSumHeap.set(0, 0, 0, 0);
					for (final Bone bone : vertex.getBones()) {
						editorRenderModel.getRenderNode(bone).getWorldMatrix().transform(vertexHeap, appliedVertexHeap);
						vertexSumHeap.add(appliedVertexHeap);
					}
					divVertexSumHeap(boneCount);
				} else {
					vertexSumHeap.set(vertexHeap);
				}
				if (vertex.getNormal() != null) {
					setHeap(normalHeap, vertex.getNormal(), 0);
					if (boneCount > 0) {
						normalSumHeap.set(0, 0, 0, 0);
						for (final Bone bone : vertex.getBones()) {
							editorRenderModel.getRenderNode(bone).getWorldMatrix().transform(normalHeap, appliedNormalHeap);
							normalSumHeap.add(appliedNormalHeap);
						}
					} else {
						normalSumHeap.set(normalHeap);
					}

					normalizeNormalSumHeap();

					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
					paintNormal();
				}
			}
		}
	}

	private void renderHdNormals(Geoset geo) {
		for (final Triangle tri : geo.getTriangles()) {
			for (final GeosetVertex vertex : tri.getVerts()) {
				setHeap(vertexHeap, vertex, 1);
				skinBonesMatrixSumHeap.setZero();

//				vertexSumHeap.set(0, 0, 0, 0);
				boolean processedBones = processHdBones(vertex);
				if (!processedBones) {
					skinBonesMatrixSumHeap.setIdentity();
				}
				skinBonesMatrixSumHeap.transform(vertexHeap, vertexSumHeap);
				if (vertex.getNormal() != null) {
					setHeap(normalHeap, vertex.getNormal(), 0);
					skinBonesMatrixSumHeap.transform(normalHeap, normalSumHeap);

					normalizeNormalSumHeap();
					if (Float.isNaN(normalSumHeap.x)
							|| Float.isNaN(normalSumHeap.y)
							|| Float.isNaN(normalSumHeap.z)
							|| Float.isNaN(normalSumHeap.w)
							|| Float.isInfinite(normalSumHeap.x)
							|| Float.isInfinite(normalSumHeap.y)
							|| Float.isInfinite(normalSumHeap.z)
							|| Float.isInfinite(normalSumHeap.w)) {
						continue;
					}

					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
					paintNormal();
//					GL11.glVertex3f(vertexSumHeap.y, vertexSumHeap.z, vertexSumHeap.x);
//
//					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
//					GL11.glVertex3f(vertexSumHeap.y + (float) ((normalSumHeap.y * 6) / m_zoom),
//							vertexSumHeap.z + (float) ((normalSumHeap.z * 6) / m_zoom),
//							vertexSumHeap.x + (float) ((normalSumHeap.x * 6) / m_zoom));
				}
			}
		}
	}

	int popupCount = 0;

	private boolean processHdBones(GeosetVertex vertex) {
		final Bone[] skinBones = vertex.getSkinBones();
		final short[] skinBoneWeights = vertex.getSkinBoneWeights();
		boolean processedBones = false;
		for (int boneIndex = 0; boneIndex < 4; boneIndex++) {
			final Bone skinBone = skinBones[boneIndex];
			if (skinBone == null) {
				continue;
			}
			processedBones = true;
			final Mat4 worldMatrix = editorRenderModel.getRenderNode(skinBone).getWorldMatrix();
			skinBonesMatrixHeap.set(worldMatrix);

			float skinBoneWeight = skinBoneWeights[boneIndex] / 255f;
			skinBonesMatrixSumHeap.addToThis(skinBonesMatrixHeap.getUniformlyScaled(skinBoneWeight));
		}
		return processedBones;
	}

	public void bindLayer(final Layer layer, final Bitmap tex, final Integer texture) {
		if (texture != null) {
			// texture.bind();
			setTexture(tex, texture);
		} else if (textureMap.size() > 0) {
			setTexture(tex, 0);
		}
		boolean depthMask = false;
		switch (layer.getFilterMode()) {
			case BLEND -> setBlendWOAlpha(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			case ADDITIVE, ADDALPHA -> setBlendWOAlpha(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			case MODULATE -> setBlendWOAlpha(GL11.GL_ZERO, GL11.GL_SRC_COLOR);
			case MODULATE2X -> setBlendWOAlpha(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
			case NONE -> {
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				depthMask = true;
			}
			case TRANSPARENT -> {
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.75f);
				GL11.glDisable(GL11.GL_BLEND);
				depthMask = true;
			}
		}
		if (layer.getTwoSided()) {
			GL11.glDisable(GL11.GL_CULL_FACE);
		} else {
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		if (layer.getNoDepthTest()) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		} else {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		if (layer.getNoDepthSet()) {
			GL11.glDepthMask(false);
		} else {
			GL11.glDepthMask(depthMask);
		}
		if (layer.getUnshaded()) {
			GL11.glDisable(GL_LIGHTING);
		} else {
			glEnable(GL_LIGHTING);
		}
	}

	public void setBlendWOAlpha(int sFactor, int dFactor) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(sFactor, dFactor);
	}

	public void bindLayer(final ParticleEmitter2 particle2, final Bitmap tex, final Integer texture) {
		if (texture != null) {
			// texture.bind();
			setTexture(tex, texture);
		} else if (textureMap.size() > 0) {
			setTexture(tex, 0);
		}
		switch (particle2.getFilterMode()) {
			case BLEND -> setBlendWOAlpha(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			case ADDITIVE, ALPHAKEY -> setBlendWOAlpha(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			case MODULATE -> setBlendWOAlpha(GL11.GL_ZERO, GL11.GL_SRC_COLOR);
			case MODULATE2X -> setBlendWOAlpha(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
		}
		if (particle2.getUnshaded()) {
			GL11.glDisable(GL_LIGHTING);
		} else {
			glEnable(GL_LIGHTING);
		}
	}

//	public void renderGeosets(final int formatVersion, final Iterable<Geoset> geosets, boolean overriddenColors) {
//		boolean overriddenColors = false;
//		for (final Geoset geo : geosets) {
//			renderGeosets(geo, true, false, false, formatVersion);
//		}
//		for (final Geoset geo : geosets) {
//			renderGeosets(geo, false, false, false, formatVersion);
//		}
//	}

	public void renderGeosets(int formatVersion, Iterable<Geoset> geosets, boolean overriddenColors) {
		for (final Geoset geo : geosets) {
			if (!modelView.getEditableGeosets().contains(geo) && modelView.getHighlightedGeoset() != geo || !overriddenColors) {
				renderGeosets(geo, true, false, overriddenColors, formatVersion);
			}
		}
		for (final Geoset geo : geosets) {
			if (!modelView.getEditableGeosets().contains(geo) && modelView.getHighlightedGeoset() != geo || !overriddenColors) {
				renderGeosets(geo, false, false, overriddenColors, formatVersion);
			}
		}
	}

	public void renderGeosets(final Geoset geo, final boolean renderOpaque, final boolean overriddenMaterials, final boolean overriddenColors, final int formatVersion) {
		final GeosetAnim geosetAnim = geo.getGeosetAnim();
		float geosetAnimVisibility = 1;
		final AnimatedRenderEnvironment timeEnvironment = editorRenderModel.getAnimatedRenderEnvironment();
		final BasicTimeBoundProvider animation = timeEnvironment == null ? null : timeEnvironment.getCurrentAnimation();
		if ((animation != null) && (geosetAnim != null)) {
			geosetAnimVisibility = geosetAnim.getRenderVisibility(timeEnvironment);
			if (geosetAnimVisibility < RenderModel.MAGIC_RENDER_SHOW_CONSTANT) {
				return;
			}
		}
		for (int i = 0; i < geo.getMaterial().getLayers().size(); i++) {
			if (ModelUtils.isShaderStringSupported(modelView.getModel().getFormatVersion())) {
				if ((geo.getMaterial().getShaderString() != null) && (geo.getMaterial().getShaderString().length() > 0)) {
					if (i > 0) {
						break;
					}
				}
			}
			final Layer layer = geo.getMaterial().getLayers().get(i);

			if (!overriddenColors) {
				setStandardColors(geosetAnim, geosetAnimVisibility, timeEnvironment, animation, layer);
			}

			final FilterMode filterMode = layer.getFilterMode();
			final boolean opaqueLayer = (filterMode == FilterMode.NONE) || (filterMode == FilterMode.TRANSPARENT);
			if ((renderOpaque && opaqueLayer) || (!renderOpaque && !opaqueLayer)) {
				if (!overriddenMaterials) {
					final Bitmap tex = layer.getRenderTexture(timeEnvironment, modelView.getModel());
					final Integer texture = textureMap.get(tex);
					bindLayer(layer, tex, texture);
				}
				if (overriddenColors) {
					GL11.glDisable(GL11.GL_ALPHA_TEST);
				}
				glBegin(GL11.GL_TRIANGLES);
				if ((ModelUtils.isTangentAndSkinSupported(formatVersion)) && (geo.getVertices().size() > 0) && (geo.getVertex(0).getSkinBones() != null)) {
					renderHdMesh(geo, layer, false);
				} else {
//					renderSdMesh(geo, layer);
					renderSdMesh(geo, layer, false);
				}
				glEnd();
			}
		}
	}

	public void renderSdMesh(Geoset geo, Layer layer) {
		for (final Triangle tri : geo.getTriangles()) {
			for (final GeosetVertex vertex : tri.getVerts()) {
				setHeap(vertexHeap, vertex, 1);
				final int boneCount = vertex.getBones().size();
				if (boneCount > 0) {
					vertexSumHeap.set(0, 0, 0, 0);
					for (final Bone bone : vertex.getBones()) {
						editorRenderModel.getRenderNode(bone).getWorldMatrix().transform(vertexHeap, appliedVertexHeap);
						vertexSumHeap.add(appliedVertexHeap);
					}
					divVertexSumHeap(boneCount);
				} else {
					vertexSumHeap.set(vertexHeap);
				}
				if (vertex.getNormal() != null) {
					setHeap(normalHeap, vertex.getNormal(), 0);
					if (boneCount > 0) {
						normalSumHeap.set(0, 0, 0, 0);
						for (final Bone bone : vertex.getBones()) {
							editorRenderModel.getRenderNode(bone).getWorldMatrix().transform(normalHeap, appliedNormalHeap);
							normalSumHeap.add(appliedNormalHeap);
						}
					} else {
						normalSumHeap.set(normalHeap);
					}

					normalizeNormalSumHeap();

					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
				}

				paintVert(layer, vertex);
			}
		}
	}

	public void renderSdMesh(Geoset geo, Layer layer, boolean onlyNormals) {
		for (final Triangle tri : geo.getTriangles()) {
			for (final GeosetVertex vertex : tri.getVerts()) {
				processesSdBones(vertex, vertexHeap, vertex, 1, vertexSumHeap, appliedVertexHeap);
				if (vertex.getNormal() != null) {
					processesSdBones(vertex, normalHeap, vertex.getNormal(), 0, normalSumHeap, appliedNormalHeap);

					normalizeNormalSumHeap();
					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);

					if (onlyNormals) {
						paintNormal();
					}
				}

				if (!onlyNormals) {
					paintVert(layer, vertex);
				}
			}
		}
	}

	public void processesSdBones(GeosetVertex vertex, Vec4 vertexHeap, Vec3 v, int w, Vec4 sumHeap, Vec4 appliedHeap) {
		final int boneCount = vertex.getBones().size();
		setHeap(vertexHeap, v, w);
		if (boneCount > 0) {
			sumHeap.set(0, 0, 0, 0);
			for (final Bone bone : vertex.getBones()) {
				editorRenderModel.getRenderNode(bone).getWorldMatrix().transform(vertexHeap, appliedHeap);
				sumHeap.add(appliedHeap);
			}
			divHeap(sumHeap, boneCount);
		} else {
			sumHeap.set(vertexHeap);
		}
	}

	public void paintNormal() {
		GL11.glVertex3f(vertexSumHeap.y, vertexSumHeap.z, vertexSumHeap.x);

		Vec3 vSA = new Vec3(vertexSumHeap.toArray());
		Vec3 nSA = new Vec3(normalSumHeap.toArray());
		nSA.scale((float) (6 / m_zoom));
		vSA.add(nSA);

		GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
		GL11.glVertex3f(vSA.y, vSA.z, vSA.x);
//		GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
//		GL11.glVertex3f(vertexSumHeap.y + (float) ((normalSumHeap.y * 6) / m_zoom),
//				vertexSumHeap.z + (float) ((normalSumHeap.z * 6) / m_zoom),
//				vertexSumHeap.x + (float) ((normalSumHeap.x * 6) / m_zoom));
	}

	public void renderHdMesh(Geoset geo, Layer layer, boolean onlyNormals) {
		for (final Triangle tri : geo.getTriangles()) {
			for (final GeosetVertex vertex : tri.getVerts()) {
				setHeap(vertexHeap, vertex, 1);
				skinBonesMatrixSumHeap.setZero();
				boolean processedBones = processHdBones(vertex);
				if (!processedBones) {
					skinBonesMatrixSumHeap.setIdentity();
				}
				vertexSumHeap.set(0, 0, 0, 0);
				skinBonesMatrixSumHeap.transform(vertexHeap, vertexSumHeap);

				if (vertex.getNormal() != null) {
					setHeap(normalHeap, vertex.getNormal(), 0);
					skinBonesMatrixSumHeap.transform(normalHeap, normalSumHeap);
					normalizeNormalSumHeap();

					if (!normalSumHeap.isValid()) {
						continue;
					}

					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);

					if (onlyNormals) {
						paintNormal();
					}
				}
				if (!onlyNormals) {

					paintVert(layer, vertex);
				}
			}
		}
	}

	public void paintVert(Layer layer, GeosetVertex vertex) {
		int coordId = layer.getCoordId();
		if (coordId >= vertex.getTverts().size()) {
			coordId = vertex.getTverts().size() - 1;
		}
		GL11.glTexCoord2f(vertex.getTverts().get(coordId).x, vertex.getTverts().get(coordId).y);
		GL11.glVertex3f(vertexSumHeap.y, vertexSumHeap.z, vertexSumHeap.x);
	}

	public void renderHdMesh(Geoset geo, Layer layer) {
		for (final Triangle tri : geo.getTriangles()) {
			for (final GeosetVertex vertex : tri.getVerts()) {
				setHeap(vertexHeap, vertex, 1);
				skinBonesMatrixSumHeap.setZero();
				vertexSumHeap.set(0, 0, 0, 0);
				boolean processedBones = processHdBones(vertex);
				if (!processedBones) {
					skinBonesMatrixSumHeap.setIdentity();
				}
				skinBonesMatrixSumHeap.transform(vertexHeap, vertexSumHeap);
				if (vertex.getNormal() != null) {
					setHeap(normalHeap, vertex.getNormal(), 0);
					skinBonesMatrixSumHeap.transform(normalHeap, normalSumHeap);

					normalizeNormalSumHeap();

					GL11.glNormal3f(normalSumHeap.y, normalSumHeap.z, normalSumHeap.x);
				}
				paintVert(layer, vertex);
			}
		}
	}

	private void setStandardColors(GeosetAnim geosetAnim, float geosetAnimVisibility, AnimatedRenderEnvironment timeEnvironment, BasicTimeBoundProvider animation, Layer layer) {
		if (animation != null) {
			final float layerVisibility = layer.getRenderVisibility(timeEnvironment);
			final float alphaValue = geosetAnimVisibility * layerVisibility;
			if (/* geo.getMaterial().isConstantColor() && */ geosetAnim != null) {
				final Vec3 renderColor = geosetAnim.getRenderColor(timeEnvironment);
				if (renderColor != null) {
					GL11.glColor4f(renderColor.z * 1f, renderColor.y * 1f, renderColor.x * 1f, alphaValue);
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

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == reAssignMatrix) {
			// MatrixPopup matrixPopup = new MatrixPopup(dispMDL.getMDL());
			// String[] words = { "Accept", "Cancel" };
			// int i = JOptionPane.showOptionDialog(MainFrame.panel,
			// matrixPopup,
			// "Rebuild Matrix", JOptionPane.PLAIN_MESSAGE,
			// JOptionPane.YES_NO_OPTION, null, words, words[1]);
			// if (i == 0) {
			// // JOptionPane.showMessageDialog(null,"action approved");
			// dispMDL.setMatrix(matrixPopup.newRefs);
			// }
		}
	}

	private void cogBone() {
		JOptionPane.showMessageDialog(this,
				"Please use other viewport, this action is not implemented for this viewport.");
	}

	private void clickTimerAction() {
		final int xoff = 0;
		final int yoff = 0;
		// Component temp = this;
		// while (temp != null) {
		// xoff += temp.getX();
		// yoff += temp.getY();
		// if (temp.getClass() == ModelPanel.class) {
		// temp = MainFrame.panel;
		// } else {
		// temp = temp.getParent();
		// }
		// }
		final double mx = MouseInfo.getPointerInfo().getLocation().x - xoff;// MainFrame.frame.getX()-8);
		final double my = MouseInfo.getPointerInfo().getLocation().y - yoff;// MainFrame.frame.getY()-30);
		// JOptionPane.showMessageDialog(null,mx+","+my+" as mouse,
		// "+lastClick.x+","+lastClick.y+" as last.");
		// System.out.println(xoff+" and "+mx);
		if (lastClick != null) {
			// translate Viewport Camera
			vertexHeap.x = 0;
			vertexHeap.y = (float) (((int) mx - lastClick.x) / m_zoom);
			vertexHeap.z = -(float) (((int) my - lastClick.y) / m_zoom);
			vertexHeap.w = 1;
			inverseCameraRotationQuat.transform(vertexHeap);
			cameraPos.x += vertexHeap.x;
			cameraPos.y += vertexHeap.y;
			cameraPos.z += vertexHeap.z;
			lastClick.x = (int) mx;
			lastClick.y = (int) my;

			// rip it from magos's code:
			// final double dx = ((int) mx - lastClick.x) / m_zoom;
			// final double dy = ((int) my - lastClick.y) / m_zoom;
			// final double yaw = xangle;
			// final double pitch = yangle;
			// final double x_x = dx * Math.sin(yaw);
			// final double x_y = -dy * Math.sin(pitch) * Math.cos(yaw);
			// final double y_x = -dx * Math.cos(yaw);
			// final double y_y = -dy * Math.sin(pitch) * Math.sin(yaw);
			// final double z_x = 0;
			// final double z_y = dy * Math.cos(pitch);
			// cameraPos.x += (x_x + x_y);
			// cameraPos.y += (y_x + y_y);
			// cameraPos.z += (z_x + z_y);
			// lastClick.x = (int) mx;
			// lastClick.y = (int) my;
		}
		if (leftClickStart != null) {
			// rotate Viewport Camera

			xangle += mx - leftClickStart.x;
			yangle += my - leftClickStart.y;

			axisHeap.set(0, 1, 0, (float) Math.toRadians(yangle));
			inverseCameraRotationYSpin.setFromAxisAngle(axisHeap);
			axisHeap.set(0, 0, 1, (float) Math.toRadians(xangle));
			inverseCameraRotationZSpin.setFromAxisAngle(axisHeap);
			inverseCameraRotationYSpin.mul(inverseCameraRotationZSpin, inverseCameraRotationQuat);
			flipQuatDirection(inverseCameraRotationQuat);
			flipQuatDirection(inverseCameraRotationYSpin);
			flipQuatDirection(inverseCameraRotationZSpin);

			leftClickStart.x = (int) mx;
			leftClickStart.y = (int) my;
		}
		// MainFrame.panel.setMouseCoordDisplay(m_d1,m_d2,((mx-getWidth()/2)/m_zoom)-m_a,-(((my-getHeight()/2)/m_zoom)-m_b));

		if (actStart != null) {
			final Point actEnd = new Point((int) mx, (int) my);
			final Point2D.Double convertedStart = new Point2D.Double(geomX(actStart.x), geomY(actStart.y));
			final Point2D.Double convertedEnd = new Point2D.Double(geomX(actEnd.x), geomY(actEnd.y));
			// dispMDL.updateAction(convertedStart,convertedEnd,m_d1,m_d2);
			actStart = actEnd;
		}
	}

	private void setViewportCamera(int x, int y, int z, int rX, int rY) {
		lastClick = null;
		leftClickStart = null;
		actStart = null;

		xangle = rX;
		yangle = rY;

		axisHeap.set(0, 1, 0, (float) Math.toRadians(yangle));
		inverseCameraRotationYSpin.setFromAxisAngle(axisHeap);
		axisHeap.set(0, 0, 1, (float) Math.toRadians(xangle));
		inverseCameraRotationZSpin.setFromAxisAngle(axisHeap);
		inverseCameraRotationYSpin.mul(inverseCameraRotationZSpin, inverseCameraRotationQuat);
		flipQuatDirection(inverseCameraRotationQuat);
		flipQuatDirection(inverseCameraRotationYSpin);
		flipQuatDirection(inverseCameraRotationZSpin);

		vertexHeap.x = (float) -x;
		vertexHeap.y = (float) y;
		vertexHeap.z = (float) -z;
		vertexHeap.w = 1;
		cameraPos.x = vertexHeap.x;
		cameraPos.y = vertexHeap.y;
		cameraPos.z = vertexHeap.z;
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		clickTimer.setRepeats(true);
		clickTimer.start();
		mouseInBounds = true;
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if ((leftClickStart == null) && (actStart == null) && (lastClick == null)) {
			clickTimer.stop();
		}
		mouseInBounds = false;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (programPreferences.getThreeDCameraPanButton().isButton(e)) {
			lastClick = new Point(e.getXOnScreen(), e.getYOnScreen());
		} else if (programPreferences.getThreeDCameraSpinButton().isButton(e)) {
			leftClickStart = new Point(e.getXOnScreen(), e.getYOnScreen());
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			actStart = new Point(e.getX(), e.getY());
			final Point2D.Double convertedStart = new Point2D.Double(geomX(actStart.x), geomY(actStart.y));
			// dispMDL.startAction(convertedStart,m_d1,m_d2,MainFrame.panel.currentActionType());
		}
		System.out.println("camPos: " + cameraPos + ", invQ: " + inverseCameraRotationQuat + ", invYspin: " + inverseCameraRotationYSpin + ", invZspin: " + inverseCameraRotationZSpin);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (programPreferences.getThreeDCameraPanButton().isButton(e) && (lastClick != null)) {
			cameraPos.x += (e.getXOnScreen() - lastClick.x) / m_zoom;
			cameraPos.y += (e.getYOnScreen() - lastClick.y) / m_zoom;
			lastClick = null;
		} else if (programPreferences.getThreeDCameraSpinButton().isButton(e) && (leftClickStart != null)) {
			final Point selectEnd = new Point(e.getX(), e.getY());
			final Rectangle2D.Double area = pointsToGeomRect(leftClickStart, selectEnd);
			// System.out.println(area);
			// dispMDL.selectVerteces(area,m_d1,m_d2,MainFrame.panel.currentSelectionType());
			leftClickStart = null;
		} else if ((e.getButton() == MouseEvent.BUTTON3) && (actStart != null)) {
			final Point actEnd = new Point(e.getX(), e.getY());
			final Point2D.Double convertedStart = new Point2D.Double(geomX(actStart.x), geomY(actStart.y));
			final Point2D.Double convertedEnd = new Point2D.Double(geomX(actEnd.x), geomY(actEnd.y));
			// dispMDL.finishAction(convertedStart,convertedEnd,m_d1,m_d2);
			actStart = null;
		}
		if (!mouseInBounds && (leftClickStart == null) && (actStart == null) && (lastClick == null)) {
			clickTimer.stop();
		}
		/*
		 * if( dispMDL != null ) dispMDL.refreshUndo();
		 */
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {

			// if( actEnd.equals(actStart) )
			// {
			// actStart = null;

			// JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			// ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
			// contextMenu.show(this, e.getX(), e.getY());
			// }
		}
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		int wr = e.getWheelRotation();
		final boolean neg = wr < 0;

		if (neg) {
			wr = -wr;
		}
		for (int i = 0; i < wr; i++) {
			if (neg) {
				// cameraPos.x -= (mx - getWidth() / 2)
				// * (1 / m_zoom - 1 / (m_zoom * 1.15));
				// cameraPos.y -= (my - getHeight() / 2)
				// * (1 / m_zoom - 1 / (m_zoom * 1.15));
				// cameraPos.z -= (getHeight() / 2)
				// * (1 / m_zoom - 1 / (m_zoom * 1.15));
				m_zoom *= 1.15;
			} else {
				m_zoom /= 1.15;
				// cameraPos.x -= (mx - getWidth() / 2)
				// * (1 / (m_zoom * 1.15) - 1 / m_zoom);
				// cameraPos.y -= (my - getHeight() / 2)
				// * (1 / (m_zoom * 1.15) - 1 / m_zoom);
				// cameraPos.z -= (getHeight() / 2)
				// * (1 / (m_zoom * 1.15) - 1 / m_zoom);
			}
		}
	}


	public Rectangle2D.Double pointsToGeomRect(final Point a, final Point b) {
		final Point2D.Double topLeft = new Point2D.Double(Math.min(geomX(a.x), geomX(b.x)), Math.min(geomY(a.y), geomY(b.y)));
		final Point2D.Double lowRight = new Point2D.Double(Math.max(geomX(a.x), geomX(b.x)), Math.max(geomY(a.y), geomY(b.y)));
		return new Rectangle2D.Double(topLeft.x, topLeft.y, lowRight.x - topLeft.x, lowRight.y - topLeft.y);
	}

	private static final int BYTES_PER_PIXEL = 4;

	public Rectangle2D.Double pointsToRect(final Point a, final Point b) {
		final Point2D.Double topLeft = new Point2D.Double(Math.min(a.x, b.x), Math.min(a.y, b.y));
		final Point2D.Double lowRight = new Point2D.Double(Math.max(a.x, b.x), Math.max(a.y, b.y));
		return new Rectangle2D.Double(topLeft.x, topLeft.y, lowRight.x - topLeft.x, lowRight.y - topLeft.y);
	}

	private final class Particle2TextureInstance implements InternalResource, InternalInstance {
		private final Bitmap bitmap;
		private final ParticleEmitter2 particle;
		private boolean loaded = false;

		public Particle2TextureInstance(final Bitmap bitmap, final ParticleEmitter2 particle) {
			this.bitmap = bitmap;
			this.particle = particle;
		}

		@Override
		public void setTransformation(final Vec3 worldLocation, final Quat rotation, final Vec3 worldScale) {
		}

		@Override
		public void setSequence(final int index) {
		}

		@Override
		public void show() {
		}

		@Override
		public void setPaused(final boolean paused) {
		}

		@Override
		public void move(final Vec3 deltaPosition) {
		}

		@Override
		public void hide() {
		}

		@Override
		public void bind() {
			if (!loaded) {
				loadToTexMap(bitmap);
				loaded = true;
			}
			final Integer texture = textureMap.get(bitmap);
			bindLayer(particle, bitmap, texture);
		}

		@Override
		public InternalInstance addInstance() {
			return this;
		}

	}

	@Override
	public InternalResource allocateTexture(final Bitmap bitmap, final ParticleEmitter2 textureSource) {
		return new Particle2TextureInstance(bitmap, textureSource);
	}

	public void setViewportBackground(final Color background) {
		setBackground(background);
	}

	public double convertX(final double x) {
		return ((x + cameraPos.x) * m_zoom) + (getWidth() / 2.0);
	}

	public double convertY(final double y) {
		return ((-y + cameraPos.y) * m_zoom) + (getHeight() / 2.0);
	}

	public double geomX(final double x) {
		return ((x - (getWidth() / 2.0)) / m_zoom) - cameraPos.x;
	}

	public double geomY(final double y) {
		return -(((y - (getHeight() / 2.0)) / m_zoom) - cameraPos.y);
	}

	private void flipQuatDirection(Quat quat) {
		quat.x = -quat.x;
		quat.y = -quat.y;
		quat.z = -quat.z;
	}

	private void setHeap(Vec4 vec4Heap, Vec3 vec3, int w) {
		vec4Heap.x = vec3.x;
		vec4Heap.y = vec3.y;
		vec4Heap.z = vec3.z;
		vec4Heap.w = w;
	}

	private void divVertexSumHeap(int boneCount) {
		vertexSumHeap.x /= boneCount;
		vertexSumHeap.y /= boneCount;
		vertexSumHeap.z /= boneCount;
		vertexSumHeap.w /= boneCount;
	}

	private void divHeap(Vec4 vec4Heap, int boneCount) {
		vec4Heap.x /= boneCount;
		vec4Heap.y /= boneCount;
		vec4Heap.z /= boneCount;
		vec4Heap.w /= boneCount;
	}

	private void normalizeNormalSumHeap() {
		if (normalSumHeap.length() > 0) {
			normalSumHeap.normalize();
		} else {
			normalSumHeap.set(0, 1, 0, 0);
		}
	}

	private void normalizeHeap(Vec4 vec4Heap) {
		if (vec4Heap.length() > 0) {
			vec4Heap.normalize();
		} else {
			vec4Heap.set(0, 1, 0, 0);
		}
	}
}