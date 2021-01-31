package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;

import java.awt.geom.Point2D.Double;

public final class MoveManipulator extends AbstractMoveManipulator {

	public MoveManipulator(final ModelEditor modelEditor, String dir) {
		super(modelEditor, dir);
	}

	@Override
	protected void buildMoveVector(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		if (dir.contains("x")){
			moveVector.setCoord(dim1, mouseEnd.x - mouseStart.x);
		}
		if (dir.contains("y")){
			moveVector.setCoord(dim2, mouseEnd.y - mouseStart.y);
		}
	}

}
