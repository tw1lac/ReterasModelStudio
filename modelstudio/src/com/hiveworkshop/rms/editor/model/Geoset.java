package com.hiveworkshop.rms.editor.model;

import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.parsers.mdlx.MdlxExtent;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
import jassimp.AiMesh;

import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Geoset implements Named, VisibilitySource {
	ExtLog extents;
	List<GeosetVertex> vertices = new ArrayList<>();
	List<Triangle> triangles = new ArrayList<>();
	List<Matrix> matrix = new ArrayList<>();
	List<Animation> anims = new ArrayList<>();
	Material material;
	int selectionGroup = 0;
	EditableModel parentModel;
	GeosetAnim geosetAnim = null;
	int levelOfDetail = -1;
	String levelOfDetailName = "";
	List<short[]> skin;
	List<float[]> tangents;
	boolean unselectable = false;

	public Geoset() {

	}

	public Geoset(final MdlxGeoset geoset, final EditableModel model) {
		setExtLog(new ExtLog(geoset.extent));

		for (final MdlxExtent extent : geoset.sequenceExtents) {
			final ExtLog extents = new ExtLog(extent);
			final Animation anim = new Animation(extents);
			add(anim);
		}

		material = model.getMaterial((int) geoset.materialId);

		final float[] vertices = geoset.vertices;
		final float[] normals = geoset.normals;
		final float[][] uvSets = geoset.uvSets;
		final int nVertices = vertices.length / 3;
		final short[] vertexGroups = geoset.vertexGroups;

		for (int k = 0; k < nVertices; k++) {
			final int i = k * 3;
			final int j = k * 2;
			final GeosetVertex gv = new GeosetVertex(vertices[i], vertices[i + 1],vertices[i + 2]);

			add(gv);

			if (k >= vertexGroups.length) {
				gv.setVertexGroup(-1);
			} else {
				gv.setVertexGroup((256 + vertexGroups[k]) % 256);
			}
			// this is an unsigned byte, the other guys java code will read as
			// signed
			if (normals.length > 0) {
				gv.setNormal(new Vec3(normals[i], normals[i + 1], normals[i + 2]));
			}

			for (float[] uvSet : uvSets) {
				gv.addTVertex(new Vec2(uvSet[j], uvSet[j + 1]));
			}
		}
		// guys I didn't code this to allow experimental
		// non-triangle faces that were suggested to exist
		// on the web (i.e. quads).
		// if you wanted to fix that, you'd want to do it below
		final int[] faces = geoset.faces;

		for (int i = 0; i < faces.length; i += 3) {
			add(new Triangle(faces[i], faces[i + 1], faces[i + 2], this));
		}

		if (geoset.selectionFlags == 4) {
			unselectable = true;
		}

		setSelectionGroup((int)geoset.selectionGroup);
		setLevelOfDetail(geoset.lod);
		setLevelOfDetailName(geoset.lodName);

		int index = 0;
		for (final long size : geoset.matrixGroups) {
			final Matrix m = new Matrix();
			for (int i = 0; i < size; i++) {
				m.addId((int)geoset.matrixIndices[index++]);
			}
			addMatrix(m);
		}

		if (geoset.tangents != null) {
			final float[] tangents = geoset.tangents;
			final short[] skin = geoset.skin;

			// version 900
			this.skin = new ArrayList<>();
			this.tangents = new ArrayList<>();
			for (int i = 0; i < tangents.length; i += 4) {
				this.tangents.add(new float[] { tangents[i], tangents[i + 1], tangents[i + 2], tangents[i + 3] });
			}
			for (int i = 0; i < skin.length; i += 8) {
				this.skin.add(new short[] { skin[i], skin[i + 1], skin[i + 2], skin[i + 3], skin[i + 4], skin[i + 5], skin[i + 6], skin[i + 7] });
			}
		}
	}

	public Geoset(final AiMesh mesh, final EditableModel model) {
		System.out.println("IMPLEMENT Geoset(AiMesh)");

		this.levelOfDetailName = mesh.getName();

		final List<FloatBuffer> uvSets = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			if (mesh.hasTexCoords(i)) {
				uvSets.add(mesh.getTexCoordBuffer(i));
			}
		}
		
		final int uvSetCount = Math.max(uvSets.size(), 1);
		final boolean hasUVs = uvSets.size() > 0;

		final FloatBuffer vertices = mesh.getPositionBuffer();
		final FloatBuffer normals = mesh.getNormalBuffer();

		for (int i = 0, l = mesh.getNumVertices(); i < l; i++) {
			final GeosetVertex gv = new GeosetVertex(vertices.get(), vertices.get(), vertices.get());

			add(gv);

			gv.setVertexGroup(-1);
			
			if (normals != null) {
				gv.setNormal(new Vec3(normals.get(), normals.get(), normals.get()));
			}

			for (int uvId = 0; uvId < uvSetCount; uvId++) {
				Vec2 coord = new Vec2();

				if (hasUVs) {
					coord.x = uvSets.get(uvId).get();
					coord.y = uvSets.get(uvId).get();
				}

				gv.addTVertex(coord);
			}
		}

		final IntBuffer indices = mesh.getFaceBuffer();

		for (int i = 0, l = mesh.getNumFaces(); i < l; i++) {
			add(new Triangle(indices.get(), indices.get(), indices.get(), this));
		}
		
		material = model.getMaterial(mesh.getMaterialIndex());
	}

	public MdlxGeoset toMdlx(final EditableModel model) {
		final MdlxGeoset geoset = new MdlxGeoset();

		if (getExtents() != null) {
			geoset.extent = getExtents().toMdlx();
		}

		for (int i = 0, l = getAnims().size(); i < l; i++) {
			geoset.sequenceExtents.add(getAnim(i).getExtents().toMdlx());
		}


		geoset.materialId = model.computeMaterialID(material);

		final int numVertices = getVertices().size();
		final int nrOfTextureVertexGroups = numUVLayers();

		geoset.vertices = new float[numVertices * 3];
		geoset.normals = new float[numVertices * 3];

		geoset.vertexGroups = new short[numVertices];
		geoset.uvSets = new float[nrOfTextureVertexGroups][numVertices * 2];

		for (int vId = 0; vId < numVertices; vId++) {
			final GeosetVertex vertex = getVertex(vId);

			geoset.vertices[(vId * 3) + 0] = vertex.x;
			geoset.vertices[(vId * 3) + 1] = vertex.y;
			geoset.vertices[(vId * 3) + 2] = vertex.z;
			
			final Vec3 norm = vertex.getNormal();

			geoset.normals[(vId * 3) + 0] = norm.x;
			geoset.normals[(vId * 3) + 1] = norm.y;
			geoset.normals[(vId * 3) + 2] = norm.z;

			for (int uvLayerIndex = 0; uvLayerIndex < nrOfTextureVertexGroups; uvLayerIndex++) {
                final Vec3 uv = vertex.getTVertex(uvLayerIndex);

                geoset.uvSets[uvLayerIndex][(vId * 2) + 0] = uv.x;
                geoset.uvSets[uvLayerIndex][(vId * 2) + 1] = uv.y;
            }

			geoset.vertexGroups[vId] = (byte) vertex.getVertexGroup();
		}

		// Again, the current implementation of my mdl code is that it only
		// handles triangle facetypes
		// (there's another note about this in the MDX -> MDL geoset code)
		geoset.faceGroups = new long[] { getTriangles().size() * 3 };
		geoset.faceTypeGroups = new long[] { 4 }; // triangles!
		geoset.faces = new int[getTriangles().size() * 3]; // triangles!

		int i = 0;
		for (final Triangle tri : getTriangles()) {
			for (int v = 0; v < /* tri.size() */3; v++) {
				geoset.faces[i++] = tri.getId(v);
			}
		}

		if (unselectable) {
			geoset.selectionFlags = 4;
		}

		geoset.selectionGroup = getSelectionGroup();

		int matrixIndexsSize = 0;
		for (final Matrix matrix : getMatrix()) {
			int size = matrix.size();
			if (size == -1) {
				size = 1;
			}
			matrixIndexsSize += size;
		}

		geoset.matrixGroups = new long[getMatrix().size()];
		if (matrixIndexsSize == -1) {
			matrixIndexsSize = 1;
		}

		geoset.matrixIndices = new long[matrixIndexsSize];
		i = 0;
		int groupIndex = 0;
		for (final Matrix matrix : getMatrix()) {
			for (int index = 0; index < matrix.size(); index++) {
				geoset.matrixIndices[i++] = matrix.getBoneId(index);
			}
			if (matrix.size() <= 0) {
				geoset.matrixIndices[i++] = -1;
			}
			int size = matrix.size();
			if (size == -1) {
				size = 1;
			}
			geoset.matrixGroups[groupIndex++] = size;
		}

		geoset.lod = getLevelOfDetail();
		geoset.lodName = getLevelOfDetailName();

		if ((numVertices > 0) && (getVertex(0).getSkinBones() != null)) {
			// v900
			geoset.skin = new short[8 * numVertices];
			geoset.tangents = new float[4 * numVertices];

			for (i = 0; i < numVertices; i++) {
				for (int j = 0; j < 4; j++) {
					final GeosetVertex vertex = getVertex(i);
					geoset.skin[(i * 8) + j] = vertex.getSkinBoneIndexes()[j];
					geoset.skin[(i * 8) + j + 4] = (byte) (vertex.getSkinBoneWeights()[j]);
					geoset.tangents[(i * 4) + j] = vertex.getTangent()[j];
				}
			}
		}

		return geoset;
	}

	@Override
	public String getName() {
		return "Geoset " + (parentModel.getGeosetId(this) + 1);// parentModel.getName()
																// + "
	}

	public void addVertex(final GeosetVertex v) {
		add(v);
	}

	public void add(final GeosetVertex v) {
		vertices.add(v);
	}

	public GeosetVertex getVertex(final int vertId) {
		return vertices.get(vertId);
	}

	public int getVertexId(final GeosetVertex v) {
		return vertices.indexOf(v);
	}

	public void remove(final GeosetVertex v) {
		vertices.remove(v);
	}

	public boolean containsReference(final IdObject obj) {
		// boolean does = false;
		for (GeosetVertex vertex : vertices) {
			if (vertex.bones.contains(obj)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(final Triangle t) {
		return triangles.contains(t);
	}

	public boolean contains(final Vec3 v) {
		return vertices.contains(v);
	}

	public int numVerteces() {
		return vertices.size();
	}

	public int numUVLayers() {
		return vertices.get(0).getTverts().size();
	}

	public void setTriangles(final List<Triangle> list) {
		triangles = list;
	}

	public void addTriangle(final Triangle p) {
		// Left for compat
		add(p);
	}

	public void add(final Triangle p) {
		if (!triangles.contains(p)) {
			triangles.add(p);
		} else {
			System.out.println("2x triangles");
		}
	}

	public Triangle getTriangle(final int triId) {
		return triangles.get(triId);
	}

	/**
	 * Returns all vertices that directly inherit motion from the specified Bone, or
	 * an empty list if no vertices reference the object.
	 */
	public List<GeosetVertex> getChildrenOf(final Bone parent) {
		final List<GeosetVertex> children = new ArrayList<>();
		for (final GeosetVertex gv : vertices) {
			if (gv.bones.contains(parent)) {
				children.add(gv);
			}
		}
		return children;
	}

	public int numTriangles() {
		return triangles.size();
	}

	public void removeTriangle(final Triangle t) {
		triangles.remove(t);
	}

	public void addMatrix(final Matrix v) {
		matrix.add(v);
	}

	public Matrix getMatrix(final int vertId) {
		if ((vertId < 0) && (vertId >= -128)) {
			return getMatrix(256 + vertId);
		}
		if (vertId >= matrix.size()) {
			return null;
		}
		return matrix.get(vertId);
	}

	public int numMatrices() {
		return matrix.size();
	}

	public void setMaterial(final Material m) {
		material = m;
	}

	public Material getMaterial() {
		return material;
	}

	public void setExtLog(final ExtLog e) {
		extents = e;
	}

	public ExtLog getExtLog() {
		return extents;
	}

	public void add(final Animation a) {
		anims.add(a);
    }

    public Animation getAnim(final int id) {
        return anims.get(id);
    }

    public int numAnims() {
        return anims.size();
    }

    public List<Vec3> getTVertecesInArea(final Rectangle2D.Double area, final int layerId) {
        final List<Vec3> temp = new ArrayList<>();
        for (GeosetVertex vertex : vertices) {
            final Vec3 ver = vertex.getTVertex(layerId);
            // Point2D.Double p = new
            // Point(ver.getCoords(dim1),ver.getCoords(dim2))
            if (area.contains(ver.x, ver.y)) {
                temp.add(ver);
            }
        }
        return temp;
    }

	public List<Vec3> getVertecesInArea(final Rectangle2D.Double area, final byte dim1, final byte dim2) {
		final List<Vec3> temp = new ArrayList<>();
		for (final Vec3 ver : vertices) {
			// Point2D.Double p = new
			// Point(ver.getCoords(dim1),ver.getCoords(dim2))
			if (area.contains(ver.getCoord(dim1), ver.getCoord(dim2))) {
				temp.add(ver);
			}
		}
		return temp;
	}

	public void updateToObjects(final EditableModel mdlr) {
		// upload the temporary UVLayer and Matrix objects into the vertices
		// themselves
		final int sz = numVerteces();
		for (final Matrix m : matrix) {
			m.updateBones(mdlr);
		}
		for (int i = 0; i < sz; i++) {
			final GeosetVertex gv = vertices.get(i);
			if ((ModelUtils.isTangentAndSkinSupported(mdlr.getFormatVersion())) && (tangents != null)) {
				gv.initV900();
				for (int j = 0; j < 4; j++) {
					short boneLookupId = skin.get(i)[j];
					if (boneLookupId < 0) {
						boneLookupId += 256;
					}
					short boneWeight = skin.get(i)[j + 4];
					if (boneWeight < 0) {
						boneWeight += 256;
					}
					final Bone bone;
					if ((boneLookupId >= mdlr.getIdObjectsSize()) || (boneLookupId < 0)) {
						bone = null;
					} else {
						final IdObject idObject = mdlr.getIdObject(boneLookupId);
						if (idObject instanceof Bone) {
							bone = (Bone) idObject;
						} else {
							bone = null;
						}
					}
					gv.getSkinBones()[j] = bone;
					gv.getSkinBoneWeights()[j] = boneWeight;
					gv.getTangent()[j] = tangents.get(i)[j];
				}
			}
			final Matrix mx;
			if ((gv.getVertexGroup() == -1) && (ModelUtils.isTangentAndSkinSupported(mdlr.getFormatVersion()))) {
				mx = null;
			} else {
				mx = getMatrix(gv.getVertexGroup());
			}
			if (mx != null) {
				final int szmx = mx.size();
				gv.clearBoneAttachments();
				for (int m = 0; m < szmx; m++) {
					final int boneId = mx.getBoneId(m);
					if ((boneId >= 0) && (boneId < mdlr.getIdObjectsSize())) {
						gv.addBoneAttachment((Bone) mdlr.getIdObject(boneId));
					}
				}
			}
			for (final Triangle t : triangles) {
				if (t.containsRef(gv)) {
					gv.triangles.add(t);
				}
				t.geoset = this;
			}
			gv.geoset = this;

			// gv.addBoneAttachment(null);//Why was this here?
		}
		parentModel = mdlr;
	}

	public void applyMatricesToVertices(final EditableModel mdlr) {
		final int sz = numVerteces();
		for (int i = 0; i < sz; i++) {
			final GeosetVertex gv = vertices.get(i);
			gv.clearBoneAttachments();
			final Matrix mx = getMatrix(gv.getVertexGroup());
			if (((gv.getVertexGroup() == -1) || (mx == null))) {
				if (!ModelUtils.isTangentAndSkinSupported(mdlr.getFormatVersion())) {
					throw new IllegalStateException(
							"You have empty vertex groupings but FormatVersion is 800. Did you load HD mesh into an SD model?");
				}
			} else {
				mx.updateIds(mdlr);
				final int szmx = mx.size();
				for (int m = 0; m < szmx; m++) {
					gv.addBoneAttachment((Bone) mdlr.getIdObject(mx.getBoneId(m)));
				}
			}
		}
	}

	public void applyVerticesToMatrices(final EditableModel mdlr) {
		matrix.clear();
		for (GeosetVertex vertex : vertices) {
			Matrix newTemp = new Matrix(vertex.bones);
			boolean newMatrix = true;
			for (int m = 0; (m < matrix.size()) && newMatrix; m++) {
				if (newTemp.equals(matrix.get(m))) {
					newTemp = matrix.get(m);
					newMatrix = false;
				}
			}
			if (newMatrix) {
				matrix.add(newTemp);
				newTemp.updateIds(mdlr);
			}
			vertex.VertexGroup = matrix.indexOf(newTemp);
			vertex.setMatrix(newTemp);
		}
	}

	public void purifyFaces() {
		for (int i = triangles.size() - 1; i >= 0; i--) {
			final Triangle tri = triangles.get(i);
			for (int ix = 0; ix < triangles.size(); ix++) {
				final Triangle trix = triangles.get(ix);
				if (trix != tri) {
					if (trix.equalRefsNoIds(tri))// Changed this from
													// "sameVerts" -- this means
													// that
													// triangles with the same
													// vertices but in a
													// different order will no
													// longer be purged
													// automatically.
					{
						triangles.remove(tri);
						break;
					}
				}
			}
		}
	}

	public boolean isEmpty() {
		return vertices.size() <= 0;
	}

	public void doSavePrep(final EditableModel mdlr) {
		purifyFaces();

		// Clearing matrix list
		matrix.clear();
		for (final GeosetVertex geosetVertex : vertices) {
			if (geosetVertex.getSkinBones() != null) {
				if (matrix.isEmpty()) {
					final List<Bone> bones = mdlr.sortedIdObjects(Bone.class);
					for (int j = 0; (j < bones.size()) && (j < 256); j++) {
						final List<Bone> singleBoneList = new ArrayList<>();
						singleBoneList.add(bones.get(j));
						final Matrix e = new Matrix(singleBoneList);
						e.updateIds(mdlr);
						matrix.add(e);
					}
				}
				int skinIndex = 0;
				for (final Bone bone : geosetVertex.getSkinBones()) {
					if (bone != null) {
						final List<Bone> singleBoneList = new ArrayList<>();
						singleBoneList.add(bone);
						final Matrix newTemp = new Matrix(singleBoneList);
						int index = -1;
						for (int m = 0; (m < matrix.size()) && (index == -1); m++) {
							if (newTemp.equals(matrix.get(m))) {
								index = m;
							}
						}
						geosetVertex.getSkinBoneIndexes()[skinIndex++] = (byte) index;
					}
				}
				geosetVertex.VertexGroup = -1;
			} else {
				Matrix newTemp = new Matrix(geosetVertex.bones);
				boolean newMatrix = true;
				for (int m = 0; (m < matrix.size()) && newMatrix; m++) {
					if (newTemp.equals(matrix.get(m))) {
						newTemp = matrix.get(m);
						newMatrix = false;
					}
				}
				if (newMatrix) {
					matrix.add(newTemp);
					newTemp.updateIds(mdlr);
				}
				geosetVertex.VertexGroup = matrix.indexOf(newTemp);
				geosetVertex.setMatrix(newTemp);
			}
		}
		for (Triangle triangle : triangles) {
			triangle.updateVertexIds(this);
		}
		int boneRefCount = 0;
		for (Matrix item : matrix) {
			boneRefCount += item.bones.size();
		}
		for (Matrix value : matrix) {
			value.updateIds(mdlr);
		}
	}

	public GeosetAnim forceGetGeosetAnim() {
		if (geosetAnim == null) {
			geosetAnim = new GeosetAnim(this);
			parentModel.add(geosetAnim);
		}
		return geosetAnim;
	}

	@Override
	public void setVisibilityFlag(final AnimFlag a) {
		if (a != null) {
			forceGetGeosetAnim().setVisibilityFlag(a);
		}
	}

	@Override
	public AnimFlag getVisibilityFlag() {
		if (geosetAnim != null) {
			return geosetAnim.getVisibilityFlag();
		}
		return null;
	}

	@Override
	public String visFlagName() {
		return "Alpha";
	}

	public ExtLog getExtents() {
		return extents;
	}

	public void setExtents(final ExtLog extents) {
		this.extents = extents;
	}

	public List<GeosetVertex> getVertices() {
		return vertices;
	}

	public void setVertex(final List<GeosetVertex> vertex) {
		this.vertices = vertex;
	}

	public List<Triangle> getTriangles() {
		return triangles;
	}

	public void setTriangle(final List<Triangle> triangle) {
		triangles = triangle;
	}

	public List<Matrix> getMatrix() {
		return matrix;
	}

	public void setMatrix(final List<Matrix> matrix) {
		this.matrix = matrix;
	}

	public List<Animation> getAnims() {
		return anims;
	}

	public void setAnims(final List<Animation> anims) {
		this.anims = anims;
	}

	public int getSelectionGroup() {
		return selectionGroup;
	}

	public void setSelectionGroup(final int selectionGroup) {
		this.selectionGroup = selectionGroup;
	}

	public boolean getUnselectable() {
		return unselectable;
	}

	public void setUnselectable(final boolean unselectable) {
		this.unselectable = unselectable;
	}

	public void setLevelOfDetail(final int levelOfDetail) {
		this.levelOfDetail = levelOfDetail;
	}

	public void setLevelOfDetailName(final String levelOfDetailName) {
		this.levelOfDetailName = levelOfDetailName;
	}

	public int getLevelOfDetail() {
		return levelOfDetail;
	}

	public String getLevelOfDetailName() {
		return levelOfDetailName;
	}

	public EditableModel getParentModel() {
		return parentModel;
	}

	public void setParentModel(final EditableModel parentModel) {
		this.parentModel = parentModel;
	}

	public GeosetAnim getGeosetAnim() {
		return geosetAnim;
	}

	public void setGeosetAnim(final GeosetAnim geosetAnim) {
		this.geosetAnim = geosetAnim;
	}

	public void remove(final Triangle tri) {
		triangles.remove(tri);
	}

	public ExtLog calculateExtent() {
		double maximumDistanceFromCenter = 0;
		double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
		for (final GeosetVertex geosetVertex : vertices) {
			if (geosetVertex.x > maxX) {
				maxX = geosetVertex.x;
			}
			if (geosetVertex.y > maxY) {
				maxY = geosetVertex.y;
			}
			if (geosetVertex.z > maxZ) {
				maxZ = geosetVertex.z;
			}
			if (geosetVertex.x < minX) {
				minX = geosetVertex.x;
			}
			if (geosetVertex.y < minY) {
				minY = geosetVertex.y;
			}
			if (geosetVertex.z < minZ) {
				minZ = geosetVertex.z;
			}
			final double distanceFromCenter = Math.sqrt((geosetVertex.x * geosetVertex.x)
					+ (geosetVertex.y * geosetVertex.y) + (geosetVertex.z * geosetVertex.z));
			if (distanceFromCenter > maximumDistanceFromCenter) {
				maximumDistanceFromCenter = distanceFromCenter;
			}
		}
		return new ExtLog(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ), maximumDistanceFromCenter);
	}
}
