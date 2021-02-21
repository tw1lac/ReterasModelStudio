package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Helper;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.IterableListModel;

import javax.swing.*;
import java.util.*;

public class ModelHolderThing {
	public final Map<Bone, BonePanel> boneToPanel = new HashMap<>();
	public final Map<Bone, BoneShell> boneToShell = new HashMap<>();
	public final Set<BoneShell> futureBoneListExQuickLookupSet = new HashSet<>();
	public EditableModel currentModel;
	public EditableModel importModel;
	public Map<IdObject, IdObject> oldParentMap;
	public Map<IdObject, IdObject> importParentMap;


	//// ugly shit below... should be migrated to the new shit abow
	public Map<IdObject, IdObject> newParentMap;
	public IterableListModel<BoneShell> futureBoneList = new IterableListModel<>();
	public List<BoneShell> oldBoneShellList;
	public List<BoneShell> newBonesShellList;
	public JCheckBox clearExistingBones = new JCheckBox("Clear pre-existing bones and helpers");
	public IterableListModel<BoneShell> futureBoneListEx = new IterableListModel<>();
	public List<IterableListModel<BoneShell>> futureBoneListExFixableItems = new ArrayList<>();
	public ArrayList<BoneShell> oldHelpersShellList;
	public ArrayList<BoneShell> newHelpersShellList;
	//	public IterableListModel<BonePanel> bonePanels = new IterableListModel<>();
//  public JList<BonePanel> boneTabs = new JList<>(bonePanels);
	public IterableListModel<BoneShell> boneShells = new IterableListModel<>();
	public JList<BoneShell> boneTabs = new JList<>(boneShells);
	IterableListModel<BoneShell> existingBones = new IterableListModel<>();
	long totalAddTime1;
	long addCount1;
	long totalRemoveTime1;
	long removeCount1;


	public ModelHolderThing(EditableModel currentModel, EditableModel importModel) {
		this.currentModel = currentModel;
		this.importModel = importModel;
		oldParentMap = new HashMap<>();
		importParentMap = new HashMap<>();
		newParentMap = new HashMap<>();

		fillParentMap(currentModel, oldParentMap);
		fillParentMap(importModel, importParentMap);
	}

	private void fillParentMap(EditableModel model, Map<IdObject, IdObject> parentMap) {
		for (IdObject idObject : model.getIdObjects()) {
			parentMap.put(idObject, idObject.getParent());
		}
	}

	public IterableListModel<BoneShell> getFutureBoneList() {
		if (oldBoneShellList == null) {
			oldBoneShellList = new ArrayList<>();
			newBonesShellList = new ArrayList<>();
			for (final IdObject b : oldParentMap.keySet()) {
				if (b instanceof Bone && !(b instanceof Helper)) {
					final BoneShell bs = new BoneShell((Bone) b);
					bs.modelName = currentModel.getName();
					oldBoneShellList.add(bs);
				}
			}
			for (final IdObject b : importParentMap.keySet()) {
				if (b instanceof Bone && !(b instanceof Helper)) {
					final BoneShell bs = new BoneShell((Bone) b);
					bs.modelName = importModel.getName();
//					bs.panel = boneToPanel.get(b);
					newBonesShellList.add(bs);
				}
			}
		}
		return updateFutureBoneList();
	}

	private IterableListModel<BoneShell> updateFutureBoneList() {
		if (!clearExistingBones.isSelected()) {
			for (final BoneShell b : oldBoneShellList) {
				if (!futureBoneList.contains(b)) {
					futureBoneList.addElement(b);
				}
			}
		} else {
			for (final BoneShell b : oldBoneShellList) {
				if (futureBoneList.contains(b)) {
					futureBoneList.removeElement(b);
				}
			}
		}
		for (final BoneShell b : newBonesShellList) {
			if (b.shouldImportBone) {
				if (!futureBoneList.contains(b)) {
					futureBoneList.addElement(b);
				}
			} else {
				if (futureBoneList.contains(b)) {
					futureBoneList.removeElement(b);
				}
			}
		}
		return futureBoneList;
	}

