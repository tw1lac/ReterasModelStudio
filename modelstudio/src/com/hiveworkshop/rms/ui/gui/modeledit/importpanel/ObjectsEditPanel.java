package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.Helper;
import com.hiveworkshop.rms.editor.model.IdObject;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ObjectsEditPanel {
	JPanel bonesPanel;
	ImportPanel importPanel;
	ObjectPanel currObjectPanel;
	private ModelHolderThing mht;


	public ObjectsEditPanel(ModelHolderThing mht, ImportPanel importPanel
	) {
		this.mht = mht;
		this.bonesPanel = new JPanel();
		this.importPanel = importPanel;
	}

	public JPanel makeObjectsPanel() {
		JPanel objectsPanel = new JPanel();
		mht.getFutureBoneListExtended(false);

		CardLayout objectCardLayout = new CardLayout();
		JPanel objectPanelCards = new JPanel(objectCardLayout);

		// Build the objectTabs list of ObjectShells
		final ObjPanelListCellRenderer objectPanelRenderer = new ObjPanelListCellRenderer();
		for (IdObject obj : mht.importModel.getIdObjects()) {
			if ((obj.getClass() != Bone.class) && (obj.getClass() != Helper.class)) {
				ObjectShell objectShell = new ObjectShell(obj);
				mht.objectShells.addElement(objectShell);
			}
		}

		for (Camera obj : mht.importModel.getCameras()) {
			ObjectShell objectShell = new ObjectShell(obj);
			mht.objectShells.addElement(objectShell);
		}

		currObjectPanel = new ObjectPanel(mht.getFutureBoneListExtended(true));
		if (mht.objectShells.size() > 0) {
			currObjectPanel.setCurrentObject(mht.objectShells.get(0));
		}
		objectPanelCards.add(currObjectPanel, "single");

		MultiObjectPanel multiObjectPane = new MultiObjectPanel(mht.getFutureBoneListExtended(true), importPanel);
		objectPanelCards.add(new JPanel(), "blank");
		objectPanelCards.add(multiObjectPane, "multiple");

		mht.objectTabsShell.setCellRenderer(objectPanelRenderer);
		mht.objectTabsShell.addListSelectionListener(e -> objectTabsValueChanged(multiObjectPane, objectCardLayout, objectPanelCards, mht.objectTabsShell));
		mht.objectTabsShell.setSelectedIndex(0);

		JButton importAllObjs = new JButton("Import All");
		importAllObjs.addActionListener(e -> setImportStatusForAllObjects(true));
//		bonesPanel.add(importAllObjs);

		JButton uncheckAllObjs = new JButton("Leave All");
		uncheckAllObjs.addActionListener(e -> setImportStatusForAllObjects(false));
//		bonesPanel.add(uncheckAllObjs);

//		JScrollPane objectTabsPane = new JScrollPane(mht.objectTabs);
		JScrollPane objectTabsPane = new JScrollPane(mht.objectTabsShell);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, objectTabsPane, objectPanelCards);

		objectsPanel.setLayout(new MigLayout("gap 0", "[grow][grow]", "[][grow]"));
		objectsPanel.add(importAllObjs, "cell 0 0, right");
		objectsPanel.add(uncheckAllObjs, "cell 1 0, left");
		objectsPanel.add(splitPane, "cell 0 1, growx, growy, spanx 2");

		return objectsPanel;
	}

	private void objectTabsValueChanged(MultiObjectPanel multiObjectPane, CardLayout objectCardLayout, JPanel objectPanelCards, JList<ObjectShell> objectTabs) {
		List<ObjectShell> selectedValuesList = objectTabs.getSelectedValuesList();
		if (selectedValuesList.size() < 1) {
			objectCardLayout.show(objectPanelCards, "blank");

		} else if (selectedValuesList.size() == 1) {
			mht.getFutureBoneListExtended(false);
//			objectCardLayout.show(objectPanelCards, (objectTabs.getSelectedIndex()) + "");// .title.getText()
			objectCardLayout.show(objectPanelCards, "single");
			currObjectPanel.setCurrentObject(objectTabs.getSelectedValue());

		} else {
			objectCardLayout.show(objectPanelCards, "multiple");
			boolean selected = selectedValuesList.get(0).getShouldImport();

			if (selectedValuesList.stream().anyMatch(objectPanel -> objectPanel.getShouldImport() != selected)) {
//				multiObjectPane.doImport.setSelected(selected);
//				multiObjectPane.doImport.setEnabled(false);
				multiObjectPane.doImport.setBackground(Color.orange);
				multiObjectPane.doImport.setToolTipText("Selected objects got different import statuses.");

			} else {
				multiObjectPane.doImport.setSelected(selected);
				multiObjectPane.doImport.setToolTipText(null);
				multiObjectPane.doImport.setBackground(new Color(255, 255, 255, 0));
			}
		}
	}

	private void setImportStatusForAllObjects(boolean b) {
		for (ObjectShell objectShell : mht.objectShells) {
			objectShell.setShouldImport(b);
		}
	}

}
