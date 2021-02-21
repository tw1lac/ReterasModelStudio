package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.Helper;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BonesEditPanel {
	//	private final Map<Bone, BonePanel> boneToPanel = new HashMap<>();
	BoneShellListCellRenderer boneShellRenderer;
	JTabbedPane geosetAnimTabs;
	ImportPanel importPanel;
	IterableListModel<ObjectPanel> objectPanels;
	//	public JList<BonePanel> boneTabs;
	BonePanel currBonePanel;
	MultiBonePanel multiBonePane;
	CardLayout boneCardLayout;
	JPanel bonePanelCards;
	private IterableListModel<BoneShell> selectBoneListModel;
	private ModelHolderThing mht;

	public BonesEditPanel(ModelHolderThing mht,
	                      IterableListModel<ObjectPanel> objectPanels,
	                      BoneShellListCellRenderer boneShellRenderer,
	                      JTabbedPane geosetAnimTabs,
	                      ImportPanel importPanel) {
		this.mht = mht;
		this.objectPanels = objectPanels;
		this.boneShellRenderer = boneShellRenderer;
		this.geosetAnimTabs = geosetAnimTabs;
		this.importPanel = importPanel;
//		boneTabs = new JList<>(mht.bonePanels);
//		currBonePanel = new BonePanel(mht, boneShellRenderer, importPanel);

		// Initialized up here for use with BonePanels
		boneCardLayout = new CardLayout();
		bonePanelCards = new JPanel(boneCardLayout);

		bonePanelCards.setBackground(Color.yellow);
		bonePanelCards.setOpaque(true);

		currBonePanel = new BonePanel(mht, boneShellRenderer, importPanel);
//		currBonePanel.setBackground(Color.YELLOW);
//		currBonePanel.setOpaque(true);
		bonePanelCards.add(currBonePanel, "single bone");
//		mht.bonePanels.addElement(bonePanel1);

		final BonePanel bonePanel2 = new BonePanel(mht, boneShellRenderer, importPanel);
//		bonePanelCards.add(bonePanel2, importedMDLBones.size() + (bonePanelCards.getComponentCount()-1) + "");
		bonePanelCards.add(bonePanel2, "single helper");
		bonePanel2.setBackground(Color.ORANGE.darker());
		bonePanel2.setOpaque(true);


		multiBonePane = new MultiBonePanel(mht, boneShellRenderer);
		bonePanelCards.add(new JPanel(), "blank");
		bonePanelCards.add(multiBonePane, "multiple");
	}

	private static void checkSelectedOrSomething(Map<Bone, BonePanel> boneToPanel, List<BonePanel> usedBonePanels, BonePanel currentBonePanel) {
		int k = 0;
		BoneShell shell = currentBonePanel.getCurrentBoneShell();
		while (true) {
			if ((currentBonePanel == null) || (currentBonePanel.getSelectedIndex() == 1)) {
				break;
			}
			shell = shell.parentBs;
			// If shell is null, then the bone has "No Parent" If currentBonePanel's selected index is not 2,
			if (shell == null) {
				break;
			} else {
				currentBonePanel = boneToPanel.get(shell.bone);
				if (usedBonePanels.contains(currentBonePanel)) {
					break;
				} else {
					usedBonePanels.add(currentBonePanel);
				}
			}
			k++;
			if (k > 1000) {
				JOptionPane.showMessageDialog(null,
						"Unexpected error has occurred: Bone parent loop, circular logic");
				break;
			}
		}
	}

	private static void setImportStatusForAllBones(IterableListModel<BoneShell> boneShells, int selectionIndex) {
		for (BoneShell boneShell : boneShells) {
			boneShell.shouldImportBone = (selectionIndex == 0);
			boneShell.importStatus = selectionIndex;
		}
	}

	public JPanel makeBonePanel(BonePanelListCellRenderer bonePanelRenderer) {
////		addTab("Bones", boneIcon, bonesPanel, "Controls which bones will be imported.");
//		selectBoneListModel = new IterableListModel<>();
//		selectBoneListModel.addAll(existingBonesList);
//
//		boneList = new JList<>(selectBoneListModel);
//		boneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		boneList.setCellRenderer(renderer);
//		boneList.addListSelectionListener(e -> listItemSelected());
//		JScrollPane boneListPane = new JScrollPane(boneList);
//
//		for (BoneShell bs : selectBoneListModel) {
//			if (bs.bone.getName().equals(bone.getName()) && (bs.importBone == null) && (!(bs.bone.getName().contains("Mesh")
//					|| bs.bone.getName().contains("Object")
//					|| bs.bone.getName().contains("Box"))
//					|| bs.bone.getPivotPoint().equalLocs(bone.getPivotPoint()))) {
//				boneList.setSelectedValue(bs, true);
//				bs.setImportBone(bone);
//				break;
//			}
//		}

		JPanel bonesPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[][grow]"));
		final List<Bone> currentMDLBones = mht.currentModel.getBones();
		final List<Helper> currentMDLHelpers = mht.currentModel.getHelpers();
		System.out.println("currentMDLBones Size: " + currentMDLBones.size());
		System.out.println("currentMDLHelpers Size: " + currentMDLHelpers.size());
		final List<Bone> importMDLBones = mht.importModel.getBones();
		final List<Helper> importMDLHelpers = mht.importModel.getHelpers();
		System.out.println("importMDLBones Size: " + importMDLBones.size());
		System.out.println("importMDLHelpers Size: " + importMDLHelpers.size());

		for (Bone importMDLBone : importMDLBones) {
			BoneShell bs = new BoneShell(importMDLBone);
			mht.existingBones.addElement(bs);
			mht.boneToShell.put(importMDLBone, bs);
		}
		for (Helper importMDLHelper : importMDLHelpers) {
			BoneShell bs = new BoneShell(importMDLHelper);
			mht.existingBones.addElement(bs);
			mht.boneToShell.put(importMDLHelper, bs);
		}
		for (BoneShell bs : mht.existingBones) {
			if (bs.oldParent != null) {
				bs.parentBs = mht.boneToShell.get(bs.oldParent);
			}
		}

		System.out.println("boneShellSize: " + mht.boneShells.size());
		System.out.println("existingBones: " + mht.existingBones.size());
		mht.boneShells.addAll(mht.existingBones);
		System.out.println("boneShellSize: " + mht.boneShells.size());

		// Initialized up here for use with BonePanels
//		CardLayout boneCardLayout = new CardLayout();
//		JPanel bonePanelCards = new JPanel(boneCardLayout);
//
//		bonePanelCards.setBackground(Color.yellow);
//		bonePanelCards.setOpaque(true);
//
//		final BonePanel bonePanel1 = new BonePanel(mht, boneShellRenderer, importPanel);
//		bonePanel1.setBackground(Color.YELLOW);
//		bonePanel1.setOpaque(true);
//		bonePanelCards.add(bonePanel1, "single bone");
////		mht.bonePanels.addElement(bonePanel1);
//
//		final BonePanel bonePanel2 = new BonePanel(mht, boneShellRenderer, importPanel);
////		bonePanelCards.add(bonePanel2, importedMDLBones.size() + (bonePanelCards.getComponentCount()-1) + "");
//		bonePanelCards.add(bonePanel2, "single helper");
//		bonePanel2.setBackground(Color.ORANGE.darker());
//		bonePanel2.setOpaque(true);


//		mht.bonePanels.addElement(bonePanel2);

//		for (Bone b : importedMDLBones) {
////			final BonePanel bonePanel = new BonePanel(b, existingBones, boneShellRenderer, importPanel);
//			final BonePanel bonePanel = new BonePanel(existingBones, boneShellRenderer, importPanel);
//			bonePanelCards.add(bonePanel, (bonePanelCards.getComponentCount()-1) + "");
//			bonePanels.addElement(bonePanel);
////			boneToPanel.put(b, bonePanel);
//		}
//		for (Bone b : importedMDLHelpers) {
////			final BonePanel bonePanel = new BonePanel(b, existingBones, boneShellRenderer, importPanel);
//			final BonePanel bonePanel = new BonePanel(existingBones, boneShellRenderer, importPanel);
//			bonePanelCards.add(bonePanel, importedMDLBones.size() + (bonePanelCards.getComponentCount()-1) + "");
//			bonePanels.addElement(bonePanel);
////			boneToPanel.put(b, bonePanel);
//		}

//		for (BonePanel bonePanel : bonePanels) {
//			bonePanel.initList();
//		}


//		MultiBonePanel multiBonePane = new MultiBonePanel(mht, boneShellRenderer);
//		bonePanelCards.add(new JPanel(), "blank");
//		bonePanelCards.add(multiBonePane, "multiple");
		mht.boneTabs.setCellRenderer(bonePanelRenderer);// bonePanelRenderer);
		mht.boneTabs.addListSelectionListener(e -> boneTabsValueChanged(boneCardLayout, bonePanelCards, mht.boneTabs, multiBonePane));
		mht.boneTabs.setSelectedIndex(0);
//		bonePanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JButton importAllBones = createButton(e -> BonesEditPanel.setImportStatusForAllBones(mht.boneShells, 0), "Import All");
		bonesPanel.add(importAllBones);

		JButton uncheckAllBones = createButton(e -> BonesEditPanel.setImportStatusForAllBones(mht.boneShells, 2), "Leave All");
		bonesPanel.add(uncheckAllBones);

		JButton motionFromBones = createButton(e -> BonesEditPanel.setImportStatusForAllBones(mht.boneShells, 1), "Motion From All");
		bonesPanel.add(motionFromBones);

		JButton uncheckUnusedBones = createButton(e -> uncheckUnusedBones(), "Uncheck Unused");
		bonesPanel.add(uncheckUnusedBones);

		JScrollPane boneTabsPane = new JScrollPane(mht.boneTabs);
		JPanel bigPanel = new JPanel(new MigLayout("gap 0, fill"));
		bigPanel.add(boneTabsPane, "spany, growy");
		bigPanel.add(bonePanelCards, "spany, growx, growy");
//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boneTabsPane, bonePanelCards);


		JPanel topPanel = new JPanel(new MigLayout("gap 0, debug", "[][][][]", "[][align center]"));
		topPanel.setBackground(Color.magenta);
		topPanel.setOpaque(true);
		topPanel.add(importAllBones);
		topPanel.add(motionFromBones);
		topPanel.add(uncheckUnusedBones);
		topPanel.add(uncheckAllBones, "wrap");
		topPanel.add(mht.clearExistingBones, "spanx 4, align center");

		bonesPanel.add(topPanel, "align center, wrap");
//		bonesPanel.add(splitPane, "growx, growy");
		bonesPanel.add(bigPanel, "growx, growy");


		return bonesPanel;
	}

	private void checkSelectedOrSomething(List<BoneShell> usedBoneShells, BoneShell currBs) {
		int k = 0;
		BoneShell shell = currBs;
		while (true) {
			// If shell is null, then the bone has "No Parent" If currentBonePanel's selected index is not 2,
			if (shell == null || usedBoneShells.contains(shell) || (shell.importStatus == 1)) {
				break;
			} else {
				usedBoneShells.add(shell);
			}
			shell = shell.parentBs;
			k++;
			if (k > 1000) {
				JOptionPane.showMessageDialog(null,
						"Unexpected error has occurred: Bone parent loop, circular logic");
				break;
			}
		}
	}

	//	private static void boneTabsValueChanged(CardLayout boneCardLayout, JPanel bonePanelCards, JList<BonePanel> boneTabs, MultiBonePanel multiBonePane) {
	private void boneTabsValueChanged(CardLayout boneCardLayout, JPanel bonePanelCards, JList<BoneShell> boneTabs, MultiBonePanel multiBonePane) {
		System.out.println("boneTabsValueChanged");
		List<BoneShell> selectedBones = boneTabs.getSelectedValuesList();
		if (selectedBones.size() < 1) {
			boneCardLayout.show(bonePanelCards, "blank");

		} else if (selectedBones.size() == 1) {
			boneCardLayout.show(bonePanelCards, "single bone");
			currBonePanel.setSelectedBone(boneTabs.getSelectedValue());
//			boneTabs.getSelectedValue().updateSelectionPicks();

		} else {
			boneCardLayout.show(bonePanelCards, "multiple");

			int tempIndex = selectedBones.get(0).importStatus;

			if (selectedBones.stream().anyMatch(boneShell -> boneShell.importStatus != tempIndex)) {
				multiBonePane.setMultiTypes();
			} else {
				multiBonePane.setSelectedIndex(tempIndex);
			}
		}
	}

	public JButton createButton(ActionListener actionListener, String text) {
		JButton jButton = new JButton(text);
		jButton.addActionListener(actionListener);
		return jButton;
	}

	private void uncheckUnusedBones() {
		// Unselect all bones by iterating + setting to index 2 ("Do not import" index)
		// Bones could be referenced by:
		// - A matrix
		// - Another bone
		// - An IdObject
		final List<BoneShell> usedBoneShells = new ArrayList<>();

		for (ObjectPanel objectPanel : objectPanels) {
			if (objectPanel.doImport.isSelected() && (objectPanel.parentsList != null)) {
				BoneShell bs = objectPanel.parentsList.getSelectedValue();
				if ((bs != null) && (bs.bone != null)) {
					checkSelectedOrSomething(usedBoneShells, bs);
				}
			}
		}
		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
			if (geosetAnimTabs.isEnabledAt(i)) {
				System.out.println("Performing check on geoset: " + i);
				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
				for (MatrixShell ms : bap.oldBoneRefs) {
					for (final BoneShell bs : ms.newBones) {
						checkSelectedOrSomething(usedBoneShells, bs);
					}
				}
			}
		}
//		for (BonePanel bonePanel : mht.bonePanels) {
//			if (bonePanel.getSelectedIndex() != 1) {
//				if (usedBonePanels.contains(bonePanel)) {
//					// System.out.println("Performing check on base: "+bonePanel.bone.getName());
//					checkSelectedOrSomething(mht.boneToPanel, usedBonePanels, bonePanel);
//				}
//			}
//		}
//		for (BonePanel bonePanel : mht.bonePanels) {
//			if (bonePanel.getSelectedIndex() != 1) {
//				if (usedBonePanels.contains(bonePanel)) {
//					bonePanel.setSelectedIndex(0);
//				} else {
//					bonePanel.setSelectedIndex(2);
//				}
//			}
//		}
		for (BoneShell boneShell : mht.boneShells) {
			if (boneShell.importStatus != 1 && usedBoneShells.contains(boneShell)) {
				checkSelectedOrSomething(usedBoneShells, boneShell);
			}
		}
		for (BoneShell boneShell : mht.boneShells) {
			if (boneShell.importStatus != 1) {
				if (usedBoneShells.contains(boneShell)) {
					boneShell.importStatus = 0;
				} else {
					boneShell.importStatus = 2;
				}
			}
		}
	}

