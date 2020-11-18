package com.hiveworkshop.wc3.mdl.v2;

import java.util.Set;
import java.util.HashSet;

import com.hiveworkshop.wc3.mdl.Camera;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.IdObject;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.v2.render.RenderByViewMeshRenderer;
import com.hiveworkshop.wc3.mdl.v2.render.RenderByViewModelRenderer;
import com.hiveworkshop.wc3.mdl.v2.visitor.MeshVisitor;
import com.hiveworkshop.wc3.mdl.v2.visitor.ModelVisitor;
import com.hiveworkshop.wc3.util.ModelUtils;

public final class ModelViewManager implements ModelView {
	private final EditableModel model;
	private final ModelViewStateNotifier modelViewStateNotifier;
	private final Set<Geoset> editableGeosets;// TODO should be a set
	private final Set<Geoset> visibleGeosets;
	private final Set<IdObject> editableIdObjects;
	private final Set<Camera> editableCameras;
	private Geoset highlightedGeoset;
	private IdObject highlightedNode;
	private final RenderByViewModelRenderer renderByViewModelRenderer;
	private final RenderByViewMeshRenderer renderByViewMeshRenderer;

	public ModelViewManager(final EditableModel model) {
		this.model = model;
		this.modelViewStateNotifier = new ModelViewStateNotifier();
		this.editableGeosets = new HashSet<>();
		for (final Geoset geoset : model.getGeosets()) {
			if (!ModelUtils.isLevelOfDetailSupported(model.getFormatVersion()) || (geoset.getLevelOfDetail() == 0)) {
				editableGeosets.add(geoset);
			}
		}
		this.visibleGeosets = new HashSet<>();
		this.editableIdObjects = new HashSet<>();
		this.editableCameras = new HashSet<>();
		this.renderByViewModelRenderer = new RenderByViewModelRenderer(this);
		this.renderByViewMeshRenderer = new RenderByViewMeshRenderer(this);
	}

	@Override
	public void visit(final ModelVisitor visitor) {
		model.render(renderByViewModelRenderer.reset(visitor));
	}

	@Override
	public void addStateListener(final ModelViewStateListener listener) {
		modelViewStateNotifier.subscribe(listener);
	}

	@Override
	public Set<Geoset> getVisibleGeosets() {
		return visibleGeosets;
	}

	@Override
	public Set<Geoset> getEditableGeosets() {
		return editableGeosets;
	}

	@Override
	public Set<IdObject> getEditableIdObjects() {
		return editableIdObjects;
	}

	@Override
	public Set<Camera> getEditableCameras() {
		return editableCameras;
	}

	@Override
	public EditableModel getModel() {
		return model;
	}

	@Override
	public Geoset getHighlightedGeoset() {
		return highlightedGeoset;
	}

	@Override
	public IdObject getHighlightedNode() {
		return highlightedNode;
	}

	public void makeGeosetEditable(final Geoset geoset) {
		editableGeosets.add(geoset);
		modelViewStateNotifier.geosetEditable(geoset);
	}

	public void makeGeosetNotEditable(final Geoset geoset) {
		editableGeosets.remove(geoset);
		modelViewStateNotifier.geosetNotEditable(geoset);
	}

	public void makeGeosetVisible(final Geoset geoset) {
		visibleGeosets.add(geoset);
		modelViewStateNotifier.geosetVisible(geoset);
	}

	public void makeGeosetNotVisible(final Geoset geoset) {
		visibleGeosets.remove(geoset);
		modelViewStateNotifier.geosetNotVisible(geoset);
	}

	public void makeIdObjectVisible(final IdObject bone) {
		editableIdObjects.add(bone);
		modelViewStateNotifier.idObjectVisible(bone);
	}

	public void makeIdObjectNotVisible(final IdObject bone) {
		editableIdObjects.remove(bone);
		modelViewStateNotifier.idObjectNotVisible(bone);
	}

	public void makeCameraVisible(final Camera camera) {
		editableCameras.add(camera);
		modelViewStateNotifier.cameraVisible(camera);
	}

	public void makeCameraNotVisible(final Camera camera) {
		editableCameras.remove(camera);
		modelViewStateNotifier.cameraNotVisible(camera);
	}

	public void highlightGeoset(final Geoset geoset) {
		highlightedGeoset = geoset;
		modelViewStateNotifier.highlightGeoset(geoset);
	}

	public void unhighlightGeoset(final Geoset geoset) {
		if (highlightedGeoset == geoset) {
			highlightedGeoset = null;
		}
		modelViewStateNotifier.unhighlightGeoset(geoset);
	}

	public void highlightNode(final IdObject node) {
		highlightedNode = node;
		modelViewStateNotifier.highlightNode(node);
	}

	public void unhighlightNode(final IdObject node) {
		if (highlightedNode == node) {
			highlightedNode = null;
		}
		modelViewStateNotifier.unhighlightNode(node);

	}

	@Override
	public void visitMesh(final MeshVisitor visitor) {
		model.visit(renderByViewMeshRenderer.reset(visitor));
	}
}
