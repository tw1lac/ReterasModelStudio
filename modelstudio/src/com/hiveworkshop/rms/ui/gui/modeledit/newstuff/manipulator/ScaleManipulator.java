package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.util.Vec3;

public final class ScaleManipulator extends AbstractScaleManipulator {
	private final Vec3 resettableScaleFactors;

	public ScaleManipulator(final ModelEditor modelEditor, final SelectionView selectionView, String dir) {
		super(modelEditor, selectionView, dir);
		resettableScaleFactors = new Vec3(0, 0, 0);
	}

	@Override
	protected final void scaleWithFactor(final ModelEditor modelEditor, final Vec3 center, final double scaleFactor,
										 final byte dim1, final byte dim2, String dir) {
		resettableScaleFactors.x = 1;
		resettableScaleFactors.y = 1;
		resettableScaleFactors.z = 1;
		if(dir.contains("x")){
			resettableScaleFactors.setCoord(dim1, scaleFactor);
		}
		if(dir.contains("y")){
			resettableScaleFactors.setCoord(dim2, scaleFactor);
		}
		if(dir.contains("z")){
			resettableScaleFactors.z = (float) scaleFactor;
//			resettableScaleFactors.setCoord(dim2, scaleFactor);
		}
		getScaleAction().updateScale(resettableScaleFactors.x, resettableScaleFactors.y, resettableScaleFactors.z);
//		getScaleAction().updateScale(scaleFactor, scaleFactor, scaleFactor);
	}

	@Override
	protected Vec3 buildScaleVector(final double scaleFactor, final byte dim1, final byte dim2, String dir) {
		resettableScaleFactors.x = 1;
		resettableScaleFactors.y = 1;
		resettableScaleFactors.z = 1;
		if(dir.contains("x")){
			resettableScaleFactors.setCoord(dim1, scaleFactor);
		}
		if(dir.contains("y")){
			resettableScaleFactors.setCoord(dim2, scaleFactor);
		}
		if(dir.contains("z")){
			resettableScaleFactors.z = (float) scaleFactor;
//			resettableScaleFactors.setCoord(dim2, scaleFactor);
		}
		return resettableScaleFactors;
//		return new Vec3(scaleFactor, scaleFactor, scaleFactor);
	}
}
