package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

//public class BonePanel extends JPanel implements ListSelectionListener, ActionListener {
public class BonePanel extends JPanel implements ListSelectionListener {
	static final String IMPORT = "Import this bone";
	static final String MOTIONFROM = "Import motion to pre-existing:";
	static final String LEAVE = "Do not import";
	Bone bone;
	JLabel title;
	String[] impOptions = {IMPORT, MOTIONFROM, LEAVE};

	JComboBox<String> importTypeBox = new JComboBox<>(impOptions);

	// List for which bone to transfer motion
	DefaultListModel<BoneShell> existingBones;
	DefaultListModel<BoneShell> listModel;
	JList<BoneShell> boneList;
	JScrollPane boneListPane;
	JPanel cardPanel;
	CardLayout cards = new CardLayout();
	JPanel dummyPanel = new JPanel();
	DefaultListModel<BoneShell> futureBones;
	JList<BoneShell> futureBonesList;
	JScrollPane futureBonesListPane;
	JLabel parentTitle;
	ImportPanel impPanel;
	Object[] oldSelection = new Object[0];
	boolean listenSelection = true;

	protected BonePanel() {
		// This constructor is negative mojo
	}

	public BonePanel(final Bone whichBone, final DefaultListModel<BoneShell> existingBonesList, final BoneShellListCellRenderer renderer,
	                 final ImportPanel thePanel) {
		setLayout(new MigLayout("gap 0"));
		bone = whichBone;
		existingBones = existingBonesList;
		impPanel = thePanel;
		listModel = new DefaultListModel<>();
		for (int i = 0; i < existingBonesList.size(); i++) {
			listModel.addElement(existingBonesList.get(i));
		}

		title = new JLabel(bone.getClass().getSimpleName() + " \"" + bone.getName() + "\"");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "cell 0 0, spanx, align center, wrap");

		importTypeBox.setEditable(false);
//		importTypeBox.addItemListener(this);
		importTypeBox.addActionListener(e -> listItemSelected());
		importTypeBox.setMaximumSize(new Dimension(200, 20));
		add(importTypeBox, "cell 0 1");

		if (bone.getParent() != null) {
			parentTitle = new JLabel("Parent:      (Old Parent: " + bone.getParent().getName() + ")");
		} else {
			parentTitle = new JLabel("Parent:      (Old Parent: {no parent})");
		}
		add(parentTitle, "cell 2 1");

		boneList = new JList<>(listModel);
		boneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		boneList.setCellRenderer(renderer);
		boneList.addListSelectionListener(e -> listItemSelected());
		boneListPane = new JScrollPane(boneList);
		for (int i = 0; i < listModel.size(); i++) {
			final BoneShell bs = listModel.get(i);
			if (bs.bone.getName().equals(bone.getName()) && (bs.importBone == null)
					&& (!(bs.bone.getName().contains("Mesh") || bs.bone.getName().contains("Object")
					|| bs.bone.getName().contains("Box"))
					|| bs.bone.getPivotPoint().equalLocs(bone.getPivotPoint()))) {
				boneList.setSelectedValue(bs, true);
				bs.setImportBone(bone);
				i = listModel.size();
				// System.out.println("GREAT BALLS OF FIRE");
			}
		}

//		add(boneListPane);
		cardPanel = new JPanel(cards);
		cardPanel.add(boneListPane, "boneList");
		cardPanel.add(dummyPanel, "blank");
		cards.show(cardPanel, "blank");
		add(cardPanel, "cell 1 2, growy");

		futureBones = getImportPanel().getFutureBoneListExtended(true);
		futureBonesList = new JList<>(futureBones);
		futureBonesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		futureBonesList.setCellRenderer(renderer);
		futureBonesListPane = new JScrollPane(futureBonesList);
		add(futureBonesListPane, "cell 2 2, growy");


//		final GroupLayout layout = new GroupLayout(this);
//		layout.setHorizontalGroup(layout.createSequentialGroup().addGap(8)
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
////						.addComponent(title)
//						.addGroup(layout.createSequentialGroup()
////								.addComponent(importTypeBox)
//								.addComponent(cardPanel)
//								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//										.addComponent(parentTitle)
//										.addComponent(futureBonesListPane)))).addGap(8));
//		layout.setVerticalGroup(layout.createSequentialGroup()
////				.addComponent(title).addGap(16)
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
////						.addComponent(importTypeBox)
//						.addComponent(cardPanel)
//						.addGroup(layout.createSequentialGroup()
//								.addComponent(parentTitle)
//								.addComponent(futureBonesListPane))));
//		setLayout(layout);
	}

