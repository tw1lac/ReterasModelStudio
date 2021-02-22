package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.Helper;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BonesEditPanel {
	BoneShellListCellRenderer boneShellRenderer;
	ImportPanel importPanel;
	BonePanel currBonePanel;
	MultiBonePanel multiBonePane;
	CardLayout boneCardLayout;
	JPanel bonePanelCards;
	private ModelHolderThing mht;

	public BonesEditPanel(ModelHolderThing mht,
	                      BoneShellListCellRenderer boneShellRenderer,
	                      ImportPanel importPanel) {
		this.mht = mht;
		this.boneShellRenderer = boneShellRenderer;
		this.importPanel = importPanel;
//		boneTabs = new JList<>(mht.bonePanels);
//		currBonePanel = new BonePanel(mht, boneShellRenderer, importPanel);

		// Initialized up here for use with BonePanels
		boneCardLayout = new CardLayout();
		bonePanelCards = new JPanel(boneCardLayout);

		bonePanelCards.setBackground(Color.yellow);
		bonePanelCards.setOpaque(true);

		currBonePanel = new BonePanel("curr", mht, boneShellRenderer, importPanel);
//		currBonePanel.setBackground(Color.YELLOW);
//		currBonePanel.setOpaque(true);
		bonePanelCards.add(currBonePanel, "single bone");
//		mht.bonePanels.addElement(bonePanel1);

		final BonePanel bonePanel2 = new BonePanel("Ugg2", mht, boneShellRenderer, importPanel);
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
			shell = shell.getParent();
			// If shell is null, then the bone has "No Parent" If currentBonePanel's selected index is not 2,
			if (shell == null) {
				break;
			} else {
				currentBonePanel = boneToPanel.get(shell.getBone());
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

	private void setImportStatusForAllBones(int selectionIndex) {
		for (BoneShell boneShell : mht.boneShells) {
			boneShell.setImportStatus(selectionIndex);
		}
	}

	public JPanel makeBonePanel(BonePanelListCellRenderer bonePanelRenderer) {
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
			if (bs.getOldParent() != null) {
				bs.setParent(mht.boneToShell.get(bs.getOldParent()));
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

		JButton importAllBones = createButton(e -> setImportStatusForAllBones(0), "Import All");
		bonesPanel.add(importAllBones);

		JButton uncheckAllBones = createButton(e -> setImportStatusForAllBones(2), "Leave All");
		bonesPanel.add(uncheckAllBones);

		JButton motionFromBones = createButton(e -> setImportStatusForAllBones(1), "Motion From All");
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
			if (shell == null || usedBoneShells.contains(shell) || (shell.getImportStatus() == 1)) {
				break;
			} else {
				usedBoneShells.add(shell);
			}
			shell = shell.getParent();
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

			int tempIndex = selectedBones.get(0).getImportStatus();

			if (selectedBones.stream().anyMatch(boneShell -> boneShell.getImportStatus() != tempIndex)) {
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

//		for (ObjectPanel objectPanel : mht.objectPanels) {
//			if (objectPanel.doImport.isSelected() && (objectPanel.parentsList != null)) {
//				BoneShell bs = objectPanel.parentsList.getSelectedValue();
//				if ((bs != null) && (bs.getBone() != null)) {
//					checkSelectedOrSomething(usedBoneShells, bs);
//				}
//			}
//		}

		for (ObjectShell objectShell : mht.objectShells) {
			if (objectShell.getShouldImport() && (objectShell.getParent() != null)) {
				BoneShell bs = objectShell.getParent();
				if ((bs != null) && (bs.getBone() != null)) {
					checkSelectedOrSomething(usedBoneShells, bs);
				}
			}
		}
		for (int i = 0; i < mht.geosetAnimTabs.getTabCount(); i++) {
			if (mht.geosetAnimTabs.isEnabledAt(i)) {
				System.out.println("Performing check on geoset: " + i);
				final BoneAttachmentPanel bap = (BoneAttachmentPanel) mht.geosetAnimTabs.getComponentAt(i);
				for (MatrixShell ms : bap.oldBoneRefs) {
					for (final BoneShell bs : ms.newBones) {
						checkSelectedOrSomething(usedBoneShells, bs);
					}
				}
			}
		}
		for (BoneShell boneShell : mht.boneShells) {
			if (boneShell.getImportStatus() != 1 && usedBoneShells.contains(boneShell)) {
				checkSelectedOrSomething(usedBoneShells, boneShell);
			}
		}
		for (BoneShell boneShell : mht.boneShells) {
			if (boneShell.getImportStatus() != 1) {
				if (usedBoneShells.contains(boneShell)) {
					boneShell.setImportStatus(0);
				} else {
					boneShell.setImportStatus(2);
				}
			}
		}
	}

}
