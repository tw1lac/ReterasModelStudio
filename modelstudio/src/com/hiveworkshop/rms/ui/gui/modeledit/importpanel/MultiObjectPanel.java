package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

//class MultiObjectPanel extends ObjectPanel implements ChangeListener {
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

//		final GroupLayout layout = new GroupLayout(this);
//		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//				.addComponent(title)
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//						.addComponent(doImport)
//						.addComponent(oldParentLabel)
//						.addGroup(layout.createSequentialGroup()
//								.addComponent(parentLabel)
//								.addComponent(parentsPane))));
//
//		layout.setVerticalGroup(layout.createSequentialGroup()
//				.addComponent(title).addGap(16)
//				.addComponent(doImport)
//				.addComponent(oldParentLabel)
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//						.addComponent(parentLabel)
//						.addComponent(parentsPane)));
//		setLayout(layout);
	}

	public void changeMultiImportStatus() {
		boolean importStatus = doImport.isSelected();

		List<ObjectShell> selectedValuesList2 = impPanel.mht.objectTabsShell.getSelectedValuesList();
		for (ObjectShell objectShell : selectedValuesList2) {
			objectShell.setShouldImport(importStatus);
		}

//		List<ObjectPanel> selectedValuesList = impPanel.mht.objectTabs.getSelectedValuesList();
//		for (ObjectPanel objectPanel : selectedValuesList) {
//			objectPanel.doImport.setSelected(importStatus);
//		}
		doImport.setBackground(new Color(255, 255, 255, 0));
		doImport.setEnabled(true);
//		doImport.setToolTipText("");
		doImport.setToolTipText(null);
	}

//	public static void setObjGroupSelected(JList<ObjectPanel> objectTabs, final boolean flag) {
//		List<ObjectPanel> selectedValuesList = objectTabs.getSelectedValuesList();
//		for (ObjectPanel objectPanel : selectedValuesList) {
//			objectPanel.doImport.setSelected(flag);
//		}
//	}

//	public ImportPanel getImportPanel() {
//		if (impPanel == null) {
//			Container temp = getParent();
//			while ((temp != null) && (temp.getClass() != ImportPanel.class)) {
//				temp = temp.getParent();
//			}
//			impPanel = (ImportPanel) temp;
//		}
//		return impPanel;
//	}

//	@Override
//	public void stateChanged(final ChangeEvent e) {
//		if (doImport.isSelected() != oldVal) {
//			setObjGroupSelected(impPanel.mht.objectTabs, doImport.isSelected());
//			oldVal = doImport.isSelected();
//		}
//	}
}
