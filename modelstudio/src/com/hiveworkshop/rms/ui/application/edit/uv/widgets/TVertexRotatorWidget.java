package com.hiveworkshop.rms.ui.application.edit.uv.widgets;

import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.util.Vec2;

import java.awt.*;

public final class TVertexRotatorWidget {
	private static final int ROTATOR_RADIUS = 60;
	private static final int ROTATOR_RADIUS_SQUARED = ROTATOR_RADIUS * ROTATOR_RADIUS;
	private final Vec2 point;
	private RotateDirection moveDirection = RotateDirection.NONE;

	public TVertexRotatorWidget(final Vec2 point) {
		this.point = new Vec2(0, 0);
		this.point.set(point);
	}

	public RotateDirection getDirectionByMouse(final Point mousePoint, final CoordinateSystem coordinateSystem) {
		final double x = coordinateSystem.convertX(point.getCoord(coordinateSystem.getPortFirstXYZ()));
		final double y = coordinateSystem.convertY(point.getCoord(coordinateSystem.getPortSecondXYZ()));

		final double deltaY = y - mousePoint.getY();
		final double deltaX = x - mousePoint.getX();
		if ((Math.abs(deltaX) <= 3) && (Math.abs(deltaY) <= ROTATOR_RADIUS)) {
			return RotateDirection.VERTICALLY;
		}
		if ((Math.abs(deltaX) <= ROTATOR_RADIUS) && (Math.abs(deltaY) <= 3)) {
			return RotateDirection.HORIZONTALLY;
		}
		final double dstSquared = (deltaY * deltaY) + (deltaX * deltaX);
		if (Math.abs(Math.sqrt(dstSquared) - ROTATOR_RADIUS) <= 3) {
			return RotateDirection.SPIN;
		}
		if (dstSquared < ROTATOR_RADIUS_SQUARED) {
			return RotateDirection.FREE;
		}

		return RotateDirection.NONE;
	}

	public Vec2 getPoint() {
		return point;
	}

	public void setPoint(final Vec2 point) {
		this.point.set(point);
	}

	public RotateDirection getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(final RotateDirection moveDirection) {
		this.moveDirection = moveDirection;
	}

	public void render(final Graphics2D graphics, final CoordinateSystem coordinateSystem) {
		final byte xDimension = coordinateSystem.getPortFirstXYZ();
		final byte yDimension = coordinateSystem.getPortSecondXYZ();
		final double x = coordinateSystem.convertX(point.getCoord(xDimension));
		final double y = coordinateSystem.convertY(point.getCoord(yDimension));
		if (moveDirection != null) {
			switch (moveDirection) {
				case FREE -> {
					graphics.setColor(new Color(0.5f, 0.5f, 0.5f, 0.4f));
					graphics.fillOval((int) (x - ROTATOR_RADIUS), (int) (y - ROTATOR_RADIUS), ROTATOR_RADIUS * 2,
							ROTATOR_RADIUS * 2);
					setColorByDimension(graphics, xDimension);
					graphics.drawLine((int) (x - ROTATOR_RADIUS), (int) y, (int) (x + ROTATOR_RADIUS), (int) y);
					setColorByDimension(graphics, yDimension);
					graphics.drawLine((int) x, (int) (y - ROTATOR_RADIUS), (int) x, (int) (y + ROTATOR_RADIUS));
					setColorByDimension(graphics, getOutwardDimension(xDimension, yDimension));
					graphics.drawOval((int) (x - ROTATOR_RADIUS), (int) (y - ROTATOR_RADIUS), ROTATOR_RADIUS * 2,
							ROTATOR_RADIUS * 2);
				}
				case SPIN -> {
					setColorByDimension(graphics, xDimension);
					graphics.drawLine((int) (x - ROTATOR_RADIUS), (int) y, (int) (x + ROTATOR_RADIUS), (int) y);
					setColorByDimension(graphics, yDimension);
					graphics.drawLine((int) (x), (int) (y - ROTATOR_RADIUS), (int) (x), (int) (y + ROTATOR_RADIUS));
					graphics.setColor(new Color(255, 255, 0));
					graphics.drawOval((int) (x - ROTATOR_RADIUS), (int) (y - ROTATOR_RADIUS), ROTATOR_RADIUS * 2,
							ROTATOR_RADIUS * 2);
				}
				case VERTICALLY -> {
					setColorByDimension(graphics, xDimension);
					graphics.drawLine((int) (x - ROTATOR_RADIUS), (int) y, (int) (x + ROTATOR_RADIUS), (int) y);
					graphics.setColor(new Color(255, 255, 0));
					graphics.drawLine((int) (x), (int) (y - ROTATOR_RADIUS), (int) (x), (int) (y + ROTATOR_RADIUS));
					setColorByDimension(graphics, getOutwardDimension(xDimension, yDimension));
					graphics.drawOval((int) (x - ROTATOR_RADIUS), (int) (y - ROTATOR_RADIUS), ROTATOR_RADIUS * 2,
							ROTATOR_RADIUS * 2);
				}
				case HORIZONTALLY -> {
					graphics.setColor(new Color(255, 255, 0));
					graphics.drawLine((int) (x - ROTATOR_RADIUS), (int) y, (int) (x + ROTATOR_RADIUS), (int) y);
					setColorByDimension(graphics, yDimension);
					graphics.drawLine((int) (x), (int) (y - ROTATOR_RADIUS), (int) (x), (int) (y + ROTATOR_RADIUS));
					setColorByDimension(graphics, getOutwardDimension(xDimension, yDimension));
					graphics.drawOval((int) (x - ROTATOR_RADIUS), (int) (y - ROTATOR_RADIUS), ROTATOR_RADIUS * 2,
							ROTATOR_RADIUS * 2);
				}
				case NONE -> {
					setColorByDimension(graphics, xDimension);
					graphics.drawLine((int) (x - ROTATOR_RADIUS), (int) y, (int) (x + ROTATOR_RADIUS), (int) y);
					setColorByDimension(graphics, yDimension);
					graphics.drawLine((int) (x), (int) (y - ROTATOR_RADIUS), (int) (x), (int) (y + ROTATOR_RADIUS));
					setColorByDimension(graphics, getOutwardDimension(xDimension, yDimension));
					graphics.drawOval((int) (x - ROTATOR_RADIUS), (int) (y - ROTATOR_RADIUS), ROTATOR_RADIUS * 2,
							ROTATOR_RADIUS * 2);
				}
			}
		}
	}

	private byte getOutwardDimension(final byte xDimension, final byte yDimension) {
		return (byte) (3 - xDimension - yDimension);
	}

	private void setColorByDimension(final Graphics2D graphics, final byte xDimension) {
		switch (xDimension) {
			case 0 -> graphics.setColor(new Color(0, 255, 0));
			case 1 -> graphics.setColor(new Color(255, 0, 0));
			case 2 -> graphics.setColor(new Color(0, 0, 255));
		}
	}

	public enum RotateDirection {
		VERTICALLY, HORIZONTALLY, SPIN, FREE, NONE
	}
}
