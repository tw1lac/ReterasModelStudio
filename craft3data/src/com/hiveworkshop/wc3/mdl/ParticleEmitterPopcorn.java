package com.hiveworkshop.wc3.mdl;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;
import com.hiveworkshop.wc3.mdl.v2.visitor.IdObjectVisitor;
import com.hiveworkshop.wc3.mdx.CornChunk;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Popcorn FX is what I am calling the CORN chunk, somebody said that's probably
 * what they represent. 2020-08: Changing the name to ParticleEmitterPopcorn to
 * match leaked Blizzard MDL. (one of the builds of the game included an MDL by
 * mistake or something)
 */
public class ParticleEmitterPopcorn extends IdObject implements VisibilitySource {
	Map<String, AnimFlag> animFlags = new HashMap<>();
	private int replaceableId;
	private float alpha;
	private Vertex color;
	private float speed;
	private float emissionRate;
	private float lifeSpan;
	String path = null;
	String animVisibilityGuide = null;
	ArrayList<String> flags = new ArrayList<>();

	private ParticleEmitterPopcorn() {

	}

	public ParticleEmitterPopcorn(final String name) {
		this.name = name;
	}

	public ParticleEmitterPopcorn(final CornChunk.ParticleEmitterPopcorn emitter) {
		this(emitter.node.name);
		// debug print:

		// ----- Convert Base NODE to "IDOBJECT" -----
		loadFrom(emitter.node);
		// ----- End Base NODE to "IDOBJECT" -----

		// System.out.println(attachment.node.name + ": " +
		// Integer.toBinaryString(attachment.unknownNull));

		if (emitter.cornVisibility != null) {
			add(new AnimFlag(emitter.cornVisibility));
		}
		if (emitter.cornEmissionRate != null) {
			add(new AnimFlag(emitter.cornEmissionRate));
		}
		if (emitter.cornAlpha != null) {
			add(new AnimFlag(emitter.cornAlpha));
		}
		if (emitter.cornLifeSpan != null) {
			add(new AnimFlag(emitter.cornLifeSpan));
		}
		if (emitter.cornSpeed != null) {
			add(new AnimFlag(emitter.cornSpeed));
		}
		if (emitter.cornColor != null) {
			add(new AnimFlag(emitter.cornColor));
		}

		lifeSpan = emitter.lifeSpan;
		emissionRate = emitter.emissionRate;
		speed = emitter.speed;
		if (emitter.cornColor == null) {
			final Vertex coloring = new Vertex(MdlxUtils.flipRGBtoBGR(emitter.color));
			if ((coloring.x != 1.0) || (coloring.y != 1.0) || (coloring.z != 1.0)) {
				setColor(coloring);
			}
		}
		alpha = emitter.alpha;
		replaceableId = emitter.replaceableId;
		setPath(emitter.path);
		setAnimVisibilityGuide(emitter.flags);
		// if( emitter. != null ) {
		// mdlEmitter.add(new AnimFlag(emitter.attachmentVisibility));
		// }

	}

	@Override
	public IdObject copy() {
		final ParticleEmitterPopcorn x = new ParticleEmitterPopcorn();

		x.name = name;
		x.pivotPoint = new Vertex(pivotPoint);
		x.objectId = objectId;
		x.parentId = parentId;
		x.setParent(getParent());

		x.path = path;
		x.animVisibilityGuide = animVisibilityGuide;
		x.replaceableId = replaceableId;
		x.alpha = alpha;
		if (color != null) {
			x.color = new Vertex(color);
		} else {
			x.color = null;
		}
		x.speed = speed;
		x.emissionRate = emissionRate;
		x.lifeSpan = lifeSpan;

		for (final AnimFlag af : animFlags.values()) {
			x.animFlags.put(af.title, new AnimFlag(af));
		}
		return x;
	}

