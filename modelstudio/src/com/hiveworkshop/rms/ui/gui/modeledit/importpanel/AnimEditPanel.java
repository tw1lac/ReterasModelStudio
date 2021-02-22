package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.ui.icons.RMSIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnimEditPanel {
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));

	private ModelHolderThing mht;

	public AnimEditPanel(ModelHolderThing mht) {
		this.mht = mht;
	}

	public JPanel makeAnimationPanel() {
		JPanel animPanel = new JPanel();
//		addTab("Animation", animIcon, animPanel, "Controls which animations will be imported.");

//		existingAnims = new IterableListModel<>();
		for (Animation animation : mht.currentModel.getAnims()) {
			mht.existingAnims.addElement(new AnimShell(animation));
		}

		final AnimListCellRenderer animsRenderer = new AnimListCellRenderer();

		JButton importAllAnims = createButton("Import All", e -> uncheckAllAnims(mht.animTabs, true));
		animPanel.add(importAllAnims);

		JButton timescaleAllAnims = createButton("Time-scale All", e -> timescaleAllAnims(mht.animTabs));
		animPanel.add(timescaleAllAnims);

		JButton renameAllAnims = createButton("Import and Rename All", e -> renameAllAnims(animPanel.getParent(), mht.animTabs));
		animPanel.add(renameAllAnims);

		JButton uncheckAllAnims = createButton("Leave All", e -> uncheckAllAnims(mht.animTabs, false));
		animPanel.add(uncheckAllAnims);

		// Build the animTabs list of AnimPanels
		for (Animation anim : mht.importModel.getAnims()) {
			final AnimPanel iAnimPanel = new AnimPanel(anim, mht.existingAnims, animsRenderer);

			mht.animTabs.addTab(anim.getName(), orangeIcon, iAnimPanel, "Click to modify data for this animation sequence.");
		}

		animPanel.add(mht.clearExistingAnims);
		animPanel.add(mht.animTabs);

		setLayout(animPanel, importAllAnims, timescaleAllAnims, renameAllAnims, uncheckAllAnims);
		return animPanel;
	}

	public JButton createButton(String text, ActionListener actionListener) {
		JButton uncheckAllAnims = new JButton(text);
		uncheckAllAnims.addActionListener(actionListener);
		return uncheckAllAnims;
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

	private void setLayout(JPanel animPanel, JButton importAllAnims, JButton timescaleAllAnims, JButton renameAllAnims, JButton uncheckAllAnims) {
		final GroupLayout animLayout = new GroupLayout(animPanel);
		animLayout.setHorizontalGroup(animLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(animLayout.createSequentialGroup()
						.addComponent(importAllAnims).addGap(8)
						.addComponent(renameAllAnims).addGap(8)
						.addComponent(timescaleAllAnims).addGap(8)
						.addComponent(uncheckAllAnims))
				.addComponent(mht.clearExistingAnims)
				.addComponent(mht.animTabs));
		animLayout.setVerticalGroup(animLayout.createSequentialGroup()
				.addGroup(animLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(importAllAnims)
						.addComponent(renameAllAnims)
						.addComponent(timescaleAllAnims)
						.addComponent(uncheckAllAnims))
				.addComponent(mht.clearExistingAnims).addGap(8)
				.addComponent(mht.animTabs));
		animPanel.setLayout(animLayout);
	}
}
