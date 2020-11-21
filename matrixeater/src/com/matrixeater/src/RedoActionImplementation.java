package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.NoSuchElementException;

public final class RedoActionImplementation extends AbstractAction {
	private final MainPanel mainPanel;

	public RedoActionImplementation(final String name, final MainPanel mainPanel) {
		super(name);
		this.mainPanel = mainPanel;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final ModelPanel mpanel = mainPanel.currentModelPanel;
		if (mpanel != null) {
			try {
				mpanel.getUndoManager().redo();
			} catch (final NoSuchElementException exc) {
				JOptionPane.showMessageDialog(mainPanel, "Nothing to redo!");
			} catch (final Exception exc) {
				ExceptionPopup.display(exc);
			}
		}
		mainPanel.menuBar.refreshUndo();
		mainPanel.repaintSelfAndChildren(mpanel);
	}
}