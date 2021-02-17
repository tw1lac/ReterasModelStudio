package com.hiveworkshop.rms.editor.model.visitor;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.util.Vec3;

import java.util.List;

public interface TriangleVisitor {
	VertexVisitor vertex(double x, double y, double z,
	                     double normalX, double normalY, double normalZ,
	                     List<Bone> bones);

	TriangleVisitor NO_ACTION = new TriangleVisitor() {
		@Override
		public VertexVisitor vertex(final double x, final double y, final double z,
		                            final double normalX, final double normalY, final double normalZ,
		                            final List<Bone> bones) {
			return VertexVisitor.NO_ACTION;
		}

		@Override
		public VertexVisitor vertex(Vec3 vert, Vec3 normal, final List<Bone> bones) {
			return VertexVisitor.NO_ACTION;
		}

		@Override
		public VertexVisitor hdVertex(final double x, final double y, final double z,
		                              final double normalX, final double normalY, final double normalZ,
		                              final Bone[] skinBones, final short[] skinBoneWeights) {
			return VertexVisitor.NO_ACTION;
		}

		@Override
		public VertexVisitor hdVertex(Vec3 vert, Vec3 normal, final Bone[] skinBones, final short[] skinBoneWeights) {
			return VertexVisitor.NO_ACTION;
		}

		@Override
		public void triangleFinished() {
		}
	};

	VertexVisitor hdVertex(double x, double y, double z,
	                       double normalX, double normalY, double normalZ,
	                       Bone[] skinBones, short[] skinBoneWeights);

	VertexVisitor vertex(Vec3 vert, Vec3 normal, List<Bone> bones);

	void triangleFinished();

	VertexVisitor hdVertex(Vec3 vert, Vec3 normal, Bone[] skinBones, short[] skinBoneWeights);
}
