package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.uv;

import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.MoveDimension;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.TVertexEditor;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

public final class ScaleTVertexManipulator extends AbstractScaleTVertexManipulator {
	private final Vec3 resettableScaleFactors;

	public ScaleTVertexManipulator(final TVertexEditor modelEditor, final SelectionView selectionView, MoveDimension dir) {
		super(modelEditor, selectionView, dir);
		resettableScaleFactors = new Vec3(1, 1, 1);
	}

	@Override
	protected final void scaleWithFactor(final TVertexEditor modelEditor, final Vec2 center, final double scaleFactor, final byte dim1, final byte dim2) {
		resettableScaleFactors.x = 1;
		resettableScaleFactors.y = 1;
		resettableScaleFactors.z = 1;
		if (dir == MoveDimension.XYZ) {
			resettableScaleFactors.x = (float) scaleFactor;
			resettableScaleFactors.y = (float) scaleFactor;
			resettableScaleFactors.z = (float) scaleFactor;
		} else {
			if (dir.containDirection(dim1)) {
				resettableScaleFactors.setCoord(dim1, scaleFactor);
			}
			if (dir.containDirection(dim2)) {
				resettableScaleFactors.setCoord(dim2, scaleFactor);
			}
		}
		getScaleAction().updateScale(resettableScaleFactors.x, resettableScaleFactors.y, resettableScaleFactors.z);
	}

	@Override
	protected Vec3 buildScaleVector(final double scaleFactor, final byte dim1, final byte dim2) {
		resettableScaleFactors.x = 1;
		resettableScaleFactors.y = 1;
		resettableScaleFactors.z = 1;
		if (dir == MoveDimension.XYZ) {
			resettableScaleFactors.x = (float) scaleFactor;
			resettableScaleFactors.y = (float) scaleFactor;
			resettableScaleFactors.z = (float) scaleFactor;
		} else {
			if (dir.containDirection(dim1)) {
				resettableScaleFactors.setCoord(dim1, scaleFactor);
			}
			if (dir.containDirection(dim2)) {
				resettableScaleFactors.setCoord(dim2, scaleFactor);
			}
		}
		return resettableScaleFactors;
	}
}
