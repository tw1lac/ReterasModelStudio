package com.hiveworkshop.rms.ui.application.actions.uv;

import com.hiveworkshop.rms.ui.application.edit.uv.panel.UVPanel;
import com.hiveworkshop.rms.ui.application.edit.uv.panel.UVPanel.UnwrapDirection;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.util.Vec3;

import java.util.List;

public class UVRemapAction implements UndoAction {

	private final List<Vec3> tVertices;
	private final List<Vec3> newValueHolders;
	private final List<Vec3> oldValueHolders;
	private final UnwrapDirection direction;

	public UVRemapAction(final List<Vec3> tVertices, final List<Vec3> newValueHolders,
						 final List<Vec3> oldValueHolders, final UVPanel.UnwrapDirection direction) {
		this.tVertices = tVertices;
		this.newValueHolders = newValueHolders;
		this.oldValueHolders = oldValueHolders;
		this.direction = direction;
	}

	@Override
	public void undo() {
		for (int i = 0; i < tVertices.size(); i++) {
			tVertices.get(i).set(oldValueHolders.get(i));
		}
	}

	@Override
	public void redo() {
		for (int i = 0; i < tVertices.size(); i++) {
			tVertices.get(i).set(newValueHolders.get(i));
		}
	}

	@Override
	public String actionName() {
		return "remap TVertices " + direction;
	}

}
