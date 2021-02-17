package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Helper;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BonesEditPanel {
	//	private final Map<Bone, BonePanel> boneToPanel = new HashMap<>();
	private final Map<Bone, BonePanel> boneToPanel;
	DefaultListModel<BoneShell> existingBones;
	JCheckBox clearExistingBones;
	BoneShellListCellRenderer boneShellRenderer;
	JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
	ImportPanel importPanel;
	DefaultListModel<ObjectPanel> objectPanels = new DefaultListModel<>();
	DefaultListModel<BonePanel> bonePanels = new DefaultListModel<>();
	JList<BonePanel> boneTabs = new JList<>(bonePanels);

	public BonesEditPanel(DefaultListModel<BoneShell> existingBones,
	                      JCheckBox clearExistingBones,
	                      BoneShellListCellRenderer boneShellRenderer,
	                      JTabbedPane geosetAnimTabs,
	                      ImportPanel importPanel, Map<Bone, BonePanel> boneToPanel) {

		this.existingBones = existingBones;
		this.clearExistingBones = clearExistingBones;
		this.boneShellRenderer = boneShellRenderer;
		this.geosetAnimTabs = geosetAnimTabs;
		this.importPanel = importPanel;

		this.boneToPanel = boneToPanel;
	}

	private static void checkSelectedOrSomething(Map<Bone, BonePanel> boneToPanel, List<BonePanel> usedBonePanels, BonePanel current) {
		boolean good = true;
		int k = 0;
		while (good) {
			if ((current == null) || (current.getSelectedIndex() == 1)) {
				break;
			}
			BoneShell shell = current.futureBonesList.getSelectedValue();
			// If shell is null, then the bone has "No Parent"
			// If current's selected index is not 2,
			if (shell == null) {
				good = false;
			} else {
				current = boneToPanel.get(shell.bone);
				if (usedBonePanels.contains(current)) {
					good = false;
				} else {
					usedBonePanels.add(current);
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

	private static void boneTabsValueChanged(CardLayout boneCardLayout, JPanel bonePanelCards, JList<BonePanel> boneTabs, MultiBonePanel multiBonePane) {
		// boolean listEnabledNow = false;
		if (boneTabs.getSelectedValuesList().toArray().length < 1) {
			// listEnabledNow = listEnabled;
			boneCardLayout.show(bonePanelCards, "blank");
		} else if (boneTabs.getSelectedValuesList().toArray().length == 1) {
			// listEnabledNow = true;
			boneCardLayout.show(bonePanelCards, (boneTabs.getSelectedIndex()) + "");
			((BonePanel) boneTabs.getSelectedValue()).updateSelectionPicks();
		} else if (boneTabs.getSelectedValuesList().toArray().length > 1) {
			boneCardLayout.show(bonePanelCards, "multiple");
			// listEnabledNow = false;
			final Object[] selected = boneTabs.getSelectedValuesList().toArray();
			boolean dif = false;
			int tempIndex = -99;
			for (int i = 0; (i < selected.length) && !dif; i++) {
				final BonePanel temp = (BonePanel) selected[i];
				if (tempIndex == -99) {
					tempIndex = temp.importTypeBox.getSelectedIndex();
				}
				if (tempIndex != temp.importTypeBox.getSelectedIndex()) {
					dif = true;
				}
			}
			if (dif) {
				multiBonePane.setMultiTypes();
			} else {
				multiBonePane.setSelectedIndex(tempIndex);
			}
		}
		// if( listEnabledNow != listEnabled )
		// {
		// for( int i = 0; i < bonePanels.size(); i++ )
		// {
		// BonePanel bonePanel = bonePanels.get(i);
		// bonePanel.boneListPane.setEnabled(listEnabledNow);
		// bonePanel.boneList.setEnabled(listEnabledNow);
		// }
		// listEnabled = listEnabledNow;
		// }
	}

	private static void importAllBones(DefaultListModel<BonePanel> bonePanels, int selsctionIndex) {
		for (int i = 0; i < bonePanels.size(); i++) {
			final BonePanel bonePanel = bonePanels.get(i);
			bonePanel.setSelectedIndex(selsctionIndex);
		}
	}

	public JPanel makeBonePanel(EditableModel currentModel, EditableModel importedModel, BonePanelListCellRenderer bonePanelRenderer) {
//		addTab("Bones", boneIcon, bonesPanel, "Controls which bones will be imported.");
		JPanel bonesPanel = new JPanel();
//		existingBones = new DefaultListModel<>();
//		final List<Bone> currentMDLBones = currentModel.sortedIdObjects(Bone.class);
		final List<Bone> currentMDLBones = currentModel.getBones();
//		final List<Helper> currentMDLHelpers = currentModel.sortedIdObjects(Helper.class);
		final List<Helper> currentMDLHelpers = currentModel.getHelpers();

		for (Bone currentMDLBone : currentMDLBones) {
			existingBones.addElement(new BoneShell(currentMDLBone));
		}
		for (Helper currentMDLHelper : currentMDLHelpers) {
			existingBones.addElement(new BoneShell(currentMDLHelper));
		}

//		final List<Bone> importedMDLBones = importedModel.sortedIdObjects(Bone.class);
		final List<Bone> importedMDLBones = importedModel.getBones();
//		final List<Helper> importedMDLHelpers = importedModel.sortedIdObjects(Helper.class);
		final List<Helper> importedMDLHelpers = importedModel.getHelpers();

//		clearExistingBones = new JCheckBox("Clear pre-existing bones and helpers");
		// Initialized up here for use with BonePanels


		CardLayout boneCardLayout = new CardLayout();
		JPanel bonePanelCards = new JPanel(boneCardLayout);

		for (int i = 0; i < importedMDLBones.size(); i++) {
			final Bone b = importedMDLBones.get(i);
			final BonePanel bonePanel = new BonePanel(b, existingBones, boneShellRenderer, importPanel);
			// boneTabs.addTab(b.getClass().getName() + " \"" + b.getName() +
			// "\"", cyanIcon, bonePanel, "Controls import settings for this
			// bone.");;
			bonePanelCards.add(bonePanel, i + "");// (bonePanel.title.getText()));
			bonePanels.addElement(bonePanel);
			boneToPanel.put(b, bonePanel);
		}
		for (int i = 0; i < importedMDLHelpers.size(); i++) {
			final Bone b = importedMDLHelpers.get(i);
			final BonePanel bonePanel = new BonePanel(b, existingBones, boneShellRenderer, importPanel);
			// boneTabs.addTab(b.getClass().getName() + " \"" + b.getName() +
			// "\"", cyanIcon, bonePanel, "Controls import settings for this
			// bone.");;
			bonePanelCards.add(bonePanel, importedMDLBones.size() + i + "");// (bonePanel.title.getText()));
			bonePanels.addElement(bonePanel);
			boneToPanel.put(b, bonePanel);
		}
		for (int i = 0; i < bonePanels.size(); i++) {
			bonePanels.get(i).initList();
		}
		MultiBonePanel multiBonePane = new MultiBonePanel(existingBones, boneShellRenderer);
		bonePanelCards.add(new JPanel(), "blank");
		bonePanelCards.add(multiBonePane, "multiple");
		boneTabs.setCellRenderer(bonePanelRenderer);// bonePanelRenderer);
		boneTabs.addListSelectionListener(e -> boneTabsValueChanged(boneCardLayout, bonePanelCards, boneTabs, multiBonePane));
		boneTabs.setSelectedIndex(0);
		bonePanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JButton importAllBones = new JButton("Import All");
		importAllBones.addActionListener(e -> importAllBones(bonePanels, 0));
		bonesPanel.add(importAllBones);

		JButton uncheckAllBones = new JButton("Leave All");
		uncheckAllBones.addActionListener(e -> importAllBones(bonePanels, 2));
		bonesPanel.add(uncheckAllBones);

		JButton motionFromBones = new JButton("Motion From All");
		motionFromBones.addActionListener(e -> importAllBones(bonePanels, 1));
		bonesPanel.add(motionFromBones);

		JButton uncheckUnusedBones = new JButton("Uncheck Unused");
		uncheckUnusedBones.addActionListener(e -> uncheckUnusedBones());
		bonesPanel.add(uncheckUnusedBones);

		JScrollPane boneTabsPane = new JScrollPane(boneTabs);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boneTabsPane, bonePanelCards);

		setLayout(importAllBones, uncheckAllBones, motionFromBones, uncheckUnusedBones, splitPane, bonesPanel);

		return bonesPanel;
	}

	private void setLayout(JButton importAllBones, JButton uncheckAllBones, JButton motionFromBones, JButton uncheckUnusedBones, JSplitPane splitPane, JPanel bonesPanel) {
		final GroupLayout boneLayout = new GroupLayout(bonesPanel);
		boneLayout.setHorizontalGroup(boneLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(boneLayout.createSequentialGroup()
								.addComponent(importAllBones).addGap(8)
								.addComponent(motionFromBones).addGap(8)
								.addComponent(uncheckUnusedBones).addGap(8)
								.addComponent(uncheckAllBones))
						.addComponent(clearExistingBones)
						.addComponent(splitPane)
				// .addGroup(boneLayout.createSequentialGroup()
				// .addComponent(boneTabsPane)
				// .addComponent(bonePanelCards)
				// )
		);
		boneLayout.setVerticalGroup(boneLayout.createSequentialGroup()
						.addGroup(boneLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(importAllBones)
								.addComponent(motionFromBones)
								.addComponent(uncheckUnusedBones)
								.addComponent(uncheckAllBones))
						.addComponent(clearExistingBones).addGap(8)
						.addComponent(splitPane)
				// .addGroup(boneLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				// .addComponent(boneTabsPane)
				// .addComponent(bonePanelCards)
				// )
		);
		bonesPanel.setLayout(boneLayout);
	}

	private void uncheckUnusedBones() {
		// Unselect all bones by iterating + setting to index 2 ("Do not import" index)
		// Bones could be referenced by:
		// - A matrix
		// - Another bone
		// - An IdObject
		final List<BonePanel> usedBonePanels = new ArrayList<>();
		// for( int i = 0; i < bonePanels.size(); i++ )
		// {
		// BonePanel bonePanel = bonePanels.get(i);
		// if( bonePanel.getSelectedIndex() != 1 )
		// bonePanel.setSelectedIndex(2);
		// }
		for (int i = 0; i < bonePanels.size(); i++) {
			final BonePanel bonePanel = bonePanels.get(i);
			if (bonePanel.getSelectedIndex() == 0) {
			}
		}
		for (int i = 0; i < objectPanels.size(); i++) {
			final ObjectPanel objectPanel = objectPanels.get(i);
			if (objectPanel.doImport.isSelected() && (objectPanel.parentsList != null)) {
				BoneShell bs = objectPanel.parentsList.getSelectedValue();
				if ((bs != null) && (bs.bone != null)) {
					BonePanel current = boneToPanel.get(bs.bone);
					if (!usedBonePanels.contains(current)) {
						usedBonePanels.add(current);
					}

					checkSelectedOrSomething(boneToPanel, usedBonePanels, current);
				}
			}
		}
		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
			if (geosetAnimTabs.isEnabledAt(i)) {
				System.out.println("Performing check on geoset: " + i);
				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
				for (int mk = 0; mk < bap.oldBoneRefs.size(); mk++) {
					final MatrixShell ms = bap.oldBoneRefs.get(mk);
					for (final BoneShell bs : ms.newBones) {
						BonePanel current = boneToPanel.get(bs.bone);
						if (!usedBonePanels.contains(current)) {
							usedBonePanels.add(current);
						}

						checkSelectedOrSomething(boneToPanel, usedBonePanels, current);
					}
				}
			}
		}
		for (int i = 0; i < bonePanels.size(); i++) {
			final BonePanel bonePanel = bonePanels.get(i);
			if (bonePanel.getSelectedIndex() != 1) {
				if (usedBonePanels.contains(bonePanel)) {
					// System.out.println("Performing check on base: "+bonePanel.bone.getName());
					checkSelectedOrSomething(boneToPanel, usedBonePanels, bonePanel);
				}
			}
		}
		for (int i = 0; i < bonePanels.size(); i++) {
			final BonePanel bonePanel = bonePanels.get(i);
			if (bonePanel.getSelectedIndex() != 1) {
				if (usedBonePanels.contains(bonePanel)) {
					bonePanel.setSelectedIndex(0);
				} else {
					bonePanel.setSelectedIndex(2);
				}
			}
		}
	}
}
