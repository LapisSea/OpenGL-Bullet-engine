package com.lapissea.opengl.rendering.shader.uniforms;

import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.window.assets.ITexture;

public class UniformSampler2D extends UniformInt1{
	
	public UniformSampler2D(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(ITexture texture){
		super.upload(texture.getId());
	}
	
}
