package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.IdObject;

import java.util.ArrayList;
import java.util.List;

public class BoneShell {
	private final Bone bone;
	private Bone importBone;
	private String modelName;
	//	public BonePanel panel;
	private boolean showClass = false;

	private boolean shouldImportBone = true;
	private int importStatus = 0;
	private IdObject oldParent;
	private IdObject newParent;
	private BoneShell parentBs;

	public BoneShell(final Bone b) {
		bone = b;
		if (b != null) {
			oldParent = bone.getParent();
			newParent = bone.getParent();
		}
	}

	public void setImportBone(final Bone b) {
		importBone = b;
	}

	public Bone getImportBone() {
		return importBone;
	}

	public Bone getBone() {
		return bone;
//		return importBone;
	}

	public IdObject getOldParent() {
		return oldParent;
	}

	public IdObject getNewParent() {
		return newParent;
	}

	public void setParentBone(IdObject parent) {
		newParent = parent;
	}

	public BoneShell getParent() {
		return parentBs;
	}

	public void setParent(BoneShell parent) {
		newParent = parent.bone;
		parentBs = parent;
	}

	public int getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(int importStatus) {
		this.importStatus = importStatus;
		if (importStatus == 0) {
			shouldImportBone = true;
		} else if (importStatus == 2) {
			shouldImportBone = false;
		}
	}

	public boolean getShouldImportBone() {
		return shouldImportBone;
	}

	public BoneShell setShouldImportBone(boolean shouldImportBone) {
		this.shouldImportBone = shouldImportBone;
		return this;
	}

	public String getModelName() {
		return modelName;
	}

	public BoneShell setModelName(String modelName) {
		this.modelName = modelName;
		return this;
	}

	public boolean getShowClass() {
		return showClass;
	}

	public BoneShell setShowClass(boolean showClass) {
		this.showClass = showClass;
		return this;
	}

	@Override
	public String toString() {
		if (bone == null) {
			return "None";
		}
		if (showClass) {
			if (modelName == null) {
				return bone.getClass().getSimpleName() + " \"" + bone.getName() + "\"";
			} else {
				return modelName + ": " + bone.getClass().getSimpleName() + " \"" + bone.getName() + "\"";
			}
		} else {
			if (modelName == null) {
				return bone.getName();
			} else {
				return modelName + ": " + bone.getName();
			}
		}
	}

	public static List<Bone> toBonesList(final List<BoneShell> boneShells) {
		final List<Bone> bones = new ArrayList<>();
		for (final BoneShell bs : boneShells) {
			bones.add(bs.bone);
		}
		return bones;
	}
}