package com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.mdl.IdObject;

public final class SetParentAction implements UndoAction {
	private final Map<IdObject, IdObject> nodeToOldParent;
	private final IdObject newParent;
	private final ModelStructureChangeListener changeListener;
	private final List<IdObject> nodes;

	public SetParentAction(final Map<IdObject, IdObject> nodeToOldParent, final IdObject newParent,
			final ModelStructureChangeListener changeListener) {
		this.nodeToOldParent = nodeToOldParent;
		this.newParent = newParent;
		this.changeListener = changeListener;
		this.nodes = new ArrayList<>();
		nodes.addAll(nodeToOldParent.keySet());
	}

	@Override
	public void undo() {
		for (IdObject idObject: nodeToOldParent.keySet()){
			idObject.setParent(nodeToOldParent.get(idObject));
		}
		changeListener.nodesRemoved(nodes);
		changeListener.nodesAdded(nodes);
	}

	@Override
	public void redo() {
		for (IdObject idObject: nodeToOldParent.keySet()){
			idObject.setParent(newParent);
		}
		changeListener.nodesRemoved(nodes);
		changeListener.nodesAdded(nodes);
	}

	@Override
	public String actionName() {
		return "re-assign matrix";
	}

}
