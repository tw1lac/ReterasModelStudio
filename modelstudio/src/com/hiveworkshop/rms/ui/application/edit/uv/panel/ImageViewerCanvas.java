package com.hiveworkshop.rms.ui.application.edit.uv.panel;

import com.hiveworkshop.rms.editor.model.Bitmap;
import com.hiveworkshop.rms.editor.model.ExtLog;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.viewer.MouseListenerThing;
import com.hiveworkshop.rms.ui.application.viewer.ObjectRenderers.CameraManager;
import com.hiveworkshop.rms.ui.application.viewer.twiTestRenderMaster.SmarterAWTGLCanvas;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.Vec3;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;

public class ImageViewerCanvas extends SmarterAWTGLCanvas {
	private final CameraManager cameraManager;
	private final MouseListenerThing mouseAdapter;
//	private final KeylistenerThing keyAdapter;
	private Timer paintTimer;

	private Class<? extends Throwable> lastThrownErrorClass;
	private int popupCount = 0;
	private final ProgramPreferences programPreferences;

	private long lastExceptionTimeMillis = 0;
	private float xRatio;
	private float yRatio;

	private Bitmap currTexture;

	ExtLog currentExt = new ExtLog(Vec3.ZERO, Vec3.ZERO, 0);
	ExtLog modelExtent = new ExtLog(Vec3.ZERO, Vec3.ZERO, 0);

	ImageBufferFiller imageBufferFiller;

	public ImageViewerCanvas() throws LWJGLException {
		super();
		imageBufferFiller = new ImageBufferFiller();
		this.programPreferences = ProgramGlobals.getPrefs();
		cameraManager = new CameraManager(this);
		cameraManager.setOrtho(true);
		cameraManager.setAllowRotation(false);

		mouseAdapter = new MouseListenerThing(cameraManager, programPreferences);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
//		keyAdapter = new KeylistenerThing(cameraHandler, programPreferences, this);
//		addKeyListener(keyAdapter);

		setBackground(ProgramGlobals.getEditorColorPrefs().getColor(ColorThing.BACKGROUND_COLOR));
		setMinimumSize(new Dimension(200, 200));


		paintTimer = new Timer(16, e -> {
			repaint();
			if (!isShowing()) {
				paintTimer.stop();
			}
		});
		paintTimer.start();
	}

	public ImageViewerCanvas setModel(ModelView modelView, RenderModel renderModel, boolean loadDefaultCamera) {
		if (renderModel != null) {
			cameraManager.setViewportCamera(2, 0, 0, 0, -90, 90);
			cameraManager.translate(.5, -.5);
		}
		return this;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public MouseListenerThing getMouseListenerThing() {
		return mouseAdapter;
	}




	@Override
	protected void exceptionOccurred(final LWJGLException exception) {
		super.exceptionOccurred(exception);
		exception.printStackTrace();
	}

	@Override
	public void initGL() {
		try {
			imageBufferFiller.initGL();
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

	@Override
	public void paintGL() {
		setSize(getParent().getSize());
		paintGL(true);
	}

	public void paintGL(final boolean autoRepainting) {
		if ((System.currentTimeMillis() - lastExceptionTimeMillis) < 5000) {
			System.out.println("not rendering :O");
			if ((System.currentTimeMillis() - lastExceptionTimeMillis) < 100) {
				System.err.println("AnimatedPerspectiveViewport omitting frames due to avoid Exception log spam");
			}
			return;
		}
		try {
			imageBufferFiller.paintGL(this, true);
			if (autoRepainting) {
				try {
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
}
