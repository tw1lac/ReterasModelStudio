package com.hiveworkshop.wc3.mdl;

import com.hiveworkshop.wc3.gui.modeledit.CoordinateSystem;
import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;
import com.hiveworkshop.wc3.mdl.v2.visitor.IdObjectVisitor;
import com.hiveworkshop.wc3.mdx.BoneChunk;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Bones that make geometry animate.
 *
 * Eric Theller 11/10/2011
 */
public class Bone extends IdObject {
	int geosetId = -1;
	boolean multiGeoId;
	Geoset geoset;

	int geosetAnimId = -1;
	GeosetAnim geosetAnim;
	boolean hasGeoAnim;// Sometimes its "None," sometimes it's not used

	ArrayList<AnimFlag> animFlags = new ArrayList<>();
	ArrayList<String> flags = new ArrayList<>();

	public Bone() {

	}

	public Bone(final String name) {
		this.name = name;
		this.pivotPoint = new Vertex(0, 0, 0);
	}

	public Bone(final Bone b) {
		name = b.name;
		pivotPoint = new Vertex(b.pivotPoint);
		objectId = b.objectId;
		parentId = b.parentId;
		setParent(b.getParent());

		geosetId = b.geosetId;
		multiGeoId = b.multiGeoId;
		geoset = b.geoset;
		geosetAnimId = b.geosetAnimId;
		geosetAnim = b.geosetAnim;
		hasGeoAnim = b.hasGeoAnim;
		for (final AnimFlag af : b.animFlags) {
			animFlags.add(new AnimFlag(af));
		}
		flags = new ArrayList<>(b.flags);
	}

	public Bone(final BoneChunk.Bone bone) {
		// debug print:
		// System.out.println(mdlBone.getName() + ": " +
		// Integer.toBinaryString(bone.node.flags));
		if ((bone.node.flags & 256) != 256) {
			System.err.println("MDX -> MDL error: A bone '" + bone.node.name + "' not flagged as bone in MDX!");
		}
		// ----- Convert Base NODE to "IDOBJECT" -----
		loadFrom(bone.node);
		// ----- End Base NODE to "IDOBJECT" -----

		geosetId = bone.geosetId;
		geosetAnimId = bone.geosetAnimationId;
	}

