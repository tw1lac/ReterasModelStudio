package com.hiveworkshop.wc3.gui.modeledit.newstuff.actions.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.hiveworkshop.wc3.gui.modeledit.UndoAction;
import com.hiveworkshop.wc3.mdl.Bone;
import com.hiveworkshop.wc3.mdl.GeosetVertex;

public final class SetMatrixAction implements UndoAction {
	private final Map<GeosetVertex, List<Bone>> vertexToOldBoneReferences;
	private final Collection<Bone> newBoneReferences;
	private final Map<GeosetVertex, Bone[]> vertexToOldSkinBoneReferences;
	private final Map<GeosetVertex, short[]> vertexToOldSkinBoneWeightReferences;
	private final Map<GeosetVertex, Bone[]> vertexToNewSkinBoneReferences;
	private final Map<GeosetVertex, short[]> vertexToNewSkinBoneWeightReferences;

	public SetMatrixAction(final Map<GeosetVertex, List<Bone>> vertexToOldBoneReferences,
			final Map<GeosetVertex, Bone[]> vertexToOldSkinBoneReferences,
			final Map<GeosetVertex, short[]> vertexToOldSkinBoneWeightReferences,
			final Collection<Bone> newBoneReferences) {
		this.vertexToOldBoneReferences = vertexToOldBoneReferences;
		this.vertexToOldSkinBoneReferences = vertexToOldSkinBoneReferences;
		this.vertexToOldSkinBoneWeightReferences = vertexToOldSkinBoneWeightReferences;
		this.newBoneReferences = newBoneReferences;
		vertexToNewSkinBoneReferences = new HashMap<>();
		vertexToNewSkinBoneWeightReferences = new HashMap<>();

		for (GeosetVertex geosetVertex: vertexToOldSkinBoneReferences.keySet()){
			vertexToNewSkinBoneReferences.put(geosetVertex, geosetVertex.getSkinBones().clone());
		}
		for (GeosetVertex geosetVertex: vertexToOldSkinBoneWeightReferences.keySet()){
			vertexToNewSkinBoneWeightReferences.put(geosetVertex, geosetVertex.getSkinBoneWeights().clone());
		}
	}

	@Override
	public void undo() {
		for (GeosetVertex geosetVertex: vertexToOldBoneReferences.keySet()){
			geosetVertex.setBones(new ArrayList<>(vertexToOldBoneReferences.get(geosetVertex)));
		}
		for (GeosetVertex geosetVertex: vertexToOldSkinBoneReferences.keySet()){
			geosetVertex.setSkinBones(vertexToOldSkinBoneReferences.get(geosetVertex));
		}
		for (GeosetVertex geosetVertex: vertexToOldSkinBoneWeightReferences.keySet()){
			geosetVertex.setSkinBoneWeights(vertexToOldSkinBoneWeightReferences.get(geosetVertex));
		}
	}

	@Override
	public void redo() {
		for (GeosetVertex geosetVertex: vertexToOldBoneReferences.keySet()) {
			geosetVertex.setBones(new ArrayList<>(newBoneReferences));
		}
		for (GeosetVertex geosetVertex: vertexToNewSkinBoneReferences.keySet()) {
			geosetVertex.setSkinBones(vertexToNewSkinBoneReferences.get(geosetVertex));
		}
		for (GeosetVertex geosetVertex: vertexToNewSkinBoneWeightReferences.keySet()) {
			geosetVertex.setSkinBoneWeights(vertexToNewSkinBoneWeightReferences.get(geosetVertex));
		}
	}

	@Override
	public String actionName() {
		return "re-assign matrix";
	}

}
