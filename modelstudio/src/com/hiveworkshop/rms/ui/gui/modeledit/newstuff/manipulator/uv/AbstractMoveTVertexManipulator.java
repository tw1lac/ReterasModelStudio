package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.uv;

import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericMoveAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.AbstractManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.TVertexEditor;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.geom.Point2D.Double;

public abstract class AbstractMoveTVertexManipulator extends AbstractManipulator {
	protected final TVertexEditor modelEditor;
	protected final Vec3 moveVector;
	private GenericMoveAction translationAction;
	String dir;

	public AbstractMoveTVertexManipulator(final TVertexEditor modelEditor, String dir) {
		this.modelEditor = modelEditor;
		moveVector = new Vec3(0, 0, 0);
		this.dir = dir;
	}

	@Override
	protected void onStart(final Double mouseStart, final byte dim1, final byte dim2) {
		super.onStart(mouseStart, dim1, dim2);
		translationAction = modelEditor.beginTranslation();
	}

	@Override
	public void update(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2, String dir) {
		resetMoveVector();
		buildMoveVector(mouseStart, mouseEnd, dim1, dim2);
		translationAction.updateTranslation(moveVector.x, moveVector.y, moveVector.z);
	}

	@Override
	public UndoAction finish(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2, String dir) {
		update(mouseStart, mouseEnd, dim1, dim2, dir);
		resetMoveVector();
		return translationAction;
	}

	protected abstract void buildMoveVector(final Double mouseStart, final Double mouseEnd, final byte dim1,
			final byte dim2);

	private void resetMoveVector() {
		moveVector.x = 0;
		moveVector.y = 0;
		moveVector.z = 0;
	}
}
