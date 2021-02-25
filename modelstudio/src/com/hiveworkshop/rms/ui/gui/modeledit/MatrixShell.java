package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.editor.model.Matrix;
import com.hiveworkshop.rms.util.IterableListModel;

import java.util.ArrayList;

public class MatrixShell {
	private Matrix matrix;
	private IterableListModel<BoneShell> newBones;
	private ArrayList<BoneShell> orgBones;

	public MatrixShell(final Matrix m) {
		matrix = m;
		newBones = new IterableListModel<>();
	}


	public MatrixShell(final Matrix m, ArrayList<BoneShell> orgBones) {
		matrix = m;
		this.orgBones = orgBones;
		newBones = new IterableListModel<>(orgBones);
	}

	public void orgBones(ArrayList<BoneShell> orgBones) {
		this.orgBones = orgBones;
		newBones = new IterableListModel<>(orgBones);
	}

	public void resetMatrix() {
		newBones.clear();
		newBones.addAll(orgBones);
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public IterableListModel<BoneShell> getNewBones() {
		return newBones;
	}

	@Override
	public String toString() {
		return matrix.getName();
	}

	public MatrixShell setNewBones(IterableListModel<BoneShell> newBones) {
		this.newBones = newBones;
		return this;
	}

	public MatrixShell setMatrix(Matrix matrix) {
		this.matrix = matrix;
		return this;
	}

	public void addNewBone(BoneShell boneShell) {
		newBones.addElement(boneShell);
	}

	public void removeNewBone(BoneShell boneShell) {
		newBones.remove(boneShell);
	}

	public ArrayList<BoneShell> getOrgBones() {
		return orgBones;
	}

	public MatrixShell setOrgBones(ArrayList<BoneShell> orgBones) {
		this.orgBones = orgBones;
		return this;
	}

	public void clearNewBones() {
		newBones.clear();
	}

	public int moveBone(BoneShell boneShell, int step) {
		int index = newBones.indexOf(boneShell);
		if (index != -1) {
			int newIndex = Math.max(0, Math.min((index + step), (newBones.size() - 1)));
			newBones.remove(index);
			newBones.add(newIndex, boneShell);
			System.out.println("bone index: " + index + " -> " + newIndex + " (i+st: " + (index + step) + ", nbz-1: " + (newBones.size() - 1) + ") (" + boneShell.toString() + ")");
			return (newIndex);
		}
		return 0;
	}
}