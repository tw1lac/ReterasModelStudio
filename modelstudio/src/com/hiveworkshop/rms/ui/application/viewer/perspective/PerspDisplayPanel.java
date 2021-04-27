package com.hiveworkshop.rms.ui.application.viewer.perspective;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.viewer.ComPerspRenderEnv;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.lwjglcanvas.GearRenderer;
import com.hiveworkshop.rms.ui.util.lwjglcanvas.LWJGLCanvas;
import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;
import org.lwjgl.glfw.GLFWErrorCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwInit;

/**
 * Write a description of class DisplayPanel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PerspDisplayPanel extends JPanel {
	//	private PerspectiveViewport vp;
	private JPanel vpp;
	private String title;
	private final ProgramPreferences programPreferences;
	private final View view;

	// private JCheckBox wireframe;
	public PerspDisplayPanel(final String title, final ModelView modelView, final ProgramPreferences programPreferences) {
		super();
		this.programPreferences = programPreferences;
		setOpaque(true);

		setViewport(modelView);
//		getViewport().setMinimumSize(new Dimension(200, 200));
		this.title = title;

		JButton plusZoom = getButton(e -> zoom(.15), 20, 20);
		// add(plusZoom);
		JButton minusZoom = getButton(e -> zoom(-.15), 20, 20);
		// add(minusZoom);
		JButton up = getButton(e -> translateViewUpDown(20), 32, 16);
		// add(up);
		JButton down = getButton(e -> translateViewUpDown(-20), 32, 16);
		// add(down);
		JButton left = getButton(e -> translateViewLeftRight(20), 16, 32);
		// add(left);
		JButton right = getButton(e -> translateViewLeftRight(-20), 16, 32);
		// add(right);

		setLayout(new BorderLayout());
//		add(vp);
		add(getMainPanel());

		view = new View(title, null, this);
	}

	private static JPanel getMainPanel() {
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
//		frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				canvas.destroy();
//			}
//		});
		return jPanel;
	}

	private static JButton getButton(ActionListener actionListener, int width, int height) {
		Dimension dim = new Dimension(width, height);
		JButton button = new JButton("");
		button.setMaximumSize(dim);
		button.setMinimumSize(dim);
		button.setPreferredSize(dim);
		button.addActionListener(actionListener);
		// add(button);
		return button;
	}

	public void setViewportBackground(final Color background) {
//		vp.setViewportBackground(background);
	}

	//	public Color getViewportBackground() {
//		return vp.getBackground();
//	}
	public Color getViewportBackground() {
		return Color.BLACK;
	}

	public View getView() {
		return view;
	}

	public void addGeosets(final List<Geoset> list) {
		//vp.addGeosets(list);
	}

	public void reloadTextures() {
		//vp.reloadTextures();
	}

	public void reloadAllTextures() {
		//vp.reloadAllTextures();
	}

	public void setViewport(final ModelView modelView, ComPerspRenderEnv renderEnvironment) {
//	public void setViewport(final ModelView modelView, TimeEnvironmentImpl renderEnvironment) {
		setViewport(modelView, 200, renderEnvironment);
	}

	public void setViewport(final ModelView dispModel) {
		ComPerspRenderEnv renderEnvironment = new ComPerspRenderEnv();
		setViewport(dispModel, 200, renderEnvironment);
	}

	public void setViewport(final ModelView modelView, final int viewerSize, ComPerspRenderEnv renderEnvironment) {
		try {
//			if (vp != null) {
//				vp.destroy();
//			}
			removeAll();
			RenderModel renderModel = modelView.getEditorRenderModel();
//			vp = new PerspectiveViewport(modelView, renderModel, programPreferences, renderEnvironment);
//			vp.setIgnoreRepaint(false);
//			vp.setMinimumSize(new Dimension(viewerSize, viewerSize));

			setLayout(new BorderLayout());
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		add(vp, BorderLayout.CENTER);
	}

	public void setTitle(final String what) {
		title = what;
		setBorder(BorderFactory.createTitledBorder(title));
	}

//	public PerspectiveViewport getViewport() {
//		return vp;
//	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
//		vp.paint(vp.getGraphics());
		// g.drawString(title,3,3);
		// vp.repaint();
	}

	public void zoom(double v) {
//		vp.zoom(v);
//		vp.repaint();
	}

	public void translateViewLeftRight(int i) {
//		vp.translate((i * (1 / vp.getZoomAmount())), 0);
//		vp.repaint();
	}

	public void translateViewUpDown(int i) {
//		vp.translate(0, (i * (1 / vp.getZoomAmount())));
//		vp.repaint();
	}

	//	public ImageIcon getImageIcon() {
//		return new ImageIcon(vp.getBufferedImage());
//	}
	public ImageIcon getImageIcon() {
		return new ImageIcon();
	}

	//	public BufferedImage getBufferedImage() {
//		return vp.getBufferedImage();
//	}
	public BufferedImage getBufferedImage() {
		return new BufferedImage(30, 30, 1);
	}


	private void makeContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem reAssignMatrix = new JMenuItem("Re-assign Matrix");
//		reAssignMatrix.addActionListener(this);
		contextMenu.add(reAssignMatrix);

		JMenuItem cogBone = new JMenuItem("Auto-Center Bone(s)");
		cogBone.addActionListener(e -> cogBone());
		contextMenu.add(cogBone);
	}

	private void cogBone() {
		JOptionPane.showMessageDialog(this,
				"Please use other viewport, this action is not implemented for this viewport.");
	}
}
