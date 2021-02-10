package com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.visitor.IdObjectVisitor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxCollisionShape;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.NodeIconPalette;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;
import java.util.List;

public final class ResettableIdObjectRenderer implements IdObjectVisitor {
	private CoordinateSystem coordinateSystem;
	private Graphics2D graphics;
	private final int vertexSize;
	private Color lightColor;
	private Color pivotPointColor;
	private NodeIconPalette nodeIconPalette;
	private boolean crosshairIsBox;

	public ResettableIdObjectRenderer(final int vertexSize) {
		this.vertexSize = vertexSize;
	}

	public ResettableIdObjectRenderer reset(final CoordinateSystem coordinateSystem, final Graphics2D graphics,
	                                        final Color lightColor, final Color pivotPointColor,
	                                        final NodeIconPalette nodeIconPalette, final boolean crosshairIsBox) {
		this.coordinateSystem = coordinateSystem;
		this.graphics = graphics;
		this.lightColor = lightColor;
		this.pivotPointColor = pivotPointColor;
		this.nodeIconPalette = nodeIconPalette;
		this.crosshairIsBox = crosshairIsBox;
		return this;
	}

	@Override
	public void bone(final Bone object) {
		graphics.setColor(pivotPointColor);
		drawCrosshair(object);
	}

	public static void drawNodeImage(final Graphics2D graphics,
	                                 final byte xDimension, final byte yDimension,
	                                 final CoordinateSystem coordinateSystem,
	                                 final IdObject attachment, final Image nodeImage) {
		final int xCoord = (int) coordinateSystem.convertX(attachment.getPivotPoint().getCoord(xDimension));
		final int yCoord = (int) coordinateSystem.convertY(attachment.getPivotPoint().getCoord(yDimension));

		graphics.drawImage(nodeImage, xCoord - (nodeImage.getWidth(null) / 2), yCoord - (nodeImage.getHeight(null) / 2), nodeImage.getWidth(null), nodeImage.getHeight(null), null);
	}

	private void drawCrosshair(final Bone object) {
		drawCrosshair(object.getPivotPoint());
	}

	private void drawCrosshair(final Vec3 pivotPoint) {
		if (crosshairIsBox) {
			drawBox(graphics, coordinateSystem, vertexSize, pivotPoint);
		} else {
			drawCrosshair(graphics, coordinateSystem, vertexSize, pivotPoint);
		}
	}

	@Override
	public void helper(final Helper object) {
		graphics.setColor(pivotPointColor.darker());
		drawCrosshair(object);
	}

	@Override
	public void attachment(final Attachment attachment) {
		drawNodeImage(attachment, nodeIconPalette.getAttachmentImage());
	}

	@Override
	public void particleEmitter(final ParticleEmitter particleEmitter) {
		drawNodeImage(particleEmitter, nodeIconPalette.getParticleImage());
	}

	@Override
	public void popcornFxEmitter(final ParticleEmitterPopcorn popcornFxEmitter) {
		drawNodeImage(popcornFxEmitter, nodeIconPalette.getParticleImage());
	}

	@Override
	public void particleEmitter2(final ParticleEmitter2 particleEmitter) {
		drawNodeImage(particleEmitter, nodeIconPalette.getParticle2Image());
	}

	@Override
	public void ribbonEmitter(final RibbonEmitter ribbonEmitter) {
		drawNodeImage(ribbonEmitter, nodeIconPalette.getRibbonImage());
	}

	@Override
	public void eventObject(final EventObject eventObject) {
		drawNodeImage(eventObject, nodeIconPalette.getEventImage());
	}

	@Override
	public void collisionShape(final CollisionShape collisionShape) {
		drawCollisionShape(graphics, pivotPointColor, coordinateSystem, coordinateSystem.getPortFirstXYZ(), coordinateSystem.getPortSecondXYZ(), vertexSize, collisionShape, nodeIconPalette.getCollisionImage(), crosshairIsBox);
	}

