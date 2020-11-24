package com.hiveworkshop.wc3.mdl;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;
import com.hiveworkshop.wc3.mdl.render3d.EmitterIdObject;
import com.hiveworkshop.wc3.mdl.v2.visitor.IdObjectVisitor;
import com.hiveworkshop.wc3.mdx.ParticleEmitterChunk;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ParticleEmitter2 class, these are the things most people would think of as a
 * particle emitter, I think. Blizzard favored use of these over
 * ParticleEmitters and I do too simply because I so often recycle data and
 * there are more of these to use.
 *
 * Eric Theller 3/10/2012 3:32 PM
 */
public class ParticleEmitter extends EmitterIdObject implements VisibilitySource {
	@Override
	public int getBlendSrc() {
		return 0;
	}

	@Override
	public int getBlendDst() {
		return 0;
	}

	@Override
	public int getRows() {
		return 0;
	}

	@Override
	public int getCols() {
		return 0;
	}

	@Override
	public boolean isRibbonEmitter() {
		return false;
	}

	public enum TimeDoubles {
		EmissionRate, Gravity, Longitude, Latitude, LifeSpan, InitVelocity
	}

	static final String[] timeDoubleNames = { "EmissionRate", "Gravity", "Longitude", "Latitude", "LifeSpan",
			"InitVelocity" };
	double[] timeDoubleData = new double[timeDoubleNames.length];

	boolean MDLEmitter = true;
	Map<String, AnimFlag> animFlags = new HashMap<>();

	String path = null;
	ArrayList<String> flags = new ArrayList<>();

	private ParticleEmitter() {

	}

	public ParticleEmitter(final String name) {
		this.name = name;
	}

	public ParticleEmitter(final ParticleEmitterChunk.ParticleEmitter emitter) {
		this(emitter.node.name);
		// debug print:
		if ((emitter.node.flags & 4096) != 4096) {
			System.err.println("MDX -> MDL error: A particle emitter '" + emitter.node.name
					+ "' not flagged as particle emitter in MDX!");
		}
		// System.out.println(emitter.node.name + ": " +
		// Integer.toBinaryString(emitter.node.flags));
		// ----- Convert Base NODE to "IDOBJECT" -----
		loadFrom(emitter.node);
		// ----- End Base NODE to "IDOBJECT" -----

		if (emitter.unknownNull != 0) {
			System.err.println("Surprise! This model has a special emitter data point worthy of documenting! " + name);
		}
		// System.out.println(attachment.node.name + ": " +
		// Integer.toBinaryString(attachment.unknownNull));

		if (emitter.particleEmitterVisibility != null) {
			add(new AnimFlag(emitter.particleEmitterVisibility));
		}
		if (emitter.particleEmitterEmissionRate != null) {
			add(new AnimFlag(emitter.particleEmitterEmissionRate));
		} else {
			setEmissionRate(emitter.emissionRate);
		}
		if (emitter.particleEmitterGravity != null) {
			add(new AnimFlag(emitter.particleEmitterGravity));
		} else {
			setGravity(emitter.gravity);
		}
		if (emitter.particleEmitterSpeed != null) {
			add(new AnimFlag(emitter.particleEmitterSpeed));
		} else {
			setInitVelocity(emitter.initialVelocity);
		}
		if (emitter.particleEmitterLatitude != null) {
			add(new AnimFlag(emitter.particleEmitterLatitude));
		} else {
			setLatitude(emitter.latitude);
		}
		if (emitter.particleEmitterLifeSpan != null) {
			add(new AnimFlag(emitter.particleEmitterLifeSpan));
		} else {
			setLifeSpan(emitter.lifeSpan);
		}
		if (emitter.particleEmitterLongitude != null) {
			add(new AnimFlag(emitter.particleEmitterLongitude));
		} else {
			setLongitude(emitter.longitude);
		}
		setMDLEmitter(((emitter.node.flags >> 15) & 1) == 1);
		if (!isMDLEmitter() && (((emitter.node.flags >> 8) & 1) == 1)) {
			System.err.println(
					"WARNING in MDX -> MDL: ParticleEmitter of unknown type! Defaults to EmitterUsesTGA in my MDL code!");
		}
		setPath(emitter.spawnModelFileName);
		// if( emitter. != null ) {
		// mdlEmitter.add(new AnimFlag(emitter.attachmentVisibility));
		// }

	}

