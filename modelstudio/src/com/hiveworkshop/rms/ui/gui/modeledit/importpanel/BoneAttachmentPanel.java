package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;
import com.hiveworkshop.rms.util.IterableListModel;
import com.hiveworkshop.rms.util.Vec3;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class BoneAttachmentPanel extends JPanel {
	JLabel title;

	// Old bone refs (matrices)
	IterableListModel<MatrixShell> oldBoneRefs = new IterableListModel<>();
	JList<MatrixShell> oldBoneRefsList;
	JScrollPane oldBoneRefsPane;

	// New refs
	IterableListModel<BoneShell> newRefs = new IterableListModel<>();
	JList<BoneShell> newRefsList;
	JScrollPane newRefsPane;

	// Bones (all available -- NEW AND OLD)
	IterableListModel<BoneShell> bones = new IterableListModel<>();
	JList<BoneShell> bonesList;
	JScrollPane bonesPane;

	EditableModel model;
	Geoset geoset;
	MatrixShell currentMatrix = null;

	ModelHolderThing mht;

	BoneAttachmentShell currentBAP;

	public BoneAttachmentPanel(ModelHolderThing mht, final EditableModel model, final BoneShellListCellRenderer renderer) {
//		setLayout(new MigLayout("gap 0, fill", "[grow][grow]0[][grow]", "[][grow][]"));
		this.mht = mht;
		setLayout(new MigLayout("gap 0, fill", "[grow][grow]0[][grow]", "[grow]"));
		this.model = model;


//		updateBonesList();
		// Built before oldBoneRefs, so that the MatrixShells can default to
		// using New Refs with the same name as their first bone

		JPanel oldBonesPanel = new JPanel(new MigLayout("gap 0, fill", "", "[][grow]"));
		oldBonesPanel.add(new JLabel("Old Bone References"), "wrap");
//		buildOldRefsList();
		oldBoneRefsList = new JList<>(oldBoneRefs);
		oldBoneRefsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		oldBoneRefsList.setCellRenderer(new MatrixShell2DListCellRenderer(new ModelViewManager(impPanel.currentModel), new ModelViewManager(impPanel.importedModel)));
		oldBoneRefsList.addListSelectionListener(e -> oldRefSelected());
//		oldBoneRefsList.addListSelectionListener(this);
		oldBoneRefsPane = new JScrollPane(oldBoneRefsList);
		oldBonesPanel.add(oldBoneRefsPane, "growy, growx");

		add(oldBonesPanel, "growy");


		JPanel newBonesPanel = new JPanel(new MigLayout("gap 0, fill", "", "[][grow][]"));
		newBonesPanel.add(new JLabel("New Refs"), "wrap");

		newRefsList = new JList<>(newRefs);
		newRefsList.setCellRenderer(renderer);
		newRefsPane = new JScrollPane(newRefsList);
		newBonesPanel.add(newRefsPane, "growy, wrap");

		JButton removeNewRef = new JButton("Remove", ImportPanel.redXIcon);
		removeNewRef.addActionListener(e -> removeRef());
		newBonesPanel.add(removeNewRef, "alignx center");

		add(newBonesPanel, "growy");


		JPanel upDownPanel = new JPanel(new MigLayout("gap 0"));

		JButton moveUp = new JButton(ImportPanel.moveUpIcon);
		moveUp.addActionListener(e -> moveBone(-1));
		upDownPanel.add(moveUp, "wrap");

		JButton moveDown = new JButton(ImportPanel.moveDownIcon);
		moveDown.addActionListener(e -> moveBone(1));
		upDownPanel.add(moveDown, "wrap");

		add(upDownPanel, "aligny center");


		JPanel bonesPanel = new JPanel(new MigLayout("gap 0, fill", "", "[][grow][]"));
		bonesPanel.add(new JLabel("Bones"), "wrap");


		bonesList = new JList<>(bones);
		bonesList.setCellRenderer(renderer);
		bonesPane = new JScrollPane(bonesList);

		bonesPanel.add(bonesPane, "growy, growx, wrap");
		JButton useBone = new JButton("Use Bone(s)", ImportPanel.greenArrowIcon);
		useBone.addActionListener(e -> useBone());
		bonesPanel.add(useBone, "alignx center");
		add(bonesPanel, "growy");

//		refreshNewRefsList();
	}

	public void setCurrentBAP(BoneAttachmentShell bap) {
		currentBAP = bap;
		oldBoneRefsList.setModel(bap.getOldBoneRefs());
		bonesList.setModel(bap.getBones());
	}

	private void oldRefSelected() {
		newRefsList.setModel(oldBoneRefsList.getSelectedValue().getNewBones());
	}


	private void moveBone(int dir) {
		int[] selected = newRefsList.getSelectedIndices();
		List<BoneShell> selectedValuesList = newRefsList.getSelectedValuesList();

		int size = selectedValuesList.size();

		int start = Math.max(0, ((size - 1) * dir)); // moving down needs to start from bottom

		for (int i = 0; i < size; i++) {
			int index = start - (i * dir);
			selected[index] = oldBoneRefsList.getSelectedValue().moveBone(selectedValuesList.get(index), dir);
		}
		newRefsList.setSelectedIndices(selected);

	}

	private void moveBone2(int dir) {
		int[] selected = newRefsList.getSelectedIndices();
		List<Integer> indexesToMove = new ArrayList<>();
		List<Integer> stepSizes = new ArrayList<>();
		IterableListModel<BoneShell> newBones = oldBoneRefsList.getSelectedValue().getNewBones();
		int elements = newBones.size();
		int maxIndex = newBones.size() - 1;
		int lastMoveI = 0;
		if (dir == -1) {
			for (int i = 1; i < selected.length; i++) {
				if (selected[i] > 0 && selected[i] - 1 != selected[i - 1]) {
					indexesToMove.add(selected[i] - 1);
					stepSizes.add(i - lastMoveI);
				} else if (i - 1 == 0 && selected[i - 1] > 0) {
					indexesToMove.add(selected[i - 1] - 1);
					lastMoveI = i - 1;
				} else if (i == selected.length - 1) {
					indexesToMove.add(i - lastMoveI);
				}
			}
		} else if (dir == 1) {
			for (int i = 0; i < selected.length - 1; i++) {
				if (selected[i] < maxIndex && selected[i] + 1 != selected[i + 1]) {
					indexesToMove.add(selected[i] + 1);
					stepSizes.add(i - lastMoveI);
					lastMoveI = i;
				} else if (i + 1 == selected.length - 1 && selected[i + 1] < maxIndex) {
					indexesToMove.add(selected[i + 1] + 1);
					stepSizes.add(i + 1 - lastMoveI);
					lastMoveI = i + 1;
				}
			}
		}

		for (int i = 0; i < indexesToMove.size(); i++) {
			oldBoneRefsList.getSelectedValue().moveBone(newBones.get(indexesToMove.get(i)), (dir * -1) * stepSizes.get(i));
		}
	}

	private void removeRef() {
		int i = newRefsList.getSelectedIndex();
		for (final BoneShell o : newRefsList.getSelectedValuesList()) {
			oldBoneRefsList.getSelectedValue().removeNewBone(o);
		}
		newRefsList.setSelectedIndex(i);
	}


	private void useBone() {
		for (final BoneShell o : bonesList.getSelectedValuesList()) {
			MatrixShell matrixShell = oldBoneRefsList.getSelectedValue();
			if (!matrixShell.getNewBones().contains(o)) {
				matrixShell.addNewBone(o);
			}
		}
	}


	public void buildOldRefsList() {
		if (oldBoneRefs == null) {
			oldBoneRefs = new IterableListModel<>();
		} else {
			oldBoneRefs.clear();
		}
		for (final Matrix m : geoset.getMatrix()) {
			final MatrixShell ms = new MatrixShell(m);
			// For look to find similarly named stuff and add it
			for (final BoneShell bs : bones) {
				for (final Bone b : m.getBones()) {
					final String mName = b.getName();
					if (bs.getBone() == b)// .getName().equals(mName) )
					{
						ms.addNewBone(bs);
					}
				}
			}
			oldBoneRefs.addElement(ms);
		}
	}

	public void resetMatrices() {
		for (MatrixShell ms : oldBoneRefs) {
			ms.resetMatrix();
		}
//		reloadNewRefsList();
	}

	public void setMatricesToSimilarNames() {
		for (MatrixShell ms : oldBoneRefs) {
			ms.clearNewBones();
			final Matrix m = ms.getMatrix();
			// For look to find similarly named stuff and add it
			for (final BoneShell bs : bones) {
				for (final Bone b : m.getBones()) {
					final String mName = b.getName();
					if (bs.getBone().getName().equals(mName)) {
						ms.addNewBone(bs);
					}
				}
			}
		}
//		reloadNewRefsList();
	}

	public Bone attachBones(Bone dummyBone) {
		for (int l = 0; l < oldBoneRefs.size(); l++) {
			final MatrixShell ms = oldBoneRefs.get(l);
			ms.getMatrix().getBones().clear();
			for (final BoneShell bs : ms.getNewBones()) {
				if (model.contains(bs.getBone())) {
					if (bs.getBone().getClass() == Helper.class) {
						JOptionPane.showMessageDialog(null,
								"Error: Holy fo shizzle my grizzle! A geoset is trying to attach to a helper, not a bone!");
					}
					ms.getMatrix().add(bs.getBone());
				} else {
					System.out.println("Boneshaving " + bs.getBone().getName() + " out of use");
				}
			}
			if (ms.getMatrix().size() == 0) {
				JOptionPane.showMessageDialog(null,
						"Error: A matrix was functionally destroyed while importing, and may take the program with it!");
			}
			if (ms.getMatrix().getBones().size() < 1) {
				if (dummyBone == null) {
					dummyBone = new Bone();
					dummyBone.setName("Bone_MatrixEaterDummy" + (int) (Math.random() * 2000000000));
					dummyBone.setPivotPoint(new Vec3(0, 0, 0));
					if (!model.contains(dummyBone)) {
						model.add(dummyBone);
					}
					JOptionPane.showMessageDialog(null,
							"Warning: You left some matrices empty. This was detected, and a dummy bone at { 0, 0, 0 } has been generated for them named "
									+ dummyBone.getName()
									+ "\nMultiple geosets may be attached to this bone, and the error will only be reported once for your convenience.");
				}
				if (!ms.getMatrix().getBones().contains(dummyBone)) {
					ms.getMatrix().getBones().add(dummyBone);
				}
			}
			// ms.matrix.bones = ms.newBones;
		}
		return dummyBone;
	}
}
