package com.hiveworkshop.wc3.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public final class ColorChooserIcon extends JLabel {
	private Color currentColor;
	private final ColorListener colorListener;

	public ColorChooserIcon(final Color color, final ColorListener colorListener) {
		currentColor = color;
		this.colorListener = colorListener;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				final Color chosenColor = JColorChooser.showDialog(ColorChooserIcon.this, "Choose Background Color",
						currentColor);
				if (chosenColor != null) {
					setCurrentColor(chosenColor);
				}
			}
		});
		setMinimumSize(new Dimension(24, 24));
		setPreferredSize(new Dimension(24, 24));
		setMaximumSize(new Dimension(24, 24));
	}

	public void setCurrentColor(final Color currentColor) {
		this.currentColor = currentColor;
		colorListener.colorChanged(currentColor);
		repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		g.setColor(currentColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	public static void main(final String[] args) {
		final ColorChooserIcon chooser = new ColorChooserIcon(Color.YELLOW, System.out::println);
		chooser.setCurrentColor(Color.ORANGE);
		JOptionPane.showMessageDialog(null, chooser);
	}

	public interface ColorListener {
		void colorChanged(Color color);
	}
}
