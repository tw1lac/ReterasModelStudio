package com.owens.oobjloader.builder;

public class VertexTexture {

    public float u = 0;
    public float v = 0;

    VertexTexture(final float u, final float v) {
        this.u = u;
        this.v = v;
    }

    @Override
	public String toString() {
        return u + "," + v;
    }
}