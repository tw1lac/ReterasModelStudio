package com.hiveworkshop.wc3.gui.modeledit.newstuff.uv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modeledit.viewport.ResettableIdObjectRenderer;
import com.hiveworkshop.wc3.mdl.TVertex;

public final class Graphics2DToTVertexModelElementRendererAdapter implements TVertexModelElementRenderer {
	private Graphics2D graphics;
	private CoordinateSystem coordinateSystem;
	private final Point recyclePointA = new Point(), recyclePointB = new Point(), recyclePointC = new Point();
	private final int[] recycleXCoords = new int[3];
	private final int[] recycleYCoords = new int[3];
    private final int vertexSize;

    public Graphics2DToTVertexModelElementRendererAdapter(final int vertexSize,
			final ProgramPreferences programPreferences) {
		this.vertexSize = vertexSize;
        ResettableIdObjectRenderer idObjectRenderer = new ResettableIdObjectRenderer(vertexSize);
	}

	public Graphics2DToTVertexModelElementRendererAdapter reset(final Graphics2D graphics,
			final CoordinateSystem coordinateSystem) {
		this.graphics = graphics;
		this.coordinateSystem = coordinateSystem;
		return this;
	}

	@Override
	public void renderFace(final Color borderColor, final Color color, final TVertex a, final TVertex b,
			final TVertex c) {
		graphics.setColor(color);
		CoordinateSystem.Util.convertToPoint(coordinateSystem, a, recyclePointA);
		CoordinateSystem.Util.convertToPoint(coordinateSystem, b, recyclePointB);
		CoordinateSystem.Util.convertToPoint(coordinateSystem, c, recyclePointC);
		recycleXCoords[0] = recyclePointA.x;
		recycleXCoords[1] = recyclePointB.x;
		recycleXCoords[2] = recyclePointC.x;
		recycleYCoords[0] = recyclePointA.y;
		recycleYCoords[1] = recyclePointB.y;
		recycleYCoords[2] = recyclePointC.y;
		graphics.fillPolygon(recycleXCoords, recycleYCoords, 3);
		graphics.setColor(borderColor);
		graphics.drawPolygon(recycleXCoords, recycleYCoords, 3);
	}

	@Override
	public void renderVertex(final Color color, final TVertex vertex) {
		CoordinateSystem.Util.convertToPoint(coordinateSystem, vertex, recyclePointA);
		graphics.setColor(color);
		graphics.fillRect(recyclePointA.x - (vertexSize / 2), (int) (recyclePointA.y - (vertexSize / 2.0)), vertexSize,
				vertexSize);
	}

}