	@Override
	public void camera(final Camera camera) {
		graphics.setColor(Color.GREEN.darker());
		final Graphics2D g2 = ((Graphics2D) graphics.create());
		final Vec3 ver = camera.getPosition();
		final Vec3 targ = camera.getTargetPosition();
		// final boolean verSel = selection.contains(ver);
		// final boolean tarSel = selection.contains(targ);
		final Point start = new Point(
				(int) Math.round(coordinateSystem.convertX(ver.getCoord(coordinateSystem.getPortFirstXYZ()))),
				(int) Math.round(coordinateSystem.convertY(ver.getCoord(coordinateSystem.getPortSecondXYZ()))));
		final Point end = new Point(
				(int) Math.round(coordinateSystem.convertX(targ.getCoord(coordinateSystem.getPortFirstXYZ()))),
				(int) Math.round(coordinateSystem.convertY(targ.getCoord(coordinateSystem.getPortSecondXYZ()))));
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
		g2.rotate(-((Math.PI / 2) + Math.atan2(end.x - start.x, end.y - start.y)));
		final double zoom = CoordinateSystem.Util.getZoom(coordinateSystem);
		final int size = (int) (20 * zoom);
		final double dist = start.distance(end);

		// if (verSel) {
		// g2.setColor(Color.orange.darker());
		// }
		// Cam
		g2.fillRect((int) dist - vertexSize, 0 - vertexSize, 1 + (vertexSize * 2), 1 + (vertexSize * 2));
		g2.drawRect((int) dist - size, -size, size * 2, size * 2);

		// if (tarSel) {
		// g2.setColor(Color.orange.darker());
		// } else if (verSel) {
		// g2.setColor(Color.green.darker());
		// }
		// Target
		g2.fillRect(0 - vertexSize, 0 - vertexSize, 1 + (vertexSize * 2), 1 + (vertexSize * 2));
		g2.drawLine(0, 0, size, size);// (int)Math.round(vp.convertX(targ.getCoord(vp.getPortFirstXYZ())+5)),
										// (int)Math.round(vp.convertY(targ.getCoord(vp.getPortSecondXYZ())+5)));
		g2.drawLine(0, 0, size, -size);// (int)Math.round(vp.convertX(targ.getCoord(vp.getPortFirstXYZ())-5)),
										// (int)Math.round(vp.convertY(targ.getCoord(vp.getPortSecondXYZ())-5)));

		// if (!verSel && tarSel) {
		// g2.setColor(Color.green.darker());
		// }
		g2.drawLine(0, 0, (int) dist, 0);
	}

	private void drawNodeImage(final IdObject attachment, final Image nodeImage) {
		drawNodeImage(graphics, coordinateSystem.getPortFirstXYZ(), coordinateSystem.getPortSecondXYZ(), coordinateSystem, attachment, nodeImage);
	}

	public static void drawCrosshair(final Graphics2D graphics,
	                                 final CoordinateSystem coordinateSystem,
	                                 final int vertexSize, final Vec3 pivotPoint) {
		final int xCoord = (int) coordinateSystem.convertX(pivotPoint.getCoord(coordinateSystem.getPortFirstXYZ()));
		final int yCoord = (int) coordinateSystem.convertY(pivotPoint.getCoord(coordinateSystem.getPortSecondXYZ()));

		graphics.drawOval(xCoord - vertexSize, yCoord - vertexSize, vertexSize * 2, vertexSize * 2);
		graphics.drawLine(xCoord - (int) (vertexSize * 1.5f), yCoord, xCoord + (int) (vertexSize * 1.5f), yCoord);
		graphics.drawLine(xCoord, yCoord - (int) (vertexSize * 1.5f), xCoord, yCoord + (int) (vertexSize * 1.5f));
	}

	public static void drawBox(final Graphics2D graphics,
	                           final CoordinateSystem coordinateSystem,
	                           int vertexSize, final Vec3 pivotPoint) {
		vertexSize *= 3;

		final int xCoord = (int) coordinateSystem.convertX(pivotPoint.getCoord(coordinateSystem.getPortFirstXYZ()));
		final int yCoord = (int) coordinateSystem.convertY(pivotPoint.getCoord(coordinateSystem.getPortSecondXYZ()));
		graphics.fillRect(xCoord - vertexSize, yCoord - vertexSize, vertexSize * 2, vertexSize * 2);
	}

