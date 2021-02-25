package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.Matrix;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;
import com.hiveworkshop.rms.util.IterableListModel;

import java.util.ArrayList;
import java.util.List;

public class BoneAttachmentShell {
	IterableListModel<MatrixShell> oldBoneRefs;
	IterableListModel<BoneShell> newRefs = new IterableListModel<>();
	IterableListModel<BoneShell> bones;
	private EditableModel model;
	private boolean isImported;
	private Geoset geoset;
	private GeosetShell geosetShell;
	private GeosetShell importGeosetShell;
	private boolean doImport = true;
	private MatrixShell currentMatrix = null;
	private MatrixShell orgMatrix = null;
	private List<BoneShell> boneShellList = new ArrayList<>();
	private List<BoneShell> oldBoneShellList = new ArrayList<>();
	private List<MatrixShell> matrixShells = new ArrayList<>();


	public BoneAttachmentShell(GeosetShell geosetShell, EditableModel model, boolean isImported) {
		this.geosetShell = geosetShell;
		for (Matrix matrix : this.geosetShell.getGeoset().getMatrix()) {
			matrixShells.add(new MatrixShell(matrix));
		}
		this.model = model;
		this.isImported = isImported;
	}

	public BoneAttachmentShell(Geoset geoset, EditableModel model, boolean isImported, IterableListModel<MatrixShell> oldBoneRefs) {
		this.geoset = geoset;
		this.oldBoneRefs = oldBoneRefs;
		this.model = model;
		this.isImported = isImported;

	}

	public EditableModel getModel() {
		return model;
	}

	public BoneAttachmentShell setModel(EditableModel model) {
		this.model = model;
		return this;
	}

	public boolean isImported() {
		return isImported;
	}

	public BoneAttachmentShell setImported(boolean imported) {
		isImported = imported;
		return this;
	}

	public GeosetShell getGeosetShell() {
		return geosetShell;
	}

	public BoneAttachmentShell setGeosetShell(GeosetShell geosetShell) {
		this.geosetShell = geosetShell;
		return this;
	}

	public GeosetShell getImportGeosetShell() {
		return importGeosetShell;
	}

	public BoneAttachmentShell setImportGeosetShell(GeosetShell importGeosetShell) {
		this.importGeosetShell = importGeosetShell;
		return this;
	}

	public boolean isDoImport() {
		return doImport;
	}

	public BoneAttachmentShell setDoImport(boolean doImport) {
		this.doImport = doImport;
		return this;
	}

	public MatrixShell getCurrentMatrix() {
		return currentMatrix;
	}

	public BoneAttachmentShell setCurrentMatrix(MatrixShell currentMatrix) {
		this.currentMatrix = currentMatrix;
		return this;
	}

	public MatrixShell getOrgMatrix() {
		return orgMatrix;
	}

	public BoneAttachmentShell setOrgMatrix(MatrixShell orgMatrix) {
		this.orgMatrix = orgMatrix;
		return this;
	}

	public List<BoneShell> getBoneShellList() {
		return boneShellList;
	}

	public BoneAttachmentShell setBoneShellList(List<BoneShell> boneShellList) {
		this.boneShellList = boneShellList;
		return this;
	}

	public List<BoneShell> getOldBoneShellList() {
		return oldBoneShellList;
	}

	public BoneAttachmentShell setOldBoneShellList(List<BoneShell> oldBoneShellList) {
		this.oldBoneShellList = oldBoneShellList;
		return this;
	}

	public Geoset getGeoset() {
		return geoset;
	}

	public BoneAttachmentShell setGeoset(Geoset geoset) {
		this.geoset = geoset;
		return this;
	}

	public List<MatrixShell> getMatrixShells() {
		return matrixShells;
	}

	public BoneAttachmentShell setMatrixShells(List<MatrixShell> matrixShells) {
		this.matrixShells = matrixShells;
		return this;
	}

	public IterableListModel<MatrixShell> getOldBoneRefs() {
		return oldBoneRefs;
	}

	public BoneAttachmentShell setOldBoneRefs(IterableListModel<MatrixShell> oldBoneRefs) {
		this.oldBoneRefs = oldBoneRefs;
		return this;
	}

	public IterableListModel<MatrixShell> reSetOldBoneRefs(IterableListModel<MatrixShell> oldBoneRefs) {
		this.oldBoneRefs.clear();
		this.oldBoneRefs.addAll(oldBoneRefs);
		return this.oldBoneRefs;
	}

	public IterableListModel<BoneShell> getNewRefs() {
		return newRefs;
	}

	public BoneAttachmentShell setNewRefs(IterableListModel<BoneShell> newRefs) {
		this.newRefs = newRefs;
		return this;
	}

	public IterableListModel<BoneShell> reSetNewBoneRefs(IterableListModel<BoneShell> newRefs) {
		this.newRefs.clear();
		this.newRefs.addAll(newRefs);
		return this.newRefs;
	}

	public BoneAttachmentShell addNewRef(BoneShell newRef) {
		bones.remove(newRef);
		newRefs.addElement(newRef);
		return this;
	}

	public BoneAttachmentShell removeNewRef(BoneShell newRef) {
		newRefs.remove(newRef);
		bones.addElement(newRef);
		return this;
	}

	public BoneAttachmentShell removeNewRef(int index) {
		BoneShell removed = newRefs.remove(index);
		bones.addElement(removed);
		return this;
	}

	public IterableListModel<BoneShell> getBones() {
		return bones;
	}

	public BoneAttachmentShell setBones(IterableListModel<BoneShell> bones) {
		this.bones = bones;
		return this;
	}

	public BoneAttachmentShell fixBones() {
		System.out.println("bones before remove: " + bones.size());
		bones.removeAll(newRefs);
		System.out.println(" after remove: " + bones.size());
		return this;
	}
//
//	public BoneAttachmentShell fixBones() {
//		System.out.println("bones before remove: " + bones.size());
//		bones.removeAll(newRefs);
//		System.out.println(" after remove: " + bones.size());
//		return this;
//	}

	public void moveBone(BoneShell boneShell, int steps) {
		int index = newRefs.indexOf(boneShell);
		newRefs.remove(index);
		newRefs.add((index + steps), boneShell);
	}
//	public void moveBones(List<BoneShell> boneShell, int steps){
//		int index = newRefs.indexOf(boneShell);
//		newRefs.remove(index);
//		newRefs.add((index + steps), boneShell);
//	}
}
