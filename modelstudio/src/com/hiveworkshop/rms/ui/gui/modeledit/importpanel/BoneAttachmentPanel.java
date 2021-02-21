package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;
import com.hiveworkshop.rms.util.IterableListModel;
import com.hiveworkshop.rms.util.Vec3;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

class BoneAttachmentPanel extends JPanel implements ListSelectionListener {
	JLabel title;

	// Old bone refs (matrices)
	JLabel oldBoneRefsLabel;
	IterableListModel<MatrixShell> oldBoneRefs;
	JList<MatrixShell> oldBoneRefsList;
	JScrollPane oldBoneRefsPane;

	// New refs
	JLabel newRefsLabel;
	IterableListModel<BoneShell> newRefs;
	JList<BoneShell> newRefsList;
	JScrollPane newRefsPane;
	JButton removeNewRef;
	JButton moveUp;
	JButton moveDown;

	// Bones (all available -- NEW AND OLD)
	JLabel bonesLabel;
	IterableListModel<BoneShell> bones;
	JList<BoneShell> bonesList;
	JScrollPane bonesPane;
	JButton useBone;

	EditableModel model;
	Geoset geoset;
	MatrixShell currentMatrix = null;
	ImportPanel impPanel;

	public BoneAttachmentPanel(final EditableModel model, final Geoset whichGeoset, final BoneShellListCellRenderer renderer,
	                           final ImportPanel thePanel) {
		this.model = model;
		geoset = whichGeoset;
		impPanel = thePanel;

		bonesLabel = new JLabel("Bones");
		updateBonesList();
		// Built before oldBoneRefs, so that the MatrixShells can default to
		// using New Refs with the same name as their first bone
		bonesList = new JList<>(bones);
		bonesList.setCellRenderer(renderer);
		bonesPane = new JScrollPane(bonesList);

		useBone = new JButton("Use Bone(s)", ImportPanel.greenArrowIcon);
		useBone.addActionListener(e -> useBone());

		oldBoneRefsLabel = new JLabel("Old Bone References");
		buildOldRefsList();
		oldBoneRefsList = new JList<>(oldBoneRefs);
		oldBoneRefsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		oldBoneRefsList.setCellRenderer(new MatrixShell2DListCellRenderer(new ModelViewManager(impPanel.currentModel),
//				new ModelViewManager(impPanel.importedModel)));
		oldBoneRefsList.addListSelectionListener(this);
		oldBoneRefsPane = new JScrollPane(oldBoneRefsList);

		newRefsLabel = new JLabel("New Refs");
		newRefs = new IterableListModel<>();
		newRefsList = new JList<>(newRefs);
		newRefsList.setCellRenderer(renderer);
		newRefsPane = new JScrollPane(newRefsList);

		removeNewRef = new JButton("Remove", ImportPanel.redXIcon);
		removeNewRef.addActionListener(e -> removeRef());
		moveUp = new JButton(ImportPanel.moveUpIcon);
		moveUp.addActionListener(e -> moveUp());
		moveDown = new JButton(ImportPanel.moveDownIcon);
		moveDown.addActionListener(e -> moveDown());

		buildLayout();

		refreshNewRefsList();
	}

	public void buildLayout() {
		setLayout(new MigLayout("gap 0", "[grow][grow][][grow]", "[][grow][grow][]"));
		add(oldBoneRefsLabel, "cell 0 0");
		add(oldBoneRefsPane, "cell 0 1, spany 2, growy, growx");

		add(newRefsLabel, "cell 1 0");
		add(newRefsPane, "cell 1 1, spany 2, growy, growx");

		add(removeNewRef, "cell 1 3, alignx center");

		add(moveUp, "cell 2 1, bottom");
		add(moveDown, "cell 2 2, top");

		add(bonesLabel, "cell 3 0");
		add(bonesPane, "cell 3 1, spany 2, growy, growx");
		add(useBone, "cell 3 3, alignx center");


//		final GroupLayout layout = new GroupLayout(this);
//		layout.setHorizontalGroup(layout.createSequentialGroup()
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(oldBoneRefsLabel)
//						.addComponent(oldBoneRefsPane))
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(newRefsLabel)
//						.addComponent(newRefsPane)
//						.addComponent(removeNewRef))
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(moveUp)
//						.addComponent(moveDown))
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(bonesLabel)
//						.addComponent(bonesPane)
//						.addComponent(useBone)));
//
//		layout.setVerticalGroup(layout.createSequentialGroup()
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(oldBoneRefsLabel)
//						.addComponent(newRefsLabel)
//						.addComponent(bonesLabel))
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(oldBoneRefsPane)
//						.addComponent(newRefsPane)
//						.addGroup(layout.createSequentialGroup()
//								.addComponent(moveUp).addGap(16)
//								.addComponent(moveDown))
//						.addComponent(bonesPane))
//				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//						.addComponent(removeNewRef)
//						.addComponent(useBone)));
//		setLayout(layout);
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		refreshLists();
	}


	private void moveDown() {
		final int[] indices = newRefsList.getSelectedIndices();
		if ((indices != null) && (indices.length > 0)) {
			if (indices[indices.length - 1] < (newRefs.size() - 1)) {
				for (int i = indices.length - 1; i >= 0; i--) {
					final BoneShell bs = newRefs.get(indices[i]);
					newRefs.removeElement(bs);
					newRefs.add(indices[i] + 1, bs);
					indices[i] += 1;
				}
			}
			newRefsList.setSelectedIndices(indices);
		}
	}

	private void moveUp() {
		final int[] indices = newRefsList.getSelectedIndices();
		if ((indices != null) && (indices.length > 0)) {
			if (indices[0] > 0) {
				for (int i = 0; i < indices.length; i++) {
					final BoneShell bs = newRefs.get(indices[i]);
					newRefs.removeElement(bs);
					newRefs.add(indices[i] - 1, bs);
					indices[i] -= 1;
				}
			}
			newRefsList.setSelectedIndices(indices);
		}
	}

	private void removeRef() {
		for (final Object o : newRefsList.getSelectedValuesList()) {
			int i = newRefsList.getSelectedIndex();
			newRefs.removeElement(o);
			if (i > (newRefs.size() - 1)) {
				i = newRefs.size() - 1;
			}
			newRefsList.setSelectedIndex(i);
		}
		refreshNewRefsList();
	}

	private void useBone() {
		for (final Object o : bonesList.getSelectedValuesList()) {
			if (!newRefs.contains(o)) {
				newRefs.addElement((BoneShell) o);
			}
		}
		refreshNewRefsList();
	}

	public void refreshLists() {
		updateBonesList();
		refreshNewRefsList();
	}

	public void refreshNewRefsList() {
		// Does save the currently constructed matrix
		final java.util.List<BoneShell> selection = newRefsList.getSelectedValuesList();
		if (currentMatrix != null) {
			currentMatrix.newBones.clear();
			for (final Object bs : newRefs.toArray()) {
				currentMatrix.newBones.add((BoneShell) bs);
			}
		}
		newRefs.clear();
		if (oldBoneRefsList.getSelectedValue() != null) {
			for (final BoneShell bs : oldBoneRefsList.getSelectedValue().newBones) {
				if (bones.contains(bs)) {
					newRefs.addElement(bs);
				}
			}
		}

		final int[] indices = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			indices[i] = newRefs.indexOf(selection.get(i));
		}
		newRefsList.setSelectedIndices(indices);
		currentMatrix = oldBoneRefsList.getSelectedValue();
	}

	public void reloadNewRefsList() {
		// Does not save the currently constructed matrix
		final java.util.List<BoneShell> selection = newRefsList.getSelectedValuesList();
		newRefs.clear();
		if (oldBoneRefsList.getSelectedValue() != null) {
			for (final BoneShell bs : oldBoneRefsList.getSelectedValue().newBones) {
				if (bones.contains(bs)) {
					newRefs.addElement(bs);
				}
			}
		}

		final int[] indices = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			indices[i] = newRefs.indexOf(selection.get(i));
		}
		newRefsList.setSelectedIndices(indices);
		currentMatrix = oldBoneRefsList.getSelectedValue();
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
			for (final Object bs : bones.toArray()) {
				// try {
				for (final Bone b : m.getBones()) {
					final String mName = b.getName();
					if (((BoneShell) bs).bone == b)// .getName().equals(mName) )
					{
						ms.newBones.add((BoneShell) bs);
					}
				}
				// }
				// catch (NullPointerException e)
				// {
				// System.out.println("We have a null in a matrix process,
				// probably not good but it was assumed that this might
				// happen.");
				// }
			}
			oldBoneRefs.addElement(ms);
		}
	}

	public void resetMatrices() {
		for (int i = 0; i < oldBoneRefs.size(); i++) {
			final MatrixShell ms = oldBoneRefs.get(i);
			ms.newBones.clear();
			final Matrix m = ms.matrix;
			// For look to find right stuff and add it
			for (final Object bs : bones.toArray()) {
				for (final Bone b : m.getBones()) {
					if (((BoneShell) bs).bone == b)// .getName().equals(mName) )
					{
						ms.newBones.add((BoneShell) bs);
					}
				}
			}
		}
		reloadNewRefsList();
	}

	public void setMatricesToSimilarNames() {
		for (int i = 0; i < oldBoneRefs.size(); i++) {
			final MatrixShell ms = oldBoneRefs.get(i);
			ms.newBones.clear();
			final Matrix m = ms.matrix;
			// For look to find similarly named stuff and add it
			for (final Object bs : bones.toArray()) {
				for (final Bone b : m.getBones()) {
					final String mName = b.getName();
					if (((BoneShell) bs).bone.getName().equals(mName)) {
						ms.newBones.add((BoneShell) bs);
					}
				}
			}
		}
		reloadNewRefsList();
	}

	public void updateBonesList() {
		bones = getImportPanel().mht.getFutureBoneList();
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


	public Bone attachBones(Bone dummyBone) {
		for (int l = 0; l < oldBoneRefs.size(); l++) {
			final MatrixShell ms = oldBoneRefs.get(l);
			ms.matrix.getBones().clear();
			for (final BoneShell bs : ms.newBones) {
				if (model.contains(bs.bone)) {
					if (bs.bone.getClass() == Helper.class) {
						JOptionPane.showMessageDialog(null,
								"Error: Holy fo shizzle my grizzle! A geoset is trying to attach to a helper, not a bone!");
					}
					ms.matrix.add(bs.bone);
				} else {
					System.out.println("Boneshaving " + bs.bone.getName() + " out of use");
				}
			}
			if (ms.matrix.size() == 0) {
				JOptionPane.showMessageDialog(null,
						"Error: A matrix was functionally destroyed while importing, and may take the program with it!");
			}
			if (ms.matrix.getBones().size() < 1) {
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
				if (!ms.matrix.getBones().contains(dummyBone)) {
					ms.matrix.getBones().add(dummyBone);
				}
			}
			// ms.matrix.bones = ms.newBones;
		}
		return dummyBone;
	}
}
