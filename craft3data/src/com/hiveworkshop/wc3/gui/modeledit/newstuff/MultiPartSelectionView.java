package com.hiveworkshop.wc3.gui.modeledit.newstuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.newstuff.uv.TVertexModelElementRenderer;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.wc3.mdl.TVertex;
import com.hiveworkshop.wc3.mdl.Triangle;
import com.hiveworkshop.wc3.mdl.Vertex;
import com.hiveworkshop.wc3.mdl.v2.ModelView;

public class MultiPartSelectionView implements SelectionView {
	private final List<SelectionView> selectionViews;

	public MultiPartSelectionView(final List<SelectionView> selectionViews) {
		this.selectionViews = selectionViews;
	}

	@Override
	public Vertex getCenter() {
		final List<Vertex> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			vertices.addAll(selectionView.getSelectedVertices());
		}
		return Vertex.centerOfGroup(vertices);
	}

	@Override
	public Collection<Triangle> getSelectedFaces() {
		final List<Triangle> faces = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			faces.addAll(selectionView.getSelectedFaces());
		}
		return faces;
	}

	@Override
	public Collection<? extends Vertex> getSelectedVertices() {
		final List<Vertex> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			vertices.addAll(selectionView.getSelectedVertices());
		}
		return vertices;
	}

	@Override
	public double getCircumscribedSphereRadius(final Vertex center) {
		double radius = 0;
		// TODO WHY DOES THIS DISCARD THE CENTER ARG??
		final Collection<? extends Vertex> selectedVertices = getSelectedVertices();
		final Vertex centerOfGroup = Vertex.centerOfGroup(selectedVertices);
		for (final Vertex item : selectedVertices) {
			final double distance = centerOfGroup.distance(item);
			if (distance >= radius) {
				radius = distance;
			}
		}
		return radius;
	}

	@Override
	public void renderSelection(final ModelElementRenderer renderer, final CoordinateSystem coordinateSystem,
			final ModelView modelView, final ProgramPreferences programPreferences) {
		for (final SelectionView selectionView : selectionViews) {
			selectionView.renderSelection(renderer, coordinateSystem, modelView, programPreferences);
		}
	}

	@Override
	public boolean isEmpty() {
		boolean empty = true;
		for (final SelectionView selectionView : selectionViews) {
			if (!selectionView.isEmpty()) {
				empty = false;
			}
		}
		return empty;
	}

	@Override
	public TVertex getUVCenter(final int tvertexLayerId) {
		final List<TVertex> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			vertices.addAll(selectionView.getSelectedTVertices(tvertexLayerId));
		}
		return TVertex.centerOfGroup(vertices);
	}

	@Override
	public Collection<? extends TVertex> getSelectedTVertices(final int tvertexLayerId) {
		final List<TVertex> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			vertices.addAll(selectionView.getSelectedTVertices(tvertexLayerId));
		}
		return vertices;
	}

	@Override
	public double getCircumscribedSphereRadius(final TVertex center, final int tvertexLayerId) {
		double radius = 0;
		// TODO WHY DOES THIS DISCARD THE CENTER ARG??
		final Collection<? extends TVertex> selectedVertices = getSelectedTVertices(tvertexLayerId);
		final TVertex centerOfGroup = TVertex.centerOfGroup(selectedVertices);
		for (final TVertex item : selectedVertices) {
			final double distance = centerOfGroup.distance(item);
			if (distance >= radius) {
				radius = distance;
			}
		}
		return radius;
	}

	@Override
	public void renderUVSelection(final TVertexModelElementRenderer renderer, final ModelView modelView,
			final ProgramPreferences programPreferences, final int tvertexLayerId) {
		for (final SelectionView selectionView : selectionViews) {
			selectionView.renderUVSelection(renderer, modelView, programPreferences, tvertexLayerId);
		}
	}
}
