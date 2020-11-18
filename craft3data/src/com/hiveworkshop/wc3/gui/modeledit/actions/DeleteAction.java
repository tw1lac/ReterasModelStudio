package com.hiveworkshop.wc3.gui.modeledit.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.gui.modeledit.selection.VertexSelectionHelper;
import com.hiveworkshop.wc3.mdl.GeosetVertex;
import com.hiveworkshop.wc3.mdl.Triangle;
import com.hiveworkshop.wc3.mdl.Vertex;

/**
 * Something to undo when you deleted something important.
 *
 * Eric Theller 6/11/2012
 */
public class DeleteAction implements UndoAction {
	private final List<Vertex> selection;
	private final List<Vertex> deleted;
	private final List<Triangle> deletedTris;
	private final VertexSelectionHelper vertexSelectionHelper;

	public DeleteAction(final Collection<? extends Vertex> selection, final List<Triangle> deletedTris,
			final VertexSelectionHelper vertexSelectionHelper) {
		this.vertexSelectionHelper = vertexSelectionHelper;
		this.selection = new ArrayList<>(selection);
		this.deleted = new ArrayList<>(selection);
		this.deletedTris = deletedTris;
	}

	@Override
	public void redo() {
        for (Vertex value : deleted) {
            if (value.getClass() == GeosetVertex.class) {
                final GeosetVertex gv = (GeosetVertex) value;
                gv.getGeoset().remove(gv);
            }
        }
		for (final Triangle t : deletedTris) {
			t.getGeoset().removeTriangle(t);
			for (final GeosetVertex vertex : t.getAll()) {
				vertex.getTriangles().remove(t);
			}
		}
		vertexSelectionHelper.selectVertices(new ArrayList<>());
	}

	@Override
	public void undo() {
        for (Vertex value : deleted) {
            if (value.getClass() == GeosetVertex.class) {
                final GeosetVertex gv = (GeosetVertex) value;
                gv.getGeoset().addVertex(gv);
            }
        }
		for (final Triangle t : deletedTris) {
			t.getGeoset().addTriangle(t);
			for (final GeosetVertex vertex : t.getAll()) {
				vertex.getTriangles().add(t);
			}
		}
		vertexSelectionHelper.selectVertices(selection);
	}

	@Override
	public String actionName() {
		return "delete vertices";
	}
}
