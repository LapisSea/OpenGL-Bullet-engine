package com.lapissea.opengl.program.rendering.gl.shader.uniforms;

import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.window.assets.ITexture;

public class UniformSampler2D extends UniformInt1{
	
	public UniformSampler2D(Shader shader, int id, String name){
		super(shader, id, name);
	}
	
	public void upload(ITexture texture){
		super.upload(texture.getId());
	}
	
}
