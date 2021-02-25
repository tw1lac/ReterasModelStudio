package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnimEditPanel {
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));

	private ModelHolderThing mht;
	AnimPanel currAnimPanel;

	public AnimEditPanel(ModelHolderThing mht) {
		this.mht = mht;
	}

	public JPanel makeAnimationPanel() {
		JPanel animPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[][grow]"));
//		addTab("Animation", animIcon, animPanel, "Controls which animations will be imported.");

//		existingAnims = new IterableListModel<>();
		for (Animation animation : mht.receivingModel.getAnims()) {
			mht.existingAnims.addElement(new AnimShell(animation));
		}

		final AnimListCellRenderer animsRenderer = new AnimListCellRenderer();

		JPanel topPanel = getTopPanel(animPanel);

		currAnimPanel = new AnimPanel(mht.existingAnims, animsRenderer);
		// Build the animTabs list of AnimPanels
		for (Animation anim : mht.donatingModel.getAnims()) {
			mht.aniShells.addElement(new AnimShell(anim));

//			mht.animTabs.addTab(anim.getName(), orangeIcon, currAnimPanel, "Click to modify data for this animation sequence.");
		}
		mht.animTabs2.addListSelectionListener(e -> setAnim());

		JPanel bigPanel = new JPanel(new MigLayout("gap 0, fill", "[30%:30%:30%][70%:70%:70%]", "[grow]"));
//		JPanel bigPanel = new JPanel(new MigLayout("gap 0, fill", "[10%:30%:30%][100%:100%:100%]", "[grow]"));
		JScrollPane animTabsPane = new JScrollPane(mht.animTabs2);
		bigPanel.add(animTabsPane, "growy");
		bigPanel.add(currAnimPanel, "growy, growx");

		animPanel.add(topPanel, "align center, wrap");
		animPanel.add(bigPanel, "growx, growy");

		return animPanel;
	}

	private JPanel getTopPanel(JPanel animPanel) {
		JPanel topPanel = new JPanel(new MigLayout("gap 0"));

		JButton importAllAnims = createButton("Import All", e -> uncheckAllAnims(true));
		topPanel.add(importAllAnims);

		JButton timescaleAllAnims = createButton("Time-scale All", e -> timescaleAllAnims());
		topPanel.add(timescaleAllAnims);

		JButton renameAllAnims = createButton("Import and Rename All", e -> renameAllAnims(animPanel.getParent()));
		topPanel.add(renameAllAnims);

		JButton uncheckAllAnims = createButton("Leave All", e -> uncheckAllAnims(false));
		topPanel.add(uncheckAllAnims, "wrap");

		topPanel.add(mht.clearExistingAnims, "spanx, align center");
		return topPanel;
	}

	public void setAnim() {
		currAnimPanel.setCurrAnimShell(mht.animTabs2.getSelectedValue());
	}

