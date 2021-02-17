package com.hiveworkshop.rms.ui.util;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.ViewportModelRenderer;
import com.hiveworkshop.rms.ui.gui.modeledit.VertexFilter;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSnapshottingListCellRenderer2D<TYPE> extends DefaultListCellRenderer {
	private static final int SIZE = 32;
	private static final int QUARTER_SIZE = SIZE / 4;
	private static final int EIGHTH_SIZE = SIZE / 8;
	private final Map<TYPE, ImageIcon> matrixShellToCachedRenderer = new HashMap<>();
	private final ResettableVertexFilter<TYPE> matrixFilter;
	private final ModelView modelDisplay;
	private final ModelView otherDisplay;
	private Map<EditableModel, BufferedImage> modelOutlineImageMap;
	private Map<EditableModel, Vec2[]> modelBoundsSizeMap;

	private Map<EditableModel, Map<Geoset, Map<Bone, List<GeosetVertex>>>> geosetBoneMap;

	public AbstractSnapshottingListCellRenderer2D(final ModelView modelDisplay, final ModelView otherDisplay) {
		this.modelDisplay = modelDisplay;
		this.otherDisplay = otherDisplay;
		matrixFilter = createFilter();
		modelOutlineImageMap = new HashMap<>();
		modelBoundsSizeMap = new HashMap<>();
		geosetBoneMap = new HashMap<>();
	}

	protected abstract ResettableVertexFilter<TYPE> createFilter();

	@Override
	public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean iss, final boolean chf) {
		final Color backgroundColor = getBackground();
		setBackground(null);
		final TYPE matrixShell = valueToType(value);
		ImageIcon myIcon = matrixShellToCachedRenderer.get(matrixShell);
		if (myIcon == null) {
			try {
				final BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
				final Graphics graphics = image.getGraphics();
				graphics.setColor(backgroundColor);
				graphics.fill3DRect(0, 0, SIZE, SIZE, true);
				graphics.setColor(backgroundColor.brighter());
				graphics.fill3DRect(EIGHTH_SIZE, EIGHTH_SIZE, SIZE - QUARTER_SIZE, SIZE - QUARTER_SIZE, true);

				makeBoneIcon(backgroundColor, matrixShell, graphics, otherDisplay, value);
				makeBoneIcon(backgroundColor, matrixShell, graphics, modelDisplay, value);

				graphics.dispose();
				myIcon = new ImageIcon(image);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			matrixShellToCachedRenderer.put(matrixShell, myIcon);
		}
		super.getListCellRendererComponent(list, matrixShell.toString(), index, iss, chf);
		setIcon(myIcon);
		return this;
	}

	public void makeBoneIcon(Color backgroundColor, TYPE matrixShell, Graphics graphics, ModelView modelDisplay, Object value) {
		if (modelDisplay != null && contains(modelDisplay, matrixShell)) {
			EditableModel model = modelDisplay.getModel();
			BufferedImage modelOutline = getModelOutlineImage(backgroundColor, model);
			graphics.drawImage(modelOutline, 0, 0, null);
//			ViewportModelRenderer.drawFilteredTriangles(model, graphics, new Rectangle(SIZE, SIZE), (byte) 1, (byte) 2, modelBoundsSizeMap.get(model), matrixFilter.reset(matrixShell));
			ViewportModelRenderer.scaleAndTranslateGraphic((Graphics2D) graphics, new Rectangle(SIZE, SIZE), getModelBoundsSize(model));
			if (value instanceof Bone) {
//				System.out.println("BONE!!!");
				ViewportModelRenderer.drawFilteredTriangles2(model, graphics, (byte) 1, (byte) 2, getBoneMap(model), (Bone) value);
			}
			ViewportModelRenderer.drawHighlightedPoints(graphics, (byte) 1, (byte) 2, getRenderVertex(matrixShell));
		}
	}

	private Vec2[] getModelBoundsSize(EditableModel model) {
		if (modelBoundsSizeMap.containsKey(model)) {
			return modelBoundsSizeMap.get(model);
		} else {
			Vec2[] boundSize = ViewportModelRenderer.getBoundBoxSize(model, (byte) 1, (byte) 2);
			modelBoundsSizeMap.put(model, boundSize);
			return boundSize;
		}
	}

	private BufferedImage getModelOutlineImage(Color backgroundColor, EditableModel model) {
		if (modelOutlineImageMap.containsKey(model)) {
			return modelOutlineImageMap.get(model);
		} else {
			final BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
			final Graphics graphics = image.getGraphics();
			graphics.setColor(backgroundColor);
			graphics.fill3DRect(0, 0, SIZE, SIZE, true);
			graphics.setColor(backgroundColor.brighter());
			graphics.fill3DRect(EIGHTH_SIZE, EIGHTH_SIZE, SIZE - QUARTER_SIZE, SIZE - QUARTER_SIZE, true);

			ViewportModelRenderer.scaleAndTranslateGraphic((Graphics2D) graphics, new Rectangle(SIZE, SIZE), getModelBoundsSize(model));

			ViewportModelRenderer.drawGeosetFlat(model, graphics, (byte) 1, (byte) 2);
			modelOutlineImageMap.put(model, image);
//						ViewportModelRenderer.drawFittedTriangles2(otherDisplay.getModel(), graphics, new Rectangle(SIZE, SIZE), (byte) 1, (byte) 2, matrixFilter.reset(matrixShell), getRenderVertex(matrixShell));
			graphics.dispose();
			return image;
		}
	}

	private Map<Geoset, Map<Bone, List<GeosetVertex>>> getBoneMap(EditableModel model) {
		if (geosetBoneMap.containsKey(model)) {
			return geosetBoneMap.get(model);
		} else {
			Map<Geoset, Map<Bone, List<GeosetVertex>>> boneMap = new HashMap<>();
			for (Geoset geoset : model.getGeosets()) {
				boneMap.put(geoset, geoset.getBoneMap());
			}
			geosetBoneMap.put(model, boneMap);
			return boneMap;
		}
	}

	//		@Override
	public Component getListCellRendererComponent2(final JList<?> list, final Object value, final int index, final boolean iss, final boolean chf) {
		final Color backgroundColor = getBackground();
		setBackground(null);
		final TYPE matrixShell = valueToType(value);
		ImageIcon myIcon = matrixShellToCachedRenderer.get(matrixShell);
		if (myIcon == null) {
			try {
				final BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
				final Graphics graphics = image.getGraphics();
				graphics.setColor(backgroundColor);
				graphics.fill3DRect(0, 0, SIZE, SIZE, true);
				graphics.setColor(backgroundColor.brighter());
				graphics.fill3DRect(EIGHTH_SIZE, EIGHTH_SIZE, SIZE - QUARTER_SIZE, SIZE - QUARTER_SIZE, true);
				if (otherDisplay != null && contains(otherDisplay, matrixShell)) {
					ViewportModelRenderer.drawFittedTriangles2(otherDisplay.getModel(), graphics, new Rectangle(SIZE, SIZE), (byte) 1, (byte) 2, matrixFilter.reset(matrixShell), getRenderVertex(matrixShell));
				}
//				if (modelDisplay != null && contains(modelDisplay, matrixShell)) {
//					ViewportModelRenderer.drawFittedTriangles3(modelDisplay.getModel(), graphics, new Rectangle(SIZE, SIZE), (byte) 1, (byte) 2, matrixFilter.reset(matrixShell), getRenderVertex(matrixShell));
//				}
				graphics.dispose();
				myIcon = new ImageIcon(image);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			matrixShellToCachedRenderer.put(matrixShell, myIcon);
		}
		super.getListCellRendererComponent(list, matrixShell.toString(), index, iss, chf);
		setIcon(myIcon);
		return this;
	}

	protected abstract TYPE valueToType(Object value);

	protected abstract Vec3 getRenderVertex(TYPE value);

	protected abstract boolean contains(ModelView modelDisp, TYPE object);

	protected interface ResettableVertexFilter<TYPE> extends VertexFilter<GeosetVertex> {
		ResettableVertexFilter<TYPE> reset(final TYPE matrix);
	}
}
