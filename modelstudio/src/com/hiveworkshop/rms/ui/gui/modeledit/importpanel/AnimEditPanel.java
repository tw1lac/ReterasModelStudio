package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.ui.icons.RMSIcons;

import javax.swing.*;
import java.awt.*;

public class AnimEditPanel {
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));

	JTabbedPane animTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
	DefaultListModel<AnimShell> existingAnims;
	JCheckBox clearExistingAnims;

	public AnimEditPanel(JTabbedPane animTabs, DefaultListModel<AnimShell> existingAnims, JCheckBox clearExistingAnims) {
		this.animTabs = animTabs;
		this.existingAnims = existingAnims;
		this.clearExistingAnims = clearExistingAnims;
	}

	private static void renameAllAnims(Component parent, JTabbedPane animTabs) {
		final String newTagString = JOptionPane.showInputDialog(parent,
				"Choose additional naming (i.e. swim or alternate)");
		if (newTagString != null) {
			for (int i = 0; i < animTabs.getTabCount(); i++) {

				final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
				aniPanel.importTypeBox.setSelectedIndex(1);
				final String oldName = aniPanel.anim.getName();

				String baseName = oldName;
				while ((baseName.length() > 0) && baseName.contains(" ")) {
					final int lastSpaceIndex = baseName.lastIndexOf(' ');
					final String lastWord = baseName.substring(lastSpaceIndex + 1);
					boolean chunkHasInt = false;

					for (int animationId = 0; animationId < 10; animationId++) {
						if (lastWord.contains(Integer.toString(animationId))) {
							chunkHasInt = true;
						}
					}
					if (lastWord.contains("-")
							|| chunkHasInt
							|| lastWord.toLowerCase().contains("alternate")
							|| (lastWord.length() <= 0)) {
						baseName = baseName.substring(0, baseName.lastIndexOf(' '));
					} else {
						break;
					}
				}
				final String afterBase = oldName.substring(Math.min(oldName.length(), baseName.length() + 1));
				final String newName = baseName + " " + newTagString + " " + afterBase;
				aniPanel.newNameEntry.setText(newName);
			}
		}
	}

	private static void uncheckAllAnims(JTabbedPane animTabs, boolean b) {
		for (int i = 0; i < animTabs.getTabCount(); i++) {
			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
			aniPanel.setSelected(b);
		}
	}

	private static void timescaleAllAnims(JTabbedPane animTabs) {
		for (int i = 0; i < animTabs.getTabCount(); i++) {
			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
			aniPanel.importTypeBox.setSelectedIndex(2);
		}
	}

	public JPanel makeAnimationPanel(EditableModel currentModel, EditableModel importedModel) {
		JPanel animPanel = new JPanel();
//		addTab("Animation", animIcon, animPanel, "Controls which animations will be imported.");

//		existingAnims = new DefaultListModel<>();
		for (int i = 0; i < currentModel.getAnims().size(); i++) {
			existingAnims.addElement(new AnimShell(currentModel.getAnims().get(i)));
		}

		final AnimListCellRenderer animsRenderer = new AnimListCellRenderer();

		JButton importAllAnims = new JButton("Import All");
		importAllAnims.addActionListener(e -> uncheckAllAnims(animTabs, true));
		animPanel.add(importAllAnims);

		JButton timescaleAllAnims = new JButton("Time-scale All");
		timescaleAllAnims.addActionListener(e -> timescaleAllAnims(animTabs));
		animPanel.add(timescaleAllAnims);

		JButton renameAllAnims = new JButton("Import and Rename All");
		renameAllAnims.addActionListener(e -> renameAllAnims(animPanel.getParent(), animTabs));
		animPanel.add(renameAllAnims);

		JButton uncheckAllAnims = new JButton("Leave All");
		uncheckAllAnims.addActionListener(e -> uncheckAllAnims(animTabs, false));
		animPanel.add(uncheckAllAnims);

//		clearExistingAnims = new JCheckBox("Clear pre-existing animations");

		// Build the animTabs list of AnimPanels
		for (int i = 0; i < importedModel.getAnims().size(); i++) {
			final Animation anim = importedModel.getAnim(i);
			final AnimPanel iAnimPanel = new AnimPanel(anim, existingAnims, animsRenderer);

			animTabs.addTab(anim.getName(), orangeIcon, iAnimPanel, "Click to modify data for this animation sequence.");
		}

		animPanel.add(clearExistingAnims);
		animPanel.add(animTabs);

		setLayout(animPanel, importAllAnims, timescaleAllAnims, renameAllAnims, uncheckAllAnims);
		return animPanel;
	}

	private void setLayout(JPanel animPanel, JButton importAllAnims, JButton timescaleAllAnims, JButton renameAllAnims, JButton uncheckAllAnims) {
		final GroupLayout animLayout = new GroupLayout(animPanel);
		animLayout.setHorizontalGroup(animLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(animLayout.createSequentialGroup()
						.addComponent(importAllAnims).addGap(8)
						.addComponent(renameAllAnims).addGap(8)
						.addComponent(timescaleAllAnims).addGap(8)
						.addComponent(uncheckAllAnims))
				.addComponent(clearExistingAnims)
				.addComponent(animTabs));
		animLayout.setVerticalGroup(animLayout.createSequentialGroup()
				.addGroup(animLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(importAllAnims)
						.addComponent(renameAllAnims)
						.addComponent(timescaleAllAnims)
						.addComponent(uncheckAllAnims))
				.addComponent(clearExistingAnims).addGap(8)
				.addComponent(animTabs));
		animPanel.setLayout(animLayout);
	}
}
