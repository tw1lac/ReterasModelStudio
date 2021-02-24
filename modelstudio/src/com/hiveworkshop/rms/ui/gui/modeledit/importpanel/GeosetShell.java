package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.Material;

public class GeosetShell {
	EditableModel model;
	boolean isImported;
	private Geoset geoset;
	private Geoset importGeoset;
	private boolean doImport = true;
	private Material material;

	public GeosetShell(Geoset geoset, EditableModel model, boolean isImported) {
		this.geoset = geoset;
		this.model = model;
		this.isImported = isImported;
		material = geoset.getMaterial();
	}

	public Geoset getGeoset() {
		return geoset;
	}

	public boolean isDoImport() {
		return doImport;
	}

	public GeosetShell setDoImport(boolean doImport) {
		this.doImport = doImport;
		return this;
	}

	public Material getMaterial() {
		return material;
	}

	public GeosetShell setMaterial(Material material) {
		this.material = material;
		return this;
	}

	public Geoset getImportGeoset() {
		return importGeoset;
	}

	public GeosetShell setImportGeoset(Geoset importGeoset) {
		this.importGeoset = importGeoset;
		return this;
	}

	@Override
	public String toString() {
		return model.getName() + ": " + geoset.getName();
	}
}
