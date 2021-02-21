package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.BiMap;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class BonePanel extends JPanel implements ListSelectionListener {
	static final String IMPORT = "Import this bone";
	static final String MOTIONFROM = "Import motion to pre-existing:";
	static final String LEAVE = "Do not import";
	static Bone selectedBone;
	static BoneShell selectedBoneShell;
	Bone bone;
	static JLabel parentTitle;
	static JLabel title;
	static BiMap<Bone, BoneShell> boneToBoneShellMap;
	static BiMap<Bone, Bone> boneToImportBoneMap;
	private static IterableListModel<BoneShell> nullImpBoneBoneList;
	private final String[] impOptions = {IMPORT, MOTIONFROM, LEAVE};
	JComboBox<String> importTypeBox = new JComboBox<>(impOptions);
	JPanel cardPanel;
	CardLayout cards = new CardLayout();
	// List for which bone to transfer motion
	IterableListModel<BoneShell> existingBones;
	JList<BoneShell> futureBonesList;
	JList<BoneShell> animationFromBoneList;
	ImportPanel importPanel;
	boolean listenSelection = true;
	JScrollPane animationBoneListPane;
	private IterableListModel<BoneShell> listModel;
	private IterableListModel<BoneShell> hidenBonesListModel;
	private IterableListModel<BoneShell> futureBones;
	private Object[] oldSelection = new Object[0];
	private ModelHolderThing mht;

	protected BonePanel() {
		// This constructor is negative mojo
	}

//	public BonePanel(final Bone whichBone, final IterableListModel<BoneShell> existingBonesList, final BoneShellListCellRenderer renderer,
//	                 final ImportPanel importPanelin) {
//		setLayout(new MigLayout("gap 0"));
//		setBackground(Color.cyan);
//		setOpaque(true);
//		bone = whichBone;
//		existingBones = existingBonesList;
//		this.importPanel = importPanelin;
//		listModel = new IterableListModel<>();
//		listModel.addAll(existingBonesList);
//		hidenBonesListModel = new IterableListModel<>();
//		nullImpBoneBoneList = new IterableListModel<>();
//		boneToBoneShellMap = new BiMap<>();
//		boneToImportBoneMap = new BiMap<>();
//
//		for(BoneShell bs : existingBonesList){
//			boneToBoneShellMap.put(bs.bone, bs);
//		}
//		selectedBone = bone;
//
//		title = new JLabel();
//		title.setFont(new Font("Arial", Font.BOLD, 26));
//		add(title, "cell 0 0, spanx, align center, wrap");
//
//
//		boneList = new JList<>(listModel);
//		boneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		boneList.setCellRenderer(renderer);
//
//		JScrollPane boneListPane = new JScrollPane(boneList);
//		boneListPane.setVisible(false);
//		boneList.addListSelectionListener(e -> listItemSelected(boneListPane));
//
//		importTypeBox.setEditable(false);
//		importTypeBox.addActionListener(e -> listItemSelected(boneListPane));
//		importTypeBox.setMaximumSize(new Dimension(200, 20));
//		add(importTypeBox, "cell 0 1");
//
//		parentTitle = new JLabel();
//		add(parentTitle, "cell 2 1");
//
//		setImportBoneForBone();
//
////		cardPanel = new JPanel(cards);
////		cardPanel.add(boneListPane, "boneList");
////		cardPanel.add(new JPanel(), "blank");
////		cards.show(cardPanel, "blank");
//		add(boneListPane, "cell 1 2, growy");
//
//		IterableListModel<BoneShell> futureBones = importPanel.mht.getFutureBoneListExtended(true);
//		futureBonesList = new JList<>(futureBones);
//		futureBonesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		futureBonesList.setCellRenderer(renderer);
//		JScrollPane futureBonesListPane = new JScrollPane(futureBonesList);
//		add(futureBonesListPane, "cell 2 2, growy");
//	}

	public BonePanel(ModelHolderThing mht, final BoneShellListCellRenderer renderer,
	                 final ImportPanel importPanelin) {
		this.mht = mht;
		setLayout(new MigLayout("gap 0"));
		setBackground(Color.cyan);
		setOpaque(true);
		this.importPanel = importPanelin;
		listModel = new IterableListModel<>();
		listModel.addAll(mht.existingBones);
		hidenBonesListModel = new IterableListModel<>();
		nullImpBoneBoneList = new IterableListModel<>();
		boneToBoneShellMap = new BiMap<>();
		boneToImportBoneMap = new BiMap<>();

		animationFromBoneList = new JList<>(listModel);
		animationFromBoneList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		animationFromBoneList.setCellRenderer(renderer);
		animationFromBoneList.addListSelectionListener(e -> listItemSelected());

		animationBoneListPane = new JScrollPane(animationFromBoneList);
		animationBoneListPane.setVisible(false);

		for (BoneShell bs : mht.existingBones) {
			boneToBoneShellMap.put(bs.bone, bs);
		}

		title = new JLabel("Bone Title");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "cell 0 0, spanx, align center, wrap");


		importTypeBox.setEditable(false);
		importTypeBox.addActionListener(e -> listItemSelected());
		importTypeBox.addActionListener(e -> importTypeChanged());
		importTypeBox.setMaximumSize(new Dimension(200, 20));
		add(importTypeBox, "cell 0 1");

		parentTitle = new JLabel("\"Parent:      (Old Parent: {no parent?})\"");
		add(parentTitle, "cell 2 1");

//		setImportBoneForBone();

//		cardPanel = new JPanel(cards);
//		cardPanel.add(boneListPane, "boneList");
//		cardPanel.add(new JPanel(), "blank");
//		cards.show(cardPanel, "blank");
		add(animationBoneListPane, "cell 1 2, growy");

//		IterableListModel<BoneShell> futureBones = mht.getFutureBoneListExtended(true);
		futureBones = mht.getFutureBoneListExtended(true);
		futureBones.add(0, new BoneShell(new Bone("None")));
		futureBonesList = new JList<>(futureBones);
		futureBonesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		futureBonesList.setCellRenderer(renderer);
		futureBonesList.addListSelectionListener(e -> changedParent());
		JScrollPane futureBonesListPane = new JScrollPane(futureBonesList);
		add(futureBonesListPane, "cell 2 2, growy");
	}

	private void setImportBoneForBone() {
		for (BoneShell bs : listModel) {
			if (bs.bone.getName().equals(bone.getName()) && (bs.importBone == null) && (
					(
							!bs.bone.getName().contains("Mesh")
									&& !bs.bone.getName().contains("Object")
									&& !bs.bone.getName().contains("Box")
					)
							|| bs.bone.getPivotPoint().equalLocs(bone.getPivotPoint()))
			) {
				animationFromBoneList.setSelectedValue(bs, true);
				bs.setImportBone(bone);
				boneToImportBoneMap.put(bone, bone);
				break;
			}
		}
	}

	public void BoneListItemSelectionChanged(ListSelectionEvent e) {
		System.out.println(e.getValueIsAdjusting());
		System.out.println();
	}

	private void listItemSelected() {
		updateSelectionPicks();
	}

	private void importTypeChanged() {
		selectedBoneShell.importStatus = importTypeBox.getSelectedIndex();
		if (importTypeBox.getSelectedIndex() == 0) {
			selectedBoneShell.shouldImportBone = true;
		}
		animationBoneListPane.setVisible(importTypeBox.getSelectedItem() == MOTIONFROM);
//		updateSelectionPicks();
//		final boolean pastListSelectionState = listenSelection;
//		listenSelection = false;
//		boneListPane.setVisible(importTypeBox.getSelectedItem() == MOTIONFROM);
//		listenSelection = pastListSelectionState;
	}

