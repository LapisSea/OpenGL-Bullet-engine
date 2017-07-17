package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import com.lapissea.opengl.program.game.particle.Particle;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;

public class ParticleShader extends ShaderRenderer.Basic3D<Particle<?>>{
	
	public ParticleShader(){
		super("entity");
	}
	
}
