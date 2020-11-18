package com.hiveworkshop.wc3.mdx;

import java.io.IOException;

import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

public class CornEmissionRate {
	public int interpolationType;
	public int globalSequenceId;
	public EmissionRateTrack[] emissionRateTrack = new EmissionRateTrack[0];

	public static final String key = "KPPE";

	public void load(BlizzardDataInputStream in) throws IOException {
		MdxUtils.checkId(in, "KPPE");
		int nrOfTracks = in.readInt();
		interpolationType = in.readInt();
		globalSequenceId = in.readInt();
		emissionRateTrack = new EmissionRateTrack[nrOfTracks];
		for (int i = 0; i < nrOfTracks; i++) {
			emissionRateTrack[i] = new EmissionRateTrack();
			emissionRateTrack[i].load(in);
		}
	}

	public void save(BlizzardDataOutputStream out) throws IOException {
		int nrOfTracks = emissionRateTrack.length;
		out.writeNByteString("KPPE", 4);
		out.writeInt(nrOfTracks);
		out.writeInt(interpolationType);
		out.writeInt(globalSequenceId);
        for (EmissionRateTrack rateTrack : emissionRateTrack) {
            rateTrack.save(out);
        }

	}

	public int getSize() {
		int a = 0;
		a += 4;
		a += 4;
		a += 4;
		a += 4;
        for (EmissionRateTrack rateTrack : emissionRateTrack) {
            a += rateTrack.getSize();
        }

		return a;
	}

	public class EmissionRateTrack {
		public int time;
		public float emissionRate;
		public float inTan;
		public float outTan;

		public void load(BlizzardDataInputStream in) throws IOException {
			time = in.readInt();
			emissionRate = in.readFloat();
			if (interpolationType > 1) {
				inTan = in.readFloat();
				outTan = in.readFloat();
			}
		}

		public void save(BlizzardDataOutputStream out) throws IOException {
			out.writeInt(time);
			out.writeFloat(emissionRate);
			if (interpolationType > 1) {
				out.writeFloat(inTan);
				out.writeFloat(outTan);
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
