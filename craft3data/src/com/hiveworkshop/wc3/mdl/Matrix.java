package com.hiveworkshop.wc3.mdl;

import javax.swing.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Vertex motion matrices.
 *
 * Eric Theller 11/10/2011
 */
public class Matrix {
	ArrayList<Integer> m_boneIds;
	ArrayList<Bone> bones;

	public Matrix() {
		m_boneIds = new ArrayList<>();
	}

	public Matrix(final int id) {
		m_boneIds = new ArrayList<>();
		m_boneIds.add(id);
	}

	public Matrix(final ArrayList boneIds, final boolean useIds) {
		if (useIds) {
			m_boneIds = boneIds;
		} else {
			bones = boneIds;
		}
	}

	public String getName() {
		StringBuilder out = new StringBuilder();
		if (bones != null) {
			if (bones.size() > 0) {
				out = new StringBuilder(bones.get(0).getName());
				for (int i = 1; i < bones.size(); i++) {
					out.append(", ").append(bones.get(i).getName());
				}
			} else {
				out = new StringBuilder("Error bad bone list");
			}
		} else if (m_boneIds != null) {
			if (m_boneIds.size() > 0) {
				out = new StringBuilder(m_boneIds.get(0).toString());
				for (int i = 1; i < m_boneIds.size(); i++) {
					out.append(", ").append(m_boneIds.get(i).toString());
				}
			} else {
				out = new StringBuilder("Error bad bone ids");
			}
		}
		return out.toString();
	}

	long lastPopupTimeHack = 0;

	public void updateIds(final EditableModel mdlr) {
		final int sz1 = bones.size();
		if (m_boneIds == null) {
			m_boneIds = new ArrayList<>();
		} else {
			m_boneIds.clear();
		}
		for (Bone bone : bones) {
			final int newId = mdlr.getObjectId(bone);
			if (newId >= 0) {
				m_boneIds.add(newId);
			} else {
				new Exception("Matrix error").printStackTrace();
				if ((System.currentTimeMillis() - lastPopupTimeHack) > 2000) {
					JOptionPane.showMessageDialog(null,
							"Error: A matrix's bone reference was missing in the model!\nDid you move geometry between models and forget to update bones?");
					lastPopupTimeHack = System.currentTimeMillis();
				}
			}
		}
		if ((m_boneIds.size() < sz1) || ((sz1 != 0) && (m_boneIds.size() == 0))) {
			new Exception("Matrix error").printStackTrace();
			if ((System.currentTimeMillis() - lastPopupTimeHack) > 2000) {
				JOptionPane.showMessageDialog(null, "Error: bad sizes in matrix (" + (sz1 - m_boneIds.size())
						+ " as difference, should be same size)");
				lastPopupTimeHack = System.currentTimeMillis();
			}
		}
	}

	public void updateBones(final EditableModel mdlr) {
		if (bones == null) {
			bones = new ArrayList<>();
		} else {
			bones.clear();
		}
		for (Object m_boneId : m_boneIds) {
			final Bone b = mdlr.getBone((Integer) m_boneId);
			// if( b.getClass() == Helper.class )
			// {
			// JOptionPane.showMessageDialog(null,"Error: Holy fo shizzle my
			// grizzle! There's geometry attached to Helper "+b.getName()+" and
			// that is very bad!");
			// }
			if (b != null) {
				bones.add(b);
			} else {
//				JOptionPane.showMessageDialog(null, "Error: A matrix's bone id was not referencing a real bone!");
				System.err.println("Error: A matrix's bone id was not referencing a real bone! " + m_boneId);
			}
		}
	}

	public Matrix(final List<Bone> newBones) {
		bones = new ArrayList<>(newBones);
	}

	public Matrix(final int[] boneIds) {
		m_boneIds = new ArrayList<>();
		for (int boneId : boneIds) {
			m_boneIds.add(boneId);
		}
	}

	public void add(final Bone b) {
		bones.add(b);
	}

	public void addId(final int id) {
		m_boneIds.add(id);
	}

	public int getBoneId(final int index) {
		return (Integer) m_boneIds.get(index);
	}

	public int size() {
		if ((m_boneIds != null) && (m_boneIds.size() > 0)) {
			return m_boneIds.size();
		} else if ((bones != null) && (bones.size() > 0)) {
			return bones.size();
		}
		// JOptionPane.showMessageDialog(null,"Warning: A matrix with no
		// contents was used!");
		// System.out.println("Warning: A matrix with no contents was used!");
		return -1;// bad stuff
	}

	public static Matrix parseText(final String input) {
		final String[] entries = input.split(",");
		Matrix temp = null;
		final int size = entries.length;
		final int[] boneId = new int[size];
		// When { 4, 2 }, splits around commas, it has 3 segments and we
		// only want the first two (the third being "" after ending comma)
		if (size == 1) {
			try {
				boneId[0] = (int) Long.parseLong(entries[0].split("\\{")[1].split("}")[0].split(" ")[1]);
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
						"Error \"" + input + "\": Matrix data could not be interpreted.");
			}
		} else {
			try {
				boneId[0] = (int) Long.parseLong(entries[0].split("\\{")[1].split(" ")[1]);
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
						"Error \"" + input + "\": Matrix data could not be interpreted.");
			}
			for (int i = 1; i < size; i++) {
				try {
					boneId[i] = (int) Long.parseLong(entries[i].split(" ")[1]);
				} catch (final NumberFormatException e) {
					JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
							"Error \"" + input + "\": Matrix data could not be interpreted.");
				}
			}
		}
		if ((boneId != null) && (boneId[0] != -1)) {
			temp = new Matrix(boneId);
		} else {
			temp = new Matrix();
		}
		return temp;
	}

	public void printTo(final PrintWriter writer, final int tabHeight) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < tabHeight; i++) {
			tabs.append("\t");
		}
		if (m_boneIds.size() > 0) {
			writer.print(tabs + "Matrices { " + m_boneIds.get(0).toString());
		} else {
			writer.print(tabs + "Matrices { -1");
		}
		// writer.print(tabs+"Matrices { "+bones.get(0).getClass().getName());
		for (int i = 1; i < bones.size(); i++) {
			writer.print(", " + m_boneIds.get(i));
			// writer.print(", "+bones.get(i).getClass().getName());
		}
		writer.println(" },");
	}

	public boolean equals(final Matrix other) {
		if (other.size() != size()) {
			return false;
		}
		boolean same = true;
		for (int i = 0; (i < size()) && same; i++) {
			if (bones.get(i) != other.bones.get(i)) {
				same = false;
			}
		}
		return same;
	}

	public ArrayList<Bone> getBones() {
		return bones;
	}

	public void setBones(final ArrayList<Bone> bones) {
		this.bones = bones;
	}
}
