package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.gui.modeledit.importpanel.BonePanel;

import java.util.ArrayList;
import java.util.List;

public class BoneShell {
	public final Bone bone;
	public Bone importBone;
	public String modelName;
	public BonePanel panel;
	public boolean showClass = false;

	public boolean shouldImportBone = true;
	public int importStatus = 0;
	public IdObject oldParent;
	public IdObject newParent;
	public BoneShell parentBs;

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