package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.util.Vec3;

public final class ScaleManipulator extends AbstractScaleManipulator {
	private final Vec3 resettableScaleFactors;

	public ScaleManipulator(final ModelEditor modelEditor, final SelectionView selectionView, MoveDimension dir) {
		super(modelEditor, selectionView, dir);
		resettableScaleFactors = new Vec3(1, 1, 1);
	}

	@Override
	protected final void scaleWithFactor(final ModelEditor modelEditor, final Vec3 center, final double scaleFactor, final byte dim1, final byte dim2) {
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
