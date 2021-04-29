package com.hiveworkshop.rms.ui.application.edit.mesh.viewport;

import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.GeosetAnim;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.editor.model.visitor.GeosetVisitor;
import com.hiveworkshop.rms.editor.model.visitor.ModelVisitor;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.ResettableAnimatedIdObjectParentLinkRenderer;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;

import java.awt.*;

public class LinkRenderingVisitorAdapter implements ModelVisitor {
	private ResettableAnimatedIdObjectParentLinkRenderer linkRenderer;

	public LinkRenderingVisitorAdapter(ProgramPreferences programPreferences) {
		linkRenderer = new ResettableAnimatedIdObjectParentLinkRenderer(programPreferences.getVertexSize());
	}

	public ResettableAnimatedIdObjectParentLinkRenderer reset(CoordinateSystem coordinateSystem, Graphics2D graphics, RenderModel renderModel) {

		linkRenderer.reset(coordinateSystem, graphics, NodeIconPalette.HIGHLIGHT, renderModel);
		return linkRenderer;
	}

	@Override
	public GeosetVisitor beginGeoset(int geosetId, Material material, GeosetAnim geosetAnim) {
		return GeosetVisitor.NO_ACTION;
	}

	@Override
	public void visitIdObject(IdObject object) {
		linkRenderer.visitIdObject(object);
	}

	@Override
	public void camera(Camera camera) {
	}
}