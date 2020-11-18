package com.hiveworkshop.wc3.mdlx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.hiveworkshop.wc3.mdx.MdxModel;
import com.hiveworkshop.wc3.mdx.MdxUtils;
import com.hiveworkshop.wc3.mdx.SequenceChunk.Sequence;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.mpq.MpqCodebase.LoadedMPQ;

import de.wc3data.stream.BlizzardDataInputStream;

public class ParseEveryBtTModel {

	public static void main(final String[] args) {
		final String pathToBtT = "C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Maps\\(4)BtT Frozen Void Edit for Test 2.w3x";
		int parsed = 0;
		try {
			final LoadedMPQ bttMpq = MpqCodebase.get().loadMPQ(Paths.get(pathToBtT));
			final File bttListfile = MpqCodebase.get().getFile("(listfile)");
			final List<String> bttListfileLines = Files.readAllLines(bttListfile.toPath());
//			final SetView<String> mergedListfile = MpqCodebase.get().getMergedListfile();
			for (final String str : bttListfileLines) {
				if (str.toLowerCase().endsWith(".mdx")) {
//				System.err.println(str);
					try {
						final MdxModel loadModel = MdxUtils
								.loadModel(new BlizzardDataInputStream(MpqCodebase.get().getResourceAsStream(str)));
						if (loadModel.sequenceChunk != null) {
							for (int seq = 0; seq < loadModel.sequenceChunk.sequence.length; seq++) {
								final Sequence sequence = loadModel.sequenceChunk.sequence[seq];
								if (sequence.syncPoint != 0) {
									System.err.println("SYNC POINT NONZERO: " + sequence.syncPoint + " in " + str);
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
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
    }

}
