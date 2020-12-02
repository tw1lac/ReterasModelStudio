package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.actions;

import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.util.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MirrorTVerticesAction implements UndoAction {
	private final char[] DIMENSION_NAMES = {'X', 'Y'};
	private final List<Vec3> selection;
	private final byte mirrorDim;
	private final double centerX;
	private final double centerY;

	public MirrorTVerticesAction(final Collection<? extends Vec3> selection, final byte mirrorDim,
								 final double centerX, final double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.selection = new ArrayList<>(selection);
		this.mirrorDim = mirrorDim;
	}

	@Override
	public void undo() {
		doMirror();
	}

	@Override
	public void redo() {
		doMirror();
	}

	private void doMirror() {
		final Vec3 center = new Vec3(centerX, centerY, 0);
		for (final Vec3 vert : selection) {
			vert.setCoord(mirrorDim, 2 * center.getCoord(mirrorDim) - vert.getCoord(mirrorDim));
		}
	}

	@Override
	public String actionName() {
		return "mirror UV " + DIMENSION_NAMES[mirrorDim];
	}

}
