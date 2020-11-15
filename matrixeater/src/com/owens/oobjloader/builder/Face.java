package com.owens.oobjloader.builder;

// This code was written by myself, Sean R. Owens, sean at guild dot net,
// and is released to the public domain. Share and enjoy. Since some
// people argue that it is impossible to release software to the public
// domain, you are also free to use this code under any version of the
// GPL, LPGL, Apache, or BSD licenses, or contact me for use of another
// license.  (I generally don't care so I'll almost certainly say yes.)
// In addition this code may also be used under the "unlicense" described
// at http://unlicense.org/ .  See the file UNLICENSE in the repo.
import java.util.ArrayList;

public class Face {

    public final ArrayList<FaceVertex> vertices = new ArrayList<>();
    public Material material = null;
    public Material map = null;

    public Face() {
    }

    public void add(final FaceVertex vertex) {
        vertices.add(vertex);
    }
    public final VertexNormal faceNormal = new VertexNormal(0, 0, 0);

    // @TODO: This code assumes the face is a triangle.
    public void calculateTriangleNormal() {
        final float[] edge1 = new float[3];
        final float[] edge2 = new float[3];
        final float[] normal = new float[3];
        final VertexGeometric v1 = vertices.get(0).v;
        final VertexGeometric v2 = vertices.get(1).v;
        final VertexGeometric v3 = vertices.get(2).v;
        final float[] p1 = {v1.x, v1.y, v1.z};
        final float[] p2 = {v2.x, v2.y, v2.z};
        final float[] p3 = {v3.x, v3.y, v3.z};

        edge1[0] = p2[0] - p1[0];
        edge1[1] = p2[1] - p1[1];
        edge1[2] = p2[2] - p1[2];

        edge2[0] = p3[0] - p2[0];
        edge2[1] = p3[1] - p2[1];
        edge2[2] = p3[2] - p2[2];

        normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
        normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
        normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

        faceNormal.x = normal[0];
        faceNormal.y = normal[1];
        faceNormal.z = normal[2];
    }

    @Override
	public String toString() {
        StringBuilder result = new StringBuilder("\tvertices: " + vertices.size() + " :\n");
        for(final FaceVertex f : vertices) {
            result.append(" \t\t( ").append(f.toString()).append(" )\n");
        }
        return result.toString();
    }
}