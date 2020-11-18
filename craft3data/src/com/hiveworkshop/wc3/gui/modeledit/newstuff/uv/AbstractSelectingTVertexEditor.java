package com.hiveworkshop.wc3.gui.modeledit.newstuff.uv;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.selection.AddSelectionAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.selection.MakeEditableAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.selection.RemoveSelectionAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.selection.SetSelectionAction;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.listener.EditabilityToggleHandler;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectableComponent;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionManager;

public abstract class AbstractSelectingTVertexEditor<T> implements TVertexEditor {
	protected final SelectionManager<T> selectionManager;

	public AbstractSelectingTVertexEditor(final SelectionManager<T> selectionManager) {
		this.selectionManager = selectionManager;
	}

	@Override
	public final UndoAction setSelectedRegion(final Rectangle2D region, final CoordinateSystem coordinateSystem) {
		final List<T> newSelection = genericSelect(region, coordinateSystem);
		return setSelectionWithAction(newSelection);
	}

	@Override
	public final UndoAction removeSelectedRegion(final Rectangle2D region, final CoordinateSystem coordinateSystem) {
		final List<T> newSelection = genericSelect(region, coordinateSystem);
		return removeSelectionWithAction(newSelection);
	}

	@Override
	public final UndoAction addSelectedRegion(final Rectangle2D region, final CoordinateSystem coordinateSystem) {
		final List<T> newSelection = genericSelect(region, coordinateSystem);
		return addSelectionWithAction(newSelection);
	}

	protected final UndoAction setSelectionWithAction(final List<T> newSelection) {
		final Set<T> previousSelection = new HashSet<>(selectionManager.getSelection());
		selectionManager.setSelection(newSelection);
		return new SetSelectionAction<>(newSelection, previousSelection, selectionManager, "select");
	}

	protected final UndoAction removeSelectionWithAction(final List<T> newSelection) {
		final Set<T> previousSelection = new HashSet<>(selectionManager.getSelection());
		selectionManager.removeSelection(newSelection);
		return new RemoveSelectionAction<>(previousSelection, newSelection, selectionManager);
	}

	protected final UndoAction addSelectionWithAction(final List<T> newSelection) {
		final Set<T> previousSelection = new HashSet<>(selectionManager.getSelection());
		selectionManager.addSelection(newSelection);
		return new AddSelectionAction<>(previousSelection, newSelection, selectionManager);
	}

	protected abstract List<T> genericSelect(final Rectangle2D region, CoordinateSystem coordinateSystem);

	protected abstract UndoAction buildHideComponentAction(List<? extends SelectableComponent> selectableComponents,
			EditabilityToggleHandler editabilityToggleHandler, final Runnable refreshGUIRunnable);

	@Override
	public UndoAction hideComponent(final List<? extends SelectableComponent> selectableComponent,
			final EditabilityToggleHandler editabilityToggleHandler, final Runnable refreshGUIRunnable) {
		final UndoAction hideComponentAction = buildHideComponentAction(selectableComponent, editabilityToggleHandler,
				refreshGUIRunnable);
		hideComponentAction.redo();
		return hideComponentAction;
	}

	@Override
	public UndoAction showComponent(final EditabilityToggleHandler editabilityToggleHandler) {
		editabilityToggleHandler.makeEditable();
		return new MakeEditableAction(editabilityToggleHandler);
	}
}
