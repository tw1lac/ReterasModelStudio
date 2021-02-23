package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Named;
import com.hiveworkshop.rms.editor.model.VisibilitySource;

class VisibilityShell {
	Named source;
	VisibilitySource visibilitySource;
	EditableModel model;
	private boolean favorOld = true;
	private VisibilityShell newVisSource;
	private VisibilityShell oldVisSource;

	public VisibilityShell(final Named n, final EditableModel whichModel) {
		source = n;
		model = whichModel;
	}

	public VisibilityShell(final VisibilitySource vs, final EditableModel whichModel) {
		source = (Named) vs;
		visibilitySource = vs;
		model = whichModel;
	}

	public VisibilityShell(boolean b) {
		favorOld = b;
	}

	public Named getSource() {
		return source;
	}

	public EditableModel getModel() {
		return model;
	}

	public boolean getFavorOld() {
		return favorOld;
	}

	public void setFavorOld(boolean b) {
		favorOld = b;
	}

	public VisibilityShell getNewVisSource() {
		return newVisSource;
	}

	public VisibilityShell setNewVisSource(VisibilityShell newVisSource) {
		this.newVisSource = newVisSource;
		return this;
	}

	public VisibilityShell getOldVisSource() {
		return oldVisSource;
	}

	public VisibilityShell setOldVisSource(VisibilityShell oldVisSource) {
		this.oldVisSource = oldVisSource;
		return this;
	}

	public VisibilitySource getVisSource() {
		return visibilitySource;
	}

	@Override
	public String toString() {
		if (source != null) {
//			return source.getName();
			return model.getName() + ": " + source.getName();
		} else if (favorOld) {
			return "VISIBLE";
		}
		return "NOT VISIBLE";
	}
}
