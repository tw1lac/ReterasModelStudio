package com.hiveworkshop.rms.ui.util.lwjglcanvas;

import net.miginfocom.swing.MigLayout;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

/**
 * GLFW canvas embedded in AWT using jawt.
 */
public final class JAWTDemo {

	private JAWTDemo() {
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("JAWT Demo");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		if (Platform.get() == Platform.MACOSX) {
			throw new UnsupportedOperationException("This demo cannot run on macOS.");
		}

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED) {
				frame.dispose();

				glfwTerminate();
				Objects.requireNonNull(glfwSetErrorCallback(null)).free();
				return true;
			}
			return false;
		});

		frame.setLayout(new BorderLayout());
//        frame.add(canvas, BorderLayout.CENTER);
		frame.add(getMainPanel(frame), BorderLayout.CENTER);
		frame.add(new JTextField(), BorderLayout.SOUTH);

		frame.pack();
		frame.setVisible(true);
	}

	private static JPanel getMainPanel(JFrame frame) {
		GLFWErrorCallback.createPrint().set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize glfw");
		}

		GearRenderer gearRenderer = new GearRenderer();

		LWJGLCanvas canvas = new LWJGLCanvas();
		canvas.setRenderThing(gearRenderer);
		canvas.setSize(640, 480);

		JPanel jPanel = new JPanel(new MigLayout("fill"));
		jPanel.add(new JButton("Ugg"));
		jPanel.add(canvas);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				canvas.destroy();
			}
		});
		return jPanel;
	}

}