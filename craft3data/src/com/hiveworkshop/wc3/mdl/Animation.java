package com.hiveworkshop.wc3.mdl;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.hiveworkshop.wc3.gui.animedit.BasicTimeBoundProvider;
import com.hiveworkshop.wc3.mdx.SequenceChunk;

/**
 * A java object to represent MDL "Sequences" ("Animations").
 *
 * Eric Theller 11/5/2011
 */
public class Animation implements BasicTimeBoundProvider {
	private String name = "";
	private int intervalStart = 0;
	private int intervalEnd = -1;
	private ArrayList<String> tags = new ArrayList<>();// These are strings tags, i.e.
	// "MoveSpeed X," "Rarity X,"
	// "NonLooping," etc.
	private ExtLog extents;

	private Animation() {

	}

	public boolean equalsAnim(final Animation other) {
		return other.name.equals(this.name) && (other.intervalStart == intervalStart)
				&& (other.intervalEnd == intervalEnd) && other.tags.equals(tags);
	}

	public Animation(final String name, final int intervalStart, final int intervalEnd) {
		this.name = name;
		this.intervalStart = intervalStart;
		this.intervalEnd = intervalEnd;
		extents = new ExtLog(ExtLog.DEFAULT_MINEXT, ExtLog.DEFAULT_MAXEXT, ExtLog.DEFAULT_BOUNDSRADIUS);
	}

	public Animation(final String name, final int intervalStart, final int intervalEnd, final Vertex minimumExt,
			final Vertex maximumExt, final double boundsRad) {
		this.name = name;
		this.intervalStart = intervalStart;
		this.intervalEnd = intervalEnd;
		extents = new ExtLog(minimumExt, maximumExt, boundsRad);
	}

	// construct for simple animation object, within geoset
	public Animation(final ExtLog extents) {
		name = "";
		this.extents = extents;
	}

	/**
	 * Construct an Animation from an MDX sequence chunk
	 *
	 * @param seq The MDX sequence chunk to convert
	 */
	public Animation(final SequenceChunk.Sequence seq) {
		this(seq.name, seq.intervalStart, seq.intervalEnd);
		setExtents(new ExtLog(seq.minimumExtent, seq.maximumExtent, seq.boundsRadius));
		if (seq.moveSpeed != 0) {
			addTag("MoveSpeed " + seq.moveSpeed);
		}
		if (seq.nonLooping == 1) {
			addTag("NonLooping");
		}
		if (seq.rarity > 0) {
			addTag("Rarity " + seq.rarity);
		}
	}

	public float getRarity() {
		for (final String tag : tags) {
			if (tag.startsWith("Rarity")) {
				return Float.parseFloat(tag.split(" ")[1]);
			}
		}
		return 0.0f;
	}

	public void setRarity(final float newRarity) {
		boolean foundTag = false;
		for (int i = 0; (i < tags.size()) && !foundTag; i++) {
			final String tag = tags.get(i);
			if (tag.startsWith("Rarity")) {
				tags.set(i, "Rarity " + MDLReader.doubleToString(newRarity));
				foundTag = true;
			}
		}
		if (!foundTag) {
			tags.add("Rarity " + MDLReader.doubleToString(newRarity));
		}
	}

	public void setMoveSpeed(final float newMoveSpeed) {
		boolean foundTag = false;
		for (int i = 0; (i < tags.size()) && !foundTag; i++) {
			final String tag = tags.get(i);
			if (tag.startsWith("MoveSpeed")) {
				tags.set(i, "MoveSpeed " + MDLReader.doubleToString(newMoveSpeed));
				foundTag = true;
			}
		}
		if (!foundTag) {
			tags.add("MoveSpeed " + MDLReader.doubleToString(newMoveSpeed));
		}
	}

	public float getMoveSpeed() {
		for (final String tag : tags) {
			if (tag.startsWith("MoveSpeed")) {
				return Float.parseFloat(tag.split(" ")[1]);
			}
		}
		return 0.0f;
	}