//	public void initList() {
////		IterableListModel<BoneShell> futureBones = mht.getFutureBoneListExtended(false);
//		for (BoneShell bs : futureBones) {
//			if (bs.bone == selectedBone.getParent()) {
//				futureBonesList.setSelectedValue(bs, true);
//				break;
//			}
//		}
//	}

	public void selectParent() {
//		IterableListModel<BoneShell> futureBones = mht.getFutureBoneListExtended(false);
		for (BoneShell bs : futureBones) {
			if (bs.bone == selectedBone.getParent()) {
				futureBonesList.setSelectedValue(bs, true);
				break;
			}
		}
	}

//	public BonePanel setSelectedBone(Bone bone){
//		BonePanel.selectedBone = bone;
//		setParentTitle();
//		setBoneTitle();
//		return this;
//	}

	public BonePanel setParentTitle() {
		System.out.println("setParentTitle for : " + selectedBone.getName());
		if (selectedBone.getParent() != null) {
			parentTitle.setText("Parent:      (Old Parent: " + selectedBone.getParent().getName() + ")");
		} else {
			parentTitle.setText("Parent:      (Old Parent: {no parent})");
		}
		System.out.println("Parent title: " + parentTitle.getText());
		return this;
	}

	public BonePanel setBoneTitle() {
		System.out.println("setBoneTitle for : " + selectedBone.getName());
		title.setText(selectedBone.getClass().getSimpleName() + " \"" + selectedBone.getName() + "\"");
		System.out.println("title: " + title.getText());
		return this;
	}

	private void changedParent() {
		System.out.println("changed Parent");
		selectedBoneShell.parentBs = futureBonesList.getSelectedValue();
		if (futureBonesList.getSelectedValue() == null) {
			selectedBoneShell.newParent = null;
		} else {
			selectedBoneShell.newParent = futureBonesList.getSelectedValue().bone;
		}
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

	private void selectParentBs() {
		System.out.println("selectParentBs");
		futureBonesList.setSelectedValue(selectedBoneShell.parentBs, true);
		if (selectedBoneShell.parentBs != null) {
		}
	}

	public void setParent(final BoneShell pick) {
		futureBonesList.setSelectedValue(pick, true);
		selectedBoneShell.newParent = pick.bone;
		selectedBoneShell.parentBs = pick;
	}

	public void updateSelectionPicks() {
		listenSelection = false;
		// IterableListModel newModel = new IterableListModel();
//		listModel.addAll(hidenBonesListModel);
		final Object[] selection = animationFromBoneList.getSelectedValuesList().toArray();

		updateVisibleBoneList();

		final int[] indices = new int[selection.length];
		for (int i = 0; i < selection.length; i++) {
			indices[i] = listModel.indexOf(selection[i]);
		}
		animationFromBoneList.setSelectedIndices(indices);
		listenSelection = true;

		updateBoneListSelection();

		final long nanoStart = System.nanoTime();
		mht.getFutureBoneListExtended(false);
		final long nanoEnd = System.nanoTime();
		System.out.println("updating future bone list took " + (nanoEnd - nanoStart) + " ns");
	}

	private void updateVisibleBoneList() {
		if (selectedBoneShell != null && selectedBoneShell.importBone != null) {
			nullImpBoneBoneList.remove(selectedBoneShell);
		} else if (!nullImpBoneBoneList.contains(selectedBoneShell)) {
			nullImpBoneBoneList.addElement(selectedBoneShell);
		}
		listModel.clear();
		listModel.addAll(nullImpBoneBoneList);
	}

	private void updateBoneListSelection() {
		final Object[] newSelection;
		if (importTypeBox.getSelectedIndex() == 1) {
			newSelection = animationFromBoneList.getSelectedValuesList().toArray();
		} else {
			newSelection = new Object[0];
		}
		// ImportPanel panel = getImportPanel();
		for (final Object a : oldSelection) {
			((BoneShell) a).setImportBone(null);
			boneToImportBoneMap.put(((BoneShell) a).bone, null);
		}
		for (final Object a : newSelection) {
			((BoneShell) a).setImportBone(bone);
			boneToImportBoneMap.put(((BoneShell) a).bone, bone);
		}
		oldSelection = newSelection;
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		System.out.println("BonePanel value changed event");
		if (listenSelection && e.getValueIsAdjusting()) {
			System.out.println("BonePanel value changed DO!");
			updateSelectionPicks();
		}
	}

	public BoneShell getCurrentBoneShell() {
		return selectedBoneShell;
	}

	public void getSelectedBones(List<IdObject> objectsAdded, EditableModel currentModel) {
		// we will go through all bone shells for this
		// Fix cross-model referencing issue (force clean parent node's list of children)
		if (importTypeBox.getSelectedIndex() == 0) {
			currentModel.add(bone);
			objectsAdded.add(bone);
			bone.setParent(getSelectedBone());
		} else {
			bone.setParent(null);
		}
	}

	private Bone getSelectedBone() {
		final BoneShell mbs = futureBonesList.getSelectedValue();
		if (mbs != null) {
			return mbs.bone;
		} else {
			return null;
		}
	}

	public BonePanel setSelectedBone(BoneShell boneShell) {
		BonePanel.selectedBone = boneShell.bone;
		BonePanel.selectedBoneShell = boneShell;
		setParentTitle();
		setBoneTitle();
//		selectParent();
		setSelectedIndex(boneShell.importStatus);
		animationBoneListPane.setVisible(importTypeBox.getSelectedItem() == MOTIONFROM);
		selectParentBs();
		revalidate();
		repaint();
		return this;
	}
}
