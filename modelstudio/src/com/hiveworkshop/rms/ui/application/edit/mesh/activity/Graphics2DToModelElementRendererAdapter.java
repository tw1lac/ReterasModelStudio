package com.hiveworkshop.rms.ui.application.edit.mesh.activity;

import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelElementRenderer;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.NodeIconPalette;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.ResettableIdObjectRenderer;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;

public final class Graphics2DToModelElementRendererAdapter implements ModelElementRenderer {
	private Graphics2D graphics;
	private CoordinateSystem coordinateSystem;
	private final Point recyclePointA = new Point(), recyclePointB = new Point(), recyclePointC = new Point();
	private final int[] recycleXCoords = new int[3];
	private final int[] recycleYCoords = new int[3];
	private final ProgramPreferences programPreferences;
	private final int vertexSize;
	private final ResettableIdObjectRenderer idObjectRenderer;

	public Graphics2DToModelElementRendererAdapter(final int vertexSize, final ProgramPreferences programPreferences) {
		this.vertexSize = vertexSize;
		this.programPreferences = programPreferences;
		idObjectRenderer = new ResettableIdObjectRenderer(vertexSize);
	}

	public Graphics2DToModelElementRendererAdapter reset(final Graphics2D graphics,
			final CoordinateSystem coordinateSystem) {
		this.graphics = graphics;
		this.coordinateSystem = coordinateSystem;
		return this;
	}

	@Override
	public void renderFace(final Color borderColor, final Color color, final GeosetVertex a, final GeosetVertex b,
			final GeosetVertex c) {
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
	public void renderVertex(final Color color, final Vec3 vertex) {
		CoordinateSystem.Util.convertToPoint(coordinateSystem, vertex, recyclePointA);
		graphics.setColor(color);
		graphics.fillRect(recyclePointA.x - vertexSize / 2, (int) (recyclePointA.y - (vertexSize / 2.0)), vertexSize,
				vertexSize);
	}

	@Override
	public void renderIdObject(final IdObject object, final NodeIconPalette nodeIconPalette, final Color lightColor,
                               final Color pivotPointColor) {
		object.apply(idObjectRenderer.reset(coordinateSystem, graphics, lightColor, pivotPointColor, nodeIconPalette,
				programPreferences.isUseBoxesForPivotPoints()));
	}

	@Override
	public void renderCamera(final Camera camera, final Color boxColor, final Vec3 position, final Color targetColor,
			final Vec3 targetPosition) {
		final Graphics2D g2 = ((Graphics2D) graphics.create());
		// final boolean verSel = selection.contains(ver);
		// final boolean tarSel = selection.contains(targ);
		final Point start = new Point(
				(int) Math.round(coordinateSystem.convertX(position.getCoord(coordinateSystem.getPortFirstXYZ()))),
				(int) Math.round(coordinateSystem.convertY(position.getCoord(coordinateSystem.getPortSecondXYZ()))));
		final Point end = new Point(
				(int) Math.round(coordinateSystem.convertX(targetPosition.getCoord(coordinateSystem.getPortFirstXYZ()))),
				(int) Math.round(coordinateSystem.convertY(targetPosition.getCoord(coordinateSystem.getPortSecondXYZ()))));
		// if (dispCameraNames) {
		// boolean changedCol = false;
		//
		// if (verSel) {
		// g2.setColor(Color.orange.darker());
		// changedCol = true;
		// }
		// g2.drawString(cam.getName(), (int)
		// Math.round(vp.convertX(ver.getCoord(vp.getPortFirstXYZ()))),
		// (int) Math.round(vp.convertY(ver.getCoord(vp.getPortSecondXYZ()))));
		// if (tarSel) {
		// g2.setColor(Color.orange.darker());
		// changedCol = true;
		// } else if (verSel) {
		// g2.setColor(Color.green.darker());
		// changedCol = false;
		// }
		// g2.drawString(cam.getName() + "_target",
		// (int) Math.round(vp.convertX(targ.getCoord(vp.getPortFirstXYZ()))),
		// (int) Math.round(vp.convertY(targ.getCoord(vp.getPortSecondXYZ()))));
		// if (changedCol) {
		// g2.setColor(Color.green.darker());
        // }
        // }

        g2.translate(end.x, end.y);
        g2.rotate(-(Math.PI / 2 + Math.atan2(end.x - start.x, end.y - start.y)));
        final double zoom = CoordinateSystem.Util.getZoom(coordinateSystem);
        final int size = (int) (20 * zoom);
        final double dist = start.distance(end);

        g2.setColor(boxColor);
        g2.fillRect((int) dist - vertexSize, -vertexSize, 1 + vertexSize * 2, 1 + vertexSize * 2);
        g2.drawRect((int) dist - size, -size, size * 2, size * 2);

        // if (tarSel) {
        // g2.setColor(Color.orange.darker());
        // } else if (verSel) {
        g2.setColor(targetColor);
        // }
        // Target
        g2.fillRect(-vertexSize, -vertexSize, 1 + vertexSize * 2, 1 + vertexSize * 2);
        g2.drawLine(0, 0, size, size);// (int)Math.round(vp.convertX(targ.getCoord(vp.getPortFirstXYZ())+5)),
        // (int)Math.round(vp.convertY(targ.getCoord(vp.getPortSecondXYZ())+5)));
        g2.drawLine(0, 0, size, -size);// (int)Math.round(vp.convertX(targ.getCoord(vp.getPortFirstXYZ())-5)),
        // (int)Math.round(vp.convertY(targ.getCoord(vp.getPortSecondXYZ())-5)));

        // if (!verSel && tarSel) {
        // g2.setColor(Color.green.darker());
        // }
        g2.drawLine(0, 0, (int) dist, 0);
    }

}
