package com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.tools;
import java.util.Map;
import java.util.HashMap;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.mdl.Bone;
import com.hiveworkshop.wc3.mdl.Vertex;

public final class AutoCenterBonesAction implements UndoAction {
	private final Map<Bone, Vertex> boneToOldPosition;
	private final Map<Bone, Vertex> boneToNewPosition;

	public AutoCenterBonesAction(final Map<Bone, Vertex> boneToOldPosition) {
		this.boneToOldPosition = boneToOldPosition;
		boneToNewPosition = new HashMap<>();
		for (final Bone bone : boneToOldPosition.keySet()) {
			boneToNewPosition.put(bone, new Vertex(bone.getPivotPoint()));
		}
	}

	@Override
	public void undo() {
		for (Bone bone: boneToOldPosition.keySet()){
			bone.getPivotPoint().setTo(boneToOldPosition.get(bone));
		}
	}

	@Override
	public void redo() {
		for (Bone bone: boneToNewPosition.keySet()){
			bone.getPivotPoint().setTo(boneToNewPosition.get(bone));
		}
	}

	@Override
	public String actionName() {
		return "auto-center bones";
	}

}
