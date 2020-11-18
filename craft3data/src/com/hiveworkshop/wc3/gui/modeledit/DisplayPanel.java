package com.hiveworkshop.wc3.gui.modeledit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.hiveworkshop.wc3.gui.GlobalIcons;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.activity.UndoActionListener;
import com.hiveworkshop.wc3.gui.modeledit.activity.ViewportActivity;
import com.hiveworkshop.wc3.gui.modeledit.cutpaste.ViewportTransferHandler;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.ModelEditor;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.listener.ModelEditorChangeNotifier;
import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

import net.infonode.docking.View;

/**
 * Write a description of class DisplayPanel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class DisplayPanel extends JPanel implements ActionListener {
	private Viewport vp;
	private final JButton up, down, left, right, plusZoom, minusZoom;
	private final ViewportActivity activityListener;
	private final ModelEditorChangeNotifier modelEditorChangeNotifier;
	private final ModelStructureChangeListener modelStructureChangeListener;
	private final View view;
	private final ViewportListener viewportListener;

	public DisplayPanel(final String title, final byte a, final byte b, final ModelView modelView,
			final ModelEditor modelEditor, final ModelStructureChangeListener modelStructureChangeListener,
			final ViewportActivity activityListener, final ProgramPreferences preferences,
			final UndoActionListener undoListener, final CoordDisplayListener coordDisplayListener,
			final UndoHandler undoHandler, final ModelEditorChangeNotifier modelEditorChangeNotifier,
			final ViewportTransferHandler viewportTransferHandler, final RenderModel renderModel,
			final ViewportListener viewportListener) {
		super();
		this.modelStructureChangeListener = modelStructureChangeListener;
		this.activityListener = activityListener;
		this.modelEditorChangeNotifier = modelEditorChangeNotifier;
		this.viewportListener = viewportListener;
		// setBorder(BorderFactory.createTitledBorder(title));// BorderFactory.createCompoundBorder(
		// BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),BorderFactory.createBevelBorder(1)),BorderFactory.createEmptyBorder(1,1,1,1)
		// ));
		setOpaque(true);
		setViewport(a, b, modelView, preferences, undoListener, coordDisplayListener, undoHandler, modelEditor,
				viewportTransferHandler, renderModel);

		Dimension dim = new Dimension(20, 20);
		plusZoom = createAndAddIconButton(dim, GlobalIcons.PLUS);

		minusZoom = createAndAddIconButton(dim, GlobalIcons.MINUS);

		dim = new Dimension(32, 16);
		up = createAndAddIconButton(dim, GlobalIcons.ARROW_UP);

		down = createAndAddIconButton(dim, GlobalIcons.ARROW_DOWN);

		dim = new Dimension(16, 32);
		left = createAndAddIconButton(dim, GlobalIcons.ARROW_LEFT);

		right = createAndAddIconButton(dim, GlobalIcons.ARROW_RIGHT);

		final GroupLayout layout = new GroupLayout(this);
		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(vp)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(plusZoom)
						.addComponent(minusZoom)
						.addGroup(layout.createSequentialGroup().addComponent(left).addGroup(layout
								.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(up).addComponent(down))
								.addComponent(right))));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(vp)
				.addGroup(layout.createSequentialGroup().addComponent(plusZoom).addGap(16).addComponent(minusZoom)
						.addGap(16).addComponent(up).addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(left).addComponent(right))
						.addComponent(down)));

		setLayout(layout);
		this.view = new View(title, null, this);
	}

	private JButton createAndAddIconButton(Dimension dim, ImageIcon imageIcon) {
		JButton button = new JButton("");
		button.setMaximumSize(dim);
		button.setMinimumSize(dim);
		button.setPreferredSize(dim);
		button.setIcon(imageIcon);
		button.addActionListener(this);
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

	public void setViewport(final byte a, final byte b, final ModelView modelView,
			final ProgramPreferences programPreferences, final UndoActionListener undoListener,
			final CoordDisplayListener coordDisplayListener, final UndoHandler undoHandler,
			final ModelEditor modelEditor, final ViewportTransferHandler viewportTransferHandler,
			final RenderModel renderModel) {
		vp = new Viewport(a, b, modelView, programPreferences, activityListener, modelStructureChangeListener,
				undoListener, coordDisplayListener, undoHandler, modelEditor, viewportTransferHandler, renderModel,
				viewportListener);
		modelEditorChangeNotifier.subscribe(vp);
		add(vp);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// g.drawString(title,3,3);
		vp.repaint();
	}

	// public void addGeoset(Geoset g)
	// {
	// m_geosets.add(g);
	// }
	// public void setGeosetVisible(int index, boolean flag)
	// {
	// Geoset geo = (Geoset)m_geosets.get(index);
	// geo.setVisible(flag);
	// }
	// public void setGeosetHighlight(int index, boolean flag)
	// {
	// Geoset geo = (Geoset)m_geosets.get(index);
	// geo.setHighlight(flag);
	// }
	// public void clearGeosets()
	// {
	// m_geosets.clear();
	// }
	// public int getGeosetsSize()
	// {
	// return m_geosets.size();
	// }
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == up) {
			vp.translate(0, (20 * (1 / vp.getZoomAmount())));
			vp.repaint();
		}
		if (e.getSource() == down) {
			vp.translate(0, (-20 * (1 / vp.getZoomAmount())));
			vp.repaint();
		}
		if (e.getSource() == left) {
			vp.translate((20 * (1 / vp.getZoomAmount())), 0);
			vp.repaint();
		}
		if (e.getSource() == right) {
			vp.translate((-20 * (1 / vp.getZoomAmount())), 0);// *vp.getZoomAmount()
			vp.repaint();
		}
		if (e.getSource() == plusZoom) {
			vp.zoom(.15);
			vp.repaint();
		}
		if (e.getSource() == minusZoom) {
			vp.zoom(-.15);
			vp.repaint();
		}
	}

	public ImageIcon getImageIcon() {
		return new ImageIcon(vp.getBufferedImage());
	}

	public BufferedImage getBufferedImage() {
		return vp.getBufferedImage();
	}

	public Viewport getViewport() {
		return vp; // TODO why is this named vp is it the vice president
	}
}
