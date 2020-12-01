package com.hiveworkshop.rms.ui.application.edit.mesh.viewport;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ViewportActivity;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordDisplayListener;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.cutpaste.ViewportTransferHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.listener.ModelEditorChangeNotifier;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;

import net.infonode.docking.View;

/**
 * Write a description of class DisplayPanel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class DisplayPanel extends JPanel implements ActionListener {
	private Viewport viewport;
	private final JButton up, down, left, right, plusZoom, minusZoom;
	private final ViewportActivity activityListener;
	private final ModelEditorChangeNotifier modelEditorChangeNotifier;
	private final ModelStructureChangeListener modelStructureChangeListener;
	private final View view;
	private final ViewportListener viewportListener;

	public DisplayPanel(final String title, final byte a, final byte b,
						final ModelView modelView,
						final ModelEditor modelEditor,
						final ModelStructureChangeListener modelStructureChangeListener,
						final ViewportActivity activityListener,
						final ProgramPreferences preferences,
						final UndoActionListener undoListener,
						final CoordDisplayListener coordDisplayListener,
						final UndoHandler undoHandler,
						final ModelEditorChangeNotifier modelEditorChangeNotifier,
						final ViewportTransferHandler viewportTransferHandler,
						final RenderModel renderModel,
						final ViewportListener viewportListener) {
		super();
		this.modelStructureChangeListener = modelStructureChangeListener;
		this.activityListener = activityListener;
		this.modelEditorChangeNotifier = modelEditorChangeNotifier;
		this.viewportListener = viewportListener;

		setOpaque(true);
		setViewport(a, b, modelView, preferences, undoListener, coordDisplayListener, undoHandler, modelEditor,
				viewportTransferHandler, renderModel);

		plusZoom = createButton(20, 20, "Plus.png", e -> ZoomAction(.15));

		minusZoom = createButton(20, 20, "Minus.png", e -> ZoomAction(-.15));

		up = createButton(32, 16, "ArrowUp.png", e -> upDownAction(20));

		down = createButton(32, 16, "ArrowDown.png", e -> upDownAction(-20));

		left = createButton(16, 32, "ArrowLeft.png", e -> leftRightAction(20));

		right = createButton(16, 32, "ArrowRight.png", e -> leftRightAction(-20));


		final GroupLayout layout = new GroupLayout(this);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(viewport)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(plusZoom)
						.addComponent(minusZoom)
						.addGroup(layout.createSequentialGroup()
								.addComponent(left)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(up)
										.addComponent(down))
								.addComponent(right))));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(viewport)
				.addGroup(layout.createSequentialGroup()
						.addComponent(plusZoom).addGap(16)
						.addComponent(minusZoom).addGap(16)
						.addComponent(up)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(left)
								.addComponent(right))
						.addComponent(down)));

		setLayout(layout);
		view = new View(title, null, this);
	}

	private JButton createButton(int width, int height, String path, ActionListener actionListener) {
		JButton button = new JButton("");
		Dimension dim = new Dimension(width, height);
		button.setMaximumSize(dim);
		button.setMinimumSize(dim);
		button.setPreferredSize(dim);
		button.setIcon(new ImageIcon(RMSIcons.loadDeprecatedImage(path)));
		button.addActionListener(actionListener);
		add(button);
		return button;
	}

	public View getView() {
		return view;
	}

	public void setControlsVisible(final boolean flag) {
		up.setVisible(flag);
		down.setVisible(flag);
		left.setVisible(flag);
		right.setVisible(flag);
		plusZoom.setVisible(flag);
		minusZoom.setVisible(flag);
	}

	public void setViewport(final byte a, final byte b,
							final ModelView modelView,
							final ProgramPreferences programPreferences,
							final UndoActionListener undoListener,
							final CoordDisplayListener coordDisplayListener,
							final UndoHandler undoHandler,
							final ModelEditor modelEditor,
							final ViewportTransferHandler viewportTransferHandler,
							final RenderModel renderModel) {
		viewport = new Viewport(a, b, modelView, programPreferences, activityListener, modelStructureChangeListener,
				undoListener, coordDisplayListener, undoHandler, modelEditor, viewportTransferHandler, renderModel,
				viewportListener);
		modelEditorChangeNotifier.subscribe(viewport);
		add(viewport);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// g.drawString(title,3,3);
		viewport.repaint();
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
	}

	private void ZoomAction(double v) {
		viewport.zoom(v);
		viewport.repaint();
	}

	private void leftRightAction(int i) {
		viewport.translate((i * (1 / viewport.getZoomAmount())), 0);
		viewport.repaint();
	}

	private void upDownAction(int i) {
		viewport.translate(0, (i * (1 / viewport.getZoomAmount())));
		viewport.repaint();
	}

	public ImageIcon getImageIcon() {
		return new ImageIcon(viewport.getBufferedImage());
	}

	public BufferedImage getBufferedImage() {
		return viewport.getBufferedImage();
	}

	public Viewport getViewport() {
		return viewport;
	}
}
