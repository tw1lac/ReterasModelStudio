package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordSysUtils;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericRotateAction;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.event.MouseEvent;

public class SquatToolManipulator extends Manipulator {
	private final ModelEditor modelEditor;
	private final SelectionView selectionView;
	private GenericRotateAction rotationAction;
	MoveDimension dir;

	public SquatToolManipulator(ModelEditor modelEditor, SelectionView selectionView, MoveDimension dir) {
		this.modelEditor = modelEditor;
		this.selectionView = selectionView;
		this.dir = dir;
	}

	@Override
	protected void onStart(MouseEvent e, Vec2 mouseStart, byte dim1, byte dim2) {
		super.onStart(e, mouseStart, dim1, dim2);
		Vec3 center = selectionView.getCenter();
		byte planeDim1;
		byte planeDim2;

		if (dir.containDirection(dim1)) {
			planeDim1 = CoordSysUtils.getUnusedXYZ(dim1, dim2);
			planeDim2 = dim2;
		} else if (dir.containDirection(dim2)) {
			planeDim1 = dim1;
			planeDim2 = CoordSysUtils.getUnusedXYZ(dim1, dim2);
		} else {
			planeDim1 = dim1;
			planeDim2 = dim2;
		}

		rotationAction = modelEditor.beginSquatTool(center.x, center.y, center.z, planeDim1, planeDim2);
	}

	@Override
	public void update(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2) {
		Vec3 center = selectionView.getCenter();
		double radians = computeRotateRadians(mouseStart, mouseEnd, center, dim1, dim2);
		rotationAction.updateRotation(radians);
	}

	@Override
	public UndoAction finish(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2) {
		update(e, mouseStart, mouseEnd, dim1, dim2);
		return rotationAction;
	}

	private double computeRotateRadians(Vec2 startingClick, Vec2 endingClick, Vec3 center, byte portFirstXYZ, byte portSecondXYZ) {
		double deltaAngle = 0;
		if (dir == MoveDimension.XYZ) {
			Vec2 startingDelta = Vec2.getDif(startingClick, center.getProjected(portFirstXYZ, portSecondXYZ));
			Vec2 endingDelta = Vec2.getDif(endingClick, center.getProjected(portFirstXYZ, portSecondXYZ));

			double startingAngle = Math.atan2(startingDelta.y, startingDelta.x);
			double endingAngle = Math.atan2(endingDelta.y, endingDelta.x);
			deltaAngle = endingAngle - startingAngle;

		} else {
			if (dir.containDirection(portFirstXYZ)) {
				double radius = selectionView.getCircumscribedSphereRadius(center);
				if (radius <= 0) {
					radius = 64;
				}
				deltaAngle = (endingClick.y - startingClick.y) / radius;
			}
			if (dir.containDirection(portSecondXYZ)) {
				double radius = selectionView.getCircumscribedSphereRadius(center);
				if (radius <= 0) {
					radius = 64;
				}
				deltaAngle = (endingClick.x - startingClick.x) / radius;
			}
			if (dir.containDirection(CoordSysUtils.getUnusedXYZ(portFirstXYZ, portSecondXYZ))) {
				Vec2 startingDelta = Vec2.getDif(startingClick, center.getProjected(portFirstXYZ, portSecondXYZ));
				Vec2 endingDelta = Vec2.getDif(endingClick, center.getProjected(portFirstXYZ, portSecondXYZ));

				double startingAngle = Math.atan2(startingDelta.y, startingDelta.x);
				double endingAngle = Math.atan2(endingDelta.y, endingDelta.x);

				deltaAngle = endingAngle - startingAngle;
			}
		}
		return deltaAngle;
	}

}
