package com.hiveworkshop.wc3.gui.modeledit.newstuff;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionListener;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionManager;

public abstract class AbstractSelectionManager<T> implements SelectionManager<T> {
	protected final Set<T> selection = new HashSet<>();
	private final Set<SelectionListener> listeners = new HashSet<>();

	@Override
	public Set<T> getSelection() {
		return selection;
	}

	@Override
	public void setSelection(final Collection<? extends T> selectionItem) {
		selection.clear();
		selection.addAll(selectionItem);
		fireChangeListeners();
	}

	@Override
	public void addSelection(final Collection<? extends T> selectionItem) {
		selection.addAll(selectionItem);
		fireChangeListeners();
	}

	@Override
	public void removeSelection(final Collection<? extends T> selectionItem) {
		for (final T item : selectionItem) {
			selection.remove(item);
		}
		fireChangeListeners();
	}

	@Override
	public void addSelectionListener(final SelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSelectionListener(final SelectionListener listener) {
		listeners.remove(listener);
	}

	private void fireChangeListeners() {
		for (final SelectionListener listener : listeners) {
			listener.onSelectionChanged(this);
		}
	}

	@Override
	public boolean isEmpty() {
		return selection.isEmpty();
	}
}
