package com.hiveworkshop.wc3.mdlx;

import java.io.IOException;
import java.util.Set;

import com.hiveworkshop.wc3.mdx.GeosetChunk;
import com.hiveworkshop.wc3.mdx.MdxModel;
import com.hiveworkshop.wc3.mdx.MdxUtils;
import com.hiveworkshop.wc3.mpq.MpqCodebase;

import de.wc3data.stream.BlizzardDataInputStream;

public class ParseEveryModel {

	public static void main(final String[] args) {
		int parsed = 0;
		final Set<String> mergedListfile = MpqCodebase.get().getMergedListfile();
		int maxVertexGroup = 0;
		for (final String str : mergedListfile) {
			if (str.toLowerCase().endsWith(".mdx")) {
				try {
					final MdxModel loadModel = MdxUtils
							.loadModel(new BlizzardDataInputStream(MpqCodebase.get().getResourceAsStream(str)));
					if (loadModel.geosetChunk != null) {
						for (final GeosetChunk.Geoset geosetChunk : loadModel.geosetChunk.geoset) {
							for (final int matrixGroup : geosetChunk.matrixGroups) {
								if (matrixGroup > maxVertexGroup) {
									maxVertexGroup = matrixGroup;
								}
							}
						}
					}
					parsed++;
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.err.println("parsed " + parsed + " successfully");
		System.out.println("Max was: " + maxVertexGroup);
	}

}
