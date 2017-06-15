package com.lapissea.opengl.program.game.particle.particles;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.particle.Particle;
import com.lapissea.opengl.program.game.particle.ParticleHandler;
import com.lapissea.opengl.program.util.RandUtil;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ParticleFoo extends Particle<ParticleFoo>{
	
	private float maxAge=150+RandUtil.RI(100),orgScale=4F+RandUtil.RF(5);
	
	public ParticleFoo(ParticleHandler<ParticleFoo> handler, Vec3f pos){
		super(handler, Game.get().world.putHeightAt(pos));
		speed.set(RandUtil.CRF(0.1), 0.1F+RandUtil.RF(0.1), RandUtil.CRF(0.1));
		scale.set(orgScale, orgScale, orgScale);
		this.pos.sub(0, orgScale/2, 0);
		this.prevPos.sub(0, orgScale/2, 0);
		gravity=-0.001f;
		
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
