package com.hiveworkshop.wc3.gui.modeledit.actions;

import java.util.ArrayList;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.mdl.GeosetVertex;
import com.hiveworkshop.wc3.mdl.Vertex;

/**
 * Undoable snap action.
 *
 * Eric Theller 6/11/2012
 */
public class RecalculateNormalsAction implements UndoAction {
	ArrayList<Vertex> oldSelLocs;
	ArrayList<GeosetVertex> selection;
	Vertex snapPoint;

	public RecalculateNormalsAction(final ArrayList<GeosetVertex> selection, final ArrayList<Vertex> oldSelLocs,
			final Vertex snapPoint) {
		this.selection = new ArrayList<>(selection);
		this.oldSelLocs = oldSelLocs;
		this.snapPoint = new Vertex(snapPoint);
	}

	@Override
	public void undo() {
		for (int i = 0; i < selection.size(); i++) {
			selection.get(i).getNormal().setTo(oldSelLocs.get(i));
		}
	}

	@Override
	public void redo() {
        for (GeosetVertex geosetVertex : selection) {
            geosetVertex.getNormal().setTo(geosetVertex.createNormal());
        }
	}

	@Override
	public String actionName() {
		return "recalculate normals";
	}
}