	@Override
	public IdObject copy() {
		final ParticleEmitter x = new ParticleEmitter();

		x.name = name;
		x.pivotPoint = new Vertex(pivotPoint);
		x.objectId = objectId;
		x.parentId = parentId;
		x.setParent(getParent());

		x.timeDoubleData = timeDoubleData.clone();
		x.MDLEmitter = MDLEmitter;
		x.path = path;
		x.flags.addAll(flags);

		for (final AnimFlag af : animFlags.values()) {
			x.animFlags.put(af.title, new AnimFlag(af));
		}
		return x;
	}

	public static ParticleEmitter read(final BufferedReader mdl) {
		String line = MDLReader.nextLine(mdl);
		if (line.contains("ParticleEmitter")) {
			final ParticleEmitter pe = new ParticleEmitter();
			pe.setName(MDLReader.readName(line));
			MDLReader.mark(mdl);
			line = MDLReader.nextLine(mdl);
			boolean inParticle = false;
			while ((!line.contains("}") || line.contains("},") || line.contains("\t}"))
					&& !line.equals("COMPLETED PARSING")) {
				boolean foundType = false;
				if (line.contains("ObjectId")) {
					pe.objectId = MDLReader.readInt(line);
					foundType = true;
				} else if (line.contains("Parent")) {
					pe.parentId = MDLReader.splitToInts(line)[0];
					foundType = true;
					// pe.parent = mdlr.getIdObject(pe.parentId);
				} else if (line.contains("EmitterUses")) {
					pe.MDLEmitter = line.contains("MDL");
					foundType = true;
				} else if (line.contains("Path")) {
					pe.path = MDLReader.readName(line);
					foundType = true;
				} else if ((line.contains("Visibility") || line.contains("Rotation") || line.contains("Translation")
						|| line.contains("Scaling")) && !line.contains("DontInherit")) {
					addAnimationFlag(mdl, pe);
					foundType = true;
				} else if (line.contains("Particle {")) {
					foundType = true;
					inParticle = true;
				} else if (inParticle && line.contains("\t}")) {
					foundType = true;
					inParticle = false;
				}
				for (int i = 0; (i < timeDoubleNames.length) && !foundType; i++) {
					if (line.contains(timeDoubleNames[i])) {
						foundType = true;
						if (line.contains("static")) {
							pe.timeDoubleData[i] = MDLReader.readDouble(line);
						} else {
							addAnimationFlag(mdl, pe);
						}
					}
				}
				if (!foundType) {
					pe.flags.add(MDLReader.readFlag(line));
				}
				MDLReader.mark(mdl);
				line = MDLReader.nextLine(mdl);
			}
			return pe;
		} else {
			JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
					"Unable to parse ParticleEmitter: Missing or unrecognized open statement.");
		}
		return null;
	}

	private static void addAnimationFlag(BufferedReader mdl, ParticleEmitter pe) {
		MDLReader.reset(mdl);
		AnimFlag animFlag = AnimFlag.read(mdl);
		pe.animFlags.put(animFlag.title, animFlag);
	}

	@Override
	public void printTo(final PrintWriter writer) {
		// Remember to update the ids of things before using this
		// -- uses objectId value of idObject superclass
		// -- uses parentId value of idObject superclass
		// -- uses the parent (java Object reference) of idObject superclass
		final Map<String, AnimFlag> pAnimFlags = new HashMap<>(this.animFlags);
		writer.println(MDLReader.getClassName(this.getClass()) + " \"" + getName() + "\" {");
		if (objectId != -1) {
			writer.println("\tObjectId " + objectId + ",");
		}
		if (parentId != -1) {
			writer.println("\tParent " + parentId + ",\t// \"" + getParent().getName() + "\"");
		}
		if (MDLEmitter) {
			writer.println("\tEmitterUsesMDL,");
		} else {
			writer.println("\tEmitterUsesTGA,");
		}
		String currentFlag = "";
		for (int i = 0; i < 4; i++) {
			printAndRemoveMatchingStatic(writer, pAnimFlags, i, "\tstatic ", 1);
		}
		printAndRemoveMatching(writer, pAnimFlags, "Visibility");

		writer.println("\tParticle {");
		for (int i = 4; i < 6; i++) {
			printAndRemoveMatchingStatic(writer, pAnimFlags, i, "\t\tstatic ", 2);
		}
		if (path != null) {
			writer.println("\t\tPath \"" + path + "\",");
		}
		writer.println("\t}");

		printAndRemoveMatching(writer, pAnimFlags, "Translation");
		printAndRemoveMatching(writer, pAnimFlags, "Rotation");
		printAndRemoveMatching(writer, pAnimFlags, "Scaling");

		for (final String s : flags) {
			writer.println("\t" + s + ",");
		}
		writer.println("}");
	}

	private void printAndRemoveMatchingStatic(PrintWriter writer, Map<String, AnimFlag> pAnimFlags, int i, String s2, int i2) {
		String currentFlag;
		currentFlag = timeDoubleNames[i];
		if (timeDoubleData[i] != 0) {
			writer.println(s2 + currentFlag + " " + MDLReader.doubleToString(timeDoubleData[i]) + ",");
		} else {
			boolean set = false;
			for (int a = 0; (a < pAnimFlags.size()) && !set; a++) {
				if (pAnimFlags.get(a).getName().equals(currentFlag)) {
					pAnimFlags.get(a).printTo(writer, i2);
					pAnimFlags.remove(a);
					set = true;
				}
			}
			if (!set) {
				writer.println(
						s2 + currentFlag + " " + MDLReader.doubleToString(timeDoubleData[i]) + ",");
			}
		}
	}

	private void printAndRemoveMatching(PrintWriter writer, Map<String, AnimFlag> pAnimFlags, String name) {
		for (int i = pAnimFlags.size() - 1; i >= 0; i--) {
			if (pAnimFlags.get(i).getName().equals(name)) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
	}

	// VisibilitySource methods
	@Override
	public void setVisibilityFlag(final AnimFlag flag) {
		int count = 0;
		int index = 0;
		for (int i = 0; i < animFlags.size(); i++) {
			final AnimFlag af = animFlags.get(i);
			if (af.getName().equals("Visibility") || af.getName().equals("Alpha")) {
				count++;
				index = i;
				animFlags.remove(af);
			}
		}
		if (flag != null) {
			animFlags.put(flag.title, flag);
		}
		if (count > 1) {
			JOptionPane.showMessageDialog(null,
					"Some visiblity animation data was lost unexpectedly during overwrite in " + getName() + ".");
		}
	}

	@Override
	public AnimFlag getVisibilityFlag() {
		int count = 0;
		AnimFlag output = null;
		if(animFlags.containsKey("Visibility")){
			output = animFlags.get("Visibility");
		} else if(animFlags.containsKey("Alpha")){
			output = animFlags.get("Alpha");
		}
//		for (final AnimFlag af : animFlags) {
//			if (af.getName().equals("Visibility") || af.getName().equals("Alpha")) {
//				count++;
//				output = af;
//			}
//		}
		if (animFlags.containsKey("Visibility") && animFlags.containsKey("Alpha")) {
			JOptionPane.showMessageDialog(null,
					"Some visiblity animation data was lost unexpectedly during retrieval in " + getName() + ".");
		}
		return output;
	}

	@Override
	public String visFlagName() {
		return "Visibility";
	}

	@Override
	public void flipOver(final byte axis) {
//		final String currentFlag = "Rotation";
		animFlags.get("Rotation").flipOver(axis);
//        for (final AnimFlag flag : animFlags) {
//            flag.flipOver(axis);
//        }
	}

	@Override
	public void add(final String flag) {
		flags.add(flag);
	}

	@Override
	public void add(final AnimFlag af) {
		animFlags.put(af.title, af);
	}

	public double getEmissionRate() {
		return timeDoubleData[TimeDoubles.EmissionRate.ordinal()];
	}

	public void setEmissionRate(final double emissionRate) {
		timeDoubleData[TimeDoubles.EmissionRate.ordinal()] = emissionRate;
	}

	public double getGravity() {
		return timeDoubleData[TimeDoubles.Gravity.ordinal()];
	}

	public void setGravity(final double gravity) {
		timeDoubleData[TimeDoubles.Gravity.ordinal()] = gravity;
	}

	public double getLongitude() {
		return timeDoubleData[TimeDoubles.Longitude.ordinal()];
	}

	public void setLongitude(final double longitude) {
		timeDoubleData[TimeDoubles.Longitude.ordinal()] = longitude;
	}

	public double getLatitude() {
		return timeDoubleData[TimeDoubles.Latitude.ordinal()];
	}

	public void setLatitude(final double latitude) {
		timeDoubleData[TimeDoubles.Latitude.ordinal()] = latitude;
	}

	public double getLifeSpan() {
		return timeDoubleData[TimeDoubles.LifeSpan.ordinal()];
	}

	public void setLifeSpan(final double lifeSpan) {
		timeDoubleData[TimeDoubles.LifeSpan.ordinal()] = lifeSpan;
	}

	public double getInitVelocity() {
		return timeDoubleData[TimeDoubles.InitVelocity.ordinal()];
	}

	public void setInitVelocity(final double initVelocity) {
		timeDoubleData[TimeDoubles.InitVelocity.ordinal()] = initVelocity;
	}

	public boolean isMDLEmitter() {
		return MDLEmitter;
	}

	public void setMDLEmitter(final boolean mDLEmitter) {
		MDLEmitter = mDLEmitter;
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public List<String> getFlags() {
		return flags;
	}

	@Override
	public Map<String, AnimFlag> getAnimFlags() {
		return animFlags;
	}

	@Override
	public void apply(final IdObjectVisitor visitor) {
		visitor.particleEmitter(this);
	}

	@Override
	public double getClickRadius(final CoordinateSystem coordinateSystem) {
		return DEFAULT_CLICK_RADIUS / CoordinateSystem.Util.getZoom(coordinateSystem);
	}

	@Override
	public float getRenderVisibility(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag visibilityFlag = getVisibilityFlag();
		if (visibilityFlag != null) {
			final Number visibility = (Number) visibilityFlag.interpolateAt(animatedRenderEnvironment);
			return visibility.floatValue();
		}
		return 1;
	}

	@Override
	public Vertex getRenderTranslation(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("Translation");
		if (translationFlag != null) {
			return (Vertex) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return null;
	}

	@Override
	public QuaternionRotation getRenderRotation(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("Rotation");
		if (translationFlag != null) {
			return (QuaternionRotation) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return null;
	}

	@Override
	public Vertex getRenderScale(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("Scaling");
		if (translationFlag != null) {
			return (Vertex) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return null;
	}

	public double getRenderSpeed(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("InitVelocity");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return getInitVelocity();
	}

	public double getRenderLatitude(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("Latitude");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return getLatitude();
	}

	public double getRenderLongitude(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("Longitude");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return getLongitude();
	}

	public double getRenderLifeSpan(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("LifeSpan");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return getLifeSpan();
	}

	public double getRenderGravity(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("Gravity");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return getGravity();
	}

	public double getRenderEmissionRate(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("EmissionRate");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return getEmissionRate();
	}
}
