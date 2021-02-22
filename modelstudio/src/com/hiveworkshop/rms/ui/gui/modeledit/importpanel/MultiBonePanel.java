package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class MultiBonePanel extends BonePanel {
	JButton setAllParent;
	boolean listenForChange = true;
	private ModelHolderThing mht;

	public MultiBonePanel(final IterableListModel<BoneShell> existingBonesList, final BoneShellListCellRenderer renderer) {
		setLayout(new MigLayout("gap 0"));
		setBackground(Color.BLUE);
		setOpaque(true);
		bone = null;
		existingBones = existingBonesList;

		JLabel title = new JLabel("Multiple Selected");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		JScrollPane boneListPane = new JScrollPane(animationFromBoneList);
		boneListPane.setVisible(false);

		importTypeBox.setEditable(false);
		importTypeBox.addActionListener(e -> listSelectionChanged(boneListPane));
		importTypeBox.setMaximumSize(new Dimension(200, 20));
		add(importTypeBox, "wrap");

		animationFromBoneList = new JList<>(existingBones);
		animationFromBoneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		animationFromBoneList.setCellRenderer(renderer);

//		add(boneListPane);
		cardPanel = new JPanel(cards);
		cardPanel.add(boneListPane, "boneList");
		cardPanel.add(new JPanel(), "blank");
		animationFromBoneList.setEnabled(false);
		cards.show(cardPanel, "blank");
		add(cardPanel, "wrap");

		setAllParent = new JButton("Set Parent for All");
		setAllParent.addActionListener(e -> setParentMultiBones(renderer));
		add(setAllParent, "wrap");
	}

	public MultiBonePanel(ModelHolderThing mht, final BoneShellListCellRenderer renderer) {
		this.mht = mht;
		setLayout(new MigLayout("gap 0"));
		setBackground(Color.BLUE);
		setOpaque(true);
		bone = null;

		JLabel title = new JLabel("Multiple Selected");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		JScrollPane boneListPane = new JScrollPane(animationFromBoneList);
		boneListPane.setVisible(false);

		importTypeBox.setEditable(false);
		importTypeBox.addActionListener(e -> listSelectionChanged(boneListPane));
		importTypeBox.setMaximumSize(new Dimension(200, 20));
		add(importTypeBox, "wrap");

		animationFromBoneList = new JList<>(mht.existingBones);
		animationFromBoneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		animationFromBoneList.setCellRenderer(renderer);

//		add(boneListPane);
		cardPanel = new JPanel(cards);
		cardPanel.add(boneListPane, "boneList");
		cardPanel.add(new JPanel(), "blank");
		animationFromBoneList.setEnabled(false);
		cards.show(cardPanel, "blank");
		add(cardPanel, "wrap");

		setAllParent = new JButton("Set Parent for All");
		setAllParent.addActionListener(e -> setParentMultiBones(renderer));
		add(setAllParent, "wrap");
	}

	@Override
	public void setSelectedIndex(final int index) {
		listenForChange = false;
		importTypeBox.setSelectedIndex(index);
		listenForChange = true;
	}

	@Override
	public void setSelectedValue(final String value) {
		listenForChange = false;
		importTypeBox.setSelectedItem(value);
		listenForChange = true;
	}

	public void setMultiTypes() {
		listenForChange = false;
		importTypeBox.setEditable(true);
		importTypeBox.setSelectedItem("Multiple selected");
		importTypeBox.setEditable(false);
		// boneListPane.setVisible(false);
		// boneList.setVisible(false);
		cards.show(cardPanel, "blank");
		revalidate();
		listenForChange = true;
	}

//	@Override
//	public void actionPerformed(final ActionEvent e) {
//		listSelectionChanged();
//	}

	public static void setSelectedItem(JList<BoneShell> boneTabs, final String what) {
		List<BoneShell> boneShellList = boneTabs.getSelectedValuesList();
		for (BoneShell boneShell : boneShellList) {
			switch (what) {
				case "Import this bone" -> boneShell.setImportStatus(0);
				case "Import motion to pre-existing:" -> boneShell.setImportStatus(1);
				case "Do not import" -> boneShell.setImportStatus(2);
			}
		}
	}

	private void listSelectionChanged(JScrollPane boneListPane) {

		final long nanoStart = System.nanoTime();
		final boolean pastListSelectionState = listenSelection;
		listenSelection = false;

		boneListPane.setVisible(importTypeBox.getSelectedItem() != MOTIONFROM);

		listenSelection = pastListSelectionState;
		if (listenForChange) {
			setSelectedItem(mht.boneTabs, (String) importTypeBox.getSelectedItem());
		}
		final long nanoEnd = System.nanoTime();
		System.out.println("MultiBonePanel.actionPerformed() took " + (nanoEnd - nanoStart) + " ns");
	}

	/**
	 * The method run when the user pushes the "Set Parent for All" button in the
	 * MultiBone panel.
	 */
	private void setParentMultiBones(BoneShellListCellRenderer renderer) {
		final JList<BoneShell> list = new JList<>(mht.getFutureBoneListExtended(true));
		list.setCellRenderer(renderer);
		final int x = JOptionPane.showConfirmDialog(this, new JScrollPane(list), "Set Parent for All Selected Bones", JOptionPane.OK_CANCEL_OPTION);
		if (x == JOptionPane.OK_OPTION) {
			List<BoneShell> boneShells = mht.boneTabs.getSelectedValuesList();
			for (BoneShell boneShell : boneShells) {
				boneShell.setParent(list.getSelectedValue());
			}
		}
	}
}
