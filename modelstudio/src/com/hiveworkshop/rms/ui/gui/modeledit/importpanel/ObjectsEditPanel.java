package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ObjectsEditPanel {
//	DefaultListModel<ObjectPanel> objectPanels = new DefaultListModel<>();
//	private final Map<Bone, BonePanel> boneToPanel = new HashMap<>();
//	JList<ObjectPanel> objectTabs = new JList<>(objectPanels);
//	JPanel bonesPanel = new JPanel();
//	private final Set<BoneShell> futureBoneListExQuickLookupSet = new HashSet<>();
//	DefaultListModel<BoneShell> futureBoneListEx = new DefaultListModel<>();
//	List<DefaultListModel<BoneShell>> futureBoneListExFixableItems = new ArrayList<>();

	//	List<ObjectPanel> objectPanels;
	Vector<ObjectPanel> objectPanels;
	JList<ObjectPanel> objectTabs;
	JPanel bonesPanel = new JPanel();
	Set<BoneShell> futureBoneListExQuickLookupSet;
	DefaultListModel<BoneShell> futureBoneListEx;
	List<DefaultListModel<BoneShell>> futureBoneListExFixableItems;
	JCheckBox clearExistingBones;
	ArrayList<BoneShell> oldHelpers;
	ArrayList<BoneShell> newHelpers;
	EditableModel currentModel;
	EditableModel importedModel;
	ImportPanel importPanel;
	private Map<Bone, BonePanel> boneToPanel;


	public ObjectsEditPanel(JCheckBox clearExistingBones,
	                        ArrayList<BoneShell> oldHelpers,
	                        ArrayList<BoneShell> newHelpers,
	                        EditableModel currentModel,
	                        EditableModel importedModel,
	                        ImportPanel importPanel,
	                        Vector<ObjectPanel> objectPanels,
	                        Map<Bone, BonePanel> boneToPanel,
	                        JList<ObjectPanel> objectTabs,
	                        Set<BoneShell> futureBoneListExQuickLookupSet,
	                        DefaultListModel<BoneShell> futureBoneListEx,
	                        List<DefaultListModel<BoneShell>> futureBoneListExFixableItems
	) {
		this.clearExistingBones = clearExistingBones;
		this.oldHelpers = oldHelpers;
		this.newHelpers = newHelpers;
		this.currentModel = currentModel;
		this.importedModel = importedModel;
		this.importPanel = importPanel;

		this.objectPanels = objectPanels;
		this.boneToPanel = boneToPanel;
		this.objectTabs = objectTabs;
		this.futureBoneListExQuickLookupSet = futureBoneListExQuickLookupSet;
		this.futureBoneListEx = futureBoneListEx;
		this.futureBoneListExFixableItems = futureBoneListExFixableItems;
	}

	private static void objectTabsValueChanged(ImportPanel importPanel, MultiObjectPanel multiObjectPane, CardLayout objectCardLayout, JPanel objectPanelCards, JList<ObjectPanel> objectTabs) {
		if (objectTabs.getSelectedValuesList().toArray().length < 1) {
			objectCardLayout.show(objectPanelCards, "blank");
		} else if (objectTabs.getSelectedValuesList().toArray().length == 1) {
			importPanel.getFutureBoneListExtended(false);
			objectCardLayout.show(objectPanelCards, (objectTabs.getSelectedIndex()) + "");// .title.getText()
		} else if (objectTabs.getSelectedValuesList().toArray().length > 1) {
			objectCardLayout.show(objectPanelCards, "multiple");
			final Object[] selected = objectTabs.getSelectedValuesList().toArray();
			boolean dif = false;
			boolean set = false;
			boolean selectedt = false;
			for (int i = 0; (i < selected.length) && !dif; i++) {
				final ObjectPanel temp = (ObjectPanel) selected[i];
				if (!set) {
					set = true;
					selectedt = temp.doImport.isSelected();
				} else if (selectedt != temp.doImport.isSelected()) {
					dif = true;
				}
			}
			if (!dif) {
				multiObjectPane.doImport.setSelected(selectedt);
			}
		}
	}

	private static void importAllObjs(Vector<ObjectPanel> objectPanels, boolean b) {
		for (final ObjectPanel objectPanel : objectPanels) {
			objectPanel.doImport.setSelected(b);
		}
	}

	public JPanel makeObjecsPanel(EditableModel importedModel) {
		JPanel objectsPanel = new JPanel();
//		addTab("Objects", objIcon, objectsPanel, "Controls which objects are imported.");
		importPanel.getFutureBoneListExtended(false);

		CardLayout objectCardLayout = new CardLayout();
		JPanel objectPanelCards = new JPanel(objectCardLayout);

		// Build the objectTabs list of ObjectPanels
		final ObjPanelListCellRenderer objectPanelRenderer = new ObjPanelListCellRenderer();
		int panelid = 0;
		for (int i = 0; i < importedModel.getIdObjects().size(); i++) {
			final IdObject obj = importedModel.getIdObjects().get(i);
			if ((obj.getClass() != Bone.class) && (obj.getClass() != Helper.class)) {

				final ObjectPanel objPanel = new ObjectPanel(obj, importPanel.getFutureBoneListExtended(true));

				objectPanelCards.add(objPanel, panelid + "");// (objPanel.title.getText()));
				objectPanels.addElement(objPanel);
//				objectPanels.add(objPanel);
				panelid++;
				// objectTabs.addTab(obj.getClass().getName()+"
				// \""+obj.getName()+"\"",objIcon,objPanel,"Click to modify
				// object import settings.");
			}
		}
		for (int i = 0; i < importedModel.getCameras().size(); i++) {
			final Camera obj = importedModel.getCameras().get(i);

			final ObjectPanel objPanel = new ObjectPanel(obj);

			objectPanelCards.add(objPanel, panelid + "");// (objPanel.title.getText()));
			objectPanels.addElement(objPanel);
//			objectPanels.add(objPanel);
			panelid++;
		}
		MultiObjectPanel multiObjectPane = new MultiObjectPanel(importPanel.getFutureBoneListExtended(true));
		objectPanelCards.add(new JPanel(), "blank");
		objectPanelCards.add(multiObjectPane, "multiple");
		objectTabs.setCellRenderer(objectPanelRenderer);
		objectTabs.addListSelectionListener(e -> objectTabsValueChanged(importPanel, multiObjectPane, objectCardLayout, objectPanelCards, objectTabs));
		objectTabs.setSelectedIndex(0);
		objectPanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JButton importAllObjs = new JButton("Import All");
		importAllObjs.addActionListener(e -> importAllObjs(objectPanels, true));
		bonesPanel.add(importAllObjs);

		JButton uncheckAllObjs = new JButton("Leave All");
		uncheckAllObjs.addActionListener(e -> importAllObjs(objectPanels, false));
		bonesPanel.add(uncheckAllObjs);

		JScrollPane objectTabsPane = new JScrollPane(objectTabs);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, objectTabsPane, objectPanelCards);

		objectsPanel.setLayout(new MigLayout("gap 0", "[grow][grow]", "[][grow]"));
		objectsPanel.add(importAllObjs, "cell 0 0, right");
		objectsPanel.add(uncheckAllObjs, "cell 1 0, left");
		objectsPanel.add(splitPane, "cell 0 1, growx, growy, spanx 2");

		return objectsPanel;
	}

	public DefaultListModel<BoneShell> getFutureBoneListExtended(final boolean newSnapshot) {
		long totalAddTime = 0;
		long addCount = 0;
		long totalRemoveTime = 0;
		long removeCount = 0;
		if (oldHelpers == null) {
			oldHelpers = new ArrayList<>();
			newHelpers = new ArrayList<>();

//			java.util.List<? extends Bone> oldHelpersRefs = currentModel.sortedIdObjects(Bone.class);
			java.util.List<? extends Bone> oldHelpersRefs = currentModel.getBones();
			for (final Bone b : oldHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = currentModel.getName();
				bs.showClass = true;
				oldHelpers.add(bs);
			}
//			oldHelpersRefs = currentModel.sortedIdObjects(Helper.class);
			oldHelpersRefs = currentModel.getHelpers();
			for (final Bone b : oldHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = currentModel.getName();
				bs.showClass = true;
				oldHelpers.add(bs);
			}

//			List<? extends Bone> newHelpersRefs = importedModel.sortedIdObjects(Bone.class);
			List<? extends Bone> newHelpersRefs = importedModel.getHelpers();
			for (final Bone b : newHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = importedModel.getName();
				bs.showClass = true;
				bs.panel = boneToPanel.get(b);
				newHelpers.add(bs);
			}
//			newHelpersRefs = importedModel.sortedIdObjects(Helper.class);
			newHelpersRefs = importedModel.getBones();
			for (final Bone b : newHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = importedModel.getName();
				bs.showClass = true;
				bs.panel = boneToPanel.get(b);
				newHelpers.add(bs);
			}
		}
		if (!clearExistingBones.isSelected()) {
			for (final BoneShell b : oldHelpers) {
				if (!futureBoneListExQuickLookupSet.contains(b)) {
					final long startTime = System.nanoTime();
					futureBoneListEx.addElement(b);
					final long endTime = System.nanoTime();
					totalAddTime += (endTime - startTime);
					addCount++;
					futureBoneListExQuickLookupSet.add(b);
				}
			}
		} else {
			for (final BoneShell b : oldHelpers) {
				if (futureBoneListExQuickLookupSet.remove(b)) {
					final long startTime = System.nanoTime();
					futureBoneListEx.removeElement(b);
					final long endTime = System.nanoTime();
					totalRemoveTime += (endTime - startTime);
					removeCount++;
				}
			}
		}
		for (final BoneShell b : newHelpers) {
			b.panel = boneToPanel.get(b.bone);
			if (b.panel != null) {
				if (b.panel.importTypeBox.getSelectedItem() == BonePanel.IMPORT) {
					if (!futureBoneListExQuickLookupSet.contains(b)) {
						final long startTime = System.nanoTime();
						futureBoneListEx.addElement(b);
						final long endTime = System.nanoTime();
						totalAddTime += (endTime - startTime);
						addCount++;
						futureBoneListExQuickLookupSet.add(b);
					}
				} else {
					if (futureBoneListExQuickLookupSet.remove(b)) {
						final long startTime = System.nanoTime();
						futureBoneListEx.removeElement(b);
						final long endTime = System.nanoTime();
						totalRemoveTime += (endTime - startTime);
						removeCount++;
					}
				}
			}
		}
		if (addCount != 0) {
			System.out.println("average add time: " + (totalAddTime / addCount));
			System.out.println("add count: " + addCount);
		}
		if (removeCount != 0) {
			System.out.println("average remove time: " + (totalRemoveTime / removeCount));
			System.out.println("remove count: " + removeCount);
		}

		final DefaultListModel<BoneShell> listModelToReturn;
		if (newSnapshot || futureBoneListExFixableItems.isEmpty()) {
			final DefaultListModel<BoneShell> futureBoneListReplica = new DefaultListModel<>();
			futureBoneListExFixableItems.add(futureBoneListReplica);
			listModelToReturn = futureBoneListReplica;
		} else {
			listModelToReturn = futureBoneListExFixableItems.get(0);
		}
		// We CANT call clear, we have to preserve
		// the parent list
		for (final DefaultListModel<BoneShell> model : futureBoneListExFixableItems) {
			// clean things that should not be there
			for (int i = model.getSize() - 1; i >= 0; i--) {
				final BoneShell previousElement = model.get(i);
				if (!futureBoneListExQuickLookupSet.contains(previousElement)) {
					model.remove(i);
				}
			}
			// add back things who should be there
			for (int i = 0; i < futureBoneListEx.getSize(); i++) {
				final BoneShell elementAt = futureBoneListEx.getElementAt(i);
				if (!model.contains(elementAt)) {
					model.addElement(elementAt);
				}
			}
		}
//		for(DefaultListModel<BoneShell> model: futureBoneListExFixableItems) {
//			model.clear();
//			for(int i = 0; i < futureBoneListEx.getSize(); i++) {
//				model.addElement(futureBoneListEx.getElementAt(i));
//			}
//		}
		return listModelToReturn;
//		return futureBoneListEx;
	}
}
