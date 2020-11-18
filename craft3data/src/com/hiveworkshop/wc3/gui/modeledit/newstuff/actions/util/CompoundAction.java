package com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.util;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;

import java.util.List;

public final class CompoundAction implements UndoAction {
	private final List<? extends UndoAction> actions;
	private final String name;

	public CompoundAction(final String name, final List<? extends UndoAction> actions) {
		this.name = name;
		this.actions = actions;
	}

	@Override
	public void undo() {
		for (int i = actions.size() - 1; i >= 0; i--) {
			final UndoAction action = actions.get(i);
			action.undo();
		}
	}

	@Override
	public void redo() {
		for (final UndoAction action : actions) {
			action.redo();
		}
	}

	@Override
	public String actionName() {
		return name;
	}

}
