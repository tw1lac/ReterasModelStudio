package com.hiveworkshop.rms.ui.application.edit.mesh.selection;

import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelElementRenderer;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.TVertexModelElementRenderer;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultiPartSelectionView implements SelectionView {
	private final List<SelectionView> selectionViews;

	public MultiPartSelectionView(final List<SelectionView> selectionViews) {
		this.selectionViews = selectionViews;
	}

	@Override
	public Vec3 getCenter() {
		final List<Vec3> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
            vertices.addAll(selectionView.getSelectedVertices());
		}
		return Vec3.centerOfGroup(vertices);
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
	public Collection<? extends Vec3> getSelectedVertices() {
		final List<Vec3> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
            vertices.addAll(selectionView.getSelectedVertices());
		}
		return vertices;
	}

	@Override
	public double getCircumscribedSphereRadius(final Vec3 center) {
		double radius = 0;
		// TODO WHY DOES THIS DISCARD THE CENTER ARG??
		final Collection<? extends Vec3> selectedVertices = getSelectedVertices();
		final Vec3 centerOfGroup = Vec3.centerOfGroup(selectedVertices);
		for (final Vec3 item : selectedVertices) {
			final double distance = centerOfGroup.distance(item);
			if (distance >= radius) {
				radius = distance;
			}
		}
		return radius;
	}

	@Override
	public void renderSelection(
			final ModelElementRenderer renderer,
			final CoordinateSystem coordinateSystem,
			final ModelView modelView,
			final ProgramPreferences programPreferences) {
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
	public Vec3 getUVCenter(final int tvertexLayerId) {
		final List<Vec3> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			vertices.addAll(selectionView.getSelectedTVertices(tvertexLayerId));
		}
		return Vec3.centerOfGroup(vertices);
	}

	@Override
	public Collection<? extends Vec3> getSelectedTVertices(final int tvertexLayerId) {
		final List<Vec3> vertices = new ArrayList<>();
		for (final SelectionView selectionView : selectionViews) {
			vertices.addAll(selectionView.getSelectedTVertices(tvertexLayerId));
		}
		return vertices;
	}

	@Override
	public double getCircumscribedSphereRadius(final Vec3 center, final int tvertexLayerId) {
		double radius = 0;
		// TODO WHY DOES THIS DISCARD THE CENTER ARG??
		final Collection<? extends Vec3> selectedVertices = getSelectedTVertices(tvertexLayerId);
		final Vec3 centerOfGroup = Vec3.centerOfGroup(selectedVertices);
		for (final Vec3 item : selectedVertices) {
			final double distance = centerOfGroup.distance(item);
			if (distance >= radius) {
				radius = distance;
			}
		}
		return radius;
	}

	@Override
	public void renderUVSelection(
			final TVertexModelElementRenderer renderer,
			final ModelView modelView,
			final ProgramPreferences programPreferences,
			final int tvertexLayerId) {
		for (final SelectionView selectionView : selectionViews) {
			selectionView.renderUVSelection(renderer, modelView, programPreferences, tvertexLayerId);
		}
	}
}
