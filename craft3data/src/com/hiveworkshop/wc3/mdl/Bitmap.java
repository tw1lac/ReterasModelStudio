package com.hiveworkshop.wc3.mdl;

import com.hiveworkshop.wc3.mdx.TextureChunk;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * A class to represent MDL texture references. (Not materials)
 *
 * Eric Theller 11/5/2011
 */
public class Bitmap {
	private String imagePath = "";
	private int replaceableId = -1;
	private int wrapStyle;// 0 = nothing, 1 = WrapWidth, 2 = WrapHeight, 3 =
							// both

	public String getPath() {
		return imagePath;
	}

	public String getName() {
		if (!imagePath.equals("")) {
			try {
				final String[] bits = imagePath.split("\\\\");
				return bits[bits.length - 1].split("\\.")[0];
			} catch (final Exception e) {
				return "bad blp path";
			}
		} else {
			if (replaceableId == 1) {
				return "Team Color";
			} else if (replaceableId == 2) {
				return "Team Glow";
			} else {
				return "Replaceable" + replaceableId;
			}
		}
	}

	public int getReplaceableId() {
		return replaceableId;
	}

	public void setReplaceableId(final int replaceableId) {
		this.replaceableId = replaceableId;
	}

	public Bitmap(final String imagePath, final int replaceableId) {
		this.imagePath = imagePath;
		this.replaceableId = replaceableId;
	}

	public Bitmap(final Bitmap other) {
		imagePath = other.imagePath;
		replaceableId = other.replaceableId;
		wrapStyle = other.wrapStyle;
	}

	public Bitmap(final TextureChunk.Texture tex) {
		this(tex.fileName, tex.replaceableId);
		if ((replaceableId == 0) && !imagePath.equals("")) {
			replaceableId = -1; // nice and tidy it up for the MDL code
		}
		setWrapStyle(tex.flags);
	}

	public Bitmap(final String imagePath) {
		this.imagePath = imagePath;
	}

	private Bitmap() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((imagePath == null) ? 0 : imagePath.hashCode());
		result = (prime * result) + replaceableId;
		result = (prime * result) + wrapStyle;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Bitmap other = (Bitmap) obj;
		if (imagePath == null) {
			if (other.imagePath != null) {
				return false;
			}
		} else if (!imagePath.equals(other.imagePath)) {
			return false;
		}
		return replaceableId == other.replaceableId && wrapStyle == other.wrapStyle;
	}

	// @Override
	// public boolean equals(Object o)
	// {
	// if( !(o instanceof Bitmap ) )
	// {
	// return false;
	// }
	// Bitmap b = (Bitmap)o;
	// boolean does = imagePath.equals(b.imagePath)
	// && replaceableId == b.replaceableId
	// && wrapStyle == b.wrapStyle;
	// return does;
	// }
	public boolean isWrapHeight() {
		return (wrapStyle == 2) || (wrapStyle == 3);
	}

	public void setWrapHeight(final boolean flag) {
		if (flag) {
			if (wrapStyle == 1) {
				wrapStyle = 3;
			} else if (wrapStyle == 0) {
				wrapStyle = 2;
			}
		} else {
			if (wrapStyle == 3) {
				wrapStyle = 1;
			} else if (wrapStyle == 2) {
				wrapStyle = 0;
			}
		}
	}

	public boolean isWrapWidth() {
		return (wrapStyle == 1) || (wrapStyle == 3);
	}

	public void setWrapWidth(final boolean flag) {
		if (flag) {
			if (wrapStyle == 2) {
				wrapStyle = 3;
			} else if (wrapStyle == 0) {
				wrapStyle = 1;
			}
		} else {
			if (wrapStyle == 3) {
				wrapStyle = 2;
			} else if (wrapStyle == 1) {
				wrapStyle = 0;
			}
		}
	}

	public int getWrapStyle() {
		return wrapStyle;
	}

	public void setWrapStyle(final int wrapStyle) {
		this.wrapStyle = wrapStyle;
	}

	public static Bitmap read(final BufferedReader mdl) {
		String line = "";
		if ((line = MDLReader.nextLine(mdl)).contains("Bitmap")) {
			final Bitmap tex = new Bitmap();
			while (!(line = MDLReader.nextLine(mdl)).contains("\t}")) {
				if (line.contains("Image")) {
					tex.imagePath = line.split("\"")[1];
				} else if (line.contains("ReplaceableId ")) {
					tex.replaceableId = MDLReader.readInt(line);
				} else if (line.contains("WrapWidth")) {
					tex.setWrapWidth(true);
				} else if (line.contains("WrapHeight")) {
					tex.setWrapHeight(true);
				} else {
					JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
							"Error parsing Bitmap: Unrecognized statement '" + line + "'.");
				}
			}
			return tex;
		} else {
			JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
					"Unable to parse Bitmap: Missing or unrecognized open statement.");
		}
		return null;
	}

	public static ArrayList<Bitmap> readAll(final BufferedReader mdl) {
		String line = "";
		final ArrayList<Bitmap> outputs = new ArrayList<>();
		MDLReader.mark(mdl);
		if ((line = MDLReader.nextLine(mdl)).contains("Textures")) {
			MDLReader.mark(mdl);
			while (!(line = MDLReader.nextLine(mdl)).startsWith("}")) {
				MDLReader.reset(mdl);
				outputs.add(read(mdl));
				MDLReader.mark(mdl);
			}
			return outputs;
		} else {
			MDLReader.reset(mdl);
			// JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),"Unable
			// to parse Textures: Missing or unrecognized open statement.");
		}
		return outputs;
	}

	public void printTo(final PrintWriter writer, final int tabHeight) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < tabHeight; i++) {
			tabs.append("\t");
		}
		writer.println(tabs + "Bitmap {");
		writer.println(tabs + "\tImage \"" + imagePath + "\",");
		if (replaceableId != -1) {
			writer.println(tabs + "\tReplaceableId " + replaceableId + ",");
		}
		switch (wrapStyle) {
		case 0:
			break;
		case 1:
			writer.println(tabs + "\tWrapWidth,");
			break;
		case 2:
			writer.println(tabs + "\tWrapHeight,");
			break;
		case 3:
			writer.println(tabs + "\tWrapWidth,");
			writer.println(tabs + "\tWrapHeight,");
			break;
		}
		writer.println(tabs + "}");
	}

	public void setPath(final String imagePath) {
		this.imagePath = imagePath;
	}
}