//	public JPanel makeAnimationPanel() {
//		JPanel animPanel = new JPanel(new MigLayout("gap 0", "[grow]", "[][grow]"));
////		addTab("Animation", animIcon, animPanel, "Controls which animations will be imported.");
//
////		existingAnims = new IterableListModel<>();
//		for (Animation animation : mht.currentModel.getAnims()) {
//			mht.existingAnims.addElement(new AnimShell(animation));
//		}
//
//		final AnimListCellRenderer animsRenderer = new AnimListCellRenderer();
//
//		JPanel topPanel = new JPanel(new MigLayout("gap 0"));
//
//		JButton importAllAnims = createButton("Import All", e -> uncheckAllAnims(mht.animTabs, true));
//		topPanel.add(importAllAnims);
//
//		JButton timescaleAllAnims = createButton("Time-scale All", e -> timescaleAllAnims(mht.animTabs));
//		topPanel.add(timescaleAllAnims);
//
//		JButton renameAllAnims = createButton("Import and Rename All", e -> renameAllAnims(animPanel.getParent(), mht.animTabs));
//		topPanel.add(renameAllAnims);
//
//		JButton uncheckAllAnims = createButton("Leave All", e -> uncheckAllAnims(mht.animTabs, false));
//		topPanel.add(uncheckAllAnims, "wrap");
//
//		// Build the animTabs list of AnimPanels
//		for (Animation anim : mht.importModel.getAnims()) {
//			mht.aniShells.addElement(new AnimShell(anim));
//			final AnimPanel iAnimPanel = new AnimPanel(anim, mht.existingAnims, animsRenderer);
//
//			mht.animTabs.addTab(anim.getName(), orangeIcon, iAnimPanel, "Click to modify data for this animation sequence.");
//		}
//
////		animPanel.add(mht.clearExistingAnims);
//		topPanel.add(mht.clearExistingAnims, "spanx, align center");
//		animPanel.add(topPanel, "wrap, align center");
//		animPanel.add(mht.animTabs, "growx, growy");
//
//		return animPanel;
//	}

	public JButton createButton(String text, ActionListener actionListener) {
		JButton uncheckAllAnims = new JButton(text);
		uncheckAllAnims.addActionListener(actionListener);
		return uncheckAllAnims;
	}


	private void renameAllAnims(Component parent) {
		final String newTagString = JOptionPane.showInputDialog(parent,
				"Choose additional naming (i.e. swim or alternate)");
		if (newTagString != null) {
			for (AnimShell animShell : mht.aniShells) {

				animShell.setImportType(1);
				final String oldName = animShell.getName();

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
				animShell.setName(newName);
			}
		}
	}
//	private static void renameAllAnims(Component parent, JTabbedPane animTabs) {
//		final String newTagString = JOptionPane.showInputDialog(parent,
//				"Choose additional naming (i.e. swim or alternate)");
//		if (newTagString != null) {
//			for (int i = 0; i < animTabs.getTabCount(); i++) {
//
//				final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
//				aniPanel.importTypeBox.setSelectedIndex(1);
//				final String oldName = aniPanel.anim.getName();
//
//				String baseName = oldName;
//				while ((baseName.length() > 0) && baseName.contains(" ")) {
//					final int lastSpaceIndex = baseName.lastIndexOf(' ');
//					final String lastWord = baseName.substring(lastSpaceIndex + 1);
//					boolean chunkHasInt = false;
//
//					for (int animationId = 0; animationId < 10; animationId++) {
//						if (lastWord.contains(Integer.toString(animationId))) {
//							chunkHasInt = true;
//						}
//					}
//					if (lastWord.contains("-")
//							|| chunkHasInt
//							|| lastWord.toLowerCase().contains("alternate")
//							|| (lastWord.length() <= 0)) {
//						baseName = baseName.substring(0, baseName.lastIndexOf(' '));
//					} else {
//						break;
//					}
//				}
//				final String afterBase = oldName.substring(Math.min(oldName.length(), baseName.length() + 1));
//				final String newName = baseName + " " + newTagString + " " + afterBase;
//				aniPanel.newNameEntry.setText(newName);
//			}
//		}
//	}

	private void uncheckAllAnims(boolean b) {
		for (AnimShell animShell : mht.aniShells) {
			animShell.setDoImport(b);
		}
	}

//	private static void uncheckAllAnims(JTabbedPane animTabs, boolean b) {
//		for (int i = 0; i < animTabs.getTabCount(); i++) {
//			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
//			aniPanel.setSelected(b);
//		}
//	}

	private void timescaleAllAnims() {
		for (AnimShell animShell : mht.aniShells) {
			animShell.setImportType(2);
		}
	}
//	private static void timescaleAllAnims(JTabbedPane animTabs) {
//		for (int i = 0; i < animTabs.getTabCount(); i++) {
//			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
//			aniPanel.importTypeBox.setSelectedIndex(2);
//		}
//	}
}
