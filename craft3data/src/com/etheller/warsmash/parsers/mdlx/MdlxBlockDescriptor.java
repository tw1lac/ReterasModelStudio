package com.etheller.warsmash.parsers.mdlx;

import com.etheller.warsmash.util.Descriptor;

public interface MdlxBlockDescriptor<E> extends Descriptor<E> {

	MdlxBlockDescriptor<Attachment> ATTACHMENT = new MdlxBlockDescriptor<Attachment>() {
		@Override
		public Attachment create() {
			return new Attachment();
		}
	};

	MdlxBlockDescriptor<Bone> BONE = new MdlxBlockDescriptor<Bone>() {
		@Override
		public Bone create() {
			return new Bone();
		}
	};

	MdlxBlockDescriptor<Camera> CAMERA = new MdlxBlockDescriptor<Camera>() {
		@Override
		public Camera create() {
			return new Camera();
		}
	};

	MdlxBlockDescriptor<CollisionShape> COLLISION_SHAPE = new MdlxBlockDescriptor<CollisionShape>() {
		@Override
		public CollisionShape create() {
			return new CollisionShape();
		}
	};

	MdlxBlockDescriptor<EventObject> EVENT_OBJECT = new MdlxBlockDescriptor<EventObject>() {
		@Override
		public EventObject create() {
			return new EventObject();
		}
	};

	MdlxBlockDescriptor<Geoset> GEOSET = new MdlxBlockDescriptor<Geoset>() {
		@Override
		public Geoset create() {
			return new Geoset();
		}
	};

	MdlxBlockDescriptor<GeosetAnimation> GEOSET_ANIMATION = new MdlxBlockDescriptor<GeosetAnimation>() {
		@Override
		public GeosetAnimation create() {
			return new GeosetAnimation();
		}
	};

	MdlxBlockDescriptor<Helper> HELPER = new MdlxBlockDescriptor<Helper>() {
		@Override
		public Helper create() {
			return new Helper();
		}
	};

	MdlxBlockDescriptor<Light> LIGHT = new MdlxBlockDescriptor<Light>() {
		@Override
		public Light create() {
			return new Light();
		}
	};

	MdlxBlockDescriptor<Layer> LAYER = new MdlxBlockDescriptor<Layer>() {
		@Override
		public Layer create() {
			return new Layer();
		}
	};

	MdlxBlockDescriptor<Material> MATERIAL = new MdlxBlockDescriptor<Material>() {
		@Override
		public Material create() {
			return new Material();
		}
	};

	MdlxBlockDescriptor<ParticleEmitter> PARTICLE_EMITTER = new MdlxBlockDescriptor<ParticleEmitter>() {
		@Override
		public ParticleEmitter create() {
			return new ParticleEmitter();
		}
	};

	MdlxBlockDescriptor<ParticleEmitter2> PARTICLE_EMITTER2 = new MdlxBlockDescriptor<ParticleEmitter2>() {
		@Override
		public ParticleEmitter2 create() {
			return new ParticleEmitter2();
		}
	};

	MdlxBlockDescriptor<RibbonEmitter> RIBBON_EMITTER = new MdlxBlockDescriptor<RibbonEmitter>() {
		@Override
		public RibbonEmitter create() {
			return new RibbonEmitter();
		}
	};

	MdlxBlockDescriptor<Sequence> SEQUENCE = new MdlxBlockDescriptor<Sequence>() {
		@Override
		public Sequence create() {
			return new Sequence();
		}
	};

	MdlxBlockDescriptor<Texture> TEXTURE = new MdlxBlockDescriptor<Texture>() {
		@Override
		public Texture create() {
			return new Texture();
		}
	};

	MdlxBlockDescriptor<TextureAnimation> TEXTURE_ANIMATION = new MdlxBlockDescriptor<TextureAnimation>() {
		@Override
		public TextureAnimation create() {
			return new TextureAnimation();
		}
	};
}
