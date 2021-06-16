package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.jawt.JAWT;
import org.lwjgl.system.jawt.*;
import org.lwjgl.system.linux.XVisualInfo;

import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWNativeWin32.glfwAttachWin32Window;
import static org.lwjgl.opengl.GLX13.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.jawt.JAWTFunctions.*;

/**
 * A Canvas component that uses OpenGL for rendering.
 *
 * <p>This implementation supports Windows only.</p>
 */
@SuppressWarnings("serial")
public class LWJGLCanvas extends Canvas {

	private final JAWT awt;
	private JAWTDrawingSurface ds;
	private long context;
	private GLCapabilities caps;

	private RenderThing renderThing;

	public LWJGLCanvas() {
		awt = JAWT.calloc();
		awt.version(JAWT_VERSION_1_4);
		if (!JAWT_GetAWT(awt)) {
			throw new IllegalStateException("GetAWT failed");
		}

		// AWT event listeners are invoked in the EDT

		ComponentAdapter componentAdapter = getComponentAdapter();
		FocusListener focusListener = getFocusListener();
		KeyAdapter keyAdapter = getKeyAdapter();
		MouseAdapter mouseAdapter = getMouseAdapter();

		addComponentListener(componentAdapter);
		addFocusListener(focusListener);
		addKeyListener(keyAdapter);

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(System.out::println);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		paint();
//		System.out.println("ugg-painted");
		repaint();
//		System.out.println("ugg-repainted");
	}

	public LWJGLCanvas setRenderThing(RenderThing renderThing) {
		this.renderThing = renderThing;
		return this;
	}