//	private void uncheckUnusedBones() {
//		// Unselect all bones by iterating + setting to index 2 ("Do not import" index)
//		// Bones could be referenced by:
//		// - A matrix
//		// - Another bone
//		// - An IdObject
//		final List<BonePanel> usedBonePanels = new ArrayList<>();
//		final List<BoneShell> usedBoneShells = new ArrayList<>();
////		for (BonePanel bonePanel : mht.bonePanels) {
////			if (bonePanel.getSelectedIndex() == 0) {
////			}
////		}
//		for (BoneShell bonePanel : mht.bonePanels) {
//			if (bonePanel.importStatus == 0) {
//			}
//		}
//		for (ObjectPanel objectPanel : objectPanels) {
//			if (objectPanel.doImport.isSelected() && (objectPanel.parentsList != null)) {
//				BoneShell bs = objectPanel.parentsList.getSelectedValue();
//				if ((bs != null) && (bs.bone != null)) {
//					BonePanel current = mht.boneToPanel.get(bs.bone);
//					if (!usedBonePanels.contains(current)) {
//						usedBonePanels.add(current);
//					}
//
//					checkSelectedOrSomething(mht.boneToPanel, usedBonePanels, current);
//				}
//			}
//		}
//		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
//			if (geosetAnimTabs.isEnabledAt(i)) {
//				System.out.println("Performing check on geoset: " + i);
//				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
//				for (MatrixShell ms : bap.oldBoneRefs) {
//					for (final BoneShell bs : ms.newBones) {
//						BonePanel current = mht.boneToPanel.get(bs.bone);
//						if (!usedBonePanels.contains(current)) {
//							usedBonePanels.add(current);
//						}
//
//						checkSelectedOrSomething(mht.boneToPanel, usedBonePanels, current);
//					}
//				}
//			}
//		}
////		for (BonePanel bonePanel : mht.bonePanels) {
////			if (bonePanel.getSelectedIndex() != 1) {
////				if (usedBonePanels.contains(bonePanel)) {
////					// System.out.println("Performing check on base: "+bonePanel.bone.getName());
////					checkSelectedOrSomething(mht.boneToPanel, usedBonePanels, bonePanel);
////				}
////			}
////		}
////		for (BonePanel bonePanel : mht.bonePanels) {
////			if (bonePanel.getSelectedIndex() != 1) {
////				if (usedBonePanels.contains(bonePanel)) {
////					bonePanel.setSelectedIndex(0);
////				} else {
////					bonePanel.setSelectedIndex(2);
////				}
////			}
////		}
//		for (BoneShell bonePanel : mht.bonePanels) {
//			if (bonePanel.importStatus != 1) {
//				if (usedBonePanels.contains(bonePanel)) {
//					// System.out.println("Performing check on base: "+bonePanel.bone.getName());
//					checkSelectedOrSomething(mht.boneToPanel, usedBoneShells, bonePanel);
//				}
//			}
//		}
//		for (BoneShell bonePanel : mht.bonePanels) {
//			if (bonePanel.importStatus != 1) {
//				if (usedBonePanels.contains(bonePanel)) {
//					bonePanel.importStatus = 0;
//				} else {
//					bonePanel.importStatus = 2;
//				}
//			}
//		}
//	}
}
