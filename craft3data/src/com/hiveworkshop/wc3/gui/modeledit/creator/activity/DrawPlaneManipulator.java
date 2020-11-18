package com.hiveworkshop.wc3.gui.modeledit.creator.activity;

import java.awt.Graphics2D;
import java.awt.geom.Point2D.Double;

import javax.swing.JOptionPane;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.animedit.WrongModeException;
import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.ModelEditor;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.DoNothingAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util.GenericMoveAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.manipulator.AbstractManipulator;
import com.hiveworkshop.wc3.mdl.Vertex;

public class DrawPlaneManipulator extends AbstractManipulator {
	private final ModelEditor modelEditor;
	private Double mouseEnd;
	private final ProgramPreferences programPreferences;
    private GenericMoveAction addPlane;
	private final Vertex facingVector;
	private final int numberOfWidthSegments;
	private final int numberOfHeightSegments;

	public DrawPlaneManipulator(final ModelEditor modelEditor, final ProgramPreferences programPreferences,
			final CoordinateSystem coordinateSystem, final int numberOfWidthSegments, final int numberOfHeightSegments,
			final Vertex facingVector) {
		this.modelEditor = modelEditor;
		this.programPreferences = programPreferences;
        this.numberOfWidthSegments = numberOfWidthSegments;
		this.numberOfHeightSegments = numberOfHeightSegments;
		this.facingVector = facingVector;
	}

	@Override
	public void update(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		if (Math.abs(mouseEnd.x - activityStart.x) >= 0.1 && Math.abs(mouseEnd.y - activityStart.y) >= 0.1) {
			if (addPlane == null) {
				try {
					addPlane = modelEditor.addPlane(activityStart.x, activityStart.y, mouseEnd.x, mouseEnd.y, dim1,
							dim2, facingVector, numberOfWidthSegments, numberOfHeightSegments);
				} catch (final WrongModeException exc) {
					JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// TODO remove 0 arg
				addPlane.updateTranslation(mouseEnd.x - this.mouseEnd.x, mouseEnd.y - this.mouseEnd.y, 0);
			}
			this.mouseEnd = mouseEnd;
		}
	}

	@Override
	public UndoAction finish(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		if (addPlane == null) {
			return new DoNothingAction("do nothing");
		}
		return addPlane;
	}

	@Override
	public void render(final Graphics2D graphics, final CoordinateSystem coordinateSystem) {
		if (activityStart == null || mouseEnd == null) {
			return;
		}
		final double minX = Math.min(coordinateSystem.convertX(activityStart.x), coordinateSystem.convertX(mouseEnd.x));
		final double minY = Math.min(coordinateSystem.convertY(activityStart.y), coordinateSystem.convertY(mouseEnd.y));
		final double maxX = Math.max(coordinateSystem.convertX(activityStart.x), coordinateSystem.convertX(mouseEnd.x));
		final double maxY = Math.max(coordinateSystem.convertY(activityStart.y), coordinateSystem.convertY(mouseEnd.y));
		graphics.setColor(programPreferences.getActiveColor1());
		graphics.drawRect((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
	}

}
