package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.tools;

import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;

import java.util.*;

public final class SetParentAction implements UndoAction {
	private final Map<IdObject, IdObject> nodeToOldParent;
	private final IdObject newParent;
	private final ModelStructureChangeListener changeListener;
	private final Set<IdObject> nodes;

	public SetParentAction(Collection<IdObject> nodes, IdObject newParent, ModelStructureChangeListener changeListener) {
		this.nodeToOldParent = new HashMap<>();
		for (IdObject idObject : nodes) {
			nodeToOldParent.put(idObject, idObject.getParent());
		}

		this.newParent = newParent;
		this.changeListener = changeListener;
		this.nodes = new HashSet<>(nodes);
	}

	@Override
	public void undo() {
		for (IdObject idObject : nodeToOldParent.keySet()) {
			idObject.setParent(nodeToOldParent.get(idObject));
		}
		changeListener.nodesUpdated();
	}

	@Override
	public void redo() {
		for (IdObject idObject : nodes) {
			idObject.setParent(newParent);
		}
		changeListener.nodesUpdated();
	}

	@Override
	public String actionName() {
		return "re-assign matrix";
	}

}
