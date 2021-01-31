package com.hiveworkshop.rms.ui.application.edit.mesh.widgets;

import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;

public final class MoverWidget {
	private static final int TRIANGLE_OFFSET = 60 - 16;
	private final Vec3 point;
	private MoveDirection moveDirection = MoveDirection.NONE;
	private final Polygon northTriangle;
	private final Polygon eastTriangle;
	MoveDimension moveDimension = MoveDimension.NONE;

	public MoverWidget(final Vec3 point) {
		this.point = new Vec3(0, 0, 0);
		this.point.set(point);
		northTriangle = new Polygon();
		northTriangle.addPoint(-5, 0);
		northTriangle.addPoint(0, -18);
		northTriangle.addPoint(5, 0);

		eastTriangle = new Polygon();
		eastTriangle.addPoint(0, -5);
		eastTriangle.addPoint(18, 0);
		eastTriangle.addPoint(0, 5);
	}

	private long debugPrintLimiter;
	public MoveDirection getDirectionByMouse(final Point mousePoint, final CoordinateSystem coordinateSystem,  final byte dim1, final byte dim2) {
		getDirectionByMouse2(mousePoint, coordinateSystem, dim1, dim2);
		final double x = coordinateSystem.convertX(point.getCoord(dim1));
		final double y = coordinateSystem.convertY(point.getCoord(dim2));
		long currentTime = System.currentTimeMillis();
		if(debugPrintLimiter < currentTime){
			debugPrintLimiter = currentTime + 500;
			System.out.println("d1: "  + dim1 + ", d2: " + dim2);
		}
		eastTriangle.translate((int) x + TRIANGLE_OFFSET, (int) y);
		northTriangle.translate((int) x, (int) y - TRIANGLE_OFFSET);
		MoveDirection direction = MoveDirection.NONE;
		if (northTriangle.contains(mousePoint)
				|| (Math.abs(x - mousePoint.getX()) <= 1
				&& mousePoint.y < y
				&& mousePoint.y > y - TRIANGLE_OFFSET)) {
			direction = MoveDirection.UP;
		}
		if (eastTriangle.contains(mousePoint)
				|| (Math.abs(y - mousePoint.getY()) <= 1
				&& mousePoint.x > x
				&& mousePoint.x < x + TRIANGLE_OFFSET)) {
			direction = MoveDirection.RIGHT;
		}
		if (new Rectangle((int) x, (int) y - 20, 20, 20).contains(mousePoint)) {
			direction = MoveDirection.BOTH;
		}
		eastTriangle.translate(-((int) x + TRIANGLE_OFFSET), -((int) y));
		northTriangle.translate(-(int) x, -((int) y - TRIANGLE_OFFSET));
		return direction;
	}

	public MoveDimension getDirectionByMouse2(final Point mousePoint, final CoordinateSystem coordinateSystem,  final byte dim1, final byte dim2) {
		final double x = coordinateSystem.convertX(point.getCoord(dim1));
		final double y = coordinateSystem.convertY(point.getCoord(dim2));
		long currentTime = System.currentTimeMillis();
		if(debugPrintLimiter < currentTime){
			debugPrintLimiter = currentTime + 500;
			System.out.println("d1: "  + dim1 + ", d2: " + dim2);
		}
		eastTriangle.translate((int) x + TRIANGLE_OFFSET, (int) y);
		northTriangle.translate((int) x, (int) y - TRIANGLE_OFFSET);
		MoveDimension direction = MoveDimension.NONE;
		if (northTriangle.contains(mousePoint)
				|| (Math.abs(x - mousePoint.getX()) <= 1
				&& mousePoint.y < y
				&& mousePoint.y > y - TRIANGLE_OFFSET)) {
			direction = MoveDimension.getByByte(dim2);
		}
		if (eastTriangle.contains(mousePoint)
				|| (Math.abs(y - mousePoint.getY()) <= 1
				&& mousePoint.x > x
				&& mousePoint.x < x + TRIANGLE_OFFSET)) {
			direction = MoveDimension.getByByte(dim1);
		}
		if (new Rectangle((int) x, (int) y - 20, 20, 20).contains(mousePoint)) {
			direction = MoveDimension.getByByte(dim1, dim2);
		}
		eastTriangle.translate(-((int) x + TRIANGLE_OFFSET), -((int) y));
		northTriangle.translate(-(int) x, -((int) y - TRIANGLE_OFFSET));

		moveDimension = direction;
		return direction;
	}

