package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

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

	static ObjectShell currentObject;

	protected ObjectPanel() {

	}

	public ObjectPanel(final IterableListModel<BoneShell> possibleParents) {
//		setLayout(new MigLayout("gap 0", "[grow]", "[][][][][grow]"));
		setLayout(new MigLayout("gap 0", "[grow]", "[][][][][grow]"));
		title = new JLabel("Title title");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		doImport = new JCheckBox("Import this object");
		doImport.addActionListener(e -> changeImportStatus());
		add(doImport, "left, wrap");

		oldParentLabel = new JLabel("(Old Parent: ? )");
		add(oldParentLabel, "left, wrap");

		parentLabel = new JLabel("Parent:");
		add(parentLabel, "left, wrap");

		parents = possibleParents;
		parentsList = new JList<>(parents);
		parentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		parentsPane = new JScrollPane(parentsList);
		add(parentsPane, "growx, growy 200");
	}

	public void setCurrentObject(ObjectShell objectShell) {
		currentObject = objectShell;
		setTitle();
//		setOldParentTitle();
		doImport.setSelected(currentObject.getShouldImport());
		selectParent2();
	}

	public void changeImportStatus() {
		currentObject.setShouldImport(doImport.isSelected());
	}

	private void selectParent2() {
		if (currentObject.getCamera() != null) {
			parentsList.setSelectedValue(null, true);
			parentsPane.setVisible(false);
			oldParentLabel.setText("(Cameras don't have parents)");
		} else {
			parentsPane.setVisible(true);
			parentsList.setSelectedValue(currentObject.getParent(), true);
			if (currentObject.getParent() != null) {
				oldParentLabel.setText("(Old Parent: " + currentObject.getParent().toString() + ")");
			} else {
				oldParentLabel.setText("(Old Parent: {no parent})");
			}
		}
	}

	public void setTitle() {
		title.setText(currentObject.toString());
	}

//	public void setOldParentTitle(){
//		if (currentObject.getParent() != null) {
//			oldParentLabel.setText("(Old Parent: " + currentObject.getParent().toString() + ")");
//		} else {
//			oldParentLabel.setText("(Old Parent: {no parent})");
//		}
//	}
}