	public static Bone read(final BufferedReader mdl) {
		String line = MDLReader.nextLine(mdl);
		if (line.contains("Bone")) {
			final Bone b = new Bone();
			b.setName(MDLReader.readName(line));
			MDLReader.mark(mdl);
			line = MDLReader.nextLine(mdl);
			while ((!line.contains("}") || line.contains("},") || line.contains("\t}"))
					&& !line.equals("COMPLETED PARSING")) {
				if (line.contains("ObjectId")) {
					b.objectId = MDLReader.readInt(line);
				} else if (line.contains("GeosetId")) {
					final String field = MDLReader.readField(line);
					try {
						b.geosetId = Integer.parseInt(field);
						// b.geoset = mdlr.getGeoset(b.geosetId);
					} catch (final Exception e) {
						if (field.equals("Multiple")) {
							b.multiGeoId = true;
						} else {
							JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
									"Error while parsing: Could not interpret integer from: " + line);
						}
					}
				} else if (line.contains("GeosetAnimId")) {
					final String field = MDLReader.readField(line);
					b.hasGeoAnim = true;
					try {
						b.geosetAnimId = Integer.parseInt(field);
						// b.geosetAnim = mdlr.getGeosetAnim(b.geosetAnimId);
					} catch (final Exception e) {
						if (field.equals("None")) {
							b.geosetAnim = null;
						} else {
							JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
									"Error while parsing: Could not interpret integer from: " + line);
						}
					}
				} else if (line.contains("Parent")) {
					b.parentId = MDLReader.splitToInts(line)[0];
					// b.parent = mdlr.getIdObject(b.parentId);
				} else if ((line.contains("Scaling") || line.contains("Rotation") || line.contains("Translation"))
						&& !line.contains("DontInherit")) {
					MDLReader.reset(mdl);
					b.animFlags.add(AnimFlag.read(mdl));
				} else// Flags like Billboarded
				{
					b.flags.add(MDLReader.readFlag(line));
				}
				MDLReader.mark(mdl);
				line = MDLReader.nextLine(mdl);
			}
			return b;
		} else {
			JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
					"Unable to parse Bone: Missing or unrecognized open statement.");
		}
		return null;
	}

	@Override
	public void printTo(final PrintWriter writer) {
		// Remember to update the ids of things before using this
		// -- uses objectId value of idObject superclass
		// -- uses parentId value of idObject superclass
		// -- uses the parent (java Object reference) of idObject superclass
		// -- uses geosetAnimId
		// -- uses geosetId
		writer.println(MDLReader.getClassName(this.getClass()) + " \"" + getName() + "\" {");
		if (objectId != -1) {
			writer.println("\tObjectId " + objectId + ",");
		}
		if (parentId != -1) {
			writer.println("\tParent " + parentId + ",\t// \"" + getParent().getName() + "\"");
		}
        for (String flag : flags) {
            writer.println("\t" + flag + ",");
        }
		if (multiGeoId) {
			writer.println("\tGeosetId Multiple,");
		} else if (geosetId != -1) {
			writer.println("\tGeosetId " + geosetId + ",");
		}
		if (this.getClass() == Bone.class)// hasGeoAnim ) HELPERS DONT SEEM TO
											// HAVE GEOSET ANIM ID
		{
			if (geosetAnim == null || geosetAnimId == -1) {
				writer.println("\tGeosetAnimId None,");
			} else {
				writer.println("\tGeosetAnimId " + geosetAnimId + ",");
			}
		}

		// if( this.getClass() == Bone.class )
		// {
		// // writer.println("\tGeosetId Multiple,");
		// writer.println("\tGeosetAnimId None,");
		// }
        for (AnimFlag animFlag : animFlags) {
            animFlag.printTo(writer, 1);
        }
		writer.println("}");
	}

	public void copyMotionFrom(final Bone b) {
		for (final AnimFlag baf : b.animFlags) {
			boolean foundMatch = false;
			for (final AnimFlag af : animFlags) {
				boolean sameSeq = false;
				if (baf.globalSeq == null && af.globalSeq == null) {
					sameSeq = true;
				} else if (baf.globalSeq != null && af.globalSeq != null) {
					sameSeq = baf.globalSeq.equals(af.globalSeq);
				}
				if (baf.getName().equals(af.getName()) && sameSeq && baf.hasGlobalSeq == af.hasGlobalSeq) {
					// if( && baf.tags.equals(af.tags)
					foundMatch = true;
					af.copyFrom(baf);
				}
			}
			if (!foundMatch) {
				animFlags.add(baf);
			}
		}
	}

	public void clearAnimation(final Animation a) {
		for (final AnimFlag af : animFlags) {
			af.deleteAnim(a);
		}
	}

	/**
	 * Returns true if this bone contains some type of data that moves, scales, rotates, or otherwise changes based on
	 * the time track.
	 *
	 */
	public boolean animates() {
		for (final AnimFlag af : animFlags) {
			if (af.size() > 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IdObject copy() {
		return new Bone(this);
	}

	@Override
	public void flipOver(final byte axis) {
		final String currentFlag = "Rotation";
        for (final AnimFlag flag : animFlags) {
            flag.flipOver(axis);
        }
	}

	@Override
	public void add(final String flag) {
		flags.add(flag);
	}

	@Override
	public void add(final AnimFlag af) {
		animFlags.add(af);
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public void setGeoset(final Geoset geoset) {
		this.geoset = geoset;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public void setGeosetAnim(final GeosetAnim geosetAnim) {
		this.geosetAnim = geosetAnim;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public int getGeosetId() {
		return geosetId;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public void setGeosetId(final int geosetId) {
		this.geosetId = geosetId;
		setMultiGeoId(geosetId == -1);
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public boolean isMultiGeoId() {
		return multiGeoId;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public void setMultiGeoId(final boolean multiGeoId) {
		this.multiGeoId = multiGeoId;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public int getGeosetAnimId() {
		return geosetAnimId;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public void setGeosetAnimId(final int geosetAnimId) {
		this.geosetAnimId = geosetAnimId;
		setHasGeoAnim(geosetAnimId != -1);
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public boolean isHasGeoAnim() {
		return hasGeoAnim;
	}

	/**
	 * @deprecated Recalculated on save
	 */
	@Deprecated
	public void setHasGeoAnim(final boolean hasGeoAnim) {
		this.hasGeoAnim = hasGeoAnim;
	}

	@Override
	public List<String> getFlags() {
		return flags;
	}

	@Override
	public ArrayList<AnimFlag> getAnimFlags() {
		return animFlags;
	}

	@Override
	public void apply(final IdObjectVisitor visitor) {
		visitor.bone(this);
	}

	@Override
	public double getClickRadius(final CoordinateSystem coordinateSystem) {
		return DEFAULT_CLICK_RADIUS * 3 / CoordinateSystem.Util.getZoom(coordinateSystem);
	}

	@Override
	public float getRenderVisibility(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		if (geosetAnim != null) {
			return geosetAnim.getRenderVisibility(animatedRenderEnvironment);
		}
		return 1;
	}

	@Override
	public Vertex getRenderTranslation(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = AnimFlag.find(animFlags, "Translation");
		if (translationFlag != null) {
			return (Vertex) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return null;
	}

	@Override
	public QuaternionRotation getRenderRotation(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = AnimFlag.find(animFlags, "Rotation");
		if (translationFlag != null) {
			return (QuaternionRotation) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return null;
	}

	@Override
	public Vertex getRenderScale(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		final AnimFlag translationFlag = AnimFlag.find(animFlags, "Scaling");
		if (translationFlag != null) {
			return (Vertex) translationFlag.interpolateAt(animatedRenderEnvironment);
		}
		return null;
	}
}