	public Vec3 getPoint() {
		return point;
	}

	public void setPoint(final Vec3 point) {
		this.point.set(point);
	}

	public MoveDirection getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(final MoveDirection moveDirection) {
		this.moveDirection = moveDirection;
	}

	public void render(final Graphics2D graphics, final CoordinateSystem coordinateSystem) {
		final byte xDimension = coordinateSystem.getPortFirstXYZ();
		final byte yDimension = coordinateSystem.getPortSecondXYZ();
		final double x = coordinateSystem.convertX(point.getCoord(xDimension));
		final double y = coordinateSystem.convertY(point.getCoord(yDimension));

		setHighLightableColor(graphics, yDimension, moveDimension);
		drawNorthArrow(graphics, (int) x, (int) y);
		setHighLightableColor(graphics, xDimension, moveDimension);
		drawEastArrow(graphics, (int) x, (int) y);
		setColorByDimension(graphics, xDimension);
		drawShortNorthLine(graphics, (int) x, (int) y);
		setColorByDimension(graphics, yDimension);
		drawShortEastLine(graphics, (int) x, (int) y);

		if(moveDimension.containDirection(xDimension) && moveDimension.containDirection(yDimension)){
			graphics.setColor(new Color(255, 255, 0, 70));
			graphics.fillRect((int) x, (int) y - 20, 20, 20);
		}

		if (moveDirection != null) {
//			switch (moveDirection) {
//				case BOTH -> {
//					graphics.setColor(new Color(255, 255, 0, 70));
//					graphics.fillRect((int) x, (int) y - 20, 20, 20);
//					graphics.setColor(new Color(255, 255, 0));
//					drawNorthArrow(graphics, (int) x, (int) y);
//					drawEastArrow(graphics, (int) x, (int) y);
//					drawShortNorthLine(graphics, (int) x, (int) y);
//					drawShortEastLine(graphics, (int) x, (int) y);
//					setColorByDimension(graphics, xDimension);
//					setColorByDimension(graphics, yDimension);
//				}
//				case UP -> {
//					graphics.setColor(new Color(255, 255, 0));
//					drawNorthArrow(graphics, (int) x, (int) y);
//					setColorByDimension(graphics, xDimension);
//					drawEastArrow(graphics, (int) x, (int) y);
//					drawShortNorthLine(graphics, (int) x, (int) y);
//					setColorByDimension(graphics, yDimension);
//					drawShortEastLine(graphics, (int) x, (int) y);
//				}
//				case RIGHT -> {
//					graphics.setColor(new Color(255, 255, 0));
//					drawEastArrow(graphics, (int) x, (int) y);
//					setColorByDimension(graphics, xDimension);
//					drawShortNorthLine(graphics, (int) x, (int) y);
//					setColorByDimension(graphics, yDimension);
//					drawShortEastLine(graphics, (int) x, (int) y);
//					drawNorthArrow(graphics, (int) x, (int) y);
//				}
//				case NONE -> {
//					setColorByDimension(graphics, xDimension);
//					drawEastArrow(graphics, (int) x, (int) y);
//					drawShortNorthLine(graphics, (int) x, (int) y);
//					setColorByDimension(graphics, yDimension);
//					drawShortEastLine(graphics, (int) x, (int) y);
//					drawNorthArrow(graphics, (int) x, (int) y);
//				}
//			}
		} else {
			System.out.println("UGG");
		}
	}

	public void drawEastArrow(Graphics2D graphics, int x, int y) {
		drawLongEatsLine(graphics, x, y);
		drawEastTriangle(graphics, x, y);
	}