//	@Override
//	public void actionPerformed(final ActionEvent e) {
//		listItemSelected();
//	}

	private void listItemSelected() {
		updateSelectionPicks();
		final boolean pastListSelectionState = listenSelection;
		listenSelection = false;
		if (importTypeBox.getSelectedItem() == MOTIONFROM) {
			cards.show(cardPanel, "boneList");
		} else {
			cards.show(cardPanel, "blank");
		}
		listenSelection = pastListSelectionState;
	}

	public void initList() {
		futureBones = getImportPanel().getFutureBoneListExtended(false);
		for (int i = 0; i < futureBones.size(); i++) {
			final BoneShell bs = futureBones.get(i);
			if (bs.bone == bone.getParent()) {
				futureBonesList.setSelectedValue(bs, true);
			}
		}
	}

	public ImportPanel getImportPanel() {
		if (impPanel == null) {
			Container temp = getParent();
			while ((temp != null) && (temp.getClass() != ImportPanel.class)) {
				temp = temp.getParent();
			}
			impPanel = (ImportPanel) temp;
		}
		return impPanel;
	}

	public int getSelectedIndex() {
		return importTypeBox.getSelectedIndex();
	}

	public void setSelectedIndex(final int index) {
		importTypeBox.setSelectedIndex(index);
	}

	public void setSelectedValue(final String value) {
		importTypeBox.setSelectedItem(value);
	}

	public void setParent(final BoneShell pick) {
		futureBonesList.setSelectedValue(pick, true);
	}

	public void updateSelectionPicks() {
		listenSelection = false;
		// DefaultListModel newModel = new DefaultListModel();
		final Object[] selection = boneList.getSelectedValuesList().toArray();
		listModel.clear();
		for (int i = 0; i < existingBones.size(); i++) {
			final Bone temp = existingBones.get(i).importBone;
			if ((temp == null) || (temp == bone)) {
				listModel.addElement(existingBones.get(i));
			}
		}
		final int[] indices = new int[selection.length];
		for (int i = 0; i < selection.length; i++) {
			indices[i] = listModel.indexOf(selection[i]);
		}
		boneList.setSelectedIndices(indices);
		listenSelection = true;

		final Object[] newSelection;
		if (importTypeBox.getSelectedIndex() == 1) {
			newSelection = boneList.getSelectedValuesList().toArray();
		} else {
			newSelection = new Object[0];
		}
		// ImportPanel panel = getImportPanel();
		for (final Object a : oldSelection) {
			((BoneShell) a).setImportBone(null);
		}
		for (final Object a : newSelection) {
			((BoneShell) a).setImportBone(bone);
		}
		oldSelection = newSelection;
		final long nanoStart = System.nanoTime();
		futureBones = getImportPanel().getFutureBoneListExtended(false);
		final long nanoEnd = System.nanoTime();
		System.out.println("updating future bone list took " + (nanoEnd - nanoStart) + " ns");
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		if (listenSelection && e.getValueIsAdjusting()) {
			updateSelectionPicks();
		}
	}

	public void getSelectedBones(List<IdObject> objectsAdded, EditableModel currentModel) {
		final Bone b = bone;
		final int type = importTypeBox.getSelectedIndex();
		// b.setName(b.getName()+" "+importedModel.getName());
		// bonePanel.boneList.getSelectedValuesList();

		// we will go through all bone shells for this
		// Fix cross-model referencing issue (force clean parent node's list of children)
		switch (type) {
			case 0 -> {
				currentModel.add(b);
				objectsAdded.add(b);
				final BoneShell mbs = futureBonesList.getSelectedValue();
				if (mbs != null) {
					b.setParent((mbs).bone);
				} else {
					b.setParent(null);
				}
			}
			case 1, 2 -> b.setParent(null);
		}
	}
}
