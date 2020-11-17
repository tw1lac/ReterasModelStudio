package com.matrixeater.src;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.IOException;

public class CreditsPanel {

    public static void showCreditsButtonResponse(String s, String about) {
        final DefaultStyledDocument panel = new DefaultStyledDocument();
        final JTextPane epane = new JTextPane();
        epane.setForeground(Color.BLACK);
        epane.setBackground(Color.WHITE);
        final RTFEditorKit rtfk = new RTFEditorKit();
        try {
            rtfk.read(CreditsPanel.class.getResourceAsStream(s), panel, 0);
        } catch (final BadLocationException | IOException e1) {
            e1.printStackTrace();
        }
        epane.setDocument(panel);
        final JFrame frame = new JFrame(about);
        frame.setContentPane(new JScrollPane(epane));
        frame.setSize(650, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
