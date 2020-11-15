package com.etheller.warsmash.parsers.mdlx;

import com.etheller.warsmash.util.Descriptor;

public interface MdlxBlockDescriptor<E> extends Descriptor<E> {

	MdlxBlockDescriptor<Attachment> ATTACHMENT = Attachment::new;

	MdlxBlockDescriptor<Bone> BONE = Bone::new;

	MdlxBlockDescriptor<Camera> CAMERA = Camera::new;

	MdlxBlockDescriptor<CollisionShape> COLLISION_SHAPE = CollisionShape::new;

	MdlxBlockDescriptor<EventObject> EVENT_OBJECT = EventObject::new;

	MdlxBlockDescriptor<Geoset> GEOSET = Geoset::new;

	MdlxBlockDescriptor<GeosetAnimation> GEOSET_ANIMATION = GeosetAnimation::new;

	MdlxBlockDescriptor<Helper> HELPER = Helper::new;

	MdlxBlockDescriptor<Light> LIGHT = Light::new;

	MdlxBlockDescriptor<Layer> LAYER = Layer::new;

	MdlxBlockDescriptor<Material> MATERIAL = Material::new;

	MdlxBlockDescriptor<ParticleEmitter> PARTICLE_EMITTER = ParticleEmitter::new;

	MdlxBlockDescriptor<ParticleEmitter2> PARTICLE_EMITTER2 = ParticleEmitter2::new;

	MdlxBlockDescriptor<RibbonEmitter> RIBBON_EMITTER = RibbonEmitter::new;

	MdlxBlockDescriptor<Sequence> SEQUENCE = Sequence::new;

	MdlxBlockDescriptor<Texture> TEXTURE = Texture::new;

	MdlxBlockDescriptor<TextureAnimation> TEXTURE_ANIMATION = TextureAnimation::new;
}
