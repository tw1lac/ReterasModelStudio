package com.hiveworkshop.wc3.gui.modeledit;

import com.hiveworkshop.wc3.mdl.Vertex;

public interface VertexFilter<TYPE extends Vertex> {
	boolean isAccepted(TYPE vertex);

	VertexFilter<Vertex> IDENTITY = vertex -> true;
}
