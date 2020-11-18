package com.hiveworkshop.wc3.gui.modeledit.cutpaste;

import com.hiveworkshop.wc3.mdl.Camera;
import com.hiveworkshop.wc3.mdl.Geoset;
import com.hiveworkshop.wc3.mdl.IdObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CopiedModelData {
	private final List<Geoset> geosets;
	private final List<IdObject> idObjects;
	private final List<Camera> cameras;

	public CopiedModelData(final Collection<Geoset> geosets, final Collection<IdObject> idObjects,
						   final Collection<Camera> cameras) {
		this.geosets = new ArrayList<>(geosets);
		this.idObjects = new ArrayList<>(idObjects);
		this.cameras = new ArrayList<>(cameras);
	}

//	public CopiedModelData(final Collection<Geoset> geosets, final Collection<IdObject> idObjects,
//			final Collection<Camera> cameras) {
//		this.geosets = Arrays.asList(geosets.toArray(new Geoset[0]));
//		this.idObjects = Arrays.asList(idObjects.toArray(new IdObject[0]));
//		this.cameras = Arrays.asList(cameras.toArray(new Camera[0]));
//	}

	public List<Geoset> getGeosets() {
		return geosets;
	}

	public List<IdObject> getIdObjects() {
		return idObjects;
	}

	public List<Camera> getCameras() {
		return cameras;
	}
}
