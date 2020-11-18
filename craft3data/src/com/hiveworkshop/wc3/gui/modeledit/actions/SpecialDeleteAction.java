package com.hiveworkshop.wc3.gui.modeledit.actions;

import java.util.Collection;
import java.util.List;

import com.hiveworkshop.wc3.gui.modeledit.actions.newsys.ModelStructureChangeListener;
import com.hiveworkshop.wc3.gui.modeledit.selection.VertexSelectionHelper;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.Triangle;
import com.hiveworkshop.wc3.mdl.Vertex;

public class SpecialDeleteAction extends DeleteAction {

	private final List<Geoset> deletedGeosets;
	private final EditableModel parent;
	private final ModelStructureChangeListener modelStructureChangeListener;

	public SpecialDeleteAction(final Collection<? extends Vertex> selection, final List<Triangle> deletedTris,
			final VertexSelectionHelper vertexSelectionHelper, final List<Geoset> deletedGs, final EditableModel parentModel,
			final ModelStructureChangeListener modelStructureChangeListener) {
		super(selection, deletedTris, vertexSelectionHelper);
		deletedGeosets = deletedGs;
		parent = parentModel;
		this.modelStructureChangeListener = modelStructureChangeListener;
	}

	@Override
	public void redo() {
		super.redo();
        for (final Geoset g : deletedGeosets) {
            if (g.getGeosetAnim() != null) {
                parent.remove(g.getGeosetAnim());
            }
            parent.remove(g);
        }
		modelStructureChangeListener.geosetsRemoved(deletedGeosets);
	}

	@Override
	public void undo() {
		super.undo();
        for (final Geoset g : deletedGeosets) {
            if (g.getGeosetAnim() != null) {
                parent.add(g.getGeosetAnim());
            }
            parent.add(g);
        }
		modelStructureChangeListener.geosetsAdded(deletedGeosets);
	}

	@Override
	public String actionName() {
		return "delete vertices and geoset";
	}
}
