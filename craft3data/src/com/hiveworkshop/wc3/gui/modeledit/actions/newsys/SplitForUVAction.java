package com.hiveworkshop.wc3.gui.modeledit.actions.newsys;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.selection.SelectionManager;
import com.hiveworkshop.wc3.gui.modeledit.selection.VertexSelectionHelper;
import com.hiveworkshop.wc3.mdl.*;

import java.util.*;

public final class SplitForUVAction<T> implements UndoAction {

	private List<Triangle> trianglesMovedToSeparateGeo;
    private final Collection<Triangle> trisToSeparate;
    private final SelectionManager<T> selectionManager;
	private final Collection<T> selection;
	private final Collection<Vertex> newVerticesToSelect;
	private final VertexSelectionHelper vertexSelectionHelper;

	public SplitForUVAction(final Collection<Triangle> trisToSeparate, final EditableModel model,
                            final ModelStructureChangeListener modelStructureChangeListener, final SelectionManager<T> selectionManager,
                            final VertexSelectionHelper vertexSelectionHelper) {
		this.trisToSeparate = trisToSeparate;
        this.selectionManager = selectionManager;
		this.vertexSelectionHelper = vertexSelectionHelper;
        List<Geoset> geosetsModified = new ArrayList<>();
		this.newVerticesToSelect = new ArrayList<>();
		final Set<GeosetVertex> verticesInTheTriangles = new HashSet<>();
		final Set<Geoset> geosetsToCopy = new HashSet<>();
		for (final Triangle tri : trisToSeparate) {
			verticesInTheTriangles.addAll(Arrays.asList(tri.getVerts()));
			geosetsToCopy.add(tri.getGeoset());
		}
		final Map<Geoset, Geoset> oldGeoToNewGeo = new HashMap<>();
		final Map<GeosetVertex, GeosetVertex> oldVertToNewVert = new HashMap<>();
		for (final Geoset geoset : geosetsToCopy) {
			final Geoset geosetModified = geoset;
			geosetsModified.add(geosetModified);
		}
		for (final GeosetVertex vertex : verticesInTheTriangles) {
			final GeosetVertex copy = new GeosetVertex(vertex);
			final Geoset newGeoset = oldGeoToNewGeo.get(vertex.getGeoset());
			copy.setGeoset(newGeoset);
			newGeoset.add(copy);
			oldVertToNewVert.put(vertex, copy);
			newVerticesToSelect.add(copy);
		}
		for (final Triangle tri : trisToSeparate) {
			final GeosetVertex a, b, c;
			a = oldVertToNewVert.get(tri.get(0));
			b = oldVertToNewVert.get(tri.get(1));
			c = oldVertToNewVert.get(tri.get(2));
			final Geoset newGeoset = oldGeoToNewGeo.get(tri.getGeoset());
			final Triangle newTriangle = new Triangle(a, b, c, newGeoset);
			newGeoset.add(newTriangle);
			a.getTriangles().add(newTriangle);
			b.getTriangles().add(newTriangle);
			c.getTriangles().add(newTriangle);
		}
		selection = new ArrayList<>(selectionManager.getSelection());
	}

	@Override
	public void undo() {
		for (final Triangle tri : trisToSeparate) {
			final Geoset geoset = tri.getGeoset();
			for (final GeosetVertex gv : tri.getVerts()) {
				gv.getTriangles().add(tri);
				if (!geoset.getVertices().contains(gv)) {
					geoset.add(gv);
				}
			}
			geoset.add(tri);
		}
		selectionManager.setSelection(selection);
	}

	@Override
	public void redo() {
		for (final Triangle tri : trisToSeparate) {
			final Geoset geoset = tri.getGeoset();
			for (final GeosetVertex gv : tri.getVerts()) {
				gv.getTriangles().remove(tri);
				if (gv.getTriangles().isEmpty()) {
					geoset.remove(gv);
				}
			}
			geoset.removeTriangle(tri);
		}
		selectionManager.removeSelection(selection);
		vertexSelectionHelper.selectVertices(newVerticesToSelect);
	}

	@Override
	public String actionName() {
		return "split geoset";
	}

}
