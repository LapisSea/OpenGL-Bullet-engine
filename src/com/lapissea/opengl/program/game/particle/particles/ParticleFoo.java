package com.lapissea.opengl.program.game.particle.particles;

import com.lapissea.opengl.program.game.particle.Particle;
import com.lapissea.opengl.program.game.particle.ParticleHandler;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ParticleFoo extends Particle<ParticleFoo>{
	
	private float maxAge=150+RandUtil.RI(100),orgScale=4F+RandUtil.RF(5);
	
	public ParticleFoo(ParticleHandler<ParticleFoo> handler, Vec3f pos){
		super(handler, pos.sub(0, 5, 0));
		speed.set(RandUtil.CRF(0.3), 1F+RandUtil.RF(0.5), RandUtil.CRF(0.3));
		scale.set(orgScale, orgScale, orgScale);
		gravity=-0.01f;
		
	}
	
	@Override
	public void update(){
		super.update();
		if(age>=maxAge){
			kill();
			return;
		}
		float ageDiv=(float)Math.sqrt(1-age/maxAge);
//		LogUtil.println(age,maxAge);
		this.scale.set(orgScale*ageDiv, orgScale*ageDiv, orgScale*ageDiv);
	}
	
}
