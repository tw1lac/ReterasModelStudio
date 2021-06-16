package com.hiveworkshop.rms.editor.model.util.ModelSaving;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.util.BiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelInfoHolder {
	public BiMap<Integer, IdObject> idIddObjectBiMap = new BiMap<>();
	public BiMap<Integer, Geoset> idGeosetBiMap = new BiMap<>();
	public BiMap<Integer, Material> IdMaterialBiMap = new BiMap<>();
	public Map<Geoset, BiMap<Integer, Matrix>> geosetMatrices = new HashMap<>();

	public ModelInfoHolder(EditableModel model) {
		model.sortIdObjects();
		for (IdObject obj : model.getAllObjects()) {
			idIddObjectBiMap.put(idIddObjectBiMap.size(), obj);
		}

		for (Geoset geoset : model.getGeosets()) {
			idGeosetBiMap.put(idGeosetBiMap.size(), geoset);
			createGeosetMatrixes(geoset, model);
		}

		for (Material material : model.getMaterials()) {
			IdMaterialBiMap.put(IdMaterialBiMap.size(), material);
		}
	}

	private void createGeosetMatrixes(Geoset geoset, EditableModel model) {
		BiMap<Integer, Matrix> indexMatrixMap = geosetMatrices.computeIfAbsent(geoset, k -> new BiMap<>());
		for (final GeosetVertex geosetVertex : geoset.getVertices()) {
			if (geosetVertex.getSkinBoneBones() != null) {
				if (indexMatrixMap.isEmpty()) {
					List<Bone> bones = model.getBones();
					for (int j = 0; (j < bones.size()) && (j < 256); j++) {
						Matrix matrix = new Matrix(bones.get(j));
						indexMatrixMap.put(j, matrix);
					}
				}
//				int skinIndex = 0;
//				for (Bone bone : geosetVertex.getSkinBoneBones()) {
//					if (bone != null) {
//						Integer index = indexMatrixMap.getByValue(new Matrix(bone));
//						int betterIndex = index == null ? -1 : index;
//						geosetVertex.getSkinBoneIndexes()[skinIndex++] = (byte) betterIndex;
//					}
//				}
				geosetVertex.setVertexGroup(-1);
			} else {
				Matrix newTemp = new Matrix(geosetVertex.getBones());
				if (!indexMatrixMap.containsValue(newTemp)) {
					indexMatrixMap.put(indexMatrixMap.size(), newTemp);
				}

				geosetVertex.setVertexGroup(indexMatrixMap.getByValue(newTemp));
			}
		}
	}
}
