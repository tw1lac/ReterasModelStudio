package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.builder.model;

import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.commonWidgets.widgets.RotatorWidget;
import com.hiveworkshop.rms.ui.application.edit.commonWidgets.widgets.RotatorWidget.RotateDirection;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.selection.ViewportSelectionHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.Manipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.RotateHorizontalManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.RotateManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.RotateVerticalManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;

public final class RotatorWidgetManipulatorBuilder extends AbstractSelectAndEditModelEditorManipulatorBuilder {
	private final RotatorWidget moverWidget = new RotatorWidget(new Vec3(0, 0, 0));

	public RotatorWidgetManipulatorBuilder(final ModelEditor modelEditor,
										   final ViewportSelectionHandler viewportSelectionHandler,
										   final ProgramPreferences programPreferences,
										   final ModelView modelView) {
		super(viewportSelectionHandler, programPreferences, modelEditor, modelView);
	}

	@Override
	protected boolean widgetOffersEdit(final Vec3 selectionCenter,
									   final Point mousePoint,
									   final CoordinateSystem coordinateSystem,
									   final SelectionView selectionView) {
		moverWidget.setPoint(selectionView.getCenter());
		final RotateDirection directionByMouse = moverWidget.getDirectionByMouse(mousePoint, coordinateSystem);
		moverWidget.setMoveDirection(directionByMouse);
		return directionByMouse != RotateDirection.NONE;
	}

	@Override
	protected Manipulator createManipulatorFromWidget(final Vec3 selectionCenter,
													  final Point mousePoint,
													  final CoordinateSystem coordinateSystem,
													  final SelectionView selectionView) {
		moverWidget.setPoint(selectionView.getCenter());
		final RotateDirection directionByMouse = moverWidget.getDirectionByMouse(mousePoint, coordinateSystem);
		if (directionByMouse != null) {
			moverWidget.setMoveDirection(directionByMouse);
		}
		if (directionByMouse != null) {
			return switch (directionByMouse) {
				case FREE -> new RotateManipulator(getModelEditor(), selectionView);
				case HORIZONTALLY -> new RotateHorizontalManipulator(getModelEditor(), selectionView);
				case VERTICALLY -> new RotateVerticalManipulator(getModelEditor(), selectionView);
				case SPIN -> new RotateManipulator(getModelEditor(), selectionView);
				case NONE -> null;
			};
		}
		return null;
	}

	@Override
	protected Manipulator createDefaultManipulator(final Vec3 selectionCenter,
												   final Point mousePoint,
												   final CoordinateSystem coordinateSystem,
												   final SelectionView selectionView) {
		return new RotateManipulator(getModelEditor(), selectionView);
	}

	@Override
	protected void renderWidget(final Graphics2D graphics,
								final CoordinateSystem coordinateSystem,
								final SelectionView selectionView) {
		moverWidget.setPoint(selectionView.getCenter());
		moverWidget.render(graphics, coordinateSystem);
	}

}
