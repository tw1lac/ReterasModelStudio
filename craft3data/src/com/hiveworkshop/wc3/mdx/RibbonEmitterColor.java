package com.hiveworkshop.wc3.mdx;

import java.io.IOException;

import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

public class RibbonEmitterColor {
	public int interpolationType;
	public int globalSequenceId;
	public ScalingTrack[] scalingTrack = new ScalingTrack[0];

	public static final String key = "KRCO";

	public void load(final BlizzardDataInputStream in) throws IOException {
		MdxUtils.checkId(in, "KRCO");
		final int nrOfTracks = in.readInt();
		interpolationType = in.readInt();
		globalSequenceId = in.readInt();
		scalingTrack = new ScalingTrack[nrOfTracks];
		for (int i = 0; i < nrOfTracks; i++) {
			scalingTrack[i] = new ScalingTrack();
			scalingTrack[i].load(in);
		}
	}

	public void save(final BlizzardDataOutputStream out) throws IOException {
		final int nrOfTracks = scalingTrack.length;
		out.writeNByteString("KRCO", 4);
		out.writeInt(nrOfTracks);
		out.writeInt(interpolationType);
		out.writeInt(globalSequenceId);
        for (ScalingTrack track : scalingTrack) {
            track.save(out);
        }

	}

	public int getSize() {
		int a = 0;
		a += 4;
		a += 4;
		a += 4;
		a += 4;
        for (ScalingTrack track : scalingTrack) {
            a += track.getSize();
        }

		return a;
	}

	public class ScalingTrack {
		public int time;
		public float[] color = new float[3];
		public float[] inTan = new float[3];
		public float[] outTan = new float[3];

		public void load(final BlizzardDataInputStream in) throws IOException {
			time = in.readInt();
			color = MdxUtils.loadFloatArray(in, 3);
			if (interpolationType > 1) {
				inTan = MdxUtils.loadFloatArray(in, 3);
				outTan = MdxUtils.loadFloatArray(in, 3);
			}
		}

		public void save(final BlizzardDataOutputStream out) throws IOException {
			out.writeInt(time);
			if ((color.length % 3) != 0) {
				throw new IllegalArgumentException(
						"The array color needs either the length 3 or a multiple of this number. (got " + color.length
								+ ")");
			}
			MdxUtils.saveFloatArray(out, color);
			if (interpolationType > 1) {
				if ((inTan.length % 3) != 0) {
					throw new IllegalArgumentException(
							"The array inTan needs either the length 3 or a multiple of this number. (got "
									+ inTan.length + ")");
				}
				MdxUtils.saveFloatArray(out, inTan);
				if ((outTan.length % 3) != 0) {
					throw new IllegalArgumentException(
							"The array outTan needs either the length 3 or a multiple of this number. (got "
									+ outTan.length + ")");
				}
				MdxUtils.saveFloatArray(out, outTan);
			}

		}

		public int getSize() {
			int a = 0;
			a += 4;
			a += 12;
			if (interpolationType > 1) {
				a += 12;
				a += 12;
			}

			return a;
		}
	}
}
