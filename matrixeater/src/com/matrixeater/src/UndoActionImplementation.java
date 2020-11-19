package com.matrixeater.src;

import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.ModelPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.NoSuchElementException;

public final class UndoActionImplementation extends AbstractAction {
	private final MainPanel mainPanel;

	public UndoActionImplementation(final String name, final MainPanel mainPanel) {
		super(name);
		this.mainPanel = mainPanel;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final ModelPanel mpanel = ModelPanelUgg.currentModelPanel(mainPanel.currentModelPanel);
		if (mpanel != null) {
			try {
				mpanel.getUndoManager().undo();
			} catch (final NoSuchElementException exc) {
				JOptionPane.showMessageDialog(mainPanel, "Nothing to undo!");
			} catch (final Exception exc) {
				ExceptionPopup.display(exc);
			}
		}
		mainPanel.refreshUndo();
		mainPanel.repaintSelfAndChildren(mpanel);
	}
}