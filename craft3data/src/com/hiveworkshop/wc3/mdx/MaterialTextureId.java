package com.hiveworkshop.wc3.mdx;

import java.io.IOException;

import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

public class MaterialTextureId {
	public int interpolationType;
	public int globalSequenceId;
	public ScalingTrack[] scalingTrack = new ScalingTrack[0];

	public static final String key = "KMTF";

	public void load(BlizzardDataInputStream in) throws IOException {
		MdxUtils.checkId(in, "KMTF");
		int nrOfTracks = in.readInt();
		interpolationType = in.readInt();
		globalSequenceId = in.readInt();
		scalingTrack = new ScalingTrack[nrOfTracks];
		for (int i = 0; i < nrOfTracks; i++) {
			scalingTrack[i] = new ScalingTrack();
			scalingTrack[i].load(in);
		}
	}

	public void save(BlizzardDataOutputStream out) throws IOException {
		int nrOfTracks = scalingTrack.length;
		out.writeNByteString("KMTF", 4);
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
		public int textureId;
		public int inTan;
		public int outTan;

		public void load(BlizzardDataInputStream in) throws IOException {
			time = in.readInt();
			textureId = in.readInt();
			if (interpolationType > 1) {
				inTan = in.readInt();
				outTan = in.readInt();
			}
		}

		public void save(BlizzardDataOutputStream out) throws IOException {
			out.writeInt(time);
			out.writeInt(textureId);
			if (interpolationType > 1) {
				out.writeInt(inTan);
				out.writeInt(outTan);
			}

		}

		public int getSize() {
			int a = 0;
			a += 4;
			a += 4;
			if (interpolationType > 1) {
				a += 4;
				a += 4;
			}

			return a;
		}
	}
}
