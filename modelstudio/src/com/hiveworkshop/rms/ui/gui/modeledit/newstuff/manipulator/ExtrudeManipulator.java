package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.CompoundAction;

import java.awt.geom.Point2D.Double;
import java.util.Arrays;

public final class ExtrudeManipulator extends AbstractMoveManipulator {

	private UndoAction beginExtrudingSelection;

	public ExtrudeManipulator(final ModelEditor modelEditor, String dir) {
		super(modelEditor, dir);
	}

	@Override
	protected void onStart(final Double mouseStart, final byte dim1, final byte dim2) {
		super.onStart(mouseStart, dim1, dim2);
		beginExtrudingSelection = modelEditor.beginExtrudingSelection();
	}

	@Override
	public UndoAction finish(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2, String dir) {
		return new CompoundAction("extrude", Arrays.asList(beginExtrudingSelection, super.finish(mouseStart, mouseEnd, dim1, dim2, dir)));
	}

//	@Override
//	protected void buildMoveVector(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
//		moveVector.setCoord(dim1, mouseEnd.x - mouseStart.x);
//		moveVector.setCoord(dim2, mouseEnd.y - mouseStart.y);
//	}

	@Override
	protected void buildMoveVector(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		if(dir.contains("x")){
			moveVector.setCoord(dim1, mouseEnd.x - mouseStart.x);
		}
		if(dir.contains("y")){
			moveVector.setCoord(dim2, mouseEnd.y - mouseStart.y);
		}
	}

}
