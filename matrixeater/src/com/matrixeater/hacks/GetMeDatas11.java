package com.matrixeater.hacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.hiveworkshop.wc3.mdl.AnimFlag;
import com.hiveworkshop.wc3.mdl.Animation;
import com.hiveworkshop.wc3.mdl.IdObject;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdx.MdxUtils;

import de.wc3data.stream.BlizzardDataInputStream;

public class GetMeDatas11 {

	public static void main(final String[] args) {

		try (final InputStream footman = new FileInputStream(
				"C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\TCContest14\\UndeadArthas3.mdx");
				final InputStream footmanBase = new FileInputStream(
						"C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\TCContest14\\UndeadArthasBase.mdx")) {
			try {

				final EditableModel model = new EditableModel(MdxUtils.loadModel(new BlizzardDataInputStream(footman)));
				final EditableModel modelBase = new EditableModel(MdxUtils.loadModel(new BlizzardDataInputStream(footmanBase)));

				for (final IdObject node : model.getIdObjects()) {
					IdObject baseNode = null;
					for (final IdObject otherNode : modelBase.getIdObjects()) {
						if (otherNode.getName().equals(node.getName())) {
							baseNode = otherNode;
							break;
						}
					}
					if (baseNode != null) {
						final double dx = node.getPivotPoint().x - baseNode.getPivotPoint().x;
						final double dy = node.getPivotPoint().y - baseNode.getPivotPoint().y;
						final double dz = node.getPivotPoint().z - baseNode.getPivotPoint().z;
						node.getBindPose()[9] += dx;
						node.getBindPose()[10] += dy;
						node.getBindPose()[11] += dz;
					}
				}

				model.printTo(new File(
						"C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\TCContest14\\UndeadArthas4.mdx"));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
    }

	private static void fix(final AnimFlag visibilityAnimation, final Animation deathSequence) {
		final int startOfDeath = deathSequence.getStart();
		final int endOfDeath = deathSequence.getEnd();
		if (visibilityAnimation != null) {
			final int floorIndexOfStart = visibilityAnimation.floorIndex(startOfDeath);
			if (floorIndexOfStart < visibilityAnimation.floorIndex(endOfDeath)) {
				if (visibilityAnimation.getTimes().get(floorIndexOfStart) < startOfDeath) {
					visibilityAnimation.addEntry(startOfDeath, visibilityAnimation.getEntry(floorIndexOfStart).value);
				}
			}
		}
	}

}
