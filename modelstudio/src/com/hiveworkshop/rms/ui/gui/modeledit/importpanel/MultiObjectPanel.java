package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class MultiObjectPanel extends ObjectPanel {
	boolean oldVal = true;
	ImportPanel impPanel;

	public MultiObjectPanel(final IterableListModel<BoneShell> possibleParents, ImportPanel importPanel) {
		impPanel = importPanel;
		setLayout(new MigLayout("gap 0", "[grow]", "[][][][][grow]"));
		title = new JLabel("Multiple Selected");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		doImport = new JCheckBox("Import these objects (click to apply to all)");
		doImport.setSelected(true);
//		doImport.addChangeListener(this);
		doImport.addActionListener(e -> changeMultiImportStatus());
		add(doImport, "left, wrap");

		oldParentLabel = new JLabel("(Old parent can only be displayed for a single object)");
		add(oldParentLabel, "left, wrap");

		parentLabel = new JLabel("Parent:");
		add(parentLabel, "left, wrap");

		parents = possibleParents;
		parentsList = new JList<>(parents);
		parentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parentsPane = new JScrollPane(parentsList);
		parentsPane.setEnabled(false);
		parentsList.setEnabled(false);
		add(parentsPane, "growx, growy 200");

	}

	public void changeMultiImportStatus() {
		System.out.println("importStatus Changed");
		boolean importStatus = doImport.isSelected();

		List<ObjectShell> selectedValuesList2 = impPanel.mht.objectTabsShell.getSelectedValuesList();
		for (ObjectShell objectShell : selectedValuesList2) {
			objectShell.setShouldImport(importStatus);
		}
		doImport.setBackground(new Color(255, 255, 255, 0));
		doImport.setEnabled(true);
//		doImport.setToolTipText("");
		doImport.setToolTipText(null);
	}
}
