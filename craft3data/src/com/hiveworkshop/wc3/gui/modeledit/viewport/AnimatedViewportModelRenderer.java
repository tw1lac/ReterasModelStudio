package com.hiveworkshop.wc3.gui.modeledit.viewport;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.mdl.Attachment;
import com.hiveworkshop.wc3.mdl.Bone;
import com.hiveworkshop.wc3.mdl.Camera;
import com.hiveworkshop.wc3.mdl.CollisionShape;
import com.hiveworkshop.wc3.mdl.EventObject;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.GeosetAnim;
import com.hiveworkshop.wc3.mdl.Helper;
import com.hiveworkshop.wc3.mdl.IdObject;
import com.hiveworkshop.wc3.mdl.Light;
import com.hiveworkshop.wc3.mdl.ParticleEmitter;
import com.hiveworkshop.wc3.mdl.ParticleEmitter2;
import com.hiveworkshop.wc3.mdl.ParticleEmitterPopcorn;
import com.hiveworkshop.wc3.mdl.RibbonEmitter;
import com.hiveworkshop.wc3.mdl.render3d.RenderModel;
import com.hiveworkshop.wc3.mdl.renderer.GeosetRenderer;
import com.hiveworkshop.wc3.mdl.renderer.ModelRenderer;
import com.hiveworkshop.wc3.mdl.renderer.TriangleRenderer;
import com.hiveworkshop.wc3.mdl.v2.MaterialView;
import com.hiveworkshop.wc3.mdl.v2.ModelView;
import com.hiveworkshop.wc3.mdl.v2.visitor.VertexVisitor;

public class AnimatedViewportModelRenderer implements ModelRenderer {
	private Graphics2D graphics;
	private ProgramPreferences programPreferences;
	private final GeosetRendererImpl geosetRenderer;
    private byte xDimension;
	private byte yDimension;
    private CoordinateSystem coordinateSystem;
	private final ResettableAnimatedIdObjectRenderer idObjectRenderer;
	// TODO Now that I added modelView to this class, why does
	// RenderByViewModelRenderer exist???
	private ModelView modelView;
	private RenderModel renderModel;

	public AnimatedViewportModelRenderer(final int vertexSize) {
        geosetRenderer = new GeosetRendererImpl();
		idObjectRenderer = new ResettableAnimatedIdObjectRenderer(vertexSize);
	}

	public AnimatedViewportModelRenderer reset(final Graphics2D graphics, final ProgramPreferences programPreferences,
			final byte xDimension, final byte yDimension, final ViewportView viewportView,
			final CoordinateSystem coordinateSystem, final ModelView modelView, final RenderModel renderModel) {
		this.graphics = graphics;
		this.programPreferences = programPreferences;
		this.xDimension = xDimension;
		this.yDimension = yDimension;
        this.coordinateSystem = coordinateSystem;
		this.modelView = modelView;
		this.renderModel = renderModel;
		idObjectRenderer.reset(coordinateSystem, graphics, programPreferences.getLightsColor(),
				programPreferences.getAnimatedBoneUnselectedColor(), NodeIconPalette.UNSELECTED, renderModel,
				programPreferences.isUseBoxesForPivotPoints());
		return this;
	}

