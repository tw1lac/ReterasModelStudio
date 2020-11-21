package com.matrixeater.imp;

import com.matrixeater.src.MenuBarActionListeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImportPanelSimple extends JPanel implements ActionListener {
	final JButton animationTransfer = new JButton(MenuBarActionListeners.AnimIcon);// "Animation
																					// Transferer");
                                                                                    final JFrame frame;

	public ImportPanelSimple() {
		add(animationTransfer);
		animationTransfer.addActionListener(this);

		setPreferredSize(new Dimension(800, 600));
		frame = new JFrame("Simple Import Handler");
		frame.setContentPane(this);

		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);

		// animationTransfer.doClick();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == animationTransfer) {
			frame.setContentPane(new AnimationTransfer(frame));
			frame.setTitle("Animation Transferer");
			frame.setIconImage(MenuBarActionListeners.AnimIcon.getImage());
			frame.revalidate();
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
	}
}
