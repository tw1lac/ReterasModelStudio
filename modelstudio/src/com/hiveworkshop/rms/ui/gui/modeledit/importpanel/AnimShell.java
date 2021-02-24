package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Animation;

import java.util.ArrayList;
import java.util.List;

class AnimShell {
	Animation anim;
	Animation importAnim;
	List<AnimShell> animShellList = new ArrayList<>();
	private boolean doImport = true;
	private boolean reverse = false;
	private int importType = 0;
	private String name;

	public AnimShell(final Animation anim) {
		this.anim = anim;
		name = anim.getName();
	}

	public void setImportAnim(final Animation a) {
		importAnim = a;
	}

	public Animation getAnim() {
		return anim;
	}

	public Animation getImportAnim() {
		return importAnim;
	}

	public boolean isDoImport() {
		return doImport;
	}

	public AnimShell setDoImport(boolean doImport) {
		this.doImport = doImport;
		return this;
	}

	public boolean isReverse() {
		return reverse;
	}

	public AnimShell setReverse(boolean reverse) {
		this.reverse = reverse;
		return this;
	}

	public int getImportType() {
		return importType;
	}

	public AnimShell setImportType(int importType) {
		this.importType = importType;
		return this;
	}

	public String getName() {
		return name;
	}

	public AnimShell setName(String name) {
		this.name = name;
		return this;
	}

	public void addToList(AnimShell animShell) {
		animShellList.add(animShell);
	}

	public void addToList(List<AnimShell> animShells) {
		animShellList.addAll(animShells);
	}

	public void removeFromList(AnimShell animShell) {
		animShellList.remove(animShell);
	}

	public void removeFromList(List<AnimShell> animShells) {
		animShellList.removeAll(animShells);
	}

	public void setList(List<AnimShell> animShells) {
		animShellList.removeAll(animShells);
		animShellList.addAll(animShells);
	}


}