	private void paint() {
		if (ds == null) {
			// Get the drawing surface
			ds = JAWT_GetDrawingSurface(this, awt.GetDrawingSurface());
			if (ds == null) {
				throw new IllegalStateException("awt->GetDrawingSurface() failed");
			}
		}

		// Lock the drawing surface
		int lock = JAWT_DrawingSurface_Lock(ds, ds.Lock());
		if ((lock & JAWT_LOCK_ERROR) != 0) {
			throw new IllegalStateException("ds->Lock() failed");
		}

		try {
			// Get the drawing surface info
			JAWTDrawingSurfaceInfo dsi = JAWT_DrawingSurface_GetDrawingSurfaceInfo(ds, ds.GetDrawingSurfaceInfo());
			if (dsi == null) {
				throw new IllegalStateException("ds->GetDrawingSurfaceInfo() failed");
			}

			try {
				switch (Platform.get()) {
					case LINUX:
						drawForLinux(dsi);
						break;
					case WINDOWS:
						drawForWindows(dsi);
						break;
				}
			} finally {

//				System.out.println("ugg-FreeDrawingSurface");
				// Free the drawing surface info
				JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, ds.FreeDrawingSurfaceInfo());
			}
		} finally {
			// Unlock the drawing surface

//			System.out.println("ugg-Unlock DrawingSurface");
			JAWT_DrawingSurface_Unlock(ds, ds.Unlock());
		}
	}

	private void drawForWindows(JAWTDrawingSurfaceInfo dsi) {
		// Get the platform-specific drawing info
		JAWTWin32DrawingSurfaceInfo dsi_win = JAWTWin32DrawingSurfaceInfo.create(dsi.platformInfo());

		long hdc = dsi_win.hdc();
		if (hdc == NULL) {
//			System.out.println("hdc null");
			return;
		}

		// The render method is invoked in the EDT
		if (context == NULL) {
//			System.out.println("context null");
			createContextGLFW(dsi_win);
			if (renderThing != null) {
				renderThing.init();
			}
		} else {
			glfwMakeContextCurrent(context);
			GL.createCapabilities();
			GL.setCapabilities(caps);
		}

		try (MemoryStack stack = stackPush()) {
			IntBuffer pw = stack.mallocInt(1);
			IntBuffer ph = stack.mallocInt(1);

			glfwGetFramebufferSize(context, pw, ph);

			if (renderThing != null) {
//				System.out.println("ugg-renderThing1");
				renderThing.render(pw.get(0), ph.get(0));
//				System.out.println("ugg-renderThing2");
			}

			drawGrid();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("ugg-SwapBuffers");
		glfwSwapBuffers(context);

//		System.out.println("ugg-nullContext");
		glfwMakeContextCurrent(NULL);
		GL.setCapabilities(null);
	}

	private void drawForLinux(JAWTDrawingSurfaceInfo dsi) {
		// Get the platform-specific drawing info
		JAWTX11DrawingSurfaceInfo dsi_x11 = JAWTX11DrawingSurfaceInfo.create(dsi.platformInfo());

		long drawable = dsi_x11.drawable();
		if (drawable == NULL) {
			return;
		}

		if (context == NULL) {
			createContextGLX(dsi_x11);
			if (renderThing != null) {
				renderThing.init();
			}
//                            gears = new GLXGears();
		} else {
			if (!glXMakeCurrent(dsi_x11.display(), drawable, context)) {
				throw new IllegalStateException("glXMakeCurrent() failed");
			}
			GL.setCapabilities(caps);
		}

		if (renderThing != null) {
			renderThing.render(getWidth(), getHeight());
		}

		glXSwapBuffers(dsi_x11.display(), drawable);

		glXMakeCurrent(dsi_x11.display(), NULL, NULL);
		GL.setCapabilities(null);
	}

	private void drawGrid() {
		float lineLength = 20;
		float lineSpacing = 1;
		float numberOfLines = 5;
		GL11.glColor3f(1f, 0f, 1f);
//        GL11.glColor4f(.7f, .7f, .7f, .4f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glNormal3f(0, 1, 0);
		float lineSpread = (numberOfLines + 1) * lineSpacing / 2;
		GL11.glVertex3f(-600, .5f, .5f);
		GL11.glVertex3f(600, .5f, .5f);
		GL11.glVertex3f(-600, .2f, 0);
		GL11.glVertex3f(600, 0, 0);
		GL11.glEnd();
	}

	private void createContextGLFW(JAWTWin32DrawingSurfaceInfo dsi_win) {
		// glfwWindowHint can be used here to configure the GL context
		context = glfwAttachWin32Window(dsi_win.hwnd(), NULL);
		if (context == NULL) {
			throw new IllegalStateException("Failed to attach win32 window.");
		}

		// Any callbacks registered here will work. But care must be taken because
		// the callbacks are NOT invoked in the EDT, but in an AWT thread that
		// does the event polling. Many GLFW functions that require main thread
		// invocation, should only be called in that thread.

		// Because of how input focus is implemented in AWT, it is recommended that AWT
		// KeyListeners are always used for keyboard input.

		glfwMakeContextCurrent(context);
		caps = GL.createCapabilities();
	}

	// Simplest possible context creation.
	private void createContextGLX(JAWTX11DrawingSurfaceInfo dsi_x11) {
		long display = dsi_x11.display();
		long drawable = dsi_x11.drawable();

		PointerBuffer configs = Objects.requireNonNull(glXChooseFBConfig(display, 0, (IntBuffer) null));

		long config = NULL;
		for (int i = 0; i < configs.remaining(); i++) {
			XVisualInfo vi = Objects.requireNonNull(glXGetVisualFromFBConfig(display, configs.get(i)));
			if (vi.visualid() == dsi_x11.visualID()) {
				config = configs.get(i);
				break;
			}
		}

		context = glXCreateNewContext(display, config, GLX_RGBA_TYPE, NULL, true);
		if (context == NULL) {
			throw new IllegalStateException("glXCreateContext() failed");
		}

		if (!glXMakeCurrent(display, drawable, context)) {
			throw new IllegalStateException("glXMakeCurrent() failed");
		}

		caps = GL.createCapabilities();
	}

	public void destroy() {
		// Free the drawing surface
		if (renderThing != null) {
			renderThing.cleanup();
		}
		if (ds != null) {

			JAWT_FreeDrawingSurface(ds, awt.FreeDrawingSurface());
			awt.free();
		}


		if (context != NULL) {
			glfwDestroyWindow(context);
		}
	}

	private MouseAdapter getMouseAdapter() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
//				System.out.println(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
//                    System.out.println(e);
			}
		};
	}

	private KeyAdapter getKeyAdapter() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
//				System.out.println(e);
				if (renderThing != null) {
					renderThing.onKeyPressed(e);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
//				System.out.println(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
//				System.out.println(e);
			}
		};
	}

	private FocusListener getFocusListener() {
		return new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
//				System.out.println(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
//				System.out.println(e);
			}
		};
	}

	private ComponentAdapter getComponentAdapter() {
		return new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println(e);
				if (context != NULL) {
					paint();
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				System.out.println(e);
			}

			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println(e);
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println(e);
			}
		};
	}

}