	public static void drawCollisionShape(final Graphics2D graphics, final Color color,
	                                      final CoordinateSystem coordinateSystem,
	                                      final byte xDimension, final byte yDimension, final int vertexSize,
	                                      final CollisionShape collisionShape, final Image collisionImage,
	                                      final boolean crosshairIsBox) {
		final Vec3 pivotPoint = collisionShape.getPivotPoint();
		final List<Vec3> vertices = collisionShape.getVertices();
		graphics.setColor(color);
		final int xCoord = (int) coordinateSystem.convertX(pivotPoint.getCoord(xDimension));
		final int yCoord = (int) coordinateSystem.convertY(pivotPoint.getCoord(yDimension));

		if (collisionShape.getType() == MdlxCollisionShape.Type.BOX) {
			if (vertices.size() > 1) {
				final Vec3 vertex = vertices.get(0);
				final Vec3 vertex2 = vertices.get(1);

				final int firstXCoord = (int) coordinateSystem.convertX(vertex2.getCoord(xDimension));
				final int firstYCoord = (int) coordinateSystem.convertY(vertex2.getCoord(yDimension));
				final int secondXCoord = (int) coordinateSystem.convertX(vertex.getCoord(xDimension));
				final int secondYCoord = (int) coordinateSystem.convertY(vertex.getCoord(yDimension));

				final int minXCoord = Math.min(firstXCoord, secondXCoord);
				final int minYCoord = Math.min(firstYCoord, secondYCoord);
				final int maxXCoord = Math.max(firstXCoord, secondXCoord);
				final int maxYCoord = Math.max(firstYCoord, secondYCoord);

				graphics.drawRoundRect(minXCoord, minYCoord, maxXCoord - minXCoord, maxYCoord - minYCoord, vertexSize, vertexSize);
			}
		} else {
			if (collisionShape.getExtents() != null) {
				final double zoom = CoordinateSystem.Util.getZoom(coordinateSystem);
				final double boundsRadius = collisionShape.getExtents().getBoundsRadius() * zoom;
				graphics.drawOval((int) (xCoord - boundsRadius), (int) (yCoord - boundsRadius), (int) (boundsRadius * 2), (int) (boundsRadius * 2));
			}
		}
		drawNodeImage(graphics, xDimension, yDimension, coordinateSystem, collisionShape, collisionImage);

		for (final Vec3 vertex : vertices) {
			if (crosshairIsBox) {
				drawBox(graphics, coordinateSystem, vertexSize, vertex);
			} else {
				drawCrosshair(graphics, coordinateSystem, vertexSize, vertex);
			}
		}
	}

	@Override
	public void light(final Light light) {
		final Image lightImage = nodeIconPalette.getLightImage();
		graphics.setColor(lightColor);
		final int xCoord = (int) coordinateSystem.convertX(light.getPivotPoint().getCoord(coordinateSystem.getPortFirstXYZ()));
		final int yCoord = (int) coordinateSystem.convertY(light.getPivotPoint().getCoord(coordinateSystem.getPortSecondXYZ()));
		final double zoom = CoordinateSystem.Util.getZoom(coordinateSystem);
		// graphics.drawOval(xCoord - vertexSize * 2, yCoord - vertexSize * 2,
		// vertexSize * 4, vertexSize * 4);
		// graphics.setColor(programPreferences.getAmbientLightColor());
		// graphics.drawLine(xCoord - vertexSize * 3, yCoord, xCoord +
		// vertexSize * 3, yCoord);
		// graphics.drawLine(xCoord, yCoord - vertexSize * 3, xCoord, yCoord +
		// vertexSize * 3);
		graphics.drawImage(lightImage, xCoord - (lightImage.getWidth(null) / 2), yCoord - (lightImage.getHeight(null) / 2), lightImage.getWidth(null), lightImage.getHeight(null), null);

		final int attenuationStart = (int) (light.getAttenuationStart() * zoom);
		if (attenuationStart > 0) {
			graphics.drawOval(xCoord - attenuationStart, yCoord - attenuationStart, attenuationStart * 2, attenuationStart * 2);
		}
		final int attenuationEnd = (int) (light.getAttenuationEnd() * zoom);
		if (attenuationEnd > 0) {
			graphics.drawOval(xCoord - attenuationEnd, yCoord - attenuationEnd, attenuationEnd * 2, attenuationEnd * 2);
		}
	}
}