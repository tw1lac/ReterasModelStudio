package com.matrixeater.imp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImportPanelSimple extends JPanel implements ActionListener {
	final JButton animationTransfer = new JButton(com.matrixeater.src.MainPanel.AnimIcon);// "Animation
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
			frame.setIconImage(com.matrixeater.src.MainPanel.AnimIcon.getImage());
			frame.revalidate();
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
	}
}
