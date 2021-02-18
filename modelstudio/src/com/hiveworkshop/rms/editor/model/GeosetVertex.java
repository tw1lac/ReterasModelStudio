package com.hiveworkshop.rms.editor.model;

import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GeosetVertex is a extended version of the Vertex class, for use strictly
 * inside of Geosets. The idea is that a Vertex object is used all over this
 * program for any sort of point in 3d space (PivotPoint, Min/max extents, data
 * in translations and scaling) and is strictly three connected double values,
 * while a GeosetVertex is an object that has many additional useful parts for a
 * Geoset
 * <p>
 * Eric Theller 3/9/2012
 */
public class GeosetVertex extends Vec3 {
    Matrix matrixRef;
    private Vec3 normal = new Vec3();
    public int VertexGroup = -1;
    List<Vec2> tverts = new ArrayList<>();
    List<Bone> bones = new ArrayList<>();
    List<Triangle> triangles = new ArrayList<>();
    private byte[] skinBoneIndexes;
    private Bone[] skinBones;
    private short[] skinBoneWeights;
    private float[] tangent;
    private Vec4 tang;
    private SkinBone[] sskinBones;

    Geoset geoset;

    public GeosetVertex(final double x, final double y, final double z) {
        super(x, y, z);
    }

    public GeosetVertex(final double x, final double y, final double z, final Vec3 n) {
        super(x, y, z);
        normal = n;
    }

    public GeosetVertex(final GeosetVertex old) {
        super(old.x, old.y, old.z);
        normal = new Vec3(old.normal);
        bones = new ArrayList<>(old.bones);
        tverts = new ArrayList<>();
        for (final Vec2 tv : old.tverts) {
            tverts.add(new Vec2(tv));
        }
        // odd, but when writing
        geoset = old.geoset;
        // TODO copy triangles???????
        if (old.skinBoneIndexes != null) {
            skinBoneIndexes = old.skinBoneIndexes.clone();
        }
        if (old.skinBones != null) {
            skinBones = old.skinBones.clone();
        }
        if (old.skinBoneWeights != null) {
            skinBoneWeights = old.skinBoneWeights.clone();
        }
        if (old.tangent != null) {
            tangent = old.tangent.clone();
            tang = new Vec4(old.tang);
        }
    }

    public void initV900() {
        skinBoneIndexes = new byte[4];
        skinBones = new Bone[4];
        skinBoneWeights = new short[4];
        tangent = new float[4];
        sskinBones = new SkinBone[4];
        tang = new Vec4(0, 0, 0, 0);
    }

    public void un900Heuristic() {
        if (tangent != null) {
            tangent = null;
            tang = null;
        }
        if (skinBones != null) {
            bones.clear();
            int index = 0;
            boolean fallback = false;
            for (final Bone bone : skinBones) {
                if (bone != null) {
                    fallback = true;
                    if (skinBoneWeights[index] > 110) {
                        bones.add(bone);
                    }
                }
                index++;
            }
            if (bones.isEmpty() && fallback) {
                for (final Bone bone : skinBones) {
                    if (bone != null) {
                        bones.add(bone);
                    }
                }
            }
            skinBones = null;
            skinBoneWeights = null;
            skinBoneIndexes = null;
        }
    }

    public void addTVertex(final Vec2 v) {
        tverts.add(v);
    }