	public static ParticleEmitterPopcorn read(final BufferedReader mdl) {
		String line = MDLReader.nextLine(mdl);
		if (line.contains("PopcornFxEmitter") || line.contains("ParticleEmitterPopcorn")) {
			final ParticleEmitterPopcorn pe = new ParticleEmitterPopcorn();
			pe.setName(MDLReader.readName(line));
			MDLReader.mark(mdl);
			line = MDLReader.nextLine(mdl);
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
				} else if (line.contains("Path")) {
					pe.path = MDLReader.readName(line);
					foundType = true;
				} else if (line.contains("static LifeSpan")) {
					pe.lifeSpan = (float) MDLReader.readDouble(line);
					foundType = true;
				} else if (line.contains("static EmissionRate")) {
					pe.emissionRate = (float) MDLReader.readDouble(line);
					foundType = true;
				} else if (line.contains("static Speed")) {
					pe.speed = (float) MDLReader.readDouble(line);
					foundType = true;
				} else if (line.contains("static Color")) {
					pe.color = Vertex.parseText(line);
					foundType = true;
				} else if (line.contains("static Alpha")) {
					pe.alpha = (float) MDLReader.readDouble(line);
					foundType = true;
				} else if (line.contains("ReplaceableId")) {
					pe.replaceableId = MDLReader.readInt(line);
					foundType = true;
				} else if (line.contains("FlagString") || line.contains("AnimVisibilityGuide")) {
					pe.animVisibilityGuide = MDLReader.readName(line);
					foundType = true;
				} else if ((line.contains("Visibility") || line.contains("Rotation") || line.contains("Translation")
						|| line.contains("Scaling") || line.contains("Alpha") || line.contains("EmissionRate")
						|| line.contains("Speed") || line.contains("LifeSpan")) && !line.contains("DontInherit")) {
					MDLReader.reset(mdl);
					AnimFlag animFlag = AnimFlag.read(mdl);
					pe.animFlags.put(animFlag.title, animFlag);
					foundType = true;
				} else if (line.contains("SegmentColor")) {
					final float[] maybeColor = new float[8];
					boolean reading = true;
					foundType = true;
					for (int i = 0; reading && (i < 2); i++) {
						line = MDLReader.nextLine(mdl);
						if (line.contains("Color")) {
							// Reuse my quaternion parser, not actually smart
							final QuaternionRotation quatern = QuaternionRotation.parseText(line);
							maybeColor[i * 4] = (float) quatern.a;
							maybeColor[(i * 4) + 1] = (float) quatern.b;
							maybeColor[(i * 4) + 2] = (float) quatern.c;
							maybeColor[(i * 4) + 3] = (float) quatern.d;
						} else {
							reading = false;
							MDLReader.reset(mdl);
							line = MDLReader.nextLine(mdl);
						}
					}
					pe.lifeSpan = maybeColor[0];
					pe.emissionRate = maybeColor[1];
					pe.speed = maybeColor[2];
					pe.color = new Vertex(maybeColor[5], maybeColor[4], maybeColor[3]);
					pe.alpha = maybeColor[6];
					pe.replaceableId = (int) maybeColor[7];
					line = MDLReader.nextLine(mdl);
				} else if (line.contains("Color")) {
					foundType = true;
					MDLReader.reset(mdl);
					AnimFlag animFlag = AnimFlag.read(mdl);
					pe.animFlags.put(animFlag.title, animFlag);
				}
				if (!foundType) {
					pe.flags.add(MDLReader.readFlag(line));
				}
				MDLReader.mark(mdl);
				line = MDLReader.nextLine(mdl);
			}
			return pe;
		} else

		{
			JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
					"Unable to parse ParticleEmitterPopcorn: Missing or unrecognized open statement.");
		}
		return null;
	}

	@Override
	public void printTo(final PrintWriter writer) {
		// Remember to update the ids of things before using this
		// -- uses objectId value of idObject superclass
		// -- uses parentId value of idObject superclass
		// -- uses the parent (java Object reference) of idObject superclass
		final ArrayList<AnimFlag> pAnimFlags = new ArrayList<>(this.animFlags.values());
		writer.println(MDLReader.getClassName(this.getClass()) + " \"" + getName() + "\" {");
		if (objectId != -1) {
			writer.println("\tObjectId " + objectId + ",");
		}
		if (parentId != -1) {
			writer.println("\tParent " + parentId + ",\t// \"" + getParent().getName() + "\"");
		}
		for (final String s : flags) {
			writer.println("\t" + s + ",");
		}

		printAndRemoveMatching(writer, pAnimFlags, "LifeSpan", "\tstatic LifeSpan ", lifeSpan);
		printAndRemoveMatching(writer, pAnimFlags, "EmissionRate", "\tstatic EmissionRate ", emissionRate);
		printAndRemoveMatching(writer, pAnimFlags, "Speed", "\tstatic Speed ", speed);
		printAndRemoveMatching2(writer, pAnimFlags, "Color");

		printAndRemoveMatching3(writer, pAnimFlags, "Visibility");
		printAndRemoveMatching(writer, pAnimFlags, "Alpha", "\tstatic Alpha ", alpha);

		if (replaceableId != 0) {
			writer.println("\tReplaceableId " + replaceableId + ",");
		}
		if (path != null) {
			writer.println("\tPath \"" + path + "\",");
		}
		if (animVisibilityGuide != null) {
			writer.println("\tAnimVisibilityGuide \"" + animVisibilityGuide + "\",");
		}

		printAndRemoveMatchingSimple(writer, pAnimFlags, "Translation");
		printAndRemoveMatchingSimple(writer, pAnimFlags, "Rotation");
		printAndRemoveMatchingSimple(writer, pAnimFlags, "Scaling");
		writer.println("}");
	}

	private void printAndRemoveMatching3(PrintWriter writer, ArrayList<AnimFlag> pAnimFlags, String currentFlag) {
		for (int i = 0; i < pAnimFlags.size(); i++) {
			if (pAnimFlags.get(i).getName().equals(currentFlag)) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
	}

	private void printAndRemoveMatchingSimple(PrintWriter writer, ArrayList<AnimFlag> pAnimFlags, String translation) {
		for (int i = pAnimFlags.size() - 1; i >= 0; i--) {
			if (pAnimFlags.get(i).getName().equals(translation)) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
			}
		}
	}

	private void printAndRemoveMatching2(PrintWriter writer, ArrayList<AnimFlag> pAnimFlags, String currentFlag) {
		boolean foundFlag  = false;
		foundFlag = isFoundFlag(writer, pAnimFlags, currentFlag, foundFlag);
		if (!foundFlag && (color != null)) {
			writer.println("\tstatic Color " + color.toString() + ",");
		}
	}

	private boolean isFoundFlag(PrintWriter writer, ArrayList<AnimFlag> pAnimFlags, String currentFlag, boolean foundFlag) {
		for (int i = 0; i < pAnimFlags.size(); i++) {
			if (pAnimFlags.get(i).getName().equals(currentFlag)) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
				foundFlag = true;
			}
		}
		return foundFlag;
	}

	private void printAndRemoveMatching(PrintWriter writer, ArrayList<AnimFlag> pAnimFlags, String currentFlag, String s2, float emissionRate) {
		boolean foundFlag = false;
		for (int i = 0; i < pAnimFlags.size(); i++) {
			if (pAnimFlags.get(i).getName().equals(currentFlag)) {
				pAnimFlags.get(i).printTo(writer, 1);
				pAnimFlags.remove(i);
				foundFlag = true;
			}
		}
		if (!foundFlag) {
			writer.println(s2 + MDLReader.doubleToString(emissionRate) + ",");
		}
	}

	// VisibilitySource methods
	@Override
	public void setVisibilityFlag(final AnimFlag flag) {
		int count = 0;
		int index = 0;
		for (int i = 0; i < animFlags.size(); i++) {
			final AnimFlag af = animFlags.get(i);
			if (af.getName().equals("Visibility")) {
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
		for (final AnimFlag af : animFlags.values()) {
			if (af.getName().equals("Visibility")) {
				count++;
				output = af;
			}
		}
		if (count > 1) {
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
		final String currentFlag = "Rotation";
        for (final AnimFlag flag : animFlags.values()) {
            flag.flipOver(axis);
        }
	}

	@Override
	public void add(final String flag) {
		flags.add(flag);
	}

	@Override
	public void add(final AnimFlag af) {
		animFlags.put(af.title, af);
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setAnimVisibilityGuide(final String flagString) {
		this.animVisibilityGuide = flagString;
	}

	public String getAnimVisibilityGuide() {
		return animVisibilityGuide;
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
		visitor.popcornFxEmitter(this);
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

	public double getRenderEmissionRate(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = animFlags.get("EmissionRate");
		if (translationFlag != null) {
			return (Double) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return 0;
	}

	public Vertex getColor() {
		return color;
	}

	public void setColor(final Vertex color) {
		this.color = color;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}

	public float getEmissionRate() {
		return emissionRate;
	}

	public void setEmissionRate(final float emissionRate) {
		this.emissionRate = emissionRate;
	}

	public float getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(final float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}

	public int getReplaceableId() {
		return replaceableId;
	}

	public void setReplaceableId(final int replaceableId) {
		this.replaceableId = replaceableId;
	}
}
