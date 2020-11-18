package com.hiveworkshop.wc3.gui.modelviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.lwjgl.LWJGLException;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.mdl.Animation;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

public class AnimationViewer extends JPanel {
	private ModelView mdlDisp;
	private AnimatedPerspectiveViewport perspectiveViewport;
	private DefaultComboBoxModel<Animation> animations;
	private JComboBox<Animation> animationBox;
	private final boolean allowUnanimated;

	public AnimationViewer(final ModelView mdlDisp, final ProgramPreferences programPreferences,
			final boolean allowUnanimated) {
		this.mdlDisp = mdlDisp;
		this.allowUnanimated = allowUnanimated;
		try {
			perspectiveViewport = new AnimatedPerspectiveViewport(mdlDisp, programPreferences, true);
			perspectiveViewport.setMinimumSize(new Dimension(200, 200));
			perspectiveViewport.setAnimationTime(0);
			perspectiveViewport.setLive(true);
		} catch (final LWJGLException e) {
			throw new RuntimeException(e);
		}
		setLayout(new BorderLayout());
		add(perspectiveViewport, BorderLayout.CENTER);
		animations = new DefaultComboBoxModel<>();
		if (allowUnanimated || (mdlDisp.getModel().getAnims().size() == 0)) {
			animations.addElement(null);
		}
		for (final Animation animation : mdlDisp.getModel().getAnims()) {
			animations.addElement(animation);
		}
		animationBox = new JComboBox<>(animations);
		animationBox.setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, value == null ? "(Unanimated)" : value, index,
						isSelected, cellHasFocus);
			}
		});
		animationBox.addActionListener(e -> perspectiveViewport.setAnimation((Animation) animationBox.getSelectedItem()));
		add(animationBox, BorderLayout.AFTER_LAST_LINE);

	}

	public void setModel(final ModelView modelView) {
		this.mdlDisp = modelView;
		perspectiveViewport.setModel(modelView);
		reload();
	}

	public void setTitle(final String title) {
		setBorder(BorderFactory.createTitledBorder(title));
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// perspectiveViewport.repaint();
		perspectiveViewport.paint(perspectiveViewport.getGraphics());
	}

	public void reloadAllTextures() {
		perspectiveViewport.reloadAllTextures();
	}

	public void reload() {
		final Animation selectedItem = (Animation) animationBox.getSelectedItem();
		animations.removeAllElements();
		boolean sawLast = selectedItem == null;
		if (allowUnanimated || (mdlDisp.getModel().getAnims().size() == 0)) {
			animations.addElement(null);
		}
		for (final Animation animation : mdlDisp.getModel().getAnims()) {
			animations.addElement(animation);
			if (animation == selectedItem) {
				sawLast = true;
			}
		}
		perspectiveViewport.reloadTextures();
		if (sawLast && ((selectedItem != null) || allowUnanimated)) {
			animationBox.setSelectedItem(selectedItem);
		} else if (!allowUnanimated && (mdlDisp.getModel().getAnims().size() > 0)) {
			animationBox.setSelectedItem(mdlDisp.getModel().getAnim(0));
		}
	}
}