    public Vec2 getTVertex(final int i) {
        try {
            return tverts.get(i);
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void setVertexGroup(final int k) {
        VertexGroup = k;
    }

    public int getVertexGroup() {
        return VertexGroup;
    }

    public void clearBoneAttachments() {
        bones.clear();
    }

    public void clearTVerts() {
        tverts.clear();
    }

    public void addBoneAttachment(final Bone b) {
        bones.add(b);
    }

    public void addBoneAttachments(final List<Bone> b) {
        bones.addAll(b);
    }

    public List<Bone> getBoneAttachments() {
        return bones;
    }

    public void setMatrix(final Matrix ref) {
        matrixRef = ref;
    }

    public void setNormal(final Vec3 n) {
        normal = n;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public List<Vec2> getTverts() {
        return tverts;
    }

    public void setTverts(final List<Vec2> tverts) {
        this.tverts = tverts;
    }

    public List<Bone> getBones() {
        return bones;
    }

    public void setBones(final List<Bone> bones) {
        this.bones = bones;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public void setTriangles(final List<Triangle> triangles) {
        this.triangles = triangles;
    }

    public Geoset getGeoset() {
        return geoset;
    }

    /**
     * @deprecated for use only with saving functionalities inside the system
     */
    @Deprecated
    public byte[] getSkinBoneIndexes() {
        return skinBoneIndexes;
    }

    public Bone[] getSkinBones() {
        return skinBones;
    }

    public void setSkinBones(final Bone[] skinBones) {
        this.skinBones = skinBones;
    }

    public short[] getSkinBoneWeights() {
        return skinBoneWeights;
    }

    public void setSkinBoneWeights(final short[] skinBoneWeights) {
        this.skinBoneWeights = skinBoneWeights;
    }

    public float[] getTangent() {
        return tangent;
    }

    public Vec4 getTang() {
        return tang;
    }

    public void setTangent(final float[] tangent) {
        this.tangent = tangent;
        tang = new Vec4(tangent);
    }

    public void setTangent(Vec4 tangent) {
        this.tangent = tangent.toFloatArray();
        tang = tangent;
    }

    public void setGeoset(final Geoset geoset) {
        this.geoset = geoset;
    }

    @Deprecated()
    public Matrix getMatrixRef() {
        return matrixRef;
    }

    @Override
    public Vec3 rotate(final double centerX, final double centerY, final double centerZ, final double radians,
                       final byte firstXYZ, final byte secondXYZ) {
        super.rotate(centerX, centerY, centerZ, radians, firstXYZ, secondXYZ);
        normal.rotate(0, 0, 0, radians, firstXYZ, secondXYZ);
        if (tangent != null) {
//            rotateTangent(0, 0, 0, radians, firstXYZ, secondXYZ, tangent);
            tang.set(tang.getVec3().rotate(0, 0, 0, radians, firstXYZ, secondXYZ));
            tangent = tang.toFloatArray();
//            rotateTangent(0, 0, 0, radians, firstXYZ, secondXYZ);
        }
        return this;
    }

    @Override
    public Vec3 rotate(Vec3 center, final double radians,
                       final byte firstXYZ, final byte secondXYZ) {
        super.rotate(center, radians, firstXYZ, secondXYZ);
        normal.rotate(0, 0, 0, radians, firstXYZ, secondXYZ);
        if (tangent != null) {
//            rotateTangent(0, 0, 0, radians, firstXYZ, secondXYZ, tangent);
            tang.set(tang.getVec3().rotate(0, 0, 0, radians, firstXYZ, secondXYZ));
            tangent = tang.toFloatArray();
        }
        return this;
    }

//    public static void rotateTangent(final double centerX, final double centerY, final double centerZ,
//                                     final double radians,
//                                     final byte firstXYZ, final byte secondXYZ,
//                                     final float[] vertex) {
//        final double x1 = getVertexCoord(firstXYZ, vertex);
//        final double y1 = getVertexCoord(secondXYZ, vertex);
//        final double cx = getCenter(centerX, centerY, centerZ, firstXYZ);// = coordinateSystem.geomX(centerX);
//        final double dx = x1 - cx;
//        final double cy = getCenter(centerX, centerY, centerZ, secondXYZ);// = coordinateSystem.geomY(centerY);
//        final double dy = y1 - cy;
//        final double r = Math.sqrt((dx * dx) + (dy * dy));
//        double verAng = Math.acos(dx / r);
//        if (dy < 0) {
//            verAng = -verAng;
//        }
//        // if( getDimEditable(dim1) )
//        double newFirstCoord = (Math.cos(verAng + radians) * r) + cx;
//        if (!Double.isNaN(newFirstCoord)) {
//            setVertexCoord(firstXYZ, vertex, (float) newFirstCoord);
//        }
//        // if( getDimEditable(dim2) )
//        double newSecondCoord = (Math.sin(verAng + radians) * r) + cy;
//        if (!Double.isNaN(newSecondCoord)) {
//            setVertexCoord(secondXYZ, vertex, (float) newSecondCoord);
//        }
//    }
//
//    private static void setVertexCoord(byte firstXYZ, float[] vertex, float nextDim) {
//        if (firstXYZ < 0) {
//            firstXYZ = (byte) (-firstXYZ - 1);
//            nextDim = -nextDim;
//        }
//        vertex[firstXYZ] = nextDim;
//    }
//
//    private static float getVertexCoord(byte firstXYZ, float[] vertex) {
//        if(firstXYZ < 0) {
//            firstXYZ = (byte)(-firstXYZ-1);
//            return -vertex[firstXYZ];
//        }
//        return vertex[firstXYZ];
//    }

    public Vec3 createNormal() {
        final Vec3 sum = new Vec3();

        for (final Triangle triangle : triangles) {
            sum.add(triangle.getNormal());
        }

        sum.normalize();

        return sum;
    }

    public Vec3 createNormal(final List<GeosetVertex> matches) {
        final Vec3 sum = new Vec3();

        for (final GeosetVertex match : matches) {
            for (final Triangle triangle : match.triangles) {
                sum.add(triangle.getNormal());
            }
        }

        return sum.normalize();
    }

    public Vec3 createNormal(final List<GeosetVertex> matches, double maxAngle) {
        final Vec3 sum = new Vec3();
        Vec3 normal = createNormal();
        List<Vec3> uniqueNormals = new ArrayList<>();
        for (final GeosetVertex match : matches) {
            Vec3 matchNormal = match.createNormal();
            uniqueNormals.add(matchNormal);
        }
        uniqueNormals.stream().filter(n -> normal.degAngleTo(n) < maxAngle).forEach(sum::add);

        return sum.normalize();
    }


    public Vec3 createNormalFromFaces(final List<GeosetVertex> matches, double maxAngle) {
        final Vec3 sum = new Vec3();
        Vec3 normal = createNormal();
        for (final GeosetVertex match : matches) {
            for (final Triangle triangle : match.triangles) {
                Vec3 matchNormal = triangle.getNormal().normalize();
                double angle = normal.degAngleTo(matchNormal);
                if (angle < maxAngle) {
                    sum.add(matchNormal);
                }
            }
        }

        return sum.normalize();
    }

    public void rigBones(List<Bone> matrixBones) {
        if (skinBones == null) {
            clearBoneAttachments();
            addBoneAttachments(matrixBones);
        } else {
            Arrays.fill(skinBones, null);
            Arrays.fill(skinBoneWeights, (short) 0);
            final int basicWeighting = 255 / matrixBones.size();
            final int offset = 255 - (basicWeighting * matrixBones.size());
            for (int i = 0; (i < matrixBones.size()) && (i < 4); i++) {
                skinBones[i] = matrixBones.get(i);
                skinBoneWeights[i] = (short) basicWeighting;
                if (i == 0) {
                    skinBoneWeights[i] += offset;
                }
            }
        }
    }

    public short[] getSkinBoneEntry() {
        short[] skinEntry = {0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < sskinBones.length && i < 4; i++) {
            skinEntry[i] = (short) sskinBones[i].weight;
            skinEntry[i + 4] = (short) sskinBones[i].getBoneId();
        }
        return skinEntry;
    }

    public class SkinBone {
        int weight;
        Bone bone;

        SkinBone(int weight, Bone bone) {
            this.weight = weight;
            this.bone = bone;
        }

        public SkinBone setBone(Bone bone) {
            this.bone = bone;
            return this;
        }

        public SkinBone setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        int getBoneId() {
            return geoset.getParentModel().getObjectId(bone);
        }
    }
}
