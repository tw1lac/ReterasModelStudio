package com.matrixeater.hacks.blessing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BorderedBox implements FrameHandle {
	private final Rectangle bounds;
	private final BufferedImage borderTexture;
	private final int borderBoxSize;

	public BorderedBox(final Rectangle bounds, final BufferedImage borderTexture) {
		this.bounds = bounds;
		this.borderTexture = borderTexture;
		borderBoxSize = borderTexture.getHeight();
	}

	public void setLocation(final int x, final int y) {
		bounds.x = x;
		bounds.y = y;
	}

	public int getBorderBoxSize() {
		return borderBoxSize;
	}

	@Override
	public void paint(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		final int borderVerticalRepeatCount = bounds.height / borderBoxSize;
		// first draw corners

		g2.drawImage(borderTexture, bounds.x, bounds.y, bounds.x + borderBoxSize, bounds.y + borderBoxSize,
				borderBoxSize * 4, 0, (borderBoxSize * 4) + borderBoxSize, borderBoxSize, null);
		g2.drawImage(borderTexture, bounds.x, (bounds.y + bounds.height) - borderBoxSize, bounds.x + borderBoxSize,
				bounds.y + bounds.height, borderBoxSize * 6, 0, (borderBoxSize * 6) + borderBoxSize, borderBoxSize,
				null);
		g2.drawImage(borderTexture, (bounds.x + bounds.width) - borderBoxSize, bounds.y, bounds.x + bounds.width,
				bounds.y + borderBoxSize, borderBoxSize * 5, 0, (borderBoxSize * 5) + borderBoxSize, borderBoxSize,
				null);
		g2.drawImage(borderTexture, (bounds.x + bounds.width) - borderBoxSize,
				(bounds.y + bounds.height) - borderBoxSize, bounds.x + bounds.width, bounds.y + bounds.height,
				borderBoxSize * 7, 0, (borderBoxSize * 7) + borderBoxSize, borderBoxSize, null);

		for (int i = 1; i < (borderVerticalRepeatCount - 1); i++) {
			g2.drawImage(borderTexture, bounds.x, bounds.y + (borderBoxSize * i), bounds.x + borderBoxSize,
					bounds.y + (borderBoxSize * i) + borderBoxSize, 0, 0, borderBoxSize, borderBoxSize, null);
			g2.drawImage(borderTexture, (bounds.x + bounds.width) - borderBoxSize, bounds.y + (borderBoxSize * i),
					(bounds.x + bounds.width), bounds.y + (borderBoxSize * i) + borderBoxSize, borderBoxSize, 0,
					borderBoxSize * 2, borderBoxSize, null);
		}
		final int heightRemainder = bounds.height % borderBoxSize;
		g2.drawImage(borderTexture, bounds.x, bounds.y + (borderBoxSize * (borderVerticalRepeatCount - 1)),
				bounds.x + borderBoxSize,
				bounds.y + (borderBoxSize * (borderVerticalRepeatCount - 1)) + heightRemainder, 0, 0, borderBoxSize,
				heightRemainder, null);
		g2.drawImage(borderTexture, (bounds.x + bounds.width) - borderBoxSize,
				bounds.y + (borderBoxSize * (borderVerticalRepeatCount - 1)), (bounds.x + bounds.width),
				bounds.y + (borderBoxSize * (borderVerticalRepeatCount - 1)) + heightRemainder, borderBoxSize, 0,
				borderBoxSize * 2, heightRemainder, null);

		final int borderHorizontalRepeatCount = bounds.width / borderBoxSize;
		double halfBorderBoxSize = borderBoxSize / 2.0;
		for (int i = 1; i < (borderHorizontalRepeatCount - 1); i++) {
			int xBBi = bounds.x + (borderBoxSize * i);
			int bYbH = bounds.y + bounds.height;
			g2.rotate(Math.PI / 2, xBBi + halfBorderBoxSize,
					bounds.y + halfBorderBoxSize);
			g2.drawImage(borderTexture, xBBi, bounds.y,
					borderBoxSize + xBBi, bounds.y + borderBoxSize, borderBoxSize * 2, 0,
					(borderBoxSize * 2) + borderBoxSize, borderBoxSize, null);
			g2.rotate(-Math.PI / 2, xBBi + halfBorderBoxSize,
					bounds.y + halfBorderBoxSize);
			g2.rotate(Math.PI / 2, xBBi + halfBorderBoxSize,
					((bYbH) - borderBoxSize) + halfBorderBoxSize);
			g2.drawImage(borderTexture, xBBi, (bYbH) - borderBoxSize,
					borderBoxSize + xBBi, bYbH, borderBoxSize * 3, 0,
					(borderBoxSize * 3) + borderBoxSize, borderBoxSize, null);
			g2.rotate(-Math.PI / 2, xBBi + halfBorderBoxSize,
					((bYbH) - borderBoxSize) + halfBorderBoxSize);
		}
	}

	@Override
	public String getTooltip() {
		return null;
	}

	@Override
	public int getCost() {
		return 0;
	}

	@Override
	public String getUbertip() {
		return null;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}
}