	public Animation(final Animation other) {
		this.name = other.name;
		intervalStart = other.intervalStart;
		intervalEnd = other.intervalEnd;
		tags = new ArrayList<>(other.tags);
		extents = new ExtLog(other.extents);
	}

	public void addTag(final String tag) {
		tags.add(tag);
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(final ArrayList<String> tags) {
		this.tags = tags;
	}

	public ExtLog getExtents() {
		return extents;
	}

	public void setExtents(final ExtLog extents) {
		this.extents = extents;
	}

	public void setName(final String text) {
		this.name = text;
	}

	public String getName() {
		return this.name;
	}

	public boolean isNonLooping() {
		return tags.contains("NonLooping");
	}

	public void setNonLooping(final boolean nonLooping) {
		if (isNonLooping()) {
			if (!nonLooping) {
				tags.remove("NonLooping");
			}
		} else {
			if (nonLooping) {
				tags.add("NonLooping");
			}
		}
	}

	public int length() {
		return intervalEnd - intervalStart;
	}

	public void setInterval(final int start, final int end) {
		intervalStart = start;
		intervalEnd = end;
	}

	public void setIntervalStart(final int intervalStart) {
		this.intervalStart = intervalStart;
	}

	public void setIntervalEnd(final int intervalEnd) {
		this.intervalEnd = intervalEnd;
	}

	public void copyToInterval(final int start, final int end, final List<AnimFlag> flags,
			final List<EventObject> eventObjs, final List<AnimFlag> newFlags, final List<EventObject> newEventObjs) {
		for (final AnimFlag af : newFlags) {
			if (!af.hasGlobalSeq) {
				af.copyFrom(flags.get(newFlags.indexOf(af)), intervalStart, intervalEnd, start, end);
			}
		}
		for (final EventObject e : newEventObjs) {
			if (!e.hasGlobalSeq) {
				e.copyFrom(eventObjs.get(newEventObjs.indexOf(e)), intervalStart, intervalEnd, start, end);
			}
		}
	}

	public void copyToInterval(final int start, final int end, final List<AnimFlag> flags,
			final List<EventObject> eventObjs) {
		for (final AnimFlag af : flags) {
			if (!af.hasGlobalSeq) {
				af.copyFrom(new AnimFlag(af), intervalStart, intervalEnd, start, end);
			}
		}
		for (final EventObject e : eventObjs) {
			if (!e.hasGlobalSeq) {
				e.copyFrom(e.copy(), intervalStart, intervalEnd, start, end);
			}
		}
	}

	public void setInterval(final int start, final int end, final List<AnimFlag> flags,
			final List<EventObject> eventObjs) {
		for (final AnimFlag af : flags) {
			if (!af.hasGlobalSeq) {
				af.timeScale(intervalStart, intervalEnd, start, end);
			}
		}
		for (final EventObject e : eventObjs) {
			if (!e.hasGlobalSeq) {
				e.timeScale(intervalStart, intervalEnd, start, end);
			}
		}
		intervalStart = start;
		intervalEnd = end;
	}

	public void reverse(final List<AnimFlag> flags, final List<EventObject> eventObjs) {
		for (final AnimFlag af : flags) {
			if (!af.hasGlobalSeq && ((af.getTypeId() == 1) || (af.getTypeId() == 2) || (af.getTypeId() == 3))) {
				af.timeScale(intervalStart, intervalEnd, intervalEnd, intervalStart);
			}
		}
		for (final EventObject e : eventObjs) {
			e.timeScale(intervalStart, intervalEnd, intervalEnd, intervalStart);
		}
		// for( AnimFlag af: flags )
		// {
		// if( !af.hasGlobalSeq && (af.getTypeId() == 1 || af.getTypeId() == 2
		// || af.getTypeId() == 3 ) ) // wouldn't want to mess THAT up...
		// af.timeScale(m_intervalStart, m_intervalEnd, m_intervalStart+30,
		// m_intervalStart+2);
		// }
		// for( EventObject e: eventObjs )
		// {
		// e.timeScale(m_intervalStart, m_intervalEnd, m_intervalStart+30,
		// m_intervalStart+2);
		// }
	}

	public void clearData(final List<AnimFlag> flags, final List<EventObject> eventObjs) {
		for (final AnimFlag af : flags) {
			if (((af.getTypeId() == 1) || (af.getTypeId() == 2) || (af.getTypeId() == 3))) {
				// !af.hasGlobalSeq && was above before
				af.deleteAnim(this);// timeScale(m_intervalStart, m_intervalEnd,
									// m_intervalEnd, m_intervalStart);
			}
		}
		for (final EventObject e : eventObjs) {
			e.deleteAnim(this);
		}
	}

	public void setInterval(final int start, final int end, final EditableModel mdlr) {
		final List<AnimFlag> aniFlags = mdlr.getAllAnimFlags();
		final ArrayList<EventObject> eventObjects = mdlr.sortedIdObjects(EventObject.class);
		setInterval(start, end, aniFlags, eventObjects);
	}

	@Override
	public int getStart() {
		return intervalStart;
	}

	@Override
	public int getEnd() {
		return intervalEnd;
	}

	public static Animation read(final BufferedReader mdl) {
		final Animation anim = new Animation();
		boolean limited = false;
		String line = MDLReader.nextLine(mdl);
		try {
			anim.setName(line.split("\"")[1]);
			if (anim.name.equals("")) {
				anim.name = " ";
			}
		} catch (final Exception e) {
			// System.out.println("Throwing nameless anim from:
			// "+MDLReader.nextLine(mdl));//Read the word "Anim" from the top
			limited = true;
		}
		if (!limited) {
			// JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),"This
			// popup means Anims were interpreted as outside geoset!");
			// try
			// {
			// String [] entries = MDLReader.nextLine(mdl).split("Interval
			// ")[1].split(",");//Split the line into pieces, forming two
			// entries that are before and after the comma in "{ <number>,
			// <number> }"
			// entries[0] = entries[0].substring(2,entries[0].length());//Shave
			// off first two chars "{ "
			// entries[1] =
			// entries[1].substring(0,entries[1].length()-2);//Shave off last
			// two " }"
			final int[] bits = MDLReader.splitToInts(MDLReader.nextLine(mdl));
			anim.setInterval(bits[0], bits[1]);
			// }
			// catch (NumberFormatException e)
			// {
			// JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),"Unable
			// to parse animation: Interval could not be interpreted
			// numerically.\nThis may break lots of things.");
			// }
		}
		MDLReader.mark(mdl);
		line = MDLReader.nextLine(mdl);
		while (!(line).startsWith("\t}")) {
			if (line.contains("Extent") || (line).contains("BoundsRadius")) {
				MDLReader.reset(mdl);
				anim.extents = ExtLog.read(mdl);
			} else {
				anim.tags.add(MDLReader.readFlag(line));
			}
			MDLReader.mark(mdl);
			line = MDLReader.nextLine(mdl);
		}
		return anim;
	}

	public void printTo(final PrintWriter writer, final int tabHeight) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < tabHeight; i++) {
			tabs.append("\t");
		}
		if (!this.name.equals("")) {
			writer.println(tabs + "Anim \"" + this.name + "\" {");
		} else {
			writer.println(tabs + "Anim {");
		}
		if ((intervalEnd - intervalStart) > 0) {
			writer.println(tabs + "\tInterval { " + intervalStart + ", " + intervalEnd + " },");
		}
		for (String tag : tags) {
			writer.println(tabs + "\t" + tag + ",");
		}
		if (extents != null) {
			extents.printTo(writer, tabHeight + 1);
		}
		writer.println(tabs + "}");
	}

	@Override
	public String toString() {
		return getName();
	}

	public int getIntervalStart() {
		return intervalStart;
	}

	public int getIntervalEnd() {
		return intervalEnd;
	}

}
