package com.hiveworkshop.wc3.mdx;

import com.hiveworkshop.wc3.mdl.Animation;
import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequenceChunk {
	public Sequence[] sequence = new Sequence[0];

	public static final String key = "SEQS";

	public void load(final BlizzardDataInputStream in) throws IOException {
		MdxUtils.checkId(in, "SEQS");
		final int chunkSize = in.readInt();
		final List<Sequence> sequenceList = new ArrayList<>();
		int sequenceCounter = chunkSize;
		while (sequenceCounter > 0) {
			final Sequence tempsequence = new Sequence();
			sequenceList.add(tempsequence);
			tempsequence.load(in);
			sequenceCounter -= tempsequence.getSize();
		}
		sequence = sequenceList.toArray(new Sequence[0]);
	}

	public void save(final BlizzardDataOutputStream out) throws IOException {
		final int nrOfSequences = sequence.length;
		out.writeNByteString("SEQS", 4);
		out.writeInt(getSize() - 8);// ChunkSize
        for (Sequence value : sequence) {
            value.save(out);
        }

	}

	public int getSize() {
		int a = 0;
		a += 4;
		a += 4;
        for (Sequence value : sequence) {
            a += value.getSize();
        }

		return a;
	}

	public static class Sequence {
		public String name = "";
		public int intervalStart;
		public int intervalEnd;
		public float moveSpeed;
		public int nonLooping;
		public float rarity;
		public int syncPoint;
		public float boundsRadius;
		public float[] minimumExtent = new float[3];
		public float[] maximumExtent = new float[3];

		public void load(final BlizzardDataInputStream in) throws IOException {
			name = in.readCharsAsString(80);
			intervalStart = in.readInt();
			intervalEnd = in.readInt();
			moveSpeed = in.readFloat();
			nonLooping = in.readInt();
			rarity = in.readFloat();
			syncPoint = in.readInt();
			boundsRadius = in.readFloat();
			minimumExtent = MdxUtils.loadFloatArray(in, 3);
			maximumExtent = MdxUtils.loadFloatArray(in, 3);
		}

		public void save(final BlizzardDataOutputStream out) throws IOException {
			out.writeNByteString(name, 80);
			out.writeInt(intervalStart);
			out.writeInt(intervalEnd);
			out.writeFloat(moveSpeed);
			out.writeInt(nonLooping);
			out.writeFloat(rarity);
			out.writeInt(syncPoint);
			out.writeFloat(boundsRadius);
			if (minimumExtent.length % 3 != 0) {
				throw new IllegalArgumentException(
						"The array minimumExtent needs either the length 3 or a multiple of this number. (got "
								+ minimumExtent.length + ")");
			}
			MdxUtils.saveFloatArray(out, minimumExtent);
			if (maximumExtent.length % 3 != 0) {
				throw new IllegalArgumentException(
						"The array maximumExtent needs either the length 3 or a multiple of this number. (got "
								+ maximumExtent.length + ")");
			}
			MdxUtils.saveFloatArray(out, maximumExtent);

		}

		public int getSize() {
			int a = 0;
			a += 80;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 12;
			a += 12;

			return a;
		}

		public Sequence() {

		}

		public Sequence(final Animation anim) {
			name = anim.getName();
			boundsRadius = anim.getExtents() == null ? 0 : (float) anim.getExtents().getBoundsRadius();
			intervalEnd = anim.getIntervalEnd();
			intervalStart = anim.getIntervalStart();
			maximumExtent = anim.getExtents() == null ? new float[] { 0, 0, 0 }
					: anim.getExtents().getMaximumExtent().toFloatArray();
			minimumExtent = anim.getExtents() == null ? new float[] { 0, 0, 0 }
					: anim.getExtents().getMinimumExtent().toFloatArray();
			for (final String tag : anim.getTags()) {
				if (tag.startsWith("MoveSpeed")) {
					moveSpeed = Float.parseFloat(tag.split(" ")[1]);
				} else if (tag.startsWith("NonLooping")) {
					nonLooping = 1;
				} else if (tag.startsWith("Rarity")) {
					rarity = Float.parseFloat(tag.split(" ")[1]);
				}
			}
		}
	}
}
