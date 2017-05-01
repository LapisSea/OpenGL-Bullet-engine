package com.lapissea.opengl.program.game.particle.particles;

import com.lapissea.opengl.program.game.particle.Particle;
import com.lapissea.opengl.program.game.particle.ParticleHandler;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ParticleFoo extends Particle<ParticleFoo>{
	
	public ParticleFoo(ParticleHandler<ParticleFoo> handler, Vec3f pos){
		super(handler, pos);
		speed.set(RandUtil.CRF(0.3)+0.5F, 0.5F+RandUtil.RF(1), RandUtil.CRF(0.3));
		float s=2+RandUtil.RF(7);
		scale.set(s, s, s);
		gravity=-0.01f;
	}
	
}
