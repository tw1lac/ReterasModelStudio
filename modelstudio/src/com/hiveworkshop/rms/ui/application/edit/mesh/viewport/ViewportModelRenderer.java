package com.hiveworkshop.rms.ui.application.edit.mesh.viewport;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.visitor.GeosetVisitor;
import com.hiveworkshop.rms.editor.model.visitor.ModelVisitor;
import com.hiveworkshop.rms.editor.model.visitor.TriangleVisitor;
import com.hiveworkshop.rms.editor.model.visitor.VertexVisitor;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.ResettableIdObjectRenderer;
import com.hiveworkshop.rms.ui.gui.modeledit.VertexFilter;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewportModelRenderer implements ModelVisitor {
	private Graphics2D graphics;
	private ProgramPreferences programPreferences;
	private final GeosetRendererImpl geosetRenderer;
	private final int vertexSize;
	private byte xDimension;
	private byte yDimension;
	private ViewportView viewportView;
	private CoordinateSystem coordinateSystem;
	private final ResettableIdObjectRenderer idObjectRenderer;
	// TODO Now that I added modelView to this class, why does RenderByViewModelRenderer exist???
	private ModelView modelView;

	public ViewportModelRenderer(final int vertexSize) {
		this.vertexSize = vertexSize;
		geosetRenderer = new GeosetRendererImpl();
		idObjectRenderer = new ResettableIdObjectRenderer(vertexSize);
	}

	public ViewportModelRenderer reset(final Graphics2D graphics, final ProgramPreferences programPreferences,
	                                   final byte xDimension, final byte yDimension, final ViewportView viewportView,
									   final CoordinateSystem coordinateSystem, final ModelView modelView) {
		this.graphics = graphics;
		this.programPreferences = programPreferences;
		this.xDimension = xDimension;
		this.yDimension = yDimension;
		this.viewportView = viewportView;
		this.coordinateSystem = coordinateSystem;
		this.modelView = modelView;
		idObjectRenderer.reset(coordinateSystem, graphics, programPreferences.getLightsColor(),
				programPreferences.getPivotPointsColor(), NodeIconPalette.UNSELECTED,
				programPreferences.isUseBoxesForPivotPoints());
		return this;
	}

	@Override
	public GeosetVisitor beginGeoset(final int geosetId, final Material material, final GeosetAnim geosetAnim) {
		graphics.setColor(programPreferences.getTriangleColor());
		if (modelView.getHighlightedGeoset() == modelView.getModel().getGeoset(geosetId)) {
			graphics.setColor(programPreferences.getHighlighTriangleColor());
		} else {
			final Geoset geoset = modelView.getModel().getGeoset(geosetId);
			if (!modelView.getEditableGeosets().contains(geoset)) {
				graphics.setColor(programPreferences.getVisibleUneditableColor());
			}
		}
		return geosetRenderer.reset();
	}

	@Override
	public void bone(final Bone object) {
		resetIdObjectRendererWithNode(object);
		idObjectRenderer.bone(object);
	}

	private void resetIdObjectRendererWithNode(final IdObject object) {
		idObjectRenderer.reset(coordinateSystem, graphics,
				modelView.getHighlightedNode() == object ? programPreferences.getHighlighVertexColor() : programPreferences.getLightsColor(),
				modelView.getHighlightedNode() == object ? programPreferences.getHighlighVertexColor() : programPreferences.getPivotPointsColor(),
				modelView.getHighlightedNode() == object ? NodeIconPalette.HIGHLIGHT : NodeIconPalette.UNSELECTED,
				programPreferences.isUseBoxesForPivotPoints());
	}

	@Override
	public void light(final Light light) {
		resetIdObjectRendererWithNode(light);
		idObjectRenderer.light(light);
	}

	@Override
	public void helper(final Helper object) {
		resetIdObjectRendererWithNode(object);
		idObjectRenderer.helper(object);
	}

	@Override
	public void attachment(final Attachment attachment) {
		resetIdObjectRendererWithNode(attachment);
		idObjectRenderer.attachment(attachment);
	}

	@Override
	public void particleEmitter(final ParticleEmitter particleEmitter) {
		resetIdObjectRendererWithNode(particleEmitter);
		idObjectRenderer.particleEmitter(particleEmitter);
	}

	@Override
	public void particleEmitter2(final ParticleEmitter2 particleEmitter) {
		resetIdObjectRendererWithNode(particleEmitter);
		idObjectRenderer.particleEmitter2(particleEmitter);
	}

	@Override
	public void popcornFxEmitter(final ParticleEmitterPopcorn popcornFxEmitter) {
		resetIdObjectRendererWithNode(popcornFxEmitter);
		idObjectRenderer.popcornFxEmitter(popcornFxEmitter);
	}

	@Override
	public void ribbonEmitter(final RibbonEmitter ribbonEmitter) {
		resetIdObjectRendererWithNode(ribbonEmitter);
		idObjectRenderer.ribbonEmitter(ribbonEmitter);
	}

	@Override
	public void eventObject(final EventObject eventObject) {
		resetIdObjectRendererWithNode(eventObject);
		idObjectRenderer.eventObject(eventObject);
	}

	@Override
	public void collisionShape(final CollisionShape collisionShape) {
		resetIdObjectRendererWithNode(collisionShape);
		idObjectRenderer.collisionShape(collisionShape);

	}

	@Override
	public void camera(final Camera cam) {
		idObjectRenderer.camera(cam);
	}

	private final class GeosetRendererImpl implements GeosetVisitor {
		private final TriangleRendererImpl triangleRenderer = new TriangleRendererImpl();

		public GeosetRendererImpl reset() {
			return this;
		}

		@Override
		public TriangleVisitor beginTriangle() {
			return triangleRenderer.reset();
		}

		@Override
		public void geosetFinished() {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Copied directly from MDLDisplay and then made static.
	 */
	public static void drawFittedTriangles(final EditableModel model, final Graphics g, final Rectangle bounds,
	                                       final byte a, final byte b, final VertexFilter<? super GeosetVertex> filter,
	                                       final Vec3 extraHighlightPoint) {
		final List<Triangle> triangles = new ArrayList<>();
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (final Geoset geo : model.getGeosets()) {
			for (final Triangle t : geo.getTriangles()) {
				for (final GeosetVertex vertex : t.getVerts()) {
					if (filter.isAccepted(vertex) && !triangles.contains(t)) {
						triangles.add(t);
					}
				}
				final double[] x = t.getCoords(a);
				for (final double xval : x) {
					minX = Math.min(xval, minX);
					maxX = Math.max(xval, maxX);
				}
				final double[] y = t.getCoords(b);
				for (final double yval : y) {
					final double yCoord = -yval;
					minY = Math.min(yCoord, minY);
					maxY = Math.max(yCoord, maxY);

				}
			}
		}
		final double deltaX = maxX - minX;
		final double deltaY = maxY - minY;
		final double boxSize = Math.max(deltaX, deltaY);
		double boxOffsetX = minX - (boxSize - deltaX) / 2;
		double boxOffsetY = minY - (boxSize - deltaY) / 2;

//		final AffineTransform transform = ((Graphics2D) g).getTransform();
		((Graphics2D) g).scale(bounds.getWidth() / boxSize, bounds.getHeight() / boxSize);
		((Graphics2D) g).translate(-boxOffsetX, -boxOffsetY);


		g.setColor(Color.GRAY);
		for (final Geoset geo : model.getGeosets()) {
			for (final Triangle t : geo.getTriangles()) {
				drawTriangle(g, a, b, t);
			}
		}

		g.setColor(Color.RED);
		for (final Triangle t : triangles) {
			drawTriangle(g, a, b, t);
		}

		g.setColor(Color.YELLOW);
		if (extraHighlightPoint != null) {
			drawCrossHair(g, a, b, extraHighlightPoint);
		}

//		final AffineTransform transform = ((Graphics2D) g).getTransform();
//		((Graphics2D) g).setTransform(transform);
	}

	public static void drawFittedTriangles2(final EditableModel model, final Graphics g, final Rectangle bounds,
	                                        final byte a, final byte b, final VertexFilter<? super GeosetVertex> filter,
	                                        final Vec3 extraHighlightPoint) {
		Vec2[] realBounds = getBoundBoxSize(model, a, b);
		scaleAndTranslateGraphic((Graphics2D) g, bounds, realBounds);

		drawGeosetFlat(model, g, a, b);

		drawFilteredTriangles(model, g, a, b, filter);

		drawHighlightedPoints(g, a, b, extraHighlightPoint);
	}

	public static void drawGeosetFlat(EditableModel model, Graphics g, byte a, byte b) {
		g.setColor(Color.GRAY);
		for (final Geoset geo : model.getGeosets()) {
			for (final Triangle t : geo.getTriangles()) {
				drawTriangle(g, a, b, t);
			}
		}
	}

	public static void drawFilteredTriangles(final EditableModel model, Graphics g,
	                                         byte a, byte b,
	                                         VertexFilter<? super GeosetVertex> filter) {
		final List<Triangle> triangles = getTriangles(model, filter);
		g.setColor(Color.RED);
		for (final Triangle t : triangles) {
			drawTriangle(g, a, b, t);
		}
	}

	private static List<Triangle> getTriangles(EditableModel model, VertexFilter<? super GeosetVertex> filter) {
		final List<Triangle> triangles = new ArrayList<>();
		for (final Geoset geo : model.getGeosets()) {
			for (final Triangle t : geo.getTriangles()) {
				for (final GeosetVertex vertex : t.getVerts()) {
					if (filter.isAccepted(vertex) && !triangles.contains(t)) {
						triangles.add(t);
					}
				}
			}
		}
		System.out.println(triangles.size());
		return triangles;
	}

	public static void drawFilteredTriangles2(final EditableModel model, Graphics g,
	                                          byte a, byte b, Map<Geoset, Map<Bone, List<GeosetVertex>>> boneMap, Bone bone) {
		final List<Triangle> triangles = getTriangles(model, boneMap, bone);

		g.setColor(Color.RED);
		for (final Triangle t : triangles) {
			drawTriangle(g, a, b, t);
		}
	}

	private static List<Triangle> getTriangles(EditableModel model, Map<Geoset, Map<Bone, List<GeosetVertex>>> boneMap, Bone bone) {
		final List<Triangle> triangles = new ArrayList<>();
		for (final Geoset geo : model.getGeosets()) {
			if (boneMap.containsKey(geo) && boneMap.get(geo).containsKey(bone)) {
				for (final GeosetVertex vertex : boneMap.get(geo).get(bone)) {
					for (Triangle t : vertex.getTriangles()) {
						if (!triangles.contains(t) && vertex.getBones().contains(bone)) {
							triangles.add(t);
						}
					}
				}
			}
		}
		return triangles;
	}

	public static void drawHighlightedPoints(Graphics g, byte a, byte b, Vec3 extraHighlightPoint) {
		g.setColor(Color.YELLOW);
		if (extraHighlightPoint != null) {
			drawCrossHair(g, a, b, extraHighlightPoint);
		}
	}

	public static void scaleAndTranslateGraphic(Graphics2D g, Rectangle bounds, Vec2[] realBounds) {
		Vec2 delta = Vec2.getDif(realBounds[1], realBounds[0]);

		double boxSize = Math.max(delta.x, delta.y);
		Vec2 boxOffset = Vec2.getSum(realBounds[0], realBounds[1]).translate(-boxSize, -boxSize).scale(.5f);

		g.scale(bounds.getWidth() / boxSize, bounds.getHeight() / boxSize);
		g.translate(-boxOffset.x, boxSize - boxOffset.y);
	}

	public static Vec2[] getBoundBoxSize(final EditableModel model, final byte a, final byte b) {
		Vec2[] realBoxBounds = {new Vec2(Float.MAX_VALUE, Float.MAX_VALUE), new Vec2(Float.MIN_VALUE, Float.MIN_VALUE)};

		for (final Geoset geo : model.getGeosets()) {
			for (final Triangle t : geo.getTriangles()) {
				Vec2[] triVerts = t.getProjectedVerts(a, b);
				for (Vec2 v : triVerts) {
					realBoxBounds[0].minimize(v);
					realBoxBounds[1].maximize(v);
				}
			}
		}
		return realBoxBounds;
	}

	public static void drawCrossHair(Graphics g, byte a, byte b, Vec3 extraHighlightPoint) {
		final int x = (int) extraHighlightPoint.getCoord(a);
		final int y = (int) -extraHighlightPoint.getCoord(b);
		g.drawOval(x - 5, y - 5, 10, 10);
		g.drawLine(x, y - 10, x, y + 10);
		g.drawLine(x - 10, y, x + 10, y);
	}


	/**
	 * Copied directly from MDLDisplay and then made static.
	 */
	private static void drawTriangle(final Graphics g, final byte a, final byte b, final Triangle t) {
		final double[] x = t.getCoords(a);
		final double[] y = t.getCoords(b);
		final int[] xInt = new int[4];
		final int[] yInt = new int[4];
		for (int ix = 0; ix < 3; ix++) {
			xInt[ix] = (int) Math.round(x[ix]);
			yInt[ix] = (int) Math.round(-y[ix]);
		}
		xInt[3] = xInt[0];
		yInt[3] = yInt[0];
		g.drawPolyline(xInt, yInt, 4);
	}

	private final class TriangleRendererImpl implements TriangleVisitor {
		private final List<Point> previousVertices = new ArrayList<>();

		public TriangleRendererImpl reset() {
			previousVertices.clear();
			return this;
		}

		@Override
		public VertexVisitor vertex(Vec3 vert, Vec3 normal, final List<Bone> bones) {
			final double firstCoord = vert.getCoord(xDimension);
			final double secondCoord = vert.getCoord(yDimension);
			final Point point = new Point((int) coordinateSystem.convertX(firstCoord), (int) coordinateSystem.convertY(secondCoord));

			if (previousVertices.size() > 0) {
				final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
				graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
			}
			previousVertices.add(point);

			if (programPreferences.showNormals()) {
				final Color triangleColor = graphics.getColor();
				final double firstNormalCoord = normal.getCoord(xDimension);
				final double secondNormalCoord = normal.getCoord(yDimension);

				graphics.setColor(programPreferences.getNormalsColor());
				final double zoom = CoordinateSystem.Util.getZoom(coordinateSystem);

				final Point endPoint = new Point(
						(int) coordinateSystem.convertX(firstCoord + ((firstNormalCoord * 12) / zoom)),
						(int) coordinateSystem.convertY(secondCoord + ((secondNormalCoord * 12) / zoom)));
				graphics.drawLine(point.x, point.y, endPoint.x, endPoint.y);
				graphics.setColor(triangleColor);
			}
			return VertexVisitor.NO_ACTION;
		}


		@Override
		public VertexVisitor hdVertex(Vec3 vert, Vec3 normal, final Bone[] skinBones, final short[] skinBoneWeights) {
			return vertex(vert, normal, null);
		}

		@Override
		public VertexVisitor hdVertex(final double x, final double y, final double z,
		                              final double normalX, final double normalY, final double normalZ,
		                              final Bone[] skinBones, final short[] skinBoneWeights) {
			return vertex(new Vec3(x, y, z), new Vec3(normalX, normalY, normalZ), null);
		}

		@Override
		public VertexVisitor vertex(final double x, final double y, final double z,
		                            final double normalX, final double normalY, final double normalZ,
		                            final List<Bone> bones) {
			return vertex(new Vec3(x, y, z), new Vec3(normalX, normalY, normalZ), null);
		}

		@Override
		public void triangleFinished() {
			if (previousVertices.size() > 1) {
				final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
				final Point point = previousVertices.get(0);
				graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
			}
		}

	}
}
