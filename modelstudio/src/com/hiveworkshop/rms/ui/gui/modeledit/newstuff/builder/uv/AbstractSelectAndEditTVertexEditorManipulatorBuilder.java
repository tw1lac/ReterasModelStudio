package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.builder.uv;

import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ButtonType;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.selection.ViewportSelectionHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.Manipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.SelectManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.Graphics2DToTVertexModelElementRendererAdapter;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.TVertexEditor;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;

public abstract class AbstractSelectAndEditTVertexEditorManipulatorBuilder implements TVertexEditorManipulatorBuilder {
	private final ViewportSelectionHandler viewportSelectionHandler;
	private final ProgramPreferences programPreferences;
	private TVertexEditor modelEditor;
	private final Graphics2DToTVertexModelElementRendererAdapter graphics2dToModelElementRendererAdapter;
	//	private final Graphics2DToAnimatedModelElementRendererAdapter graphics2dToAnimatedModelElementRendererAdapter;
	private final ModelView modelView;

	public AbstractSelectAndEditTVertexEditorManipulatorBuilder(final ViewportSelectionHandler viewportSelectionHandler,
																final ProgramPreferences programPreferences,
																final TVertexEditor modelEditor,
																final ModelView modelView) {
		this.viewportSelectionHandler = viewportSelectionHandler;
		this.programPreferences = programPreferences;
		this.modelEditor = modelEditor;
		this.modelView = modelView;
		graphics2dToModelElementRendererAdapter = new Graphics2DToTVertexModelElementRendererAdapter(
				programPreferences.getVertexSize(), programPreferences);
	}

	@Override
	public void editorChanged(final TVertexEditor newModelEditor) {
		modelEditor = newModelEditor;
	}

	protected final TVertexEditor getModelEditor() {
		return modelEditor;
	}

	@Override
	public final Cursor getCursorAt(final int x, final int y,
									final CoordinateSystem coordinateSystem,
									final SelectionView selectionView) {
		final Point mousePoint = new Point(x, y);
		if (!selectionView.isEmpty() && widgetOffersEdit(
				selectionView.getUVCenter(modelEditor.getUVLayerIndex()), mousePoint, coordinateSystem, selectionView)) {
			return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		} else if (viewportSelectionHandler.canSelectAt(mousePoint, coordinateSystem)) {
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
		return null;
	}

	@Override
	public final Manipulator buildActivityListener(final int x, final int y,
												   final ButtonType clickedButton,
												   final CoordinateSystem coordinateSystem,
												   final SelectionView selectionView) {
		final Point mousePoint = new Point(x, y);
		if (clickedButton == ButtonType.RIGHT_MOUSE) {
			return createDefaultManipulator(selectionView.getUVCenter(modelEditor.getUVLayerIndex()), mousePoint,
					coordinateSystem, selectionView);
		} else {
			if (!selectionView.isEmpty()) {
				final Manipulator manipulatorFromWidget = createManipulatorFromWidget(
						selectionView.getUVCenter(modelEditor.getUVLayerIndex()), mousePoint, coordinateSystem,
						selectionView);
				if (manipulatorFromWidget != null) {
					return manipulatorFromWidget;
				}
			}
			return new SelectManipulator(viewportSelectionHandler, programPreferences, coordinateSystem);
		}
	}

	@Override
	public final void render(final Graphics2D graphics, final CoordinateSystem coordinateSystem,
			final SelectionView selectionView, final RenderModel renderModel) {
//		selectionView.renderUVSelection(
//				graphics2dToAnimatedModelElementRendererAdapter.reset(graphics, coordinateSystem, renderModel),
//				modelView, programPreferences, uvLayerIndex);
//		if (!selectionView.isEmpty()) {
//			renderWidget(graphics, coordinateSystem, selectionView);
//		}
	}

	@Override
	public final void renderStatic(final Graphics2D graphics,
								   final CoordinateSystem coordinateSystem,
								   final SelectionView selectionView) {
		selectionView.renderUVSelection(
				graphics2dToModelElementRendererAdapter.reset(graphics, coordinateSystem),
				modelView, programPreferences, modelEditor.getUVLayerIndex());
		if (!selectionView.isEmpty()) {
			renderWidget(graphics, coordinateSystem, selectionView);
		}
	}

	protected abstract boolean widgetOffersEdit(Vec3 selectionCenter, Point mousePoint,
												CoordinateSystem coordinateSystem, SelectionView selectionView);

	protected abstract Manipulator createManipulatorFromWidget(Vec3 selectionCenter, Point mousePoint,
															   CoordinateSystem coordinateSystem, SelectionView selectionView);

	protected abstract Manipulator createDefaultManipulator(Vec3 selectionCenter, Point mousePoint,
															CoordinateSystem coordinateSystem, SelectionView selectionView);

	protected abstract void renderWidget(final Graphics2D graphics, final CoordinateSystem coordinateSystem,
										 final SelectionView selectionView);
}
