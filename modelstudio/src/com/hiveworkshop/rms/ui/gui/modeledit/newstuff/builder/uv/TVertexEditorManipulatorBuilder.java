package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.builder.uv;

import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ButtonType;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.selection.ViewportSelectionHandler;
import com.hiveworkshop.rms.ui.application.edit.mesh.widgets.MoverWidget;
import com.hiveworkshop.rms.ui.application.edit.mesh.widgets.RotatorWidget;
import com.hiveworkshop.rms.ui.application.edit.mesh.widgets.ScalerWidget;
import com.hiveworkshop.rms.ui.application.edit.mesh.widgets.Widget;
import com.hiveworkshop.rms.ui.application.edit.uv.TVertexEditorManager;
import com.hiveworkshop.rms.ui.application.edit.uv.types.TVertexEditor;
import com.hiveworkshop.rms.ui.application.edit.uv.types.TVertexEditorChangeListener;
import com.hiveworkshop.rms.ui.application.edit.uv.types.TVertexModelElementRenderer;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.builder.ManipulatorBuilder;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.Manipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.MoveDimension;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.SelectManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.uv.MoveTVertexManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.uv.RotateTVertexManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator.uv.ScaleTVertexManipulator;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ModelEditorActionType2;
import com.hiveworkshop.rms.util.Vec2;

import java.awt.*;

public class TVertexEditorManipulatorBuilder implements ManipulatorBuilder, TVertexEditorChangeListener {
	private ViewportSelectionHandler viewportSelectionHandler;
	private TVertexEditorManager modelEditorManager;
	private final TVertexModelElementRenderer tVertexModelElementRenderer;
	private ModelView modelView;
	private TVertexEditor modelEditor;
	protected Widget widget;
	ModelEditorActionType2 currentAction;


	public TVertexEditorManipulatorBuilder(TVertexEditorManager modelEditorManager, ModelHandler modelHandler, ModelEditorActionType2 currentAction) {
		this.modelEditorManager = modelEditorManager;
		this.viewportSelectionHandler = modelEditorManager.getViewportSelectionHandler();
		this.modelView = modelHandler.getModelView();
		this.currentAction = currentAction;
		tVertexModelElementRenderer = new TVertexModelElementRenderer(ProgramGlobals.getPrefs().getVertexSize());
		createWidget(currentAction);
	}

	@Override
	public void editorChanged(TVertexEditor newModelEditor) {
		modelEditor = newModelEditor;
	}

	protected final TVertexEditor getModelEditor() {
		return modelEditor;
	}

	@Override
	public final Cursor getCursorAt(int x, int y,
	                                CoordinateSystem coordinateSystem,
	                                SelectionView selectionView) {
		Vec2 mousePoint = new Vec2(x, y);
		if (!selectionView.isEmpty() && widgetOffersEdit(selectionView.getUVCenter(modelEditor.getUVLayerIndex()), mousePoint, coordinateSystem, selectionView)) {
			return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		} else if (viewportSelectionHandler.canSelectAt(mousePoint, coordinateSystem)) {
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
		return null;
	}

	@Override
	public final Manipulator buildActivityListener(int x, int y,
	                                               ButtonType clickedButton,
	                                               CoordinateSystem coordinateSystem,
	                                               SelectionView selectionView) {
		Vec2 mousePoint = new Vec2(x, y);
		if (clickedButton == ButtonType.RIGHT_MOUSE) {
			return createDefaultManipulator(selectionView.getUVCenter(modelEditor.getUVLayerIndex()), mousePoint, coordinateSystem, selectionView);
		} else {
			if (!selectionView.isEmpty()) {
				Manipulator manipulatorFromWidget = createManipulatorFromWidget(selectionView.getUVCenter(modelEditor.getUVLayerIndex()), mousePoint, coordinateSystem, selectionView);
				if (manipulatorFromWidget != null) {
					return manipulatorFromWidget;
				}
			}
			return new SelectManipulator(viewportSelectionHandler, coordinateSystem);
		}
	}

	@Override
	public final void render(Graphics2D graphics,
	                         CoordinateSystem coordinateSystem,
	                         SelectionView selectionView,
	                         boolean isAnimated) {
		if (!isAnimated) {
			selectionView.renderUVSelection(tVertexModelElementRenderer.reset(graphics, coordinateSystem), modelView, modelEditor.getUVLayerIndex());
			if (!selectionView.isEmpty()) {
				renderWidget(graphics, coordinateSystem, selectionView);
			}
		}
	}

	protected boolean widgetOffersEdit(Vec2 selectionCenter, Vec2 mousePoint, CoordinateSystem coordinateSystem, SelectionView selectionView) {
		widget.setPoint(selectionView.getUVCenter(getModelEditor().getUVLayerIndex()));
		MoveDimension directionByMouse = widget.getDirectionByMouse(mousePoint, coordinateSystem);
		widget.setMoveDirection(directionByMouse);
		return directionByMouse != MoveDimension.NONE;
	}

	protected Manipulator createManipulatorFromWidget(Vec2 selectionCenter, Vec2 mousePoint, CoordinateSystem coordinateSystem, SelectionView selectionView){
		return createManipulatorFromWidget2(mousePoint, coordinateSystem, selectionView, currentAction);
	}

	protected Manipulator createDefaultManipulator(Vec2 selectionCenter, Vec2 mousePoint, CoordinateSystem coordinateSystem, SelectionView selectionView){
		return getBuilder2(selectionView, currentAction, MoveDimension.XYZ);
	}

	protected Manipulator createManipulatorFromWidget2(Vec2 mousePoint, CoordinateSystem coordinateSystem, SelectionView selectionView, ModelEditorActionType2 action) {
		widget.setPoint(selectionView.getCenter());
		MoveDimension directionByMouse = widget.getDirectionByMouse(mousePoint, coordinateSystem);

		widget.setMoveDirection(directionByMouse);
		if (directionByMouse != MoveDimension.NONE) {
			return getBuilder2(selectionView, action, directionByMouse);
		}
		return null;
	}

	private Manipulator getBuilder2(SelectionView selectionView, ModelEditorActionType2 editorActionType, MoveDimension directionByMouse) {
		return switch (editorActionType) {
			case SCALING -> new ScaleTVertexManipulator(getModelEditor(), selectionView, directionByMouse);
			case ROTATION -> new RotateTVertexManipulator(getModelEditor(), selectionView, directionByMouse);
			case TRANSLATION -> new MoveTVertexManipulator(getModelEditor(), directionByMouse);

		};
	}
	private void createWidget(ModelEditorActionType2 action) {
		if(action == null){
			widget = new MoverWidget();
		} else {
			switch (action) {
				case TRANSLATION -> widget = new MoverWidget();
				case ROTATION -> widget = new RotatorWidget();
				case SCALING -> widget = new ScalerWidget();
			};
		}
	}

	protected void renderWidget(Graphics2D graphics, CoordinateSystem coordinateSystem, SelectionView selectionView) {
		widget.setPoint(selectionView.getUVCenter(getModelEditor().getUVLayerIndex()));
		widget.render(graphics, coordinateSystem);
	}
}
