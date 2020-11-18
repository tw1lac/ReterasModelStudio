package com.hiveworkshop.wc3.gui.modeledit.newstuff.uv.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.VertexFilter;
import com.hiveworkshop.wc3.gui.modeledit.viewport.ViewportView;
import com.hiveworkshop.wc3.mdl.Bone;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.GeosetAnim;
import com.hiveworkshop.wc3.mdl.GeosetVertex;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.Triangle;
import com.hiveworkshop.wc3.mdl.Vertex;
import com.hiveworkshop.wc3.mdl.renderer.GeosetRenderer;
import com.hiveworkshop.wc3.mdl.renderer.TriangleRenderer;
import com.hiveworkshop.wc3.mdl.v2.MaterialView;
import com.hiveworkshop.wc3.mdl.v2.ModelView;
import com.hiveworkshop.wc3.mdl.v2.visitor.MeshVisitor;
import com.hiveworkshop.wc3.mdl.v2.visitor.VertexVisitor;

public class UVViewportModelRenderer implements MeshVisitor {
	private Graphics2D graphics;
	private ProgramPreferences programPreferences;
	private final GeosetRendererImpl geosetRenderer;
    private CoordinateSystem coordinateSystem;
	// TODO Now that I added modelView to this class, why does
	// RenderByViewModelRenderer exist???
	private ModelView modelView;
	private int uvLayerIndex;

	public UVViewportModelRenderer() {
		geosetRenderer = new GeosetRendererImpl();
	}

	public UVViewportModelRenderer reset(final Graphics2D graphics, final ProgramPreferences programPreferences,
			final ViewportView viewportView, final CoordinateSystem coordinateSystem, final ModelView modelView) {
		this.graphics = graphics;
		this.programPreferences = programPreferences;
        this.coordinateSystem = coordinateSystem;
		this.modelView = modelView;
		return this;
	}

	@Override
	public GeosetRenderer beginGeoset(final int geosetId, final MaterialView material, final GeosetAnim geosetAnim) {
		graphics.setColor(programPreferences.getTriangleColor());
		if (modelView.getHighlightedGeoset() == modelView.getModel().getGeoset(geosetId)) {
			graphics.setColor(programPreferences.getHighlighTriangleColor());
		}
		return geosetRenderer.reset();
	}

	private final class GeosetRendererImpl implements GeosetRenderer {
		private final TriangleRendererImpl triangleRenderer = new TriangleRendererImpl();

		public GeosetRendererImpl reset() {
			return this;
		}

		@Override
		public TriangleRenderer beginTriangle() {
			return triangleRenderer.reset();
		}

		@Override
		public void geosetFinished() {
		}

	}

	private final class TriangleRendererImpl implements TriangleRenderer {
		private final VertexRendererImpl vertexRenderer = new VertexRendererImpl();
		private final List<Point> previousVertices = new ArrayList<>();

		public TriangleRendererImpl reset() {
			previousVertices.clear();
			return this;
		}

		@Override
		public VertexVisitor vertex(final double x, final double y, final double z, final double normalX,
				final double normalY, final double normalZ, final List<Bone> bones) {
			return vertexRenderer.reset();
		}

		@Override
		public VertexVisitor hdVertex(final double x, final double y, final double z, final double normalX,
				final double normalY, final double normalZ, final Bone[] skinBones, final short[] skinBoneWeights) {
			return vertexRenderer.reset();
		}

		@Override
		public void triangleFinished() {
			if (previousVertices.size() > 1) {
				final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
				final Point point = previousVertices.get(0);
				graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
			}
		}

		private final class VertexRendererImpl implements VertexVisitor {
			private int index = 0;

			public VertexRendererImpl reset() {
				this.index = 0;
				return this;
			}

			@Override
			public void textureCoords(final double u, final double v) {
				if (index == uvLayerIndex) {
					final Point point = new Point((int) coordinateSystem.convertX(u),
							(int) coordinateSystem.convertY(v));
					if (previousVertices.size() > 0) {
						final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
						graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
					}
					previousVertices.add(point);
					// graphics.setColor(programPreferences.getVertexColor());
					// graphics.fillRect((int) firstCoord - vertexSize / 2, (int)
					// secondCoord - vertexSize / 2, vertexSize,
					// vertexSize);
				}
				index++;
			}

			@Override
			public void vertexFinished() {
			}

		}
	}

	/**
	 * Copied directly from MDLDisplay and then made static.
	 *
	 * @param model
	 * @param g
	 * @param bounds
	 * @param a
	 * @param b
	 * @param filter
	 * @param extraHighlightPoint
	 */
	public static void drawFittedTriangles(final EditableModel model, final Graphics g, final Rectangle bounds, final byte a,
			final byte b, final VertexFilter<? super GeosetVertex> filter, final Vertex extraHighlightPoint) {
		final List<Triangle> triangles = new ArrayList<>();
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		g.setColor(Color.GRAY);
		for (final Geoset geo : model.getGeosets()) {
			for (final Triangle t : geo.getTriangles()) {
				boolean drawTriangle = false;
				for (final GeosetVertex vertex : t.getVerts()) {
					if (filter.isAccepted(vertex)) {
						drawTriangle = true;
					}
				}
				if (drawTriangle) {
					triangles.add(t);
				}
				final double[] x = t.getCoords(a);
				for (final double xval : x) {
					if (xval < minX) {
						minX = xval;
					}
					if (xval > maxX) {
						maxX = xval;
					}
				}
				final double[] y = t.getCoords(b);
				for (final double yval : y) {
					final double yCoord = -yval;
					if (yCoord < minY) {
						minY = yCoord;
					}
					if (yCoord > maxY) {
						maxY = yCoord;
					}
				}
			}
		}
		final double deltaX = maxX - minX;
		final double deltaY = maxY - minY;
		final double boxSize = Math.max(deltaX, deltaY);
		minX -= (boxSize - deltaX) / 2;
		minY -= (boxSize - deltaY) / 2;
		final AffineTransform transform = ((Graphics2D) g).getTransform();
		((Graphics2D) g).scale(bounds.getWidth() / boxSize, bounds.getHeight() / boxSize);
		((Graphics2D) g).translate(-minX, -minY);
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
			final int x = (int) extraHighlightPoint.getCoord(a);
			final int y = (int) -extraHighlightPoint.getCoord(b);
			((Graphics2D) g).drawOval(x - 5, y - 5, 10, 10);
			((Graphics2D) g).drawLine(x, y - 10, x, y + 10);
			((Graphics2D) g).drawLine(x - 10, y, x + 10, y);
		}
		((Graphics2D) g).setTransform(transform);
	}

	/**
	 * Copied directly from MDLDisplay and then made static.
	 *
	 * @param g
	 * @param a
	 * @param b
	 * @param t
	 */
	private static void drawTriangle(final Graphics g, final byte a, final byte b, final Triangle t) {
		final double[] x = t.getCoords(a);
		final double[] y = t.getCoords(b);
		final int[] xint = new int[4];
		final int[] yint = new int[4];
		for (int ix = 0; ix < 3; ix++) {
			xint[ix] = (int) Math.round(x[ix]);
			yint[ix] = (int) Math.round(-y[ix]);
		}
		xint[3] = xint[0];
		yint[3] = yint[0];
		g.drawPolyline(xint, yint, 4);
	}

}