	public void drawNorthArrow(Graphics2D graphics, int x, int y) {
		drawLongNorthLine(graphics, x, y);
		drawNorthTriangle(graphics, x, y);
	}

	private void drawShortEastLine(Graphics2D graphics, int x, int y) {
		graphics.drawLine(x, y - 20, x + 20, y - 20);
	}

	private void drawShortNorthLine(Graphics2D graphics, int x, int y) {
		graphics.drawLine(x + 20, y, x + 20, y - 20);
	}

	private void drawLongEatsLine(Graphics2D graphics, int x, int y) {
//		graphics.drawLine(x + 15, y, x + 60, y);
		graphics.drawLine(x, y, x + 60, y);
	}

	private void drawLongNorthLine(Graphics2D graphics, int x, int y) {
//		graphics.drawLine(x, y - 15, x, y - 60);
		graphics.drawLine(x, y, x, y - 60);
	}

	private void drawEastTriangle(Graphics2D graphics, int x, int y) {
		eastTriangle.translate(x + TRIANGLE_OFFSET, y);
		graphics.fill(eastTriangle);
		eastTriangle.translate(-(x + TRIANGLE_OFFSET), -y);
	}

	private void drawNorthTriangle(Graphics2D graphics, int x, int y) {
		northTriangle.translate(x, y - TRIANGLE_OFFSET);
		graphics.fill(northTriangle);
		northTriangle.translate(-x, -(y - TRIANGLE_OFFSET));
	}

	private void setColorByDimension(final Graphics2D graphics, final byte dimension) {
		switch (dimension) {
			case 0, -1 -> graphics.setColor(new Color(0, 255, 0));
			case 1, -2 -> graphics.setColor(new Color(255, 0, 0));
			case 2, -3 -> graphics.setColor(new Color(0, 0, 255));
		}
	}

	private void setHighLightableColor(final Graphics2D graphics, final byte dimension, MoveDimension moveDimension){
//		System.out.println(moveDimension + " has " + MoveDimension.getByByte(dimension) + "?");
		if (moveDimension.containDirection(dimension)){
			graphics.setColor(new Color(255, 255, 0));
		} else {
			setColorByDimension(graphics, dimension);
		}
	}

	public enum MoveDirection {
		UP, RIGHT, BOTH, NONE
	}
	public enum MoveDimension {
		NONE, X, Y, XY, Z, XZ, YZ, XYZ;
		static MoveDimension getByByte(byte dimension){
			return switch (dimension) {
				case 0, -1 -> MoveDimension.X;
				case 1, -2 -> MoveDimension.Y;
				case 2, -3 -> MoveDimension.Z;
				default -> MoveDimension.NONE;
			};
		}
		private static MoveDimension getByByte(final byte dimension, final byte dimension2) {
			MoveDimension d1 = getByByte (dimension);
			MoveDimension d2 = getByByte (dimension2);
			return MoveDimension.values()[d1.ordinal() | d2.ordinal()];
		}
//		boolean containDirection(byte dim){
//			return this.name().contains(getByByte(dim).name());
//		}
		boolean containDirection(byte dim){
//			MoveDimension dirDim = getByByte(dim);
//			return (this.ordinal() & dirDim.ordinal()) > 0;
			return (this.ordinal() & getByByte(dim).ordinal()) > 0;
		}
	}

	private MoveDimension getDimension(final byte dimension) {
		return switch (dimension) {
			case 0, -1 -> MoveDimension.X;
			case 1, -2 -> MoveDimension.Y;
			case 2, -3 -> MoveDimension.Z;
			default -> MoveDimension.NONE;
		};
	}

	private MoveDimension getDimension(final byte dimension, final byte dimension2) {
		return switch (dimension) {
			case 0, -1 -> MoveDimension.X;
			case 1, -2 -> MoveDimension.Y;
			case 2, -3 -> MoveDimension.Z;
			default -> MoveDimension.NONE;
		};
	}
}
