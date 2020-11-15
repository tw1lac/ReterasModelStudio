package com.requestin8r.src;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginTestFrame extends JFrame {
	
	public LoginTestFrame() {
		super("Login Test Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new LoginTestPanel());
		pack();
		setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        LoginTestFrame frame = new LoginTestFrame();
		frame.setVisible(true);
	}

}