	public IterableListModel<BoneShell> getFutureBoneListExtended(final boolean newSnapshot) {
		if (oldHelpersShellList == null) {
			System.out.println("creating future bone list!");
			oldHelpersShellList = new ArrayList<>();
			newHelpersShellList = new ArrayList<>();


			for (final IdObject b : oldParentMap.keySet()) {
				if (b instanceof Bone) {
					final BoneShell bs = new BoneShell((Bone) b);
					bs.modelName = currentModel.getName();
					bs.showClass = true;
					oldHelpersShellList.add(bs);
				}
			}
			System.out.println("oldHelpersShellList size: " + oldHelpersShellList.size());

			for (final IdObject b : importParentMap.keySet()) {
				if (b instanceof Bone) {
					final BoneShell bs = new BoneShell((Bone) b);
					bs.modelName = importModel.getName();
					bs.showClass = true;
					bs.panel = boneToPanel.get(b);
					newHelpersShellList.add(bs);
				}
			}
			System.out.println("newHelpersShellList size: " + newHelpersShellList.size());

		}

		return updateFutureBoneListExt(newSnapshot);
	}

	private IterableListModel<BoneShell> updateFutureBoneListExt(boolean newSnapshot) {
		totalAddTime1 = 0;
		addCount1 = 0;
		totalRemoveTime1 = 0;
		removeCount1 = 0;
		if (!clearExistingBones.isSelected()) {
			for (final BoneShell b : oldHelpersShellList) {
				addBoneThing(b);
			}
		} else {
			for (final BoneShell b : oldHelpersShellList) {
				removeBoneThing(b);
			}
		}
		System.out.println("futureBoneListEx size: " + futureBoneListEx.size());
//		for (final BoneShell b : newHelpersShellList) {
//			b.panel = boneToPanel.get(b.bone);
//			if (boneToPanel.get(b.bone) != null) {
//				if (b.shouldImportBone) {
//					addBoneThing(b);
//				} else {
//					removeBoneThing(b);
//				}
//			}
//		}
		for (final BoneShell b : newHelpersShellList) {
			if (b.shouldImportBone) {
				addBoneThing(b);
			} else {
				removeBoneThing(b);
			}
		}
		System.out.println("futureBoneListEx size: " + futureBoneListEx.size());
		printAddRemoveCount();

		final IterableListModel<BoneShell> listModelToReturn;

		if (newSnapshot || futureBoneListExFixableItems.isEmpty()) {
			listModelToReturn = new IterableListModel<>();
			futureBoneListExFixableItems.add(listModelToReturn);
		} else {
			listModelToReturn = futureBoneListExFixableItems.get(0);
		}
		// We CANT call clear, we have to preserve the parent list
		for (final IterableListModel<BoneShell> listModel : futureBoneListExFixableItems) {
			// clean things that should not be there
			for (int i = listModel.getSize() - 1; i >= 0; i--) {
				final BoneShell oldListElement = listModel.get(i);
				if (!futureBoneListExQuickLookupSet.contains(oldListElement)) {
					listModel.remove(i);
				}
			}
			// add back things who should be there
			for (BoneShell elementAt : futureBoneListEx) {
				if (!listModel.contains(elementAt)) {
					listModel.addElement(elementAt);
				}
			}
		}
		return listModelToReturn;
	}

	private boolean shouldImpBone(BonePanel bonePanel) {
		return bonePanel.importTypeBox.getSelectedItem() == BonePanel.IMPORT;
	}

	private boolean shouldImpBone(Bone bone) {
		return boneToPanel.get(bone).importTypeBox.getSelectedItem() == BonePanel.IMPORT;
	}

	private void printAddRemoveCount() {
		if (addCount1 != 0) {
			System.out.println("average add time: " + (totalAddTime1 / addCount1));
			System.out.println("add count: " + addCount1);
		}
		if (removeCount1 != 0) {
			System.out.println("average remove time: " + (totalRemoveTime1 / removeCount1));
			System.out.println("remove count: " + removeCount1);
		}
	}

	private void removeBoneThing(BoneShell b) {
		if (futureBoneListExQuickLookupSet.remove(b)) {
			final long startTime = System.nanoTime();
			futureBoneListEx.removeElement(b);
			final long endTime = System.nanoTime();
			totalRemoveTime1 += (endTime - startTime);
			removeCount1++;
		}
	}

	private void addBoneThing(BoneShell b) {
		if (!futureBoneListExQuickLookupSet.contains(b)) {
			final long startTime = System.nanoTime();
			futureBoneListEx.addElement(b);
			final long endTime = System.nanoTime();
			totalAddTime1 += (endTime - startTime);
			addCount1++;
			futureBoneListExQuickLookupSet.add(b);
		}
	}

	public boolean shouldImportBone(Bone bone) {
		return boneToPanel.get(bone).importTypeBox.getSelectedIndex() == 1;
	}
}
