package com.lapissea.opengl.rendering.shader.shaders;

import com.lapissea.opengl.game.particle.Particle;
import com.lapissea.opengl.rendering.shader.ShaderRenderer;

public class ParticleShader extends ShaderRenderer.Basic3D<Particle<?>>{
	
	public ParticleShader(){
		super("entity");
	}
	
}
