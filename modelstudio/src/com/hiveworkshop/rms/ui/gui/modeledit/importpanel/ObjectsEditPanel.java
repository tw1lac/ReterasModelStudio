package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ObjectsEditPanel {
	IterableListModel<ObjectPanel> objectPanels;
	JList<ObjectPanel> objectTabs;
	JPanel bonesPanel;
	ImportPanel importPanel;


	public ObjectsEditPanel(ImportPanel importPanel,
	                        IterableListModel<ObjectPanel> objectPanels,
	                        JList<ObjectPanel> objectTabs
	) {
		this.bonesPanel = new JPanel();
		this.importPanel = importPanel;
		this.objectPanels = objectPanels;
		this.objectTabs = objectTabs;
	}

	private static void objectTabsValueChanged(ImportPanel importPanel, MultiObjectPanel multiObjectPane, CardLayout objectCardLayout, JPanel objectPanelCards, JList<ObjectPanel> objectTabs) {
		List<ObjectPanel> selectedValuesList = objectTabs.getSelectedValuesList();
		if (selectedValuesList.size() < 1) {
			objectCardLayout.show(objectPanelCards, "blank");

		} else if (selectedValuesList.size() == 1) {
			importPanel.mht.getFutureBoneListExtended(false);
			objectCardLayout.show(objectPanelCards, (objectTabs.getSelectedIndex()) + "");// .title.getText()

		} else {
			objectCardLayout.show(objectPanelCards, "multiple");
			boolean selectedt = selectedValuesList.get(0).doImport.isSelected();

			if (selectedValuesList.stream().anyMatch(objectPanel -> objectPanel.doImport.isSelected() != selectedt)) {
				multiObjectPane.doImport.setSelected(selectedt);
			}
		}
	}

	private static void importAllObjs(IterableListModel<ObjectPanel> objectPanels, boolean b) {
		for (final ObjectPanel objectPanel : objectPanels) {
			objectPanel.doImport.setSelected(b);
		}
	}

	public JPanel makeObjecsPanel(EditableModel importedModel) {
		JPanel objectsPanel = new JPanel();
//		addTab("Objects", objIcon, objectsPanel, "Controls which objects are imported.");
		importPanel.mht.getFutureBoneListExtended(false);

		CardLayout objectCardLayout = new CardLayout();
		JPanel objectPanelCards = new JPanel(objectCardLayout);

		// Build the objectTabs list of ObjectPanels
		final ObjPanelListCellRenderer objectPanelRenderer = new ObjPanelListCellRenderer();
		int panelId = 0;
		for (IdObject obj : importedModel.getIdObjects()) {
			if ((obj.getClass() != Bone.class) && (obj.getClass() != Helper.class)) {
				final ObjectPanel objPanel = new ObjectPanel(obj, importPanel.mht.getFutureBoneListExtended(true));

				objectPanelCards.add(objPanel, panelId + "");
				objectPanels.addElement(objPanel);
				panelId++;
			}
		}
		for (Camera obj : importedModel.getCameras()) {
			final ObjectPanel objPanel = new ObjectPanel(obj);

			objectPanelCards.add(objPanel, panelId + "");// (objPanel.title.getText()));
			objectPanels.addElement(objPanel);
			panelId++;
		}
		MultiObjectPanel multiObjectPane = new MultiObjectPanel(importPanel.mht.getFutureBoneListExtended(true), importPanel);
		objectPanelCards.add(new JPanel(), "blank");
		objectPanelCards.add(multiObjectPane, "multiple");

		objectTabs.setCellRenderer(objectPanelRenderer);
		objectTabs.addListSelectionListener(e -> objectTabsValueChanged(importPanel, multiObjectPane, objectCardLayout, objectPanelCards, objectTabs));
		objectTabs.setSelectedIndex(0);
//		objectPanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JButton importAllObjs = new JButton("Import All");
		importAllObjs.addActionListener(e -> importAllObjs(objectPanels, true));
//		bonesPanel.add(importAllObjs);

		JButton uncheckAllObjs = new JButton("Leave All");
		uncheckAllObjs.addActionListener(e -> importAllObjs(objectPanels, false));
//		bonesPanel.add(uncheckAllObjs);

		JScrollPane objectTabsPane = new JScrollPane(objectTabs);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, objectTabsPane, objectPanelCards);

		objectsPanel.setLayout(new MigLayout("gap 0", "[grow][grow]", "[][grow]"));
		objectsPanel.add(importAllObjs, "cell 0 0, right");
		objectsPanel.add(uncheckAllObjs, "cell 1 0, left");
		objectsPanel.add(splitPane, "cell 0 1, growx, growy, spanx 2");

		return objectsPanel;
	}

}
