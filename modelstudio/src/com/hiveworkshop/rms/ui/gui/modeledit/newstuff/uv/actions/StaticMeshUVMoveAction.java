package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.actions;

import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericMoveAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.TVertexEditor;
import com.hiveworkshop.rms.util.Vec3;

public final class StaticMeshUVMoveAction implements UndoAction, GenericMoveAction {
	private final TVertexEditor modelEditor;
	private final Vec3 moveVector;

	public StaticMeshUVMoveAction(final TVertexEditor modelEditor, final Vec3 moveVector) {
		this.modelEditor = modelEditor;
		this.moveVector = new Vec3(moveVector);
	}

	@Override
	public void undo() {
		modelEditor.rawTranslate(-moveVector.x, -moveVector.y);
	}

	@Override
	public void redo() {
		modelEditor.rawTranslate(moveVector.x, moveVector.y);
	}

	@Override
	public String actionName() {
		return "move UV";
	}

	@Override
	public void updateTranslation(final double deltaX, final double deltaY, final double deltaZ) {
		moveVector.x += deltaX;
		moveVector.y += deltaY;
		modelEditor.rawTranslate(deltaX, deltaY);
	}

}
