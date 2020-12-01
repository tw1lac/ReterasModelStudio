package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericScaleAction;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.geom.Point2D.Double;

public abstract class AbstractScaleManipulator extends AbstractManipulator {
	private final ModelEditor modelEditor;
	private final SelectionView selectionView;
	private GenericScaleAction scaleAction;

	public AbstractScaleManipulator(final ModelEditor modelEditor, final SelectionView selectionView) {
		this.modelEditor = modelEditor;
		this.selectionView = selectionView;
	}

	@Override
	protected void onStart(final Double mouseStart, final byte dim1, final byte dim2) {
		super.onStart(mouseStart, dim1, dim2);
		final Vec3 center = selectionView.getCenter();
		scaleAction = modelEditor.beginScaling(center.x, center.y, center.z);
	}

	@Override
	public void update(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		final Vec3 center = selectionView.getCenter();
		final double scaleFactor = computeScaleFactor(mouseStart, mouseEnd, center, dim1, dim2);
		scaleWithFactor(modelEditor, center, scaleFactor, dim1, dim2);
	}

	protected final GenericScaleAction getScaleAction() {
		return scaleAction;
	}

	@Override
	public UndoAction finish(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		update(mouseStart, mouseEnd, dim1, dim2);
		return scaleAction;
	}

	protected abstract void scaleWithFactor(final ModelEditor modelEditor, final Vec3 center,
											final double scaleFactor, byte dim1, byte dim2);

	protected abstract Vec3 buildScaleVector(final double scaleFactor, byte dim1, byte dim2);

	protected double computeScaleFactor(final Double startingClick, final Double endingClick, final Vec3 center,
										final byte dim1, final byte dim2) {
		double dxs = endingClick.x - center.getCoord(dim1);
		double dys = endingClick.y - center.getCoord(dim2);
		final double endDist = Math.sqrt(dxs * dxs + dys * dys);
		dxs = startingClick.x - center.getCoord(dim1);
		dys = startingClick.y - center.getCoord(dim2);
		final double startDist = Math.sqrt(dxs * dxs + dys * dys);
		return endDist / startDist;
	}

}
