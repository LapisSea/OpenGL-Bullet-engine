package com.lapissea.opengl.rendering.shader.uniforms;

import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.window.assets.ITextureCube;

public class UniformSamplerCube extends UniformInt1{
	
	public UniformSamplerCube(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(ITextureCube texture){
		super.upload(texture.getId());
	}
	
}
