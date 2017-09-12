package com.lapissea.opengl.game.particle.particles;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.game.particle.Particle;
import com.lapissea.opengl.game.particle.ParticleHandler;
import com.lapissea.opengl.util.Rand;
import com.lapissea.opengl.util.math.vec.Vec3f;

public class ParticleFoo extends Particle<ParticleFoo>{
	
	private float maxAge=150+Rand.i(100),orgScale=4F+Rand.f(5);
	
	public ParticleFoo(ParticleHandler<ParticleFoo> handler, Vec3f pos){
		super(handler, Game.get().world.putHeightAt(pos));
		speed.set(Rand.cd(0.1), 0.1F+Rand.f(0.1), Rand.cd(0.1));
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
