package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class ObjectPanel extends JPanel {
	JLabel title;

	IdObject object;
	Camera camera;
	JCheckBox doImport;
	JLabel parentLabel;
	JLabel oldParentLabel;
	IterableListModel<BoneShell> parents;
	JList<BoneShell> parentsList;
	JScrollPane parentsPane;

	protected ObjectPanel() {

	}

	public ObjectPanel(final IdObject whichObject, final IterableListModel<BoneShell> possibleParents) {
		object = whichObject;
//		setLayout(new MigLayout("gap 0", "[grow]", "[][][][][grow]"));
		setLayout(new MigLayout("gap 0", "[grow]", "[][][][][grow]"));
		title = new JLabel(object.getClass().getSimpleName() + " \"" + object.getName() + "\"");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		doImport = new JCheckBox("Import this object");
		doImport.setSelected(true);
		add(doImport, "left, wrap");

		if (object.getParent() != null) {
			oldParentLabel = new JLabel("(Old Parent: " + object.getParent().getName() + ")");
		} else {
			oldParentLabel = new JLabel("(Old Parent: {no parent})");
		}
		add(oldParentLabel, "left, wrap");

		parentLabel = new JLabel("Parent:");
		add(parentLabel, "left, wrap");

		parents = possibleParents;
		parentsList = new JList<>(parents);
		parentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (int i = 0; i < parents.size(); i++) {
			final BoneShell bs = parents.get(i);
			if (bs.bone == object.getParent()) {
				parentsList.setSelectedValue(bs, true);
			}
		}

		parentsPane = new JScrollPane(parentsList);
		add(parentsPane, "growx, growy 200");
	}

	public ObjectPanel(final Camera c) {
		camera = c;
		setLayout(new MigLayout("gap 0"));

		title = new JLabel(c.getClass().getSimpleName() + " \"" + c.getName() + "\"");
		title.setFont(new Font("Arial", Font.BOLD, 26));

		doImport = new JCheckBox("Import this object");
		doImport.setSelected(true);
		parentLabel = new JLabel("Parent:");
		oldParentLabel = new JLabel("(Cameras don't have parents)");
		add(title, "align center, wrap");
		add(doImport, "left, wrap");
		add(oldParentLabel, "left, wrap");
	}

	public void addSelectedObjects(List<IdObject> objectsAdded, List<Camera> camerasAdded, EditableModel model) {
		if (doImport.isSelected()) {
			if (object != null) {
				final BoneShell mbs = parentsList.getSelectedValue();
				if (mbs != null) {
					object.setParent(mbs.bone);
				} else {
					object.setParent(null);
				}
				// later make a name field?
				model.add(object);
				objectsAdded.add(object);
			} else if (camera != null) {
				model.add(camera);
				camerasAdded.add(camera);
			}
		} else {
			if (object != null) {
				object.setParent(null);
				// Fix cross-model referencing issue (force clean parent node's list of children)
			}
		}
	}
}
