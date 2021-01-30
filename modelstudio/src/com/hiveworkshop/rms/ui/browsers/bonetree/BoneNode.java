package com.hiveworkshop.rms.ui.browsers.bonetree;

import com.hiveworkshop.rms.editor.model.IdObject;

import javax.swing.tree.DefaultMutableTreeNode;

public class BoneNode extends DefaultMutableTreeNode {
	private IdObject idObject;
	private Boolean isVisible;

	public BoneNode(IdObject idObject) {
		this.idObject = idObject;
		isVisible = true;
	}
}