	@Override
	public GeosetRenderer beginGeoset(final int geosetId, final MaterialView material, final GeosetAnim geosetAnim) {
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
				modelView.getHighlightedNode() == object ? programPreferences.getHighlighVertexColor()
						: programPreferences.getLightsColor(),
				modelView.getHighlightedNode() == object ? programPreferences.getHighlighVertexColor()
						: programPreferences.getAnimatedBoneUnselectedColor(),
				modelView.getHighlightedNode() == object ? NodeIconPalette.HIGHLIGHT : NodeIconPalette.UNSELECTED,
				renderModel, programPreferences.isUseBoxesForPivotPoints());
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
			// TODO Auto-generated method stub

		}

	}

	private static final Vector4f vertexHeap = new Vector4f();
	private static final Vector4f appliedVertexHeap = new Vector4f();
	private static final Vector4f vertexSumHeap = new Vector4f();
	private static final Vector4f normalHeap = new Vector4f();
	private static final Vector4f appliedNormalHeap = new Vector4f();
	private static final Vector4f normalSumHeap = new Vector4f();
	private static final Matrix4f skinBonesMatrixHeap = new Matrix4f();
	private static final Matrix4f skinBonesMatrixSumHeap = new Matrix4f();

	private final class TriangleRendererImpl implements TriangleRenderer {
		private final List<Point> previousVertices = new ArrayList<>();

		public TriangleRendererImpl reset() {
			previousVertices.clear();
			return this;
		}

		@Override
		public VertexVisitor vertex(final double x, final double y, final double z, final double normalX,
				final double normalY, final double normalZ, final List<Bone> bones) {
			vertexHeap.x = (float) x;
			vertexHeap.y = (float) y;
			vertexHeap.z = (float) z;
			vertexHeap.w = 1;
			if (bones.size() > 0) {
				vertexSumHeap.set(0, 0, 0, 0);
				for (final Bone bone : bones) {
					Matrix4f.transform(renderModel.getRenderNode(bone).getWorldMatrix(), vertexHeap, appliedVertexHeap);
					Vector4f.add(vertexSumHeap, appliedVertexHeap, vertexSumHeap);
				}
				final int boneCount = bones.size();
				vertexSumHeap.x /= boneCount;
				vertexSumHeap.y /= boneCount;
				vertexSumHeap.z /= boneCount;
				vertexSumHeap.w /= boneCount;
			} else {
				vertexSumHeap.set(vertexHeap);
			}
			float firstCoord, secondCoord;
			switch (xDimension) {
			case 0:
				firstCoord = vertexSumHeap.x;
				break;
			case 1:
				firstCoord = vertexSumHeap.y;
				break;
			case 2:
				firstCoord = vertexSumHeap.z;
				break;
			default:
				throw new IllegalStateException("Invalid x dimension");
			}
			switch (yDimension) {
			case 0:
				secondCoord = vertexSumHeap.x;
				break;
			case 1:
				secondCoord = vertexSumHeap.y;
				break;
			case 2:
				secondCoord = vertexSumHeap.z;
				break;
			default:
				throw new IllegalStateException("Invalid y dimension");
			}
			final Point point = new Point((int) coordinateSystem.convertX(firstCoord),
					(int) coordinateSystem.convertY(secondCoord));
			if (previousVertices.size() > 0) {
				final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
				graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
			}
			previousVertices.add(point);
			// graphics.setColor(programPreferences.getVertexColor());
			// graphics.fillRect((int) firstCoord - vertexSize / 2, (int)
			// secondCoord - vertexSize / 2, vertexSize,
			// vertexSize);
			if (programPreferences.showNormals()) {
				normalHeap.x = (float) normalX;
				normalHeap.y = (float) normalY;
				normalHeap.z = (float) normalZ;
				normalHeap.w = 0;
				if (bones.size() > 0) {
					normalSumHeap.set(0, 0, 0, 0);
					for (final Bone bone : bones) {
						Matrix4f.transform(renderModel.getRenderNode(bone).getWorldMatrix(), normalHeap,
								appliedNormalHeap);
						Vector4f.add(normalSumHeap, appliedNormalHeap, normalSumHeap);
					}

					if (normalSumHeap.length() > 0) {
						normalSumHeap.normalise();
					} else {
						normalSumHeap.set(0, 1, 0, 0);
					}
				} else {
					normalSumHeap.set(normalHeap);
				}
				final Color triangleColor = graphics.getColor();
				float firstNormalCoord, secondNormalCoord;
				switch (xDimension) {
				case 0:
					firstNormalCoord = normalSumHeap.x;
					break;
				case 1:
					firstNormalCoord = normalSumHeap.y;
					break;
				case 2:
					firstNormalCoord = normalSumHeap.z;
					break;
				default:
					throw new IllegalStateException("Invalid x dimension");
				}
				switch (yDimension) {
				case 0:
					secondNormalCoord = normalSumHeap.x;
					break;
				case 1:
					secondNormalCoord = normalSumHeap.y;
					break;
				case 2:
					secondNormalCoord = normalSumHeap.z;
					break;
				default:
					throw new IllegalStateException("Invalid y dimension");
				}
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
		public VertexVisitor hdVertex(final double x, final double y, final double z, final double normalX,
				final double normalY, final double normalZ, final Bone[] skinBones, final short[] skinBoneWeights) {
			vertexHeap.x = (float) x;
			vertexHeap.y = (float) y;
			vertexHeap.z = (float) z;
			vertexHeap.w = 1;
			skinBonesMatrixSumHeap.setZero();
			vertexSumHeap.set(0, 0, 0, 0);
			boolean processedBones = false;
			for (int boneIndex = 0; boneIndex < 4; boneIndex++) {
				final Bone skinBone = skinBones[boneIndex];
				if (skinBone == null) {
					continue;
				}
				processedBones = true;
				final Matrix4f worldMatrix = renderModel.getRenderNode(skinBone).getWorldMatrix();
				skinBonesMatrixHeap.load(worldMatrix);

				skinBonesMatrixSumHeap.m00 += (skinBonesMatrixHeap.m00 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m01 += (skinBonesMatrixHeap.m01 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m02 += (skinBonesMatrixHeap.m02 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m03 += (skinBonesMatrixHeap.m03 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m10 += (skinBonesMatrixHeap.m10 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m11 += (skinBonesMatrixHeap.m11 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m12 += (skinBonesMatrixHeap.m12 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m13 += (skinBonesMatrixHeap.m13 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m20 += (skinBonesMatrixHeap.m20 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m21 += (skinBonesMatrixHeap.m21 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m22 += (skinBonesMatrixHeap.m22 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m23 += (skinBonesMatrixHeap.m23 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m30 += (skinBonesMatrixHeap.m30 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m31 += (skinBonesMatrixHeap.m31 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m32 += (skinBonesMatrixHeap.m32 * skinBoneWeights[boneIndex]) / 255f;
				skinBonesMatrixSumHeap.m33 += (skinBonesMatrixHeap.m33 * skinBoneWeights[boneIndex]) / 255f;
			}
			if (!processedBones) {
				skinBonesMatrixSumHeap.setIdentity();
			}
			Matrix4f.transform(skinBonesMatrixSumHeap, vertexHeap, vertexSumHeap);
			normalHeap.x = (float) normalX;
			normalHeap.y = (float) normalY;
			normalHeap.z = (float) normalZ;
			normalHeap.w = 0;
			Matrix4f.transform(skinBonesMatrixSumHeap, normalHeap, normalSumHeap);

			if (normalSumHeap.length() > 0) {
				normalSumHeap.normalise();
			} else {
				normalSumHeap.set(0, 1, 0, 0);
			}

			float firstCoord, secondCoord;
			switch (xDimension) {
			case 0:
				firstCoord = vertexSumHeap.x;
				break;
			case 1:
				firstCoord = vertexSumHeap.y;
				break;
			case 2:
				firstCoord = vertexSumHeap.z;
				break;
			default:
				throw new IllegalStateException("Invalid x dimension");
			}
			switch (yDimension) {
			case 0:
				secondCoord = vertexSumHeap.x;
				break;
			case 1:
				secondCoord = vertexSumHeap.y;
				break;
			case 2:
				secondCoord = vertexSumHeap.z;
				break;
			default:
				throw new IllegalStateException("Invalid y dimension");
			}
			final Point point = new Point((int) coordinateSystem.convertX(firstCoord),
					(int) coordinateSystem.convertY(secondCoord));
			if (previousVertices.size() > 0) {
				final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
				graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
			}
			previousVertices.add(point);
			if (programPreferences.showNormals()) {
				normalHeap.x = (float) normalX;
				normalHeap.y = (float) normalY;
				normalHeap.z = (float) normalZ;
				normalHeap.w = 0;
				final Color triangleColor = graphics.getColor();
				float firstNormalCoord, secondNormalCoord;
				switch (xDimension) {
				case 0:
					firstNormalCoord = normalSumHeap.x;
					break;
				case 1:
					firstNormalCoord = normalSumHeap.y;
					break;
				case 2:
					firstNormalCoord = normalSumHeap.z;
					break;
				default:
					throw new IllegalStateException("Invalid x dimension");
				}
				switch (yDimension) {
				case 0:
					secondNormalCoord = normalSumHeap.x;
					break;
				case 1:
					secondNormalCoord = normalSumHeap.y;
					break;
				case 2:
					secondNormalCoord = normalSumHeap.z;
					break;
				default:
					throw new IllegalStateException("Invalid y dimension");
				}
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
		public void triangleFinished() {
			if (previousVertices.size() > 1) {
				final Point previousPoint = previousVertices.get(previousVertices.size() - 1);
				final Point point = previousVertices.get(0);
				graphics.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
			}
		}

	}

}